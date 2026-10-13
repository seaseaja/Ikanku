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

// Reusing colors from JastiperDashboardScreen to preserve visual identity 100%
// val jastipNavy = Color(0xFF102A43)
// val jastipBlueAccent = Color(0xFF0F5B9B)
// val jastipTextGray = Color(0xFF627D98)
// val jastipGreenAccent = Color(0xFF2C9C84)

data class JastipNotification(
    val id: String,
    val category: String,       // "PESANAN", "SISTEM/PENDAPATAN", "PROMO/TIPS", "SISTEM"
    val timestamp: String,      // "2 menit yang lalu", "1 jam yang lalu", etc
    val title: String,
    val description: String,
    val isUnread: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JastiperNotificationScreen(
    viewModel: JastipViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Internal mutable state for high-fidelity notifications as described in screenshot
    val initialNotifications = remember {
        mutableStateListOf(
            JastipNotification(
                id = "1",
                category = "PESANAN",
                timestamp = "2 mnt lalu",
                title = "Pesanan Baru!",
                description = "Pelanggan Siti Aminah telah memesan 2kg Ikan Kerapu.",
                isUnread = true
            ),
            JastipNotification(
                id = "2",
                category = "SISTEM/PENDAPATAN",
                timestamp = "1 jam lalu",
                title = "Pembayaran Berhasil",
                description = "Dana sebesar Rp 125.000 telah masuk ke saldo pendapatan Anda.",
                isUnread = true
            ),
            JastipNotification(
                id = "3",
                category = "PROMO/TIPS",
                timestamp = "3 jam lalu",
                title = "Live Streaming Populer",
                description = "Tips: Lakukan live pada jam 09.00 untuk viewers lebih banyak!",
                isUnread = false
            ),
            JastipNotification(
                id = "4",
                category = "SISTEM",
                timestamp = "Kemarin",
                title = "Verifikasi Akun Berhasil",
                description = "Selamat, Anda kini adalah Jastiper Terverifikasi!",
                isUnread = false
            )
        )
    }

    // List of available filter categories
    val categories = listOf("Semua", "Pesanan", "Promo", "Sistem")
    var selectedCategory by remember { mutableStateOf("Semua") }

    // Filter implementation
    val filteredNotifications = remember(selectedCategory, initialNotifications.toList()) {
        initialNotifications.filter { notif ->
            when (selectedCategory) {
                "Semua" -> true
                "Pesanan" -> notif.category.startsWith("PESANAN")
                "Promo" -> notif.category.startsWith("PROMO")
                "Sistem" -> notif.category.startsWith("SISTEM")
                else -> true
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Notifikasi",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF102A43), // jastipNavy
                        letterSpacing = (-0.5).sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("btn_back_notif")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color(0xFF102A43)
                        )
                    }
                },
                actions = {
                    // Adaptasi mobile ringkas: "Baca Semua" link text berkontras tinggi
                    TextButton(
                        onClick = {
                            var updatedAny = false
                            for (i in 0 until initialNotifications.size) {
                                if (initialNotifications[i].isUnread) {
                                    initialNotifications[i] = initialNotifications[i].copy(isUnread = false)
                                    updatedAny = true
                                }
                            }
                            if (updatedAny) {
                                Toast.makeText(context, "Semua notifikasi ditandai sudah dibaca", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("btn_read_all")
                    ) {
                        Text(
                            text = "Baca Semua",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F5B9B) // jastipBlueAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF102A43)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // FILTER TAB: Horizontally Scrollable Chips (Pills) adaptasi mobile responsif
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    val chipBgColor = if (isSelected) Color(0xFF0F5B9B) else Color(0xFFF1F5F9)
                    val chipTextColor = if (isSelected) Color.White else Color(0xFF627D98)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(chipBgColor)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("tab_notif_$cat")
                    ) {
                        Text(
                            text = cat,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = chipTextColor
                        )
                    }
                }
            }

            Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            if (filteredNotifications.isEmpty()) {
                // Empty state view helper
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = Color(0xFF627D98).copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tidak Ada Notifikasi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF102A43)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Notifikasi baru tentang aktivitas jastip Anda akan muncul di sini.",
                            fontSize = 12.sp,
                            color = Color(0xFF627D98),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // FLAT LIST VIEW list adaptasi mobile responsif
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .testTag("list_notifications")
                ) {
                    itemsIndexed(filteredNotifications, key = { _, item -> item.id }) { index, item ->
                        // Background adaptasi: Biru sangat muda jika belum dibaca
                        val unreadBgColor = if (item.isUnread) Color(0xFFF0F7FF) else Color.White

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(unreadBgColor)
                                .clickable {
                                    // Mark single item read on tap helper
                                    val idx = initialNotifications.indexOfFirst { it.id == item.id }
                                    if (idx != -1 && initialNotifications[idx].isUnread) {
                                        initialNotifications[idx] = initialNotifications[idx].copy(isUnread = false)
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Left Icon Holder (adaptive coloring based on category)
                                val iconData = when {
                                    item.category.startsWith("PESANAN") -> Triple(Icons.Default.ShoppingCart, Color(0xFFE0F2FE), Color(0xFF0EA5E9))
                                    item.category.startsWith("SISTEM/PENDAPATAN") -> Triple(Icons.Default.AccountBalanceWallet, Color(0xFFDCFCE7), Color(0xFF22C55E))
                                    item.category.startsWith("PROMO") -> Triple(Icons.Default.Lightbulb, Color(0xFFFEF9C3), Color(0xFFEAB308))
                                    else -> Triple(Icons.Default.VerifiedUser, Color(0xFFF1F5F9), Color(0xFF64748B))
                                }

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(iconData.second),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = iconData.first,
                                        contentDescription = null,
                                        tint = iconData.third,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Center Content stack adaptasi (3-tumpukan vertikal)
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    // 1. Tumpukan Atas: Kategori / Tag disandingkan tipis dengan Timestamp
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        val categoryTextColor = when {
                                            item.category.startsWith("PESANAN") -> Color(0xFF0F5B9B)
                                            item.category.startsWith("SISTEM/PENDAPATAN") -> Color(0xFF2C9C84)
                                            else -> Color(0xFF627D98)
                                        }

                                        Text(
                                            text = item.category,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = categoryTextColor,
                                            letterSpacing = 0.5.sp
                                        )

                                        Text(
                                            text = "•",
                                            fontSize = 10.sp,
                                            color = Color(0xFF829AB1)
                                        )

                                        Text(
                                            text = item.timestamp,
                                            fontSize = 11.sp,
                                            color = Color(0xFF829AB1)
                                        )
                                    }

                                    // 2. Tumpukan Tengah: Judul Notifikasi
                                    Text(
                                        text = item.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF102A43)
                                    )

                                    // 3. Tumpukan Bawah: Deskripsi (max 2 baris truncated)
                                    Text(
                                        text = item.description,
                                        fontSize = 13.sp,
                                        color = Color(0xFF486581),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }

                        // Thin bottom item divider line
                        Divider(
                            color = Color(0xFFF1F5F9), 
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}
