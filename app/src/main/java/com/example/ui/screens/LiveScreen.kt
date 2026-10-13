package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// Data structure representing products shown in live stream
data class CustomerLiveProduct(
    val id: String,
    val title: String,
    val market: String,
    val price: Double,
    val weightInfo: String,
    val labelText: String,
    val isHotDeal: Boolean,
    val emoji: String,
    val colorHex: Long
)

// Data structure representing live message log
data class LiveChatMessage(
    val sender: String,
    val message: String,
    val isBot: Boolean = false,
    val isSeller: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen(
    viewModel: JastipViewModel,
    mitraId: String = "jastip_joko",
    onNavigateToCart: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onNavigateToTopUp: () -> Unit = {},
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    // State managers
    var isMuted by remember { mutableStateOf(false) }
    var chatInputText by remember { mutableStateOf("") }
    var showProductBottomSheet by remember { mutableStateOf(false) }
    val recentlyAddedIds = remember { mutableStateMapOf<String, Boolean>() }
    var showInsufficientBalanceDialog by remember { mutableStateOf(false) }
    var insufficientBalanceAmount by remember { mutableStateOf(0.0) }
    
    // FASE 1 Activity Diagram logic states
    var activeOrderProduct by remember { mutableStateOf<CustomerLiveProduct?>(null) }
    var orderPhase by remember { mutableIntStateOf(0) }
    var orderQuantity by remember { mutableIntStateOf(1) }
    
    // Live items defined in screenshot
    val liveCatalog = remember {
        listOf(
            CustomerLiveProduct("p1", "Ikan Kembung Banjar", "Muara Angke", 45000.0, "1kg", "STOK TERBATAS", false, "🐟", 0xFFE0F2F1),
            CustomerLiveProduct("p2", "Cumi Sero Segar", "Pasar Pabean", 65000.0, "500g", "STOK TERBATAS", false, "🦑", 0xFFEDE7F6),
            CustomerLiveProduct("p3", "Ikan Kakap Merah", "Muara Angke", 85000.0, "1kg", "HOT DEAL", true, "🐠", 0xFFFFEBEE),
            CustomerLiveProduct("p4", "Udang Vaname Super", "Pasar Kedonganan", 120000.0, "1kg", "HOT DEAL", true, "🦐", 0xFFFFF3E0),
            CustomerLiveProduct("p5", "Kepiting Bakau", "Tarakan", 185000.0, "1kg (2-3 pcs)", "STOK TERBATAS", false, "🦀", 0xFFFFF8E1),
            CustomerLiveProduct("p6", "Kerang Hijau", "Muara Karang", 35000.0, "1kg", "STOK TERBATAS", false, "🐚", 0xFFE0F7FA)
        )
    }

    val mitraName = remember(mitraId) {
        when(mitraId) {
            "jastip_joko" -> "Pak Joko"
            "jastip_made" -> "Pak Made"
            "jastip_rio" -> "Mas Rio"
            else -> "Pak Joko"
        }
    }
    
    val mitraCity = remember(mitraId) {
        when(mitraId) {
            "jastip_joko" -> "Jakarta"
            "jastip_made" -> "Bali"
            "jastip_rio" -> "Medan"
            else -> "Jakarta"
        }
    }

    val mitraMarket = remember(mitraId) {
        when(mitraId) {
            "jastip_joko" -> "Muara Angke"
            "jastip_made" -> "Pasar Kedonganan"
            "jastip_rio" -> "Pasar Bengkel"
            else -> "Muara Angke"
        }
    }
    
    // Live chats initially filled
    val conversations = remember {
        mutableStateListOf(
            LiveChatMessage("BOT IkanKu", "Selamat datang di Live Shopping! Silakan tanya stok atau request timbang ikan ya Kak.", isBot = true),
            LiveChatMessage("Budi Santoso", "Kakap merahnya ada yang ukuran 2kg gak?"),
            LiveChatMessage("$mitraName (Seller)", "Ready mas Budi! Langsung saya timbang ya. Klik 'Beli' di keranjang untuk kunci harga.", isSeller = true),
            LiveChatMessage("Siti Aminah", "Ongkir ke kota Bandung berapa ya?")
        )
    }

    // Scroll automatically when comment appends
    val chatScrollState = rememberLazyListState()
    LaunchedEffect(conversations.size) {
        if (conversations.isNotEmpty()) {
            chatScrollState.animateScrollToItem(conversations.size - 1)
        }
    }

    // Simulate new incoming chats to make it dynamic and fun!
    LaunchedEffect(Unit) {
        val sampleNames = listOf("Dewi Ratna", "Ronal", "Hendry", "Siska Amelia", "Irwan", "Yulie")
        val sampleMsgs = listOf(
            "Cumi sero segar masih ada gak om?",
            "Kepiting bakau itu capitnya besar gak ya?",
            "Mantap dapet harga lelang murah nih",
            "Beli kerang hijau 2 kilo langsung di-add",
            "Muara Angke langsung dikirim sekarang?",
            "Udang vaname super lezat dibumbu saus padang",
            "Request timbang dong kepitingnya bos"
        )
        while (true) {
            delay(8000)
            val name = sampleNames.random()
            val msg = sampleMsgs.random()
            conversations.add(LiveChatMessage(name, msg))
        }
    }

    // Collect Cart total count from StateFlow
    val cartItemsState by viewModel.allCartItems.collectAsState()
    val totalInCartValue = cartItemsState.sumOf { it.quantity }
    val userBalance by viewModel.userBalance.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF071927)) // Immersive deep twilight navy background
    ) {
        // ==============================================================
        // LAYER 0: IMMERSIVE STREAMING PORTRAIT VIDEO BACKGROUND
        // ==============================================================
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Dark bluish aquatic gradient representing underwater sea/auction live video
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0C3D59), Color(0xFF031420))
                )
            )

            // Dynamic background elements simulating pelelangan dock atmosphere
            val gridColor = Color.White.copy(alpha = 0.05f)
            // Camera grid lines
            drawLine(color = gridColor, start = Offset(0f, size.height * 0.3f), end = Offset(size.width, size.height * 0.3f), strokeWidth = 1.dp.toPx())
            drawLine(color = gridColor, start = Offset(0f, size.height * 0.65f), end = Offset(size.width, size.height * 0.65f), strokeWidth = 1.dp.toPx())
            drawLine(color = gridColor, start = Offset(size.width * 0.33f, 0f), end = Offset(size.width * 0.33f, size.height), strokeWidth = 1.dp.toPx())
            drawLine(color = gridColor, start = Offset(size.width * 0.66f, 0f), end = Offset(size.width * 0.66f, size.height), strokeWidth = 1.dp.toPx())

            // Aquatic sunbeam glowing elements from the auction dock lamp (top area)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF81D4FA).copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.15f),
                    radius = 300.dp.toPx()
                ),
                center = Offset(size.width * 0.5f, size.height * 0.15f),
                radius = 300.dp.toPx()
            )

            // Fish net grid simulation or sea waves
            drawCircle(
                color = Color(0xFF0288D1).copy(alpha = 0.15f),
                center = Offset(size.width * 0.5f, size.height * 0.45f),
                radius = 120.dp.toPx()
            )
        }

        // Animated overlay showing moving dust/ocean bubble particles
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 60.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.03f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🐟直播",
                    fontSize = 32.sp,
                    color = Color.White.copy(alpha = 0.1f)
                )
            }
        }

        // ==============================================================
        // LAYER 1: TRANSPARENT CONTROL OVERLAY (TOP STATS & FLOATING BADGES)
        // ==============================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                    ) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Top-Left Info Box
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    // LIVE badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE53935))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color.White, CircleShape)
                            )
                            Text(
                                "LIVE",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 9.sp
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "1.2k Penonton",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                }

                // Top-Right Circular Control buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Personal Shopping Cart with badge count
                    Box {
                        IconButton(
                            onClick = onNavigateToCart,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                .testTag("btn_personal_cart_live")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = "Keranjang Belanja",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Badge count if totalInCartValue > 0
                        if (totalInCartValue > 0) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                                    .border(1.dp, Color.White, CircleShape)
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = totalInCartValue.toString(),
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Volume mute toggle
                    IconButton(
                        onClick = {
                            isMuted = !isMuted
                            Toast.makeText(
                                context,
                                if (isMuted) "Suara siaran dinonaktifkan" else "Suara siaran diaktifkan",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Outlined.VolumeOff else Icons.Outlined.VolumeUp,
                            contentDescription = "Mute",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Membagikan link siaran...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Bagikan",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Stream Title Banner positioned right under Top Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.25f))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Jastip Ikan Segar LANGSUNG DARI $mitraMarket",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Color(0xFF4FC3F7),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$mitraName - Jastip $mitraCity",
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // ==============================================================
        // LAYER 2: INTERACTION CHAT & ACTION DECK (BOTTOM OVERLAYS)
        // ==============================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                        startY = 0f
                    )
                )
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Chat & comments overlapping panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .heightIn(max = 200.dp)
            ) {
                LazyColumn(
                    state = chatScrollState,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 4.dp)
                ) {
                    items(conversations) { item ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when {
                                        item.isBot -> Color(0xFFE0F2F1).copy(alpha = 0.9f)
                                        item.isSeller -> Color(0xFF0D47A1).copy(alpha = 0.9f)
                                        else -> Color.Black.copy(alpha = 0.35f)
                                    }
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Text(
                                    text = item.sender,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        item.isBot -> Color(0xFF004D40)
                                        item.isSeller -> Color(0xFFFFD54F)
                                        else -> Color(0xFFFFCC80)
                                    }
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.message,
                                    fontSize = 12.sp,
                                    color = when {
                                        item.isBot -> Color(0xFF004D40)
                                        else -> Color.White
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Input Column with Bottom Sheet Shopping Launcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Outer input text line
                OutlinedTextField(
                    value = chatInputText,
                    onValueChange = { chatInputText = it },
                    placeholder = {
                        Text(
                            "Ketik pesan...",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .shadow(2.dp, CircleShape),
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                        focusedContainerColor = Color.White.copy(alpha = 0.25f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedBorderColor = Color.White.copy(alpha = 0.4f)
                    ),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Send
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onSend = {
                            if (chatInputText.isNotBlank()) {
                                conversations.add(LiveChatMessage("Andi Wijaya (Saya)", chatInputText))
                                chatInputText = ""
                            }
                        }
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (chatInputText.isNotBlank()) {
                                    conversations.add(LiveChatMessage("Andi Wijaya (Saya)", chatInputText))
                                    chatInputText = ""
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Kirim",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )

                // Etalase FAB (Shopping Cart Button launching bottom list sheet)
                Box {
                    FloatingActionButton(
                        onClick = { showProductBottomSheet = true },
                        shape = CircleShape,
                        containerColor = Color(0xFF006684),
                        contentColor = Color.White,
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("btn_live_etalase")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Etalase Live",
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Total products badge count
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .border(1.5.dp, Color.White, CircleShape)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = liveCatalog.size.toString(),
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        // ==============================================================
        // THE BOTTOM SHEET ETALASE PRODUCT SELECTION
        // ==============================================================
        if (showProductBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showProductBottomSheet = false },
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .padding(bottom = 24.dp)
                ) {
                    // Header sheet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalMall,
                                contentDescription = null,
                                tint = Color(0xFF006684),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Produk Live Hari Ini",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF102A43)
                            )
                        }
                        
                        Text(
                            text = "${liveCatalog.size} Ikan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF5A738E),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Products vertical list
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(liveCatalog) { product ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE4E9F0))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Image Box
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(product.colorHex)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(product.emoji, fontSize = 28.sp)

                                        // Badge sticker label absolutely positioned
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .background(
                                                    if (product.isHotDeal) Color(0xFFFFEBEE) else Color(0xFFE0F2F1),
                                                    RoundedCornerShape(bottomEnd = 8.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = product.labelText,
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (product.isHotDeal) Color(0xFFC62828) else Color(0xFF00796B)
                                            )
                                        }
                                    }

                                    // Content info description
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = product.title,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF102A43)
                                        )
                                        Text(
                                            text = "${product.market} - ${product.weightInfo}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF5A738E),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = currencyFormatter.format(product.price),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF006684)
                                        )
                                    }

                                    // Buy/Add button with solid brand coloration and responsive Toast
                                    Button(
                                        onClick = {
                                            activeOrderProduct = product
                                            orderQuantity = 1
                                            orderPhase = 1
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF006684)
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier
                                            .height(34.dp)
                                            .testTag("btn_buy_live_product_${product.id}")
                                    ) {
                                        Text(
                                            text = "Pesan",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    val cartCount = totalInCartValue
                    if (cartCount > 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { 
                                showProductBottomSheet = false
                                onNavigateToCart() 
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Checkout (CO) via Katalog Live ($cartCount Item)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (orderPhase > 0 && activeOrderProduct != null) {
        val prod = activeOrderProduct!!
        
        when (orderPhase) {
            1 -> {
                // FASE 1: Isi Detail Pesanan
                AlertDialog(
                    onDismissRequest = { orderPhase = 0 },
                    title = {
                        Text(text = "Detail Pesanan", fontWeight = FontWeight.ExtraBold, color = Color(0xFF001F2A))
                    },
                    text = {
                        Column {
                            Text("${prod.title} (${prod.market})", fontWeight = FontWeight.Bold)
                            Text("Harga: ${currencyFormatter.format(prod.price)} / ${prod.weightInfo}", color = Color.Gray, fontSize = 13.sp)
                            Spacer(Modifier.height(16.dp))
                            Text("Jumlah:", fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { if (orderQuantity > 1) orderQuantity-- }) {
                                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = Color(0xFF006684))
                                }
                                Text(orderQuantity.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                                IconButton(onClick = { orderQuantity++ }) {
                                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color(0xFF006684))
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { orderPhase = 2 }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684))) {
                            Text("Pesan", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { orderPhase = 0 }) { Text("Batal", color = Color.Gray) }
                    }
                )
            }
            2 -> {
                // FASE 1: Menunggu konfirmasi Jastiper
                LaunchedEffect(Unit) {
                    delay(2500)
                    // Simulasi Jastiper terima (85%) atau tolak (15%)
                    if (Math.random() > 0.15) {
                        orderPhase = 3
                    } else {
                        orderPhase = 4
                    }
                }
                AlertDialog(
                    onDismissRequest = {},
                    title = null,
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            CircularProgressIndicator(color = Color(0xFF006684))
                            Spacer(Modifier.height(16.dp))
                            Text("Menunggu Jastiper...", fontWeight = FontWeight.Bold, color = Color(0xFF001F2A))
                            Text("Mengecek stok barang real-time", fontSize = 12.sp, color = Color.Gray)
                        }
                    },
                    confirmButton = {}
                )
            }
            3 -> {
                // FASE 1: Ringkasan Order & Terima
                val totalIkan = prod.price * orderQuantity
                val ongkir = 45000.0
                val feeJastip = 15000.0
                val grandTotal = totalIkan + ongkir + feeJastip
                
                AlertDialog(
                    onDismissRequest = { orderPhase = 0 },
                    title = {
                        Text("Ringkasan Order", fontWeight = FontWeight.ExtraBold, color = Color(0xFF001F2A))
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Jastiper menerima pesanan Anda. Stok tersedia!", color = Color(0xFF2E7D32), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${prod.title} (x${orderQuantity})", fontSize = 13.sp)
                                Text(currencyFormatter.format(totalIkan), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Ongkir", fontSize = 13.sp)
                                Text(currencyFormatter.format(ongkir), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Fee Jastip", fontSize = 13.sp)
                                Text(currencyFormatter.format(feeJastip), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total", fontWeight = FontWeight.ExtraBold)
                                Text(currencyFormatter.format(grandTotal), fontWeight = FontWeight.ExtraBold, color = Color(0xFF006684))
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { 
                                // Check if balance is sufficient before proceeding
                                val totalIkan = prod.price * orderQuantity
                                val ongkir = 45000.0
                                val feeJastip = 15000.0
                                val grandTotal = totalIkan + ongkir + feeJastip
                                
                                if (userBalance < grandTotal) {
                                    orderPhase = 0
                                    insufficientBalanceAmount = grandTotal
                                    showInsufficientBalanceDialog = true
                                    return@Button
                                }
                                
                                orderPhase = 0
                                showProductBottomSheet = false
                                // Clear cart and add only this live order item
                                coroutineScope.launch {
                                    viewModel.clearCart()
                                    kotlinx.coroutines.delay(100) // Small delay to ensure cart is cleared
                                    for (i in 1..orderQuantity) {
                                        viewModel.addToCart(prod.title, prod.market, prod.price, prod.id)
                                    }
                                    kotlinx.coroutines.delay(100) // Small delay to ensure items are added
                                    onNavigateToCheckout()
                                }
                            }, 
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684))
                        ) {
                            Text("Lanjut ke Pembayaran", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { orderPhase = 0 }) { Text("Batal", color = Color.Gray) }
                    }
                )
            }
            4 -> {
                // FASE 1: Tolak pesanan (Stok Habis)
                AlertDialog(
                    onDismissRequest = { orderPhase = 0 },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color.Red)
                            Spacer(Modifier.width(8.dp))
                            Text("Pesanan Ditolak", fontWeight = FontWeight.ExtraBold, color = Color.Red)
                        }
                    },
                    text = {
                        Text("Maaf, Jastiper menolak pesanan Anda karena stok barang sudah habis / tidak tersedia di pasar.", fontSize = 14.sp)
                    },
                    confirmButton = {
                        Button(onClick = { orderPhase = 0 }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                            Text("Tutup", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                )
            }
        }
        
        // Dialog for insufficient balance
        if (showInsufficientBalanceDialog) {
            AlertDialog(
                onDismissRequest = { showInsufficientBalanceDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFFFF6F00))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saldo Tidak Mencukupi", fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF6F00))
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Saldo WavePay Anda tidak mencukupi untuk menyelesaikan transaksi ini.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF4C626B)
                        )
                        Divider(color = Color(0xFFECEFF1))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Saldo Anda:", fontWeight = FontWeight.Medium, color = Color.Gray)
                            Text(currencyFormatter.format(userBalance), fontWeight = FontWeight.Bold, color = Color(0xFF001F2A))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Pembayaran:", fontWeight = FontWeight.Medium, color = Color.Gray)
                            Text(currencyFormatter.format(insufficientBalanceAmount), fontWeight = FontWeight.Bold, color = Color(0xFF006684))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Kekurangan:", fontWeight = FontWeight.Bold, color = Color.Red)
                            Text(currencyFormatter.format(insufficientBalanceAmount - userBalance), fontWeight = FontWeight.ExtraBold, color = Color.Red)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Silakan top up saldo WavePay Anda terlebih dahulu untuk melanjutkan transaksi.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showInsufficientBalanceDialog = false
                            onNavigateToTopUp()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Top Up Saldo", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showInsufficientBalanceDialog = false }) {
                        Text("Batal", color = Color.Gray)
                    }
                }
            )
        }
    }
}
