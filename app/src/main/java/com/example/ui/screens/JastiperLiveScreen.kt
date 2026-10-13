package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JastiperLiveScreen(
    viewModel: JastipViewModel,
    modifier: Modifier = Modifier,
    onNavigateToNotifications: () -> Unit = {}
) {
    val context = LocalContext.current
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")) }
    val setupScrollState = rememberScrollState()
    val chatListState = rememberLazyListState()
    val jastipNavy = Color(0xFF102A43)
    val jastipBlueAccent = Color(0xFF0F5B9B)
    val jastipTextGray = Color(0xFF627D98)
    val jastipGreenAccent = Color(0xFF2C9C84)
    val jastipLightBlueBg = Color(0xFFEBF3FC)

    // --- STREAM STATES ---
    var isLiveActive by remember { mutableStateOf(false) }

    // Handle Scroll to Top event from Bottom Navigation
    LaunchedEffect(Unit) {
        viewModel.scrollToTopEvent.collect {
            if (!isLiveActive) {
                setupScrollState.animateScrollTo(0)
            } else {
                chatListState.animateScrollToItem(0)
            }
        }
    }
    var locationInput by remember { mutableStateOf("Muara Angke, Jakarta") }
    var streamTitle by remember { mutableStateOf("Lelang Ikan Segar Pagi Hari") }
    var streamQuality by remember { mutableStateOf("1080p") }
    
    // Toggles
    var isMicOn by remember { mutableStateOf(true) }
    var isCameraOn by remember { mutableStateOf(true) }
    var isCameraFront by remember { mutableStateOf(false) }

    // Bottom Sheets visible
    var showProductSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var commentInputText by remember { mutableStateOf("") }
    var showExitConfirmation by remember { mutableStateOf(false) }

    // --- TIMER SYSTEM ---
    var streamTimeSeconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(isLiveActive) {
        if (isLiveActive) {
            streamTimeSeconds = 0
            while (true) {
                delay(1000)
                streamTimeSeconds++
            }
        }
    }
    
    val formattedStreamTime = remember(streamTimeSeconds) {
        val h = streamTimeSeconds / 3600
        val m = (streamTimeSeconds % 3600) / 60
        val s = streamTimeSeconds % 60
        String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
    }

    // --- LIVE PRODUCTS DATABASE LIST ---
    val products = remember {
        mutableStateListOf(
            LiveProduct("p1", "Ikan Kakap Merah Super", 85000, "/kg", 15, isPinned = false, isLiveNow = true, emoji = "🐟", photoColor = 0xFFFFEBEE),
            LiveProduct("p2", "Udang Vaname Grade A", 120000, "/kg", 8, isPinned = true, isLiveNow = false, emoji = "🦐", photoColor = 0xFFFFE0B2),
            LiveProduct("p3", "Kepiting Bakau Jumbo", 155000, "/kg", 15, isPinned = false, isLiveNow = false, emoji = "🦀", photoColor = 0xFFFCE4EC),
            LiveProduct("p4", "Cumi Sero Segar", 95000, "/kg", 10, isPinned = false, isLiveNow = false, emoji = "🦑", photoColor = 0xFFEDE7F6)
        )
    }

    val pinnedProduct = remember(products.size, products.map { it.isPinned }) {
        products.find { it.isPinned }
    }

    // --- COMMENTS LIST STATE ---
    val liveComments = remember {
        mutableStateListOf(
            LiveMessage("Budi_Santoso", "Ikan Tenggiri ada gak mas?"),
            LiveMessage("Admin_IkanKu", "Ready kak, cek keranjang nomor 2!"),
            LiveMessage("Ani_Wijaya", "Kirim ke Bandung berapa hari ya?"),
            LiveMessage("Rizky Perdana", "Min, ikan tenggiri harganya bisa goyang dikit ga buat borongan?")
        )
    }

    // Auto-scroll chat to bottom
    LaunchedEffect(liveComments.size) {
        if (liveComments.isNotEmpty()) {
            chatListState.animateScrollToItem(liveComments.size - 1)
        }
    }

    // --- SALES HISTORY ---
    val salesHistory = remember {
        mutableStateListOf(
            LiveSale("Siti Aminah", "Ikan Kakap Merah Super", 3.0, 255000),
            LiveSale("Andi Wijaya", "Udang Vaname Grade A", 2.0, 240000),
            LiveSale("Dewi Lestari", "Kepiting Bakau Jumbo", 1.5, 232500)
        )
    }

    val totalLiveRevenue = remember(salesHistory.size) {
        salesHistory.sumOf { it.totalPrice }
    }

    // Bitrate matching resolution
    val connectionBitrate = when (streamQuality) {
        "1080p" -> "6,000 kbps"
        "720p" -> "3,500 kbps"
        else -> "Auto - 4,850 kbps"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (!isLiveActive) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .statusBarsPadding()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Live",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = jastipNavy,
                            letterSpacing = (-0.5).sp
                        )
                        
                        // Notification Icon card
                        IconButton(
                            onClick = onNavigateToNotifications,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
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
                    HorizontalDivider(color = Color(0xFFE4F2FE), thickness = 1.dp)
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5FAFD))
        ) {
            if (!isLiveActive) {
                // ==========================================
                // 1. SETUP / PREVIEW SCREEN (BEFORE LIVE)
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                        .verticalScroll(setupScrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Top camera preview area (Mock camera)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        // Fake Camera Feed Background
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val gradientBg = Brush.linearGradient(
                                colors = if (isCameraFront) {
                                    listOf(Color(0xFF1B3B5A), Color(0xFF0F1E2D))
                                } else {
                                    listOf(Color(0xFF104F75), Color(0xFF051C2C))
                                }
                            )
                            drawRect(brush = gradientBg)

                            // Subtle grid lines to indicate camera
                            val gridColor = Color.White.copy(alpha = 0.15f)
                            drawLine(color = gridColor, start = Offset(0f, size.height / 3), end = Offset(size.width, size.height / 3), strokeWidth = 1.dp.toPx())
                            drawLine(color = gridColor, start = Offset(0f, size.height * 2 / 3), end = Offset(size.width, size.height * 2 / 3), strokeWidth = 1.dp.toPx())
                            drawLine(color = gridColor, start = Offset(size.width / 3, 0f), end = Offset(size.width / 3, size.height), strokeWidth = 1.dp.toPx())
                            drawLine(color = gridColor, start = Offset(size.width * 2 / 3, 0f), end = Offset(size.width * 2 / 3, size.height), strokeWidth = 1.dp.toPx())
                        }

                        // Camera Controls Overlay (Top Right)
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Flip Camera
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .clickable { isCameraFront = !isCameraFront },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlipCameraAndroid,
                                    contentDescription = "Flip Camera",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            // Flash toggle (Mock)
                            var isFlashOn by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .clickable { isFlashOn = !isFlashOn },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Toggle Flash",
                                    tint = if (isFlashOn) Color.Yellow else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Main Inputs
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE4F2FE))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Informasi Siaran",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = jastipNavy
                            )
                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            TextField(
                                value = streamTitle,
                                onValueChange = { streamTitle = it },
                                label = { Text("Judul Siaran Live") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                                leadingIcon = { Icon(Icons.Default.BorderColor, contentDescription = null, tint = jastipBlueAccent) },
                                shape = RoundedCornerShape(8.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    unfocusedContainerColor = Color(0xFFF8F9FA),
                                    focusedContainerColor = Color(0xFFF1F5F9)
                                )
                            )

                            TextField(
                                value = locationInput,
                                onValueChange = { locationInput = it },
                                label = { Text("Lokasi Utama") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = jastipBlueAccent) },
                                shape = RoundedCornerShape(8.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    unfocusedContainerColor = Color(0xFFF8F9FA),
                                    focusedContainerColor = Color(0xFFF1F5F9)
                                )
                            )

                            // Quality selection Segmented row
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Pilih Kualitas Video", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = jastipTextGray)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF8F9FA)),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val qualities = listOf("1080p", "720p", "Otomatis")
                                    qualities.forEachIndexed { index, q ->
                                        val isSel = streamQuality == q
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { streamQuality = q }
                                                .background(if (isSel) jastipBlueAccent else Color.Transparent)
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = q,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (isSel) Color.White else jastipTextGray
                                            )
                                        }
                                        if (index < qualities.size - 1) {
                                            VerticalDivider(
                                                modifier = Modifier
                                                    .width(1.dp)
                                                    .height(40.dp)
                                                    .align(Alignment.CenterVertically),
                                                color = Color(0xFFE0E0E0)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(2.dp))
                            
                            // Etalase Produk List
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF8E1))
                                    .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(8.dp))
                                    .clickable { showProductSheet = true }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFFF57F17))
                                    Column {
                                        Text("Etalase Produk", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF57F17))
                                        Text("${products.size} Produk Terpilih", fontSize = 11.sp, color = Color(0xFFF57F17).copy(alpha = 0.8f))
                                    }
                                }
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFF57F17))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // CTA Start Live
                    Button(
                        onClick = {
                            if (streamTitle.isBlank()) streamTitle = "Lelang Ikan Segar Pagi Hari"
                            isLiveActive = true
                            Toast.makeText(context, "Siaran Langsung Dimulai!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = jastipBlueAccent),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mulai Siaran Live",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // ==========================================
                // 2. ACTIVE LIVE STREAM (PORTRAIT PORT ORIENTATION OVERLAYS)
                // ==========================================
                
                // LAYER 1: PORTRAIT MOCK VIDEO BACKGROUND
                Box(modifier = Modifier.fillMaxSize()) {
                    if (isCameraOn) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val gradientBg = Brush.linearGradient(
                                colors = if (isCameraFront) {
                                    listOf(Color(0xFF1B3B5A), Color(0xFF0F1E2D)) // front container selfie gray
                                } else {
                                    listOf(Color(0xFF104F75), Color(0xFF051C2C)) // back container deep blue-cyan docks
                                }
                            )
                            drawRect(brush = gradientBg)

                            // Subtle grid lines of camera stream
                            val gridColor = Color.White.copy(alpha = 0.1f)
                            drawLine(color = gridColor, start = Offset(0f, size.height / 3), end = Offset(size.width, size.height / 3), strokeWidth = 1.dp.toPx())
                            drawLine(color = gridColor, start = Offset(0f, size.height * 2 / 3), end = Offset(size.width, size.height * 2 / 3), strokeWidth = 1.dp.toPx())
                            drawLine(color = gridColor, start = Offset(size.width / 3, 0f), end = Offset(size.width / 3, size.height), strokeWidth = 1.dp.toPx())
                            drawLine(color = gridColor, start = Offset(size.width * 2 / 3, 0f), end = Offset(size.width * 2 / 3, size.height), strokeWidth = 1.dp.toPx())

                            // Draw a simple graphic stylized ship dock at bottom level
                            val dockPath = Path().apply {
                                moveTo(0f, size.height * 0.55f)
                                lineTo(size.width * 0.4f, size.height * 0.58f)
                                lineTo(size.width * 0.35f, size.height * 0.61f)
                                lineTo(0f, size.height * 0.63f)
                                close()
                            }
                            drawPath(path = dockPath, color = Color(0xFF1E3D59).copy(alpha = 0.3f))

                            // Sparkling light particles in wet market
                            drawCircle(color = Color(0xFFFBC02D).copy(alpha = 0.25f), center = Offset(size.width * 0.82f, size.height * 0.22f), radius = 25.dp.toPx()) // glow
                        }


                    } else {
                        // CAMERA OFF STATE
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF0F1E2D)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.VideocamOff,
                                    contentDescription = "Camera Mute",
                                    tint = Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "STUDIO KAMERA DIMATIKAN",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Penonton tetap bisa mendengar suara & membeli produk",
                                    color = jastipTextGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Mic mute banner status
                    if (!isMicOn) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(top = 80.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Red.copy(alpha = 0.85f))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.MicOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                Text("AUDIO JASTIP OFF (MUTED)", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                // LAYER 2: TRANSPARENT HEADER OVERLAY (TOP STATS & ACTIONS)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent),
                                endY = 450f
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Title info & stop banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Red)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = formattedStreamTime,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = streamTitle,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 1.dp)
                            ) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = jastipGreenAccent, modifier = Modifier.size(11.dp))
                                Text(
                                    text = locationInput,
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Right header action buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Distinct Hentikan Live rounded button
                            Button(
                                onClick = {
                                    showExitConfirmation = true
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = "Selesai", tint = Color.White, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hentikan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Viewer Count & Likes Metrics row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Viewer
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "1.284",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Likes
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(11.dp))
                                Text(
                                    text = "4.2rb",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Connection tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$streamQuality • $connectionBitrate",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // LAYER 3: FLOATING INTERACTIVITY & INPUT ON BOTTOM LEVEL
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                startY = 0f
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Chat column (65% width) side-by-side with circular quick toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Left Column: Chat Room Area + pinned product card
                        Column(
                            modifier = Modifier
                                .weight(4f)
                                .height(240.dp),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            // Embedded Pinned Product card above comments / chat live stream
                            AnimatedVisibility(
                                visible = pinnedProduct != null,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                pinnedProduct?.let { prod ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 6.dp)
                                            .clickable {
                                                showProductSheet = true
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = jastipNavy.copy(alpha = 0.9f)),
                                        border = BorderStroke(1.dp, jastipBlueAccent)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1.5f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clip(CircleShape)
                                                        .background(jastipBlueAccent),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.PushPin,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        text = "PINNED • SOROTAN UTAMA",
                                                        color = Color(0xFFFFD54F),
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Black,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = prod.name,
                                                        color = Color.White,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = "${currencyFormatter.format(prod.price)}${prod.unit} • Stok: ${prod.stock}kg",
                                                        color = Color.LightGray,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            // Lepas Pin Button (with standard target size 48dp height/width via outer padding)
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color.White.copy(alpha = 0.15f))
                                                    .clickable {
                                                        val idx = products.indexOf(prod)
                                                        if (idx != -1) {
                                                            products[idx] = products[idx].copy(isPinned = false)
                                                            Toast.makeText(context, "${prod.name} dilepas dari Pin Sorotan", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Clear,
                                                        contentDescription = "Lepas Pin",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Text(
                                                        text = "Lepas Pin",
                                                        color = Color.White,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            LazyColumn(
                                state = chatListState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                contentPadding = PaddingValues(bottom = 6.dp)
                            ) {
                                items(liveComments) { msg ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black.copy(alpha = 0.45f))
                                            .border(BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f)), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = msg.author,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF64B5F6)
                                            )
                                            Spacer(modifier = Modifier.height(1.dp))
                                            Text(
                                                text = msg.text,
                                                fontSize = 11.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Right Column: Vertical Float Action Control Deck (35% width)
                        Column(
                            modifier = Modifier.weight(1.8f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            // Shopping cart with dynamic badge counter trigger
                            Box {
                                FloatingActionButton(
                                    onClick = { showProductSheet = true },
                                    containerColor = Color.White,
                                    contentColor = jastipBlueAccent,
                                    modifier = Modifier.size(44.dp),
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Produk live",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = products.size.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Flip Camera (visually indicating front vs back status)
                            val flipBgColor = if (isCameraFront) jastipBlueAccent else Color.White
                            val flipContentColor = if (isCameraFront) Color.White else jastipNavy
                            FloatingActionButton(
                                onClick = {
                                    isCameraFront = !isCameraFront
                                    Toast.makeText(
                                        context, 
                                        if (isCameraFront) "Kamera Depan Aktif" else "Kamera Belakang Aktif", 
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                containerColor = flipBgColor,
                                contentColor = flipContentColor,
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cached,
                                    contentDescription = "Balik kamera",
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Gear configuration button to load connection/technical data popup
                            FloatingActionButton(
                                onClick = { showSettingsSheet = true },
                                containerColor = Color.White.copy(alpha = 0.85f),
                                contentColor = jastipNavy,
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Menu Kontrol Siaran",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Input Composer Textbox persis di atas bottom bar navigasi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = commentInputText,
                            onValueChange = { commentInputText = it },
                            placeholder = { Text("Ketik tanggapan live...", fontSize = 13.sp, color = Color.LightGray) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Black.copy(alpha = 0.5f),
                                focusedContainerColor = Color.Black.copy(alpha = 0.7f),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                focusedBorderColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        if (commentInputText.isNotBlank()) {
                                            liveComments.add(LiveMessage("Jastiper (Anda)", commentInputText))
                                            commentInputText = ""
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send comment",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        )
                    }
                }
            }

            // ==========================================
            // MODAL BOTTOM SHEET: PRODUCTS LIST
            // ==========================================
            if (showProductSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showProductSheet = false },
                    containerColor = Color.White,
                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
                ) {
                    var showAddForm by remember { mutableStateOf(false) }
                    var tempName by remember { mutableStateOf("") }
                    var tempPrice by remember { mutableStateOf("") }
                    var tempStock by remember { mutableStateOf("") }
                    var showSalesStats by remember { mutableStateOf(false) }
                    var selectedEmoji by remember { mutableStateOf("🐟") }
                    var selectedColor by remember { mutableLongStateOf(0xFFFFEBEE) }
                    val emojiMapList = remember {
                        listOf(
                            Triple("🐟", 0xFFFFEBEE, "Ikan"),
                            Triple("🦐", 0xFFFFE0B2, "Udang"),
                            Triple("🦀", 0xFFFCE4EC, "Kepiting"),
                            Triple("🦑", 0xFFEDE7F6, "Cumi"),
                            Triple("🐚", 0xFFE0F2F1, "Kerang"),
                            Triple("📦", 0xFFECEFF1, "Lainnya")
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Title bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = jastipBlueAccent, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Produk di Live (${products.size})",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = jastipNavy
                                )
                            }

                            // Add new inline toggle
                            TextButton(
                                onClick = {
                                    showAddForm = !showAddForm
                                    showSalesStats = false
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (showAddForm) "Kembali" else "Tambah Produk",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = jastipBlueAccent
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        if (showAddForm) {
                            // ADD PRODUCT INLINE FORM VIEW
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5FAFD)),
                                border = BorderStroke(1.dp, Color(0xFFEBF3FC))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Tambah Produk Ke List Siaran", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = jastipNavy)
                                    
                                    OutlinedTextField(
                                        value = tempName,
                                        onValueChange = { tempName = it },
                                        placeholder = { Text("Nama Ikan / Seafood") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = jastipBlueAccent)
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = tempPrice,
                                            onValueChange = { tempPrice = it },
                                            placeholder = { Text("Harga /kg (Rp)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = jastipBlueAccent)
                                        )
                                        OutlinedTextField(
                                            value = tempStock,
                                            onValueChange = { tempStock = it },
                                            placeholder = { Text("Stok (kg)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(0.8f),
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = jastipBlueAccent)
                                        )
                                    }

                                    // PHOTO / VISUAL OPTION SELECTOR
                                    Text("Pilih Foto / Visual Produk:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = jastipNavy)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        emojiMapList.forEach { (emoji, colorVal, name) ->
                                            val isSel = selectedEmoji == emoji
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(colorVal))
                                                    .clickable {
                                                        selectedEmoji = emoji
                                                        selectedColor = colorVal
                                                    }
                                                    .border(
                                                        width = if (isSel) 2.dp else 1.dp,
                                                        color = if (isSel) jastipBlueAccent else Color.Transparent,
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(vertical = 4.dp)
                                            ) {
                                                Text(text = emoji, fontSize = 16.sp)
                                                Text(text = name, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = jastipNavy, maxLines = 1)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            val prc = tempPrice.toIntOrNull() ?: 0
                                            val stk = tempStock.toIntOrNull() ?: 0
                                            if (tempName.isNotBlank() && prc > 0 && stk > 0) {
                                                products.add(
                                                    LiveProduct(
                                                        id = "p_${System.currentTimeMillis()}",
                                                        name = tempName,
                                                        price = prc,
                                                        stock = stk,
                                                        emoji = selectedEmoji,
                                                        photoColor = selectedColor
                                                    )
                                                )
                                                Toast.makeText(context, "$tempName berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                                                tempName = ""
                                                tempPrice = ""
                                                tempStock = ""
                                                showAddForm = false
                                            } else {
                                                Toast.makeText(context, "Isi data dengan valid!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = jastipBlueAccent)
                                    ) {
                                        Text("Simpan Produk ke Siaran", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        } else if (showSalesStats) {
                            // REALTIME LIVE SALES HISTORY SCREEN
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = jastipNavy),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Omset Live Sementara", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        TextButton(onClick = { showSalesStats = false }) {
                                            Text("Tutup", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currencyFormatter.format(totalLiveRevenue),
                                        color = Color(0xFF2C9C84),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 10.dp))
                                    Text("Rincian Jastip Terjual:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        salesHistory.forEach { sale ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("🛒 ${sale.buyerName} - ${sale.productName} (${sale.qty}kg)", color = Color.LightGray, fontSize = 10.sp)
                                                Text(currencyFormatter.format(sale.totalPrice), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // STANDARD PRODUCTS SCROLLING PORTION
                            Button(
                                onClick = { showSalesStats = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = jastipLightBlueBg),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = jastipBlueAccent, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Lihat Penjualan Real-time", color = jastipBlueAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 240.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(products) { prod ->
                                    val isPinned = prod.isPinned
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isPinned) jastipLightBlueBg else Color.White
                                        ),
                                        border = BorderStroke(
                                            width = if (isPinned) 1.5.dp else 1.dp,
                                            color = if (isPinned) jastipBlueAccent else Color(0xFFF1F5F9)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(Color(prod.photoColor)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = prod.emoji,
                                                        fontSize = 22.sp
                                                    )
                                                }

                                                Column {
                                                    Text(
                                                        text = prod.name,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = jastipNavy
                                                    )
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Text(
                                                            text = "${currencyFormatter.format(prod.price)}${prod.unit}",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = jastipBlueAccent
                                                        )
                                                        Text(
                                                            text = "Stok: ${prod.stock}kg",
                                                            fontSize = 10.sp,
                                                            color = jastipTextGray
                                                        )
                                                    }
                                                }
                                            }

                                            // Pin toggle action
                                            IconButton(
                                                onClick = {
                                                    val idx = products.indexOf(prod)
                                                    if (idx != -1) {
                                                        val finalState = !products[idx].isPinned
                                                        // Reset all pins
                                                        for (i in products.indices) {
                                                            products[i] = products[i].copy(isPinned = false)
                                                        }
                                                        products[idx] = products[idx].copy(isPinned = finalState)
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                                    contentDescription = "Pin product",
                                                    tint = if (isPinned) jastipBlueAccent else jastipTextGray,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // MODAL BOTTOM SHEET: SETTINGS & CONTROLS
            // ==========================================
            if (showSettingsSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSettingsSheet = false },
                    containerColor = Color.White,
                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = jastipBlueAccent, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Menu & Kontrol Studio Siaran",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = jastipNavy
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        Text(
                            text = "Aksi & Kontrol Cepat",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = jastipTextGray
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Mic Toggle Action Card
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isMicOn = !isMicOn },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isMicOn) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                ),
                                border = BorderStroke(1.dp, if (isMicOn) Color(0xFF81C784) else Color(0xFFEF5350)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                                        contentDescription = null,
                                        tint = if (isMicOn) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isMicOn) "Mikrofon Jelas" else "Bisu (Muted)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isMicOn) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = if (isMicOn) "Suara Aktif" else "Suara Mati",
                                        fontSize = 9.sp,
                                        color = jastipTextGray
                                    )
                                }
                            }

                            // Camera Toggle Action Card
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isCameraOn = !isCameraOn },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCameraOn) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                ),
                                border = BorderStroke(1.dp, if (isCameraOn) Color(0xFF81C784) else Color(0xFFEF5350)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                        contentDescription = null,
                                        tint = if (isCameraOn) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isCameraOn) "Kamera Aktif" else "Kamera Mati",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isCameraOn) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = if (isCameraOn) "Video Nyala" else "Video Mati",
                                        fontSize = 9.sp,
                                        color = jastipTextGray
                                    )
                                }
                            }

                            // Share / Bagikan Live Action Card
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        Toast.makeText(context, "Link siaran live jastip disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                colors = CardDefaults.cardColors(containerColor = jastipLightBlueBg),
                                border = BorderStroke(1.dp, Color(0xFFB3D9FF)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        tint = jastipBlueAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Bagikan Live",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = jastipBlueAccent,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Salin Tautan",
                                        fontSize = 9.sp,
                                        color = jastipTextGray
                                    )
                                }
                            }
                        }

                        // Edit location inline from active live stream
                        OutlinedTextField(
                            value = locationInput,
                            onValueChange = { locationInput = it },
                            label = { Text("Lokasi Siaran Aktif") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = jastipBlueAccent) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = jastipBlueAccent,
                                unfocusedBorderColor = Color(0xFFE4E9F0)
                            )
                        )

                        // quality changing
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Kualitas Siaran", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = jastipTextGray)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val qualities = listOf("1080p", "720p", "Otomatis")
                                qualities.forEach { q ->
                                    val isSel = streamQuality == q
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) jastipBlueAccent else Color.Transparent)
                                            .clickable { streamQuality = q }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = q,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isSel) Color.White else jastipNavy
                                        )
                                    }
                                }
                            }
                        }

                        // technical data matching original screen
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = jastipLightBlueBg),
                            border = BorderStroke(1.dp, Color(0xFFB3D9FF))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Wifi, contentDescription = null, tint = jastipBlueAccent, modifier = Modifier.size(16.dp))
                                        Text(text = "Kekuatan Koneksi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = jastipNavy)
                                    }
                                    Text(
                                        text = "Sangat Baik",
                                        color = jastipGreenAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { 0.85f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = jastipGreenAccent,
                                    trackColor = Color.White
                                )

                                Text(
                                    text = "Sangat Baik - 14.2 Mbps Unggah",
                                    fontSize = 10.sp,
                                    color = jastipTextGray
                                )

                                HorizontalDivider(color = Color(0xFFB3D9FF), thickness = 0.5.dp)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Bitrate Video", fontSize = 10.sp, color = jastipTextGray)
                                        Text(connectionBitrate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = jastipNavy)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Frame Terbuang", fontSize = 10.sp, color = jastipTextGray)
                                        Text("0.0%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { showSettingsSheet = false },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = jastipNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Tutup Pengaturan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ==========================================
            // EXIT CONFIRMATION DIALOG (ERROR PREVENTION)
            // ==========================================
            if (showExitConfirmation) {
                AlertDialog(
                    onDismissRequest = { showExitConfirmation = false },
                    title = { Text("Akhiri Sesi Live?", fontWeight = FontWeight.ExtraBold, color = jastipNavy) },
                    text = { Text("Apakah Anda yakin ingin mengakhiri live stream ini? Semua data penjualan jastip Anda akan disimpan secara otomatis.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showExitConfirmation = false
                                isLiveActive = false
                                Toast.makeText(context, "Streaming dihentikan. Semua data disimpan.", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Ya, Akhiri", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showExitConfirmation = false }
                        ) {
                            Text("Batal", color = jastipNavy, fontWeight = FontWeight.Bold)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = Color.White
                )
            }
        }
    }
}

// Internal Data Classes
data class LiveProduct(
    val id: String,
    val name: String,
    val price: Int,
    val unit: String = "/kg",
    val stock: Int,
    val isPinned: Boolean = false,
    val isLiveNow: Boolean = false,
    val emoji: String = "🐟",
    val photoColor: Long = 0xFFF1F5F9
)

data class LiveSale(
    val buyerName: String,
    val productName: String,
    val qty: Double,
    val totalPrice: Int
)

data class LiveMessage(
    val author: String,
    val text: String
)
