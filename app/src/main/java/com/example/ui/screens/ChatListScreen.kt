package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JastipOrder
import com.example.viewmodel.JastipViewModel

import com.example.ui.components.GlobalHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: JastipViewModel,
    onNavigateToChat: (String) -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val allOrders by viewModel.allOrders.collectAsState()
    val allMessages by viewModel.allMessages.collectAsState()
    val allMitras by viewModel.allMitras.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Handle Scroll to Top event from Bottom Navigation
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.scrollToTopEvent.collect {
            listState.animateScrollToItem(0)
        }
    }
    
    // Group orders and messages by jastiper to ensure 1 room per jastiper
    val allChatThreads = androidx.compose.runtime.remember(allOrders, allMessages, allMitras) {
        // 1. Identify all unique Order IDs from messages
        val messageOrderIds = allMessages.map { it.orderId }.distinct()
        
        // Create a map of jastiperName to their most representative "Order" object
        val threads = mutableMapOf<String, JastipOrder>()
        
        // Process Orders
        allOrders.forEach { order ->
            val existing = threads[order.jastiperName]
            if (existing == null || order.timestamp > existing.timestamp) {
                threads[order.jastiperName] = order
            }
        }
        
        // Process Chat-only (messages with mitraId that have no order yet)
        messageOrderIds.forEach { id ->
            val mitra = allMitras.find { it.id == id }
            if (mitra != null && !threads.containsKey(mitra.name)) {
                // Find latest message for this mitra to get timestamp
                val latestMsgTime = allMessages.filter { it.orderId == id }.maxOfOrNull { it.timestamp } ?: System.currentTimeMillis()
                
                threads[mitra.name] = JastipOrder(
                    id = mitra.id,
                    customerName = "Saya",
                    jastiperName = mitra.name,
                    fishType = "Tanya Stok / Jastip",
                    weightKg = 0.0,
                    marketName = mitra.market,
                    budgetEst = 0.0,
                    jastipFee = 0.0,
                    deliveryAddress = "",
                    status = "CHAT",
                    timestamp = latestMsgTime
                )
            }
        }
        
        threads.values.sortedByDescending { it.timestamp }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GlobalHeader(
                title = "Chat",
                onNavigateToNotifications = onNavigateToNotifications
            )
        }
    ) { innerPadding ->
        if (isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = innerPadding.calculateTopPadding())
                    .height(2.dp),
                color = Color(0xFF006684),
                trackColor = Color.Transparent
            )
        }
        if (allChatThreads.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💬", fontSize = 36.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Belum Ada Obrolan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mulai obrolan baru dengan memilih Mitra Jastip terbaik pilihan Anda di tab Jastip Ikan.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .testTag("chat_list"),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp) // Segmented row dividers
            ) {
                items(allChatThreads, key = { "chat_${it.id}" }) { order ->
                    ChatThreadItem(
                        order = order,
                        onClick = { onNavigateToChat(order.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatThreadItem(
    order: JastipOrder,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("chat_thread_${order.id}"),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Initials block
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = order.jastiperName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                // Small green active badge indicator for live chats
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF2E7D32), CircleShape) // Live Active indicator
                        .align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Body
            val timeString = androidx.compose.runtime.remember(order.timestamp) {
                try {
                    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                    sdf.format(java.util.Date(order.timestamp))
                } catch (e: Exception) {
                    "10:45"
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.jastiperName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    val (statusLabel, badgeColor, textColor) = when (order.status) {
                        "PENDING" -> Triple("Menunggu", Color(0xFFFFEBE3), Color(0xFFC22F00)) // High contrast safe orange/rusty-red
                        "ACCEPTED" -> Triple("Disetujui", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                        "BUYING" -> Triple("Belanja", Color(0xFFE1F5FE), Color(0xFF0288D1))
                        "DELIVERING" -> Triple("Dikirim", Color(0xFFEDE7F6), Color(0xFF5E35B1))
                        "COMPLETED" -> Triple("Selesai", Color(0xFFECEFF1), Color(0xFF37474F))
                        "CHAT" -> Triple("Tanya Jastip", Color(0xFFBCEDFF), Color(0xFF006684))
                        else -> Triple(order.status, Color(0xFFF5F5F5), Color.DarkGray)
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = timeString,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Surface(
                            color = badgeColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = statusLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Item: ${order.fishType} (${order.weightKg} kg)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Buka Chat",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
