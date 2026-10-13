package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel
import java.text.NumberFormat
import java.util.Locale

// Specific design palette colors matching the Web Screenshot
val jastipNavy = Color(0xFF102A43)        // Deep navy
val jastipBlueAccent = Color(0xFF0F5B9B)  // Royal blue accent
val jastipLightBlueBg = Color(0xFFEBF3FC) // Very light blue background tint of guide banner
val jastipCardBg = Color(0xFFFFFFFF)
val jastipTextGray = Color(0xFF627D98)    // Slate subtitle text color
val jastipGreenAccent = Color(0xFF2C9C84) // Online or percentage green
val jastipIconBg = Color(0xFFE4F2FE)      // Accent icon backgrounds
val jastipWaveBlue = Color(0xFF1565C0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JastiperDashboardScreen(
    viewModel: JastipViewModel,
    onLogoutClick: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToLive: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Current Jastiper activity status: Online or Offline
    var isOnline by remember { mutableStateOf(true) }
    var showStatusMenu by remember { mutableStateOf(false) }

    // Live Streaming Status State
    var isLiveActive by remember { mutableStateOf(false) }
    var connectedLiveUsers by remember { mutableStateOf(0) }
    var liveLocation by remember { mutableStateOf("Muara Angke") }

    // Manual Proposal form states
    val productOptions = listOf(
        "Kakap Merah (Premium)",
        "Salmon Fillet (Premium)",
        "Tuna Steak (Fresh)",
        "Udang Windu (Super)",
        "Kepiting Bakau (Live)"
    )
    var selectedProduct by remember { mutableStateOf(productOptions[0]) }
    var selectedProductPrice by remember { mutableStateOf(85000) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    
    var weightInput by remember { mutableStateOf("2") }
    var priceInput by remember { mutableStateOf("85000") }
    var jastipFeeInput by remember { mutableStateOf("15000") }

    // Chat and manual order logic targets
    var chatSearchQuery by remember { mutableStateOf("") }
    
    // Hardcoded static lists for high fidelity representation of bottom screens
    val chatThreads = remember {
        listOf(
            ChatThread("iti_aminah", "Siti Aminah", "Mas, Kakap Merahnya masih ada?", "10:25", "S", Color(0xFFE0F7FA), true),
            ChatThread("andi_wijaya", "Andi Wijaya", "Oke mas, saya transfer sekarang.", "09:12", "A", Color(0xFFFFF3E0), false)
        )
    }
    
    var activeThreadId by remember { mutableStateOf("iti_aminah") }

    // Map holding the mutable states for each chat to provide active, dynamic interactions
    val chatMessagesMap = remember {
        mutableStateMapOf(
            "iti_aminah" to mutableStateListOf(
                LocalMessage("Siti Aminah", "Halo Jastiper! Kakap Merah yang di live tadi masih ada 2kg?", "10:22 AM", false),
                LocalMessage("Saya", "Masih ada kak Siti. Kualitasnya super segar, baru naik dari kapal 15 menit lalu.", "10:22 AM", true),
                LocalMessage("Siti Aminah", "Mas, Kakap Merahnya masih ada?", "10:25 AM", false)
            ),
            "andi_wijaya" to mutableStateListOf(
                LocalMessage("Andi Wijaya", "Sore mas Budi, saya cari Kepiting Bakau yang masih super jumbo live. Masih ada?", "09:10 AM", false),
                LocalMessage("Saya", "Ada mas Andi! Tadi pagi baru mendarat, capitnya besar mantap sekali.", "09:11 AM", true),
                LocalMessage("Andi Wijaya", "Oke mas, saya transfer sekarang.", "09:12 AM", false)
            )
        )
    }

    var messageInputText by remember { mutableStateOf("") }
    
    // SnackBar error/success feedback
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Handle Scroll to Top event from Bottom Navigation
    LaunchedEffect(Unit) {
        viewModel.scrollToTopEvent.collect {
            listState.animateScrollToItem(0)
        }
    }

    // Price calculation helpers
    val weightDouble = weightInput.toDoubleOrNull() ?: 0.0
    val priceDouble = priceInput.toDoubleOrNull() ?: 0.0
    val jastipFeeDouble = jastipFeeInput.toDoubleOrNull() ?: 15000.0

    val calculatedSubtotal = weightDouble * priceDouble
    val calculatedTotal = calculatedSubtotal + jastipFeeDouble

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
            ) {
                // Main Header Row matching screenshot precisely
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Halo, Jastiper Sultan!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = jastipNavy,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Mari bantu pelanggan mendapatkan hasil laut terbaik hari ini.",
                            fontSize = 12.sp,
                            color = jastipTextGray
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Status Switcher Badges
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFF0F4F8))
                                    .clickable { showStatusMenu = true }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isOnline) jastipGreenAccent else Color.Gray)
                                )
                                Text(
                                    text = if (isOnline) "Online" else "Offline",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = jastipNavy
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Pilih Status",
                                    tint = jastipNavy,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showStatusMenu,
                                onDismissRequest = { showStatusMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Setel Online") },
                                    onClick = {
                                        isOnline = true
                                        showStatusMenu = false
                                    },
                                    leadingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(jastipGreenAccent)
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Setel Offline") },
                                    onClick = {
                                        isOnline = false
                                        showStatusMenu = false
                                    },
                                    leadingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color.Gray)
                                        )
                                    }
                                )
                            }
                        }

                        // Notification Icon card
                        IconButton(
                            onClick = onNavigateToNotifications,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F4F8))
                        ) {
                            Box {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifikasi",
                                    tint = jastipNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                                // Red dot notification badge
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                        .align(Alignment.TopEnd)
                                        .offset(x = 2.dp, y = (-2).dp)
                                )
                            }
                        }
                    }
                }
                Divider(color = Color(0xFFE4F2FE), thickness = 1.dp)
            }
        }
    ) { paddingValues ->
        // Main scrollable content
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5FAFD))
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Carousel (Horizontal Grid Carousel of KPI Cards)
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ringkasan Performa",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = jastipNavy,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Pendapatan Bulan Ini Card
                        item {
                            Card(
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(130.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = jastipCardBg),
                                border = BorderStroke(1.dp, Color(0xFFE4E9F0))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(jastipIconBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccountBalanceWallet,
                                                contentDescription = null,
                                                tint = jastipBlueAccent,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = "+12%",
                                                color = jastipGreenAccent,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Icon(
                                                imageVector = Icons.Default.TrendingUp,
                                                contentDescription = null,
                                                tint = jastipGreenAccent,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = "Pendapatan Bulan Ini",
                                            fontSize = 11.sp,
                                            color = jastipTextGray,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Rp 4.500.000",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = jastipNavy
                                        )
                                    }
                                }
                            }
                        }

                        // Jumlah Pesanan Card
                        item {
                            Card(
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(130.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = jastipCardBg),
                                border = BorderStroke(1.dp, Color(0xFFE4E9F0))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(jastipIconBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ShoppingCart,
                                                contentDescription = null,
                                                tint = jastipBlueAccent,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        
                                        Text(
                                            text = "Bulan ini",
                                            color = jastipTextGray,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "Jumlah Pesanan",
                                            fontSize = 11.sp,
                                            color = jastipTextGray,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "128 Pesanan",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = jastipNavy
                                        )
                                    }
                                }
                            }
                        }

                        // Customer Rating Card
                        item {
                            Card(
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(130.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = jastipCardBg),
                                border = BorderStroke(1.dp, Color(0xFFE4E9F0))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(jastipIconBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = Color(0xFFFFB300),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        
                                        Text(
                                            text = "Sangat Baik",
                                            color = jastipGreenAccent,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "Rating Jastiper",
                                            fontSize = 11.sp,
                                            color = jastipTextGray,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "4.9 / 5.0",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = jastipNavy
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Live Control Center Quick Access (Prominent Blue Card)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, jastipBlueAccent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(jastipBlueAccent, jastipWaveBlue)
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isLiveActive) Color.Red else Color.White)
                                )
                                Text(
                                    text = "PUSAT STREAMING JASTIP",
                                    color = Color(0xFFFFD1D1),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = liveLocation,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isLiveActive) "Live siaran sedang berlangsung aktif!" else "Siarkan Kualitas Ikan Dermaga Secara Live",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isLiveActive) "👥 Menyambungkan dengan pelanggan..." else "Tarik pesanan lebih cepat dengan interaksi langsung di pelelangan.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 15.sp
                        )
                        
                        Spacer(modifier = Modifier.height(18.dp))
                        
                        Button(
                            onClick = {
                                onNavigateToLive()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isLiveActive) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = jastipBlueAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isLiveActive) "Matikan Live" else "Mulai Live",
                                    color = jastipBlueAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Simplified Quick Guides (Vertical layout with large number icons)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = jastipLightBlueBg),
                    border = BorderStroke(1.dp, Color(0xFFD4E6F6))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Text(
                            text = "Panduan Cepat Jastiper",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = jastipNavy
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            GuideStep(
                                number = "1",
                                title = "Pilih Produk Segar",
                                subtext = "Pilih seafood kualitas ekspor terbaik langsung di dermaga."
                            )
                            GuideStep(
                                number = "2",
                                title = "Siarkan Live Stream",
                                subtext = "Tunjukkan fisik produk laut segar secara live kepada pelanggan."
                            )
                            GuideStep(
                                number = "3",
                                title = "Kirim Link Checkout",
                                subtext = "Buat penawaran instan dan kirim langsung di chat pelanggan."
                            )
                        }
                    }
                }
            }
            

        }
    }
}

@Composable
fun GuideStep(
    number: String,
    title: String,
    subtext: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(jastipBlueAccent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = jastipNavy
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                fontSize = 11.sp,
                color = jastipTextGray,
                lineHeight = 15.sp
            )
        }
    }
}

data class ChatThread(
    val id: String,
    val name: String,
    val lastMsg: String,
    val time: String,
    val initial: String,
    val initialBg: Color,
    val isOnline: Boolean
)

data class LocalMessage(
    val sender: String,
    val text: String,
    val time: String,
    val isFromMe: Boolean
)
