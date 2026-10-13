package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.viewmodel.JastipViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPesananScreen(
    orderId: String,
    viewModel: JastipViewModel,
    onBackClick: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val orders by viewModel.allOrders.collectAsState()
    val order = orders.find { it.id == orderId }

    val currencyFormatter = remember {
        java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF7FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Pesanan", fontWeight = FontWeight.Bold, color = Color(0xFF003049), fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFF003049)) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFF7FAFC))
            )
        }
    ) { innerPadding ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Memuat data pesanan...")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Pesanan Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("STATUS PESANAN", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            val statusDisplay = when(order.status) {
                                "PENDING" -> "Menunggu Konfirmasi"
                                "ACCEPTED" -> "Diterima Jastiper"
                                "BUYING" -> "Sedang Belanja"
                                "DELIVERING" -> "Sedang Dikirim"
                                "COMPLETED" -> "Selesai"
                                else -> order.status
                            }
                            Text(statusDisplay, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(Modifier.height(4.dp))
                            val dateString = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(java.util.Date(order.timestamp))
                            Text("Dibuat pada $dateString", fontSize = 13.sp, color = Color.Gray)
                        }
                        Box(modifier = Modifier.size(40.dp).background(
                            when(order.status) {
                                "COMPLETED" -> Color(0xFFE8F5E9)
                                "PENDING" -> Color(0xFFFFF3E0)
                                else -> Color(0xFFE3F2FD)
                            }, CircleShape
                        ), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when(order.status) {
                                    "COMPLETED" -> Icons.Outlined.Check
                                    "PENDING" -> Icons.Outlined.PendingActions
                                    "DELIVERING" -> Icons.Outlined.LocalShipping
                                    else -> Icons.Outlined.ShoppingBag
                                },
                                contentDescription = null,
                                tint = when(order.status) {
                                    "COMPLETED" -> Color(0xFF2E7D32)
                                    "PENDING" -> Color(0xFFE65100)
                                    else -> Color(0xFF1976D2)
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Info Jastiper Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("INFO JASTIPER", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(48.dp).background(Color(0xFFE2E8F0), CircleShape), contentAlignment = Alignment.Center) {
                                    Text(order.jastiperName.take(1), fontWeight = FontWeight.Bold, color = Color(0xFF003049))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(order.jastiperName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF003049), modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("4.9 • Top Jastiper", fontSize = 13.sp, color = Color.Gray)
                                    }
                                }
                            }
                            IconButton(
                                onClick = { onNavigateToChat(order.id) },
                                modifier = Modifier.background(Color(0xFF003049), RoundedCornerShape(8.dp)).size(36.dp)
                            ) {
                                Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Alamat Pengiriman Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color(0xFF003049), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("ALAMAT PENGIRIMAN", fontSize = 11.sp, color = Color(0xFF003049), fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black, modifier = Modifier.padding(start = 28.dp))
                        Text(
                            order.deliveryAddress,
                            fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp, modifier = Modifier.padding(start = 28.dp, top = 4.dp)
                        )
                    }
                }

                // Daftar Belanja Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("DAFTAR BELANJA", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(16.dp))
                        
                        // Parse fish types (already in "Title (Qtykg)" format)
                        val items = order.fishType.split(", ")
                        items.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(50.dp).background(Color(0xFFE2F3F9), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                    Text("🐟", fontSize = 24.sp)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(item, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                                    Text("Market: ${order.marketName}", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                        
                        if (order.notes.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(Modifier.height(8.dp))
                            Text("Catatan:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                            Text(order.notes, fontSize = 13.sp, color = Color.DarkGray)
                        }
                    }
                }

                // Rincian Pembayaran Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("RINCIAN PEMBAYARAN", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal Produk", fontSize = 13.sp, color = Color.Gray)
                            Text(currencyFormatter.format(order.budgetEst), fontSize = 13.sp, color = Color.Black)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Jasa Jastip", fontSize = 13.sp, color = Color.Gray)
                            Text(currencyFormatter.format(order.jastipFee), fontSize = 13.sp, color = Color.Black)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Biaya Layanan", fontSize = 13.sp, color = Color.Gray)
                            Text(currencyFormatter.format(2000.0), fontSize = 13.sp, color = Color.Black)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Total Pembayaran", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(currencyFormatter.format(order.budgetEst + order.jastipFee + 2000.0), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF003049))
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp)).padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF003049), modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Metode Pembayaran", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                                    Text("WavePay Balance", fontSize = 13.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
