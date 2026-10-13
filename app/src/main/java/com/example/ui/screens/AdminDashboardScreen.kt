package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JastipOrder
import com.example.viewmodel.JastipViewModel

// Admin Color Palette
private val AdminNavy = Color(0xFF14142B)
private val AdminBlueHeader = Color(0xFF204A77)
private val AdminTeal = Color(0xFF5CC8A0)
private val AdminMuted = Color(0xFF6B7A90)
private val AdminBlueBtn = Color(0xFF1B4E8D)
private val AdminLightBlueBg = Color(0xFFE3F2FD)
private val AdminSoftGray = Color(0xFFF3F5F7)

@Composable
fun AdminDashboardScreen(
    viewModel: JastipViewModel,
    onLogout: () -> Unit
) {
    var selectedMenu by remember { mutableStateOf("Dashboard") }

    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar
        Column(
            modifier = Modifier
                .width(260.dp)
                .fillMaxHeight()
                .background(Color.White)
                .border(1.dp, Color.LightGray.copy(alpha = 0.4f))
        ) {
            // Header Sidebar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AdminBlueHeader)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AdminTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Text("I", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("IkanKu", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Admin Panel", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val menuItems = listOf(
                "Dashboard" to Icons.Default.Dashboard,
                "Verifikasi Jastip" to Icons.Default.VerifiedUser,
                "Moderasi Konten" to Icons.Default.AdminPanelSettings,
                "Keuangan & Escrow" to Icons.Default.AccountBalance,
                "Penanganan Komplain" to Icons.Default.Support,
                "Lacak Kurir" to Icons.Default.LocationOn,
                "Artikel & Edukasi" to Icons.Default.Article
            )

            Column(modifier = Modifier.weight(1f)) {
                menuItems.forEach { (label, icon) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(if (selectedMenu == label) AdminLightBlueBg else Color.Transparent)
                            .clickable { selectedMenu = label }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (selectedMenu == label) AdminBlueBtn else AdminMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = label,
                            color = if (selectedMenu == label) AdminBlueBtn else AdminNavy,
                            fontWeight = if (selectedMenu == label) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AdminBlueHeader),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("LOGOUT")
            }
        }

        // Main Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(AdminSoftGray)
                .padding(24.dp)
        ) {
            when (selectedMenu) {
                "Dashboard" -> AdminDashboardContent(viewModel)
                "Verifikasi Jastip" -> VerifikasiJastipContent(viewModel)
                "Moderasi Konten" -> ModerasiKontenContent(viewModel)
                "Keuangan & Escrow" -> KeuanganEscrowContent(viewModel)
                "Penanganan Komplain" -> PenangananKomplainContent(viewModel)
                "Lacak Kurir" -> LacakKurirContent(viewModel)
                "Artikel & Edukasi" -> ArtikelEdukasiContent(viewModel)
                else -> AdminPlaceholderContent(selectedMenu)
            }
        }
    }
}

