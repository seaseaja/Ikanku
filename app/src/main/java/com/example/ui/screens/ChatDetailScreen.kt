package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.data.OrderStatus
import com.example.viewmodel.JastipViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    orderId: String,
    viewModel: JastipViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToOrderDetail: (String) -> Unit,
    onNavigateToPaymentSuccess: (String, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.allOrders.collectAsState()
    val mitras by viewModel.allMitras.collectAsState()
    
    val order = orders.find { it.id == orderId }
    val partner = if (order == null) mitras.find { it.id == orderId } else null

    val context = LocalContext.current
    val chatMessages by viewModel.activeChatMessages.collectAsState()
    var textMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Bind this screen to active chat
    LaunchedEffect(orderId) {
        viewModel.selectActiveChat(orderId)
    }

    DisposableEffect(orderId) {
        onDispose {
            viewModel.selectActiveChat(null)
        }
    }

    // Auto scroll to bottom when new messages stream in
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    val timeFormatter = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (order != null || partner != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFBCEDFF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (order?.customerName ?: "S").take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF006684),
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    order?.jastiperName ?: partner?.name ?: "Chat Room",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    order?.marketName ?: partner?.market ?: "IkanKu Jastip",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        Text("Chat Room")
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigateBack()
                        },
                        modifier = Modifier.testTag("btn_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Percakapan"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (order == null && partner == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Gagal memuat detail chat room!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5FAFD))
                ) {
                    // Modern Header Card explaining the Jastip details
                    if (order != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFCFE6F1).copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Rencana Jastip: ${order.fishType}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF001F2A),
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Beban: ${order.weightKg} kg  •  Ongkir Jastip: Rp %,.0f"
                                            .format(Locale("id", "ID"), order.jastipFee),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                                
                                val (statusText, statusBg, statusColor) = when (order.status) {
                                    "PENDING" -> Triple("Menunggu", Color(0xFFFFF3E0), Color(0xFFE65100))
                                    "ACCEPTED" -> Triple("Disetujui", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                                    "BUYING" -> Triple("Proses Belanja", Color(0xFFE1F5FE), Color(0xFF0288D1))
                                    "DELIVERING" -> Triple("Sedang Dikirim", Color(0xFFEDE7F6), Color(0xFF5E35B1))
                                    "COMPLETED" -> Triple("Selesai & Lunas", Color(0xFFECEFF1), Color(0xFF37474F))
                                    else -> Triple(order.status, Color(0xFFF5F5F5), Color.DarkGray)
                                }
                                
                                Surface(
                                    color = statusBg,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = statusText,
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Info Placeholder for new chat
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Mulai obrolan baru dengan ${partner?.name}. Anda dapat bertanya tentang ketersediaan ikan atau memesan langsung.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF01579B),
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Chats listing pane
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chatMessages, key = { it.id }) { message ->
                            ChatBubbleRow(
                                message = message, 
                                timeFormatter = timeFormatter,
                                onCheckoutClicked = {
                                    // Update order status live
                                    order?.let { ord ->
                                        viewModel.updateOrderStatus(ord.id, OrderStatus.COMPLETED)
                                        onNavigateToPaymentSuccess(ord.id, ord.budgetEst + ord.jastipFee, ord.id)
                                    }
                                }
                            )
                        }
                    }

                    // Quick Communication Chips - CUSTOMER FACING PRESETS
                    QuickChipsSection(
                        onChipClicked = { textMessage = it }
                    )

                    // Bottom Input Row - Optimized for space and conditional actions
                    Surface(
                        color = Color.White,
                        tonalElevation = 2.dp,
                        border = BorderStroke(1.dp, Color(0xFFCFE6F1).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Unified Attachment Button
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Membuka Galeri...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("btn_photo_upload")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tambah Lampiran",
                                    tint = Color(0xFF006684),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Message prompt writing box - maximized width
                            OutlinedTextField(
                                value = textMessage,
                                onValueChange = { textMessage = it },
                                placeholder = { Text("Tulis pesan...", fontSize = 14.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 40.dp)
                                    .testTag("chat_input_field"),
                                maxLines = 4,
                                textStyle = TextStyle(fontSize = 14.sp),
                                shape = RoundedCornerShape(20.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF006684),
                                    unfocusedBorderColor = Color(0xFFCFE6F1),
                                    focusedContainerColor = Color(0xFFF8FAFC),
                                    unfocusedContainerColor = Color(0xFFF8FAFC)
                                )
                            )

                            // Emoji button outside as requested
                            IconButton(
                                onClick = { /* Emoji picker */ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SentimentSatisfied,
                                    contentDescription = "Emoji",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Conditional Send/Mic button
                            AnimatedContent(
                                targetState = textMessage.trim().isNotEmpty(),
                                transitionSpec = {
                                    fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                                },
                                label = "SendButtonAnimation"
                            ) { isNotEmpty ->
                                if (isNotEmpty) {
                                    IconButton(
                                        onClick = {
                                            if (textMessage.trim().isNotEmpty()) {
                                                viewModel.sendChatMessage(orderId, textMessage)
                                                textMessage = ""
                                            }
                                        },
                                        modifier = Modifier
                                            .background(Color(0xFF006684), CircleShape)
                                            .size(36.dp)
                                            .testTag("btn_send_chat"),
                                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Kirim pesan",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            Toast.makeText(context, "Fitur suara belum tersedia", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = "Pesan Suara",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(22.dp)
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

    // Success Payment Dialog
    // Removed and replaced with full screen navigation
}

@Composable
fun ChatBubbleRow(
    message: ChatMessage,
    timeFormatter: SimpleDateFormat,
    onCheckoutClicked: () -> Unit
) {
    val isSystem = message.text.startsWith("[INFO]")
    val isOrder = message.text.startsWith("[ORDER]")

    if (isSystem) {
        // Centered system notification pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = Color(0xFFE2F3F9),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Detail pesan sistem",
                        tint = Color(0xFF006684),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = message.text.removePrefix("[INFO] "),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF001F2A),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    } else if (isOrder) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .testTag("order_proposal_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, Color(0xFF006684)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚓", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "IKANKU INSTANT CHECKOUT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF006684)
                            )
                        }
                        Surface(
                            color = Color(0xFFFFF8E1),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFCC80))
                        ) {
                            Text(
                                "Menunggu Pembayaran",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFCFE6F1))
                    Spacer(modifier = Modifier.height(10.dp))

                    val cleanText = message.text
                        .removePrefix("[ORDER]")
                        .replace("⚓ PENAWARAN JASTIP BARU:\n", "")
                        .trim()

                    Text(
                        text = cleanText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF001F2A),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onCheckoutClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_pay_checkout"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Buka Link Bayar",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Bayar Jastip Sekarang (Checkout)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Standard user or courier bubble
        val isMe = message.isFromMe
        val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
        val bubbleBag = if (isMe) Color(0xFF006684) else Color.White
        val textColor = if (isMe) Color.White else Color(0xFF001F2A)
        val strokeOption = if (isMe) BorderStroke(0.dp, Color.Transparent) else BorderStroke(1.dp, Color(0xFFCFE6F1).copy(alpha = 0.6f))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = alignment
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                if (!isMe) {
                    // Profile Circle for the client
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(28.dp)
                            .background(Color(0xFFE3F2FD), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message.senderName.take(1).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF006684)
                        )
                    }
                }

                Column(
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    // Actual speech bubble content
                    Surface(
                        color = bubbleBag,
                        border = strokeOption,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        ),
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Small timestamp
                    Text(
                        text = timeFormatter.format(Date(message.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickChipsSection(
    onChipClicked: (String) -> Unit
) {
    val quickAnswers = listOf(
        "Pak, pilihkan ikan yang insangnya segar merah ya! 🙏",
        "Tolong dibersihkan sisik dan isi perutnya sekalian ya pak.",
        "Minta tolong dipotong fillet steak tebal ya pak.",
        "Bisa tolong difotokan penampakan ikannya pas di lapak pak?",
        "Minta link pembayaran checkoutnya ya pak, biar langsung lunas."
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Hubungi Jastiper Lebih Cepat:",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006684),
                fontSize = 10.sp
            )
            androidx.compose.foundation.lazy.LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickAnswers) { answer ->
                    Surface(
                        color = Color(0xFFBCEDFF).copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, Color(0xFF006684).copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clickable { onChipClicked(answer) }
                            .testTag("chip_preset_${answer.take(10)}")
                    ) {
                        Text(
                            text = answer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            color = Color(0xFF001F2A)
                        )
                    }
                }
            }
        }
    }
}
