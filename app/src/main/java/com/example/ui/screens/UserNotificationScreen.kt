package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserNotificationScreen(
    viewModel: JastipViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToOrderDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notifications by viewModel.allNotifications.collectAsState()

    val categories = listOf("Semua", "Pesanan", "Promo", "Info")
    var selectedCategory by remember { mutableStateOf("Semua") }

    val filteredNotifications = remember(selectedCategory, notifications) {
        if (selectedCategory == "Semua") {
            notifications
        } else {
            notifications.filter { notif ->
                when (selectedCategory) {
                    "Pesanan" -> notif.category == "PESANAN"
                    "Promo" -> notif.category == "PROMO"
                    "Info" -> notif.category == "INFO"
                    else -> true
                }
            }
        }
    }

    val unreadCount = notifications.count { it.isUnread }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Notifikasi",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF102A43)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("btn_back")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color(0xFF102A43)
                        )
                    }
                },
                actions = {
                    if (unreadCount > 0) {
                        TextButton(
                            onClick = {
                                viewModel.markAllNotificationsAsRead()
                                Toast.makeText(context, "Semua dibaca", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text(
                                "Tandai Dibaca",
                                color = Color(0xFF0F5B9B),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF7FAFC),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    val bgColor by animateColorAsState(
                        if (isSelected) Color(0xFFE3F2FD) else Color.Transparent
                    )
                    val textColor by animateColorAsState(
                        if (isSelected) Color(0xFF0F5B9B) else Color(0xFF627D98)
                    )
                    
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = bgColor,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF0F5B9B) else Color(0xFFD9E2EC)
                        ),
                        modifier = Modifier.clickable { selectedCategory = category }
                    ) {
                        Text(
                            text = category,
                            color = textColor,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            
            HorizontalDivider(color = Color(0xFFE2E8F0))

            if (filteredNotifications.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsNone,
                            contentDescription = "Empty",
                            tint = Color(0xFF9FB3C8),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Belum ada Notifikasi",
                            color = Color(0xFF102A43),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tunggu update pesanan dan info terbaru akan muncul di sini.",
                            color = Color(0xFF627D98),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    itemsIndexed(filteredNotifications, key = { _, it -> it.id }) { index, notif ->
                        UserNotificationItem(
                            notification = notif,
                            onClick = {
                                if (notif.isUnread) {
                                    viewModel.markNotificationAsRead(notif.id)
                                }
                            },
                            onDetailClick = { orderId ->
                                if (notif.isUnread) {
                                    viewModel.markNotificationAsRead(notif.id)
                                }
                                onNavigateToOrderDetail(orderId)
                            }
                        )
                        if (index < filteredNotifications.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Color(0xFFF1F5F9),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to format timestamp
private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "Baru saja"
        diff < 3600000 -> "${diff / 60000} mnt lalu"
        diff < 86400000 -> "${diff / 3600000} jam lalu"
        diff < 172800000 -> "Kemarin"
        else -> {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            sdf.format(Date(timestamp))
        }
    }
}

@Composable
fun UserNotificationItem(
    notification: com.example.data.Notification,
    onClick: () -> Unit,
    onDetailClick: (String) -> Unit
) {
    val bgColor by animateColorAsState(if (notification.isUnread) Color(0xFFF0F7FF) else Color.White)
    
    // Determine icon and color based on category
    val (iconRes, iconTint, iconBg) = when(notification.category) {
        "PESANAN" -> Triple(Icons.Outlined.ShoppingBag, Color(0xFF0F5B9B), Color(0xFFE5F0FF))
        "PROMO" -> Triple(Icons.Outlined.LocalOffer, Color(0xFFE11D48), Color(0xFFFFE4E6))
        "INFO" -> Triple(Icons.Outlined.Info, Color(0xFF2C9C84), Color(0xFFE6F6F4))
        else -> Triple(Icons.Outlined.Notifications, Color(0xFF627D98), Color(0xFFF0F4F8))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable {
                if (notification.category == "PESANAN" && notification.orderId != null) {
                    onDetailClick(notification.orderId)
                } else {
                    onClick()
                }
            }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon Container
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBg)
        ) {
            Icon(
                imageVector = iconRes,
                contentDescription = notification.category,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Text Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = notification.title,
                    fontWeight = if (notification.isUnread) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color(0xFF102A43),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatTimestamp(notification.timestamp),
                    fontSize = 12.sp,
                    color = Color(0xFF627D98),
                    maxLines = 1
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = notification.description,
                fontSize = 14.sp,
                color = if (notification.isUnread) Color(0xFF243B53) else Color(0xFF627D98),
                lineHeight = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            if (notification.category == "PESANAN") {
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = { notification.orderId?.let { onDetailClick(it) } },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text(
                        "Cek Detail Pesanan",
                        fontSize = 13.sp,
                        color = Color(0xFF0F5B9B),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Detail",
                        tint = Color(0xFF0F5B9B),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
        
        // Unread Indicator dot
        if (notification.isUnread) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F5B9B))
                    .align(Alignment.CenterVertically)
            )
        }
    }
}
