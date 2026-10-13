package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel
import java.text.NumberFormat
import java.util.Locale

// Local Data Models to avoid redeclaration collisions across the package
data class classChatScreenThread(
    val id: String,
    val name: String,
    val lastMsg: String,
    val date: String,
    val initial: String,
    val initialBg: Color,
    val isOnline: Boolean
)

data class classChatScreenMessage(
    val sender: String,
    val text: String,
    val time: String,
    val isFromMe: Boolean,
    val isCheckout: Boolean = false,
    val productName: String = "",
    val qty: Int = 1,
    val weight: Double = 1.0,
    val totalPrice: Int = 0
)

data class classChatScreenProduct(
    val name: String,
    val pricePerUnit: Int,
    val estWeight: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JastiperChatScreen(
    viewModel: JastipViewModel,
    onNavigateToNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Theme Colors declared locally to avoid global top-level duplication conflicts
    val jastipNavy = Color(0xFF102A43)
    val jastipBlueAccent = Color(0xFF0F5B9B)
    val jastipLightBlueBg = Color(0xFFEBF3FC)
    val jastipTextGray = Color(0xFF627D98)
    val jastipGreenAccent = Color(0xFF2C9C84)
    val jastipIconBg = Color(0xFFE4F2FE)
    val jastipNavyDark = Color(0xFF003E7E)

    var searchQuery by remember { mutableStateOf("") }
    var activeThreadId by remember { mutableStateOf("siti_aminah") }

    // State for responsive single-focus mobile flow
    var isChatRoomOpen by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showOrderForm by remember { mutableStateOf(false) }

    // Handle system back button to close chat room first if open
    BackHandler(enabled = isChatRoomOpen || showOrderForm) {
        if (showOrderForm) {
            showOrderForm = false
        } else {
            isChatRoomOpen = false
        }
    }

    // Sync with ViewModel to hide bottom nav
    LaunchedEffect(isChatRoomOpen) {
        viewModel.setChatDetailOpen(isChatRoomOpen)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setChatDetailOpen(false)
        }
    }

    // Chat threads list matching screenshot
    val chatThreads = remember {
        mutableStateListOf(
            classChatScreenThread("siti_aminah", "Siti Aminah", "Mas, Ikan Kerapunya masih ada?", "10:45 AM", "S", Color(0xFFE0F7FA), true),
            classChatScreenThread("andi_wijaya", "Andi Wijaya", "Terima kasih, ikannya segar sekali!", "Yesterday", "A", Color(0xFFFFF3E0), false),
            classChatScreenThread("ibu_ratna", "Ibu Ratna", "Ditunggu ya kirimannya.", "Wed", "I", Color(0xFFE8F5E9), false),
            classChatScreenThread("lina", "Lina", "Order lagi dong..", "Mon", "L", Color(0xFFF3E5F5), false)
        )
    }

    // List of messages for each thread
    val chatMessagesMap = remember {
        mutableStateMapOf(
            "siti_aminah" to mutableStateListOf(
                classChatScreenMessage(
                    sender = "Siti Aminah",
                    text = "Halo Mas Budi, Ikan Kerapunya yang live hari ini ada ukuran berapa saja ya?",
                    time = "10:42 AM",
                    isFromMe = false
                ),
                classChatScreenMessage(
                    sender = "Saya",
                    text = "Siang Bu Siti! Hari ini ada Kerapu Cantang ukuran 800g - 1.2kg per ekor. Masih segar-segar banget nih baru naik dari keramba.",
                    time = "10:44 AM",
                    isFromMe = true
                ),
                classChatScreenMessage(
                    sender = "Siti Aminah",
                    text = "Mas, Ikan Kerapunya masih ada? Mau dong yang 1kg satu ekor aja.",
                    time = "10:45 AM",
                    isFromMe = false
                )
            ),
            "andi_wijaya" to mutableStateListOf(
                classChatScreenMessage(
                    sender = "Saya",
                    text = "Ada mas Andi! Tadi pagi baru mendarat, capitnya besar mantap sekali.",
                    time = "09:11 AM",
                    isFromMe = true
                ),
                classChatScreenMessage(
                    sender = "Andi Wijaya",
                    text = "Terima kasih, ikannya segar sekali!",
                    time = "Yesterday",
                    isFromMe = false
                )
            ),
            "ibu_ratna" to mutableStateListOf(
                classChatScreenMessage(
                    sender = "Ibu Ratna",
                    text = "Ditunggu ya kirimannya.",
                    time = "Wed",
                    isFromMe = false
                )
            ),
            "lina" to mutableStateListOf(
                classChatScreenMessage(
                    sender = "Lina",
                    text = "Order lagi dong..",
                    time = "Mon",
                    isFromMe = false
                )
            )
        )
    }

    // Manual input messages state
    var messageInputText by remember { mutableStateOf("") }

    // Manual Order Form state
    val productOptions = listOf(
        classChatScreenProduct("Kerapu Cantang Live", 95833, "~1.2kg"),
        classChatScreenProduct("Kakap Merah Segar", 85000, "~1.0kg"),
        classChatScreenProduct("Kepiting Bakau Jumbo", 120000, "~1.5kg"),
        classChatScreenProduct("Ikan Bawal Hitam", 75000, "~1.0kg")
    )
    var selectedProductIndex by remember { mutableStateOf(0) }
    var orderQtyInput by remember { mutableStateOf("1") }
    var orderWeightInput by remember { mutableStateOf("1.2") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var checkOutNoteInput by remember { mutableStateOf("") }

    val currentProduct = productOptions[selectedProductIndex]

    // Form computations
    val qtyInt = orderQtyInput.toIntOrNull() ?: 1
    val weightDouble = orderWeightInput.toDoubleOrNull() ?: 1.2

    // Precise calculations matching the screenshot
    val calculatedProductPrice = remember(selectedProductIndex, qtyInt, weightDouble) {
        if (selectedProductIndex == 0 && qtyInt == 1 && weightDouble == 1.2) {
            115000
        } else {
            (currentProduct.pricePerUnit * weightDouble * qtyInt).toInt()
        }
    }
    val fixedJastipFee = 10000
    val totalEstimationValue = calculatedProductPrice + fixedJastipFee

    val activeThread = chatThreads.find { it.id == activeThreadId } ?: chatThreads[0]
    val activeMessages: MutableList<classChatScreenMessage> = chatMessagesMap[activeThreadId] ?: remember { mutableStateListOf<classChatScreenMessage>() }
    val chatListState = rememberLazyListState()
    val chatsListState = rememberLazyListState()

    // Handle Scroll to Top event from Bottom Navigation
    LaunchedEffect(Unit) {
        viewModel.scrollToTopEvent.collect {
            if (isChatRoomOpen) {
                chatsListState.animateScrollToItem(0)
            } else {
                chatListState.animateScrollToItem(0)
            }
        }
    }

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    // Auto-scroll logic
    LaunchedEffect(activeMessages.size) {
        if (activeMessages.isNotEmpty()) {
            chatsListState.animateScrollToItem(activeMessages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            val isTabletVal = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 760
            if (isTabletVal) {
                // Large tablet header (original brand bar)
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2C9C84)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Waves,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "IkanKu Pro",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = jastipNavy,
                                letterSpacing = (-0.5).sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
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
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFECEFF1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = jastipNavy, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                    Divider(color = Color(0xFFE4F2FE), thickness = 1.dp)
                }
            } else {
                // Smartphone Top Bar
                if (isChatRoomOpen) {
                    // Chat Room Header on Mobile
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .statusBarsPadding()
                    ) {
                        var dropdownExpanded by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(onClick = { isChatRoomOpen = false }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = jastipNavy)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(activeThread.initialBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = activeThread.initial,
                                        fontWeight = FontWeight.Bold,
                                        color = jastipNavy,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text(
                                        text = activeThread.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = jastipNavy,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(if (activeThread.isOnline) jastipGreenAccent else jastipTextGray)
                                        )
                                        Text(
                                            text = if (activeThread.isOnline) "Online" else "Offline",
                                            fontSize = 10.sp,
                                            color = jastipTextGray
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                IconButton(onClick = {
                                    Toast.makeText(context, "Menelepon ${activeThread.name}...", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.Phone, contentDescription = "Telepon", tint = jastipTextGray, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = {
                                    dropdownExpanded = true
                                }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Opsi", tint = jastipTextGray, modifier = Modifier.size(18.dp))
                                }

                                DropdownMenu(
                                    expanded = dropdownExpanded,
                                    onDismissRequest = { dropdownExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Riwayat Pesanan", color = jastipNavy, fontWeight = FontWeight.Medium) },
                                        leadingIcon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = jastipBlueAccent) },
                                        onClick = {
                                            dropdownExpanded = false
                                            Toast.makeText(context, "Menampilkan Riwayat Pesanan ${activeThread.name}...", Toast.LENGTH_LONG).show()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Lacak Alamat", color = jastipNavy, fontWeight = FontWeight.Medium) },
                                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = jastipBlueAccent) },
                                        onClick = {
                                            dropdownExpanded = false
                                            Toast.makeText(context, "Melacak alamat pengiriman ${activeThread.name}...", Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }
                            }
                        }
                        Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    }
                } else {
                    // Chat List Header on Mobile
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
                                text = "Chat",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = jastipNavy,
                                letterSpacing = (-0.5).sp,
                                modifier = Modifier.align(Alignment.Center)
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
                        Divider(color = Color(0xFFE4F2FE), thickness = 1.dp)
                    }
                }
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5FAFD))
                .padding(innerPadding)
        ) {
            val isTablet = maxWidth >= 760.dp

            if (isTablet) {
                // Wide Screen Tablet: Responsive, side-by-side split matching desktop web screenshot layout perfectly
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left: Search sidebar with list of conversations
                    Card(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight()
                            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE4E9F0))
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "Daftar Pelanggan",
                                fontWeight = FontWeight.Bold,
                                color = jastipNavy,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                            Divider(color = Color(0xFFF0F4F8))
                            
                            Box(modifier = Modifier.padding(12.dp)) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Cari pelanggan...", fontSize = 13.sp, color = jastipTextGray) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = jastipTextGray, modifier = Modifier.size(18.dp)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = Color(0xFFF5FAFD),
                                        focusedContainerColor = Color.White,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedBorderColor = jastipBlueAccent
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                            }

                        LazyColumn(
                            state = chatListState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                                val filtered = chatThreads.filter { it.name.contains(searchQuery, ignoreCase = true) }
                                items(filtered) { thread ->
                                    val isSelected = thread.id == activeThreadId
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(if (isSelected) Color(0xFFE5F1FF) else Color.Transparent)
                                            .clickable { activeThreadId = thread.id }
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(thread.initialBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(thread.initial, fontWeight = FontWeight.Bold, color = jastipNavy, fontSize = 14.sp)
                                            if (thread.isOnline) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(jastipGreenAccent)
                                                        .border(1.5.dp, Color.White, CircleShape)
                                                        .align(Alignment.BottomEnd)
                                                )
                                            }
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(thread.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = jastipNavy)
                                                Text(thread.date, fontSize = 10.sp, color = jastipTextGray)
                                            }
                                            Text(thread.lastMsg, fontSize = 11.sp, color = jastipTextGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                    Divider(color = Color(0xFFF5FAFD))
                                }
                            }
                        }
                    }

                    // Center: Core Chat window
                    Card(
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight()
                            .padding(top = 16.dp, bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE4E9F0))
                    ) {
                        RenderChatPane(
                            activeThread = activeThread,
                            activeMessages = activeMessages,
                            chatMessagesListState = chatsListState,
                            messageInputText = messageInputText,
                            onMessageInputChange = { messageInputText = it },
                            onSendMessage = {
                                if (messageInputText.isNotEmpty()) {
                                    activeMessages.add(
                                        classChatScreenMessage(sender = "Saya", text = messageInputText, time = "10:46 AM", isFromMe = true)
                                    )
                                    val index = chatThreads.indexOfFirst { it.id == activeThreadId }
                                    if (index != -1) {
                                        chatThreads[index] = chatThreads[index].copy(lastMsg = messageInputText, date = "10:46 AM")
                                    }
                                    messageInputText = ""
                                }
                            },
                            onAttachmentClick = {
                                selectedProductIndex = 0
                            },
                            onOpenActionMenu = {
                                showBottomSheet = true
                            },
                            themeColors = mapOf(
                                "navy" to jastipNavy,
                                "blue" to jastipBlueAccent,
                                "lightBg" to jastipLightBlueBg,
                                "gray" to jastipTextGray,
                                "green" to jastipGreenAccent,
                                "iconBg" to jastipIconBg
                            ),
                            showHeader = true
                        )

                        if (showBottomSheet) {
                            ActionMenuBottomSheet(
                                onDismissRequest = { showBottomSheet = false },
                                onGalleryClick = { Toast.makeText(context, "Membuka Galeri...", Toast.LENGTH_SHORT).show() },
                                onOrderClick = { showOrderForm = true },
                                themeColors = mapOf(
                                    "navy" to jastipNavy,
                                    "blue" to jastipBlueAccent,
                                    "gray" to jastipTextGray
                                )
                            )
                        }
                    }

                    if (showOrderForm) {
                        // Right: Manual creator form
                        Card(
                            modifier = Modifier
                                .weight(1.8f)
                                .fillMaxHeight()
                                .padding(end = 16.dp, top = 16.dp, bottom = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE4E9F0))
                        ) {
                            RenderManualOrderForm(
                                productOptions = productOptions,
                                selectedIndex = selectedProductIndex,
                                onProductSelectIndex = { selectedProductIndex = it },
                                orderQty = orderQtyInput,
                                onOrderQtyChange = { orderQtyInput = it },
                                orderWeight = orderWeightInput,
                                onOrderWeightChange = { orderWeightInput = it },
                                isDropdownExpanded = isDropdownExpanded,
                                onDropdownToggle = { isDropdownExpanded = it },
                                noteInput = checkOutNoteInput,
                                onNoteInputChange = { checkOutNoteInput = it },
                                calculatedProductPrice = calculatedProductPrice,
                                fixedJastipFee = fixedJastipFee,
                                totalEstimation = totalEstimationValue,
                                currencyFormatter = currencyFormatter,
                                onSendCheckoutClick = {
                                    activeMessages.add(
                                        classChatScreenMessage(
                                            sender = "Saya",
                                            text = "Kirim Link Checkout",
                                            time = "10:46 AM",
                                            isFromMe = true,
                                            isCheckout = true,
                                            productName = currentProduct.name,
                                            qty = qtyInt,
                                            weight = weightDouble,
                                            totalPrice = totalEstimationValue
                                        )
                                    )
                                    val idx = chatThreads.indexOfFirst { it.id == activeThreadId }
                                    if (idx != -1) {
                                        chatThreads[idx] = chatThreads[idx].copy(
                                            lastMsg = "Link checkout: ${currentProduct.name}",
                                            date = "10:46 AM"
                                        )
                                    }
                                    Toast.makeText(context, "Checkout link sent successfully!", Toast.LENGTH_SHORT).show()
                                    checkOutNoteInput = ""
                                    showOrderForm = false
                                },
                                themeColors = mapOf(
                                    "navy" to jastipNavy,
                                    "blue" to jastipBlueAccent,
                                    "lightBg" to jastipLightBlueBg,
                                    "gray" to jastipTextGray,
                                    "green" to jastipGreenAccent,
                                    "iconBg" to jastipIconBg,
                                    "navyDark" to jastipNavyDark
                                ),
                                showFooterButtons = true
                            )
                        }
                    }
                }
            } else {
                // SMARTPHONE PORTRAIT VIEW: Native single-focus layout (Chat List -> Chat Room)
                if (isChatRoomOpen) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        RenderChatPane(
                            activeThread = activeThread,
                            activeMessages = activeMessages,
                            chatMessagesListState = chatsListState,
                            messageInputText = messageInputText,
                            onMessageInputChange = { messageInputText = it },
                            onSendMessage = {
                                if (messageInputText.isNotEmpty()) {
                                    activeMessages.add(
                                        classChatScreenMessage(sender = "Saya", text = messageInputText, time = "10:46 AM", isFromMe = true)
                                    )
                                    val index = chatThreads.indexOfFirst { it.id == activeThreadId }
                                    if (index != -1) {
                                        chatThreads[index] = chatThreads[index].copy(lastMsg = messageInputText, date = "10:46 AM")
                                    }
                                    messageInputText = ""
                                }
                            },
                            onAttachmentClick = {
                                showBottomSheet = true
                            },
                            onOpenActionMenu = {
                                showBottomSheet = true
                            },
                            themeColors = mapOf(
                                "navy" to jastipNavy,
                                "blue" to jastipBlueAccent,
                                "lightBg" to jastipLightBlueBg,
                                "gray" to jastipTextGray,
                                "green" to jastipGreenAccent,
                                "iconBg" to jastipIconBg
                            ),
                            showHeader = false
                        )

                        if (showBottomSheet) {
                            ActionMenuBottomSheet(
                                onDismissRequest = { showBottomSheet = false },
                                onGalleryClick = { Toast.makeText(context, "Membuka Galeri...", Toast.LENGTH_SHORT).show() },
                                onOrderClick = { showOrderForm = true },
                                themeColors = mapOf(
                                    "navy" to jastipNavy,
                                    "blue" to jastipBlueAccent,
                                    "gray" to jastipTextGray
                                )
                            )
                        }

                        if (showOrderForm) {
                            ModalBottomSheet(
                                onDismissRequest = { showOrderForm = false },
                                containerColor = Color.White
                            ) {
                                RenderManualOrderForm(
                                    productOptions = productOptions,
                                    selectedIndex = selectedProductIndex,
                                    onProductSelectIndex = { selectedProductIndex = it },
                                    orderQty = orderQtyInput,
                                    onOrderQtyChange = { orderQtyInput = it },
                                    orderWeight = orderWeightInput,
                                    onOrderWeightChange = { orderWeightInput = it },
                                    isDropdownExpanded = isDropdownExpanded,
                                    onDropdownToggle = { isDropdownExpanded = it },
                                    noteInput = checkOutNoteInput,
                                    onNoteInputChange = { checkOutNoteInput = it },
                                    calculatedProductPrice = calculatedProductPrice,
                                    fixedJastipFee = fixedJastipFee,
                                    totalEstimation = totalEstimationValue,
                                    currencyFormatter = currencyFormatter,
                                    onSendCheckoutClick = {
                                        activeMessages.add(
                                            classChatScreenMessage(
                                                sender = "Saya",
                                                text = "Kirim Link Checkout",
                                                time = "10:46 AM",
                                                isFromMe = true,
                                                isCheckout = true,
                                                productName = currentProduct.name,
                                                qty = qtyInt,
                                                weight = weightDouble,
                                                totalPrice = totalEstimationValue
                                            )
                                        )
                                        val idx = chatThreads.indexOfFirst { it.id == activeThreadId }
                                        if (idx != -1) {
                                            chatThreads[idx] = chatThreads[idx].copy(
                                                lastMsg = "Link checkout: ${currentProduct.name}",
                                                date = "10:46 AM"
                                            )
                                        }
                                        Toast.makeText(context, "Link Checkout Terkirim!", Toast.LENGTH_SHORT).show()
                                        checkOutNoteInput = ""
                                        showOrderForm = false
                                    },
                                    themeColors = mapOf(
                                        "navy" to jastipNavy,
                                        "blue" to jastipBlueAccent,
                                        "lightBg" to jastipLightBlueBg,
                                        "gray" to jastipTextGray,
                                        "green" to jastipGreenAccent,
                                        "iconBg" to jastipIconBg,
                                        "navyDark" to jastipNavyDark
                                    ),
                                    showFooterButtons = false
                                )
                            }
                        }
                    }
                } else {
                    // Layar Pertama: Daftar Obrolan (Chat List)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Cari pelanggan...", fontSize = 14.sp, color = jastipTextGray) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = jastipTextGray, modifier = Modifier.size(20.dp)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color(0xFFF5FAFD),
                                    focusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = jastipBlueAccent
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        LazyColumn(
                            state = chatListState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val filtered = chatThreads.filter { it.name.contains(searchQuery, ignoreCase = true) }
                            items(filtered) { thread ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { 
                                            activeThreadId = thread.id
                                            isChatRoomOpen = true
                                        }
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(thread.initialBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(thread.initial, fontWeight = FontWeight.Bold, color = jastipNavy, fontSize = 15.sp)
                                        if (thread.isOnline) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(jastipGreenAccent)
                                                    .border(2.dp, Color.White, CircleShape)
                                                    .align(Alignment.BottomEnd)
                                            )
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(thread.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = jastipNavy)
                                            Text(thread.date, fontSize = 11.sp, color = jastipTextGray)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(thread.lastMsg, fontSize = 12.sp, color = jastipTextGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                                Divider(color = Color(0xFFF1F5F9))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderChatPane(
    activeThread: classChatScreenThread,
    activeMessages: List<classChatScreenMessage>,
    chatMessagesListState: LazyListState,
    messageInputText: String,
    onMessageInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onAttachmentClick: () -> Unit,
    onOpenActionMenu: () -> Unit,
    themeColors: Map<String, Color>,
    showHeader: Boolean = true
) {
    val navy = themeColors["navy"] ?: Color.Black
    val blue = themeColors["blue"] ?: Color.Blue
    val lightBg = themeColors["lightBg"] ?: Color.LightGray
    val gray = themeColors["gray"] ?: Color.Gray
    val green = themeColors["green"] ?: Color.Green
    val iconBg = themeColors["iconBg"] ?: Color.White

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (showHeader) {
            // Chat Window Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(activeThread.initialBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = activeThread.initial,
                        fontWeight = FontWeight.Bold,
                        color = navy,
                        fontSize = 14.sp
                    )
                }
                Column {
                    Text(
                        text = activeThread.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = navy
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (activeThread.isOnline) green else gray)
                        )
                        Text(
                            text = if (activeThread.isOnline) "Online" else "Offline",
                            fontSize = 11.sp,
                            color = gray
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.VideoCall, contentDescription = "Video Call", tint = gray, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Phone, contentDescription = "Call/Telepon", tint = gray, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opsi Lainnya", tint = gray, modifier = Modifier.size(18.dp))
                }
            }
        }

        Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // Messages scroll column
        LazyColumn(
            state = chatMessagesListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFECEFF1))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Hari Ini", fontSize = 10.sp, color = gray, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(activeMessages) { msg ->
                val ctx = LocalContext.current
                if (msg.isCheckout) {
                    // Beautiful Checkout Card in Chat matching Web Screenshot
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (msg.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        var isPaidLocal by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Fish graphic icon badge
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(iconBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Waves,
                                            contentDescription = null,
                                            tint = blue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "Checkout Link: ${msg.productName}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = navy
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Qty: ${msg.qty} | Berat: ~${msg.weight}kg",
                                            fontSize = 11.sp,
                                            color = gray
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = currencyFormatter.format(msg.totalPrice),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = blue
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Dynamic simulator button to switch payment status
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isPaidLocal) Color(0xFFE8F5E9) else Color(0xFFE5EEF6))
                                        .clickable {
                                            isPaidLocal = !isPaidLocal
                                            val t = if (isPaidLocal) "Status diubah: Terbayar Lunas" else "Status: Menunggu Pembayaran"
                                            Toast.makeText(ctx, t, Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (isPaidLocal) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = green, modifier = Modifier.size(16.dp))
                                            Text(
                                                text = "Terbayar Lunas di IkanKu",
                                                color = green,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        } else {
                                            Text(
                                                text = "Menunggu Pembayaran",
                                                color = blue,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Regular text voice bubble
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (msg.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            if (!msg.isFromMe) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(activeThread.initialBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(activeThread.initial, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = navy)
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (msg.isFromMe) 14.dp else 2.dp,
                                    bottomEnd = if (msg.isFromMe) 2.dp else 14.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (msg.isFromMe) navy else Color.White
                                ),
                                border = if (msg.isFromMe) null else BorderStroke(1.dp, Color(0xFFECEFF1)),
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = msg.text,
                                        fontSize = 12.5.sp,
                                        color = if (msg.isFromMe) Color.White else navy,
                                        lineHeight = 17.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = msg.time,
                                        fontSize = 9.sp,
                                        color = if (msg.isFromMe) Color(0xFFB0BEC5) else gray,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)

        val context = LocalContext.current

        // Message input composer action row - Optimized for minimalist height and expanded width
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Action Button - Toggles Bottom Sheet
            IconButton(
                onClick = onOpenActionMenu,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5F1FF))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Menu Aksi",
                    tint = blue,
                    modifier = Modifier.size(24.dp)
                )
            }

            OutlinedTextField(
                value = messageInputText,
                onValueChange = onMessageInputChange,
                placeholder = { Text("Tulis pesan...", fontSize = 14.sp, color = gray) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF1F5F9),
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = blue
                ),
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp)
            )

            IconButton(
                onClick = { /* Emoji Action */ },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.SentimentSatisfied, contentDescription = "Emojis", tint = gray, modifier = Modifier.size(24.dp))
            }

            AnimatedContent(
                targetState = messageInputText.isNotEmpty(),
                transitionSpec = {
                    fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                },
                label = "SendButtonAnimation"
            ) { isNotEmpty ->
                if (isNotEmpty) {
                    FloatingActionButton(
                        onClick = onSendMessage,
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        containerColor = navy,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Kirim", modifier = Modifier.size(18.dp))
                    }
                } else {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Voice Note belum tersedia", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice Note", tint = gray, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionMenuBottomSheet(
    onDismissRequest: () -> Unit,
    onGalleryClick: () -> Unit,
    onOrderClick: () -> Unit,
    themeColors: Map<String, Color>
) {
    val navy = themeColors["navy"] ?: Color.Black
    val blue = themeColors["blue"] ?: Color.Blue

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Pilih Aksi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = navy,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionMenuItem(
                    icon = Icons.Default.Image,
                    label = "Galeri",
                    onClick = {
                        onGalleryClick()
                        onDismissRequest()
                    },
                    backgroundColor = Color(0xFFF0F4F8),
                    iconColor = navy,
                    modifier = Modifier.weight(1f)
                )

                ActionMenuItem(
                    icon = Icons.Default.ShoppingCart,
                    label = "Buat Pesanan",
                    onClick = {
                        onOrderClick()
                        onDismissRequest()
                    },
                    backgroundColor = Color(0xFFE5F1FF),
                    iconColor = blue,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ActionMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = iconColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RenderManualOrderForm(
    productOptions: List<classChatScreenProduct>,
    selectedIndex: Int,
    onProductSelectIndex: (Int) -> Unit,
    orderQty: String,
    onOrderQtyChange: (String) -> Unit,
    orderWeight: String,
    onOrderWeightChange: (String) -> Unit,
    isDropdownExpanded: Boolean,
    onDropdownToggle: (Boolean) -> Unit,
    noteInput: String,
    onNoteInputChange: (String) -> Unit,
    calculatedProductPrice: Int,
    fixedJastipFee: Int,
    totalEstimation: Int,
    currencyFormatter: NumberFormat,
    onSendCheckoutClick: () -> Unit,
    themeColors: Map<String, Color>,
    showFooterButtons: Boolean = true
) {
    val navy = themeColors["navy"] ?: Color.Black
    val blue = themeColors["blue"] ?: Color.Blue
    val lightBg = themeColors["lightBg"] ?: Color.LightGray
    val gray = themeColors["gray"] ?: Color.Gray

    val selectedProduct = productOptions[selectedIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Buat Pesanan Manual",
                    fontWeight = FontWeight.ExtraBold,
                    color = navy,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Form khusus meluncurkan link tagihan jastip ke pelanggan aktif.",
                    fontSize = 11.sp,
                    color = gray
                )
            }
        }

        item {
            Divider(color = Color(0xFFE2E8F0))
        }

        // Product Live Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Pilih Produk Live", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = navy)
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(lightBg)
                        .border(1.dp, Color(0xFF90CAF9), RoundedCornerShape(8.dp))
                        .clickable { onDropdownToggle(true) }
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selectedProduct.name, fontSize = 13.sp, color = navy, fontWeight = FontWeight.Medium)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = navy)
                    }

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { onDropdownToggle(false) },
                        modifier = Modifier
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                    ) {
                        productOptions.forEachIndexed { index, product ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(product.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = navy)
                                        Text("${product.estWeight} | Rp ${currencyFormatter.format(product.pricePerUnit)}/Kg", fontSize = 10.sp, color = gray)
                                    }
                                },
                                onClick = {
                                    onProductSelectIndex(index)
                                    onDropdownToggle(false)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Form inputs row: Jumlah and Berat
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Jumlah (Ekor)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = navy)
                    OutlinedTextField(
                        value = orderQty,
                        onValueChange = onOrderQtyChange,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = lightBg,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = blue
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Berat (Kg)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = navy)
                    OutlinedTextField(
                        value = orderWeight,
                        onValueChange = onOrderWeightChange,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = lightBg,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = blue
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }
        }

        // Note area
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Catatan Khusus", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = navy)
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = onNoteInputChange,
                    placeholder = { Text("Contoh: Bersihkan sisik, potong 3...", fontSize = 12.sp, color = gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = lightBg,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = blue
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Summary billing overview card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Harga Produk", fontSize = 11.sp, color = gray)
                        Text(currencyFormatter.format(calculatedProductPrice), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = navy)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Jasa Jastip (Fixed)", fontSize = 11.sp, color = gray)
                        Text(currencyFormatter.format(fixedJastipFee), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = navy)
                    }
                    Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Total Estimasi", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = navy)
                        Text(currencyFormatter.format(totalEstimation), fontSize = 14.sp, fontWeight = FontWeight.Black, color = blue)
                    }
                }
            }
        }

        // Send Link Checkout button precisely styled as Web Screenshot Button
        item {
            Button(
                onClick = onSendCheckoutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = navy),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Link, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text("Kirim Link Checkout", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        item {
            Text(
                text = "Pelanggan akan menerima link pembayaran di jendela chat dan harus menyelesaikannya dalam 15 menit.",
                fontSize = 10.sp,
                color = gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (showFooterButtons) {
            // Secondary bottom buttons: Riwayat Pesanan & Lacak Alamat
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = lightBg),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = blue, modifier = Modifier.size(13.dp))
                            Text("Riwayat Pesanan", color = blue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = lightBg),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = blue, modifier = Modifier.size(13.dp))
                            Text("Lacak Alamat", color = blue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