@Composable
fun AdminDashboardContent(viewModel: JastipViewModel) {
    val orders by viewModel.allOrders.collectAsState(initial = emptyList())
    val totalOrders = orders.size
    val pendingOrders = orders.count { it.status == "PENDING" }
    val completedOrders = orders.count { it.status == "COMPLETED" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Dashboard Utama",
            color = AdminNavy,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AdminStatCard("Total Pesanan", "$totalOrders", Icons.Default.ShoppingBag, Modifier.weight(1f))
            AdminStatCard("Pesanan Pending", "$pendingOrders", Icons.Default.HourglassEmpty, Modifier.weight(1f))
            AdminStatCard("Selesai", "$completedOrders", Icons.Default.CheckCircle, Modifier.weight(1f))
            AdminStatCard("Dispute Open", "2", Icons.Default.Warning, Modifier.weight(1f))
        }

        // Recent Orders Table
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Pesanan Terbaru",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AdminNavy
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (orders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada data pesanan", color = AdminMuted)
                    }
                } else {
                    orders.take(10).forEach { order ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(order.customerName, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text(order.fishType, color = AdminMuted, fontSize = 12.sp)
                            }
                            Text(
                                order.status,
                                color = when (order.status) {
                                    "COMPLETED" -> Color(0xFF4CAF50)
                                    "PENDING" -> Color(0xFFFF9800)
                                    else -> AdminBlueBtn
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

@Composable
fun VerifikasiJastipContent(viewModel: JastipViewModel) {
    val pendingJastipers = listOf(
        Triple("Budi Santoso", "Jakarta - Muara Angke", "Menunggu Verifikasi KTP"),
        Triple("Siti Aminah", "Surabaya - Pabean", "Dokumen Lengkap"),
        Triple("Andi Pratama", "Bandung - Ciroyom", "Menunggu Review Lapak")
    )

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Verifikasi Jastiper Baru", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AdminNavy)
        Spacer(modifier = Modifier.height(20.dp))
        
        pendingJastipers.forEach { (name, location, status) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AdminNavy)
                        Text(location, color = AdminMuted, fontSize = 14.sp)
                        Text(status, color = AdminBlueBtn, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = AdminTeal), shape = RoundedCornerShape(8.dp)) {
                            Text("Terima", fontSize = 12.sp)
                        }
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373)), shape = RoundedCornerShape(8.dp)) {
                            Text("Tolak", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModerasiKontenContent(viewModel: JastipViewModel) {
    val reports = listOf(
        Triple("Ulasan Palsu", "User123", "Jastiper ini sangat buruk (Padahal belum beli)"),
        Triple("Foto Tidak Sesuai", "Buyer99", "Foto ikan busuk tapi barang asli bagus"),
        Triple("Kata Kasar", "AdminBot", "Terdapat kata tidak sopan di kolom chat")
    )

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Moderasi Konten & Laporan", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AdminNavy)
        Spacer(modifier = Modifier.height(20.dp))

        reports.forEach { (reason, reporter, content) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(reason, fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 14.sp)
                        Text("Oleh: $reporter", color = AdminMuted, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("\"$content\"", fontSize = 14.sp, color = AdminNavy)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = AdminNavy), shape = RoundedCornerShape(8.dp)) {
                            Text("Hapus Konten", fontSize = 12.sp)
                        }
                        OutlinedButton(onClick = {}, shape = RoundedCornerShape(8.dp)) {
                            Text("Abaikan", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KeuanganEscrowContent(viewModel: JastipViewModel) {
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Keuangan & Escrow", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AdminNavy)
        Spacer(modifier = Modifier.height(20.dp))

        // Escrow Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AdminBlueBtn),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Total Dana Terendap (Escrow)", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Text("Rp 14.250.000", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("24 Pesanan Berlangsung", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Transaksi Terbaru", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AdminNavy)
        Spacer(modifier = Modifier.height(12.dp))

        transactions.take(10).forEach { trx ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(trx.type, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(trx.method, color = AdminMuted, fontSize = 12.sp)
                    }
                    Text(
                        "Rp ${String.format("%,.0f", trx.amount)}",
                        fontWeight = FontWeight.Bold,
                        color = if (trx.type == "TOP_UP") AdminTeal else Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun PenangananKomplainContent(viewModel: JastipViewModel) {
    val orders by viewModel.allOrders.collectAsState(initial = emptyList())
    val complainedOrders = orders.filter { it.complaintStatus != null }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Penanganan Komplain (Dispute)", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AdminNavy)
        Spacer(modifier = Modifier.height(20.dp))

        if (complainedOrders.isEmpty()) {
            // Dummy complaints if none in DB
            val dummyComplaints = listOf(
                Pair("Pesanan #IK-9921", "Ikan Salmon tidak segar, bau menyengat"),
                Pair("Pesanan #IK-8812", "Berat kurang 0.5kg dari timbangan")
            )
            dummyComplaints.forEach { (id, msg) ->
                ComplaintCard(id, "Pelanggan A", msg)
            }
        } else {
            complainedOrders.forEach { order ->
                ComplaintCard("Pesanan #${order.id.take(8).uppercase()}", order.customerName, order.complaintStatus ?: "")
            }
        }
    }
}

@Composable
fun ComplaintCard(orderId: String, customer: String, message: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(orderId, fontWeight = FontWeight.Bold, color = AdminBlueBtn)
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFFFF3E0)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Text("OPEN", color = Color(0xFFFF9800), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Pelanggan: $customer", fontSize = 12.sp, color = AdminMuted)
            Spacer(modifier = Modifier.height(10.dp))
            Text(message, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = AdminNavy), modifier = Modifier.weight(1f)) {
                    Text("Buka Tiket Chat", fontSize = 12.sp)
                }
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = AdminTeal), modifier = Modifier.weight(1f)) {
                    Text("Refund Dana", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun LacakKurirContent(viewModel: JastipViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Lacak Kurir & Jastiper", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AdminNavy)
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Map, null, modifier = Modifier.size(64.dp), tint = AdminMuted)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Peta Live Tracking", fontWeight = FontWeight.Bold, color = AdminMuted)
                    Text("8 Jastiper sedang aktif mengantar", color = AdminMuted, fontSize = 12.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(AdminLightBlueBg), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DirectionsBike, null, tint = AdminBlueBtn)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Pak Joko (Jastiper)", fontWeight = FontWeight.Bold)
                    Text("Sedang menuju: Jl. Muara Karang No. 12", fontSize = 12.sp, color = AdminMuted)
                }
            }
        }
    }
}

@Composable
fun ArtikelEdukasiContent(viewModel: JastipViewModel) {
    val articles = listOf(
        "Cara Memilih Ikan Segar untuk Pemula",
        "Tips Menjadi Jastiper Sukses di IkanKu",
        "Update Protokol Pengiriman Seafood 2024",
        "Mengenal Jenis-jenis Ikan Laut Indonesia"
    )

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Artikel & Edukasi", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AdminNavy)
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = AdminBlueBtn)) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Text("Buat Baru")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        articles.forEach { title ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Row {
                        IconButton(onClick = {}) { Icon(Icons.Default.Edit, null, tint = AdminMuted, modifier = Modifier.size(20.dp)) }
                        IconButton(onClick = {}) { Icon(Icons.Default.Delete, null, tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(20.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(AdminLightBlueBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = AdminBlueBtn, modifier = Modifier.size(24.dp))
            }
            Column {
                Text(title, color = AdminMuted, fontSize = 12.sp)
                Text(value, color = AdminNavy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdminPlaceholderContent(menu: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Construction, null, modifier = Modifier.size(64.dp), tint = AdminMuted)
            Spacer(modifier = Modifier.height(16.dp))
            Text(menu, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AdminNavy)
            Text("Halaman ini sedang dalam pengembangan", color = AdminMuted)
        }
    }
}
