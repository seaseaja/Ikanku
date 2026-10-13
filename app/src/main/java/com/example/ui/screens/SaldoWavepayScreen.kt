package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaldoWavepayScreen(
    viewModel: com.example.viewmodel.JastipViewModel,
    onBackClick: () -> Unit,
    onNavigateToIsiSaldo: () -> Unit
) {
    // INTEGRASI SALDO WAVEPAY
    // Saldo ini terintegrasi dengan UserProfileScreen dan akan ter-update secara real-time
    // ketika user melakukan top-up (IsiSaldoScreen) atau checkout pembayaran (CheckoutScreen)
    val userBalance by viewModel.userBalance.collectAsState()
    val currencyFormatter = remember {
        java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF7FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Saldo WavePay", fontWeight = FontWeight.Bold, color = Color(0xFF003049), fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFF003049)) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFF7FAFC))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Balance Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF003B73))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("TOTAL SALDO", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Medium)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("Rp", fontSize = 16.sp, color = Color.White.copy(alpha = 0.9f), modifier = Modifier.padding(bottom = 4.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(currencyFormatter.format(userBalance), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = onNavigateToIsiSaldo,
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF003049)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Outlined.ArrowDownward, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Isi Saldo", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }
            }

    

            // Transaction History Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Riwayat Transaksi", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                TextButton(onClick = {}) {
                    Text("Lihat Semua", color = Color(0xFF003B73), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            // Filtering Tabs
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterTab("Semua", true)
                FilterTab("Masuk", false)
                FilterTab("Keluar", false)
                FilterTab("Refund", false)
            }

            // Transaction List
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TransactionItem(
                    title = "Pembayaran Jastip",
                    date = "24 Okt 2023 • 14:20",
                    amount = "-Rp 85.000",
                    isIncome = false,
                    iconType = "out"
                )
                TransactionItem(
                    title = "Top Up Alfamart",
                    date = "22 Okt 2023 • 09:15",
                    amount = "+Rp 100.000",
                    isIncome = true,
                    iconType = "in"
                )
                TransactionItem(
                    title = "Pembelian Ikan Kakap",
                    date = "20 Okt 2023 • 18:45",
                    amount = "-Rp 125.000",
                    isIncome = false,
                    iconType = "out"
                )
                TransactionItem(
                    title = "Refund Pesanan Gagal",
                    date = "19 Okt 2023 • 10:00",
                    amount = "+Rp 60.000",
                    isIncome = true,
                    iconType = "in"
                )
            }

            Spacer(Modifier.height(16.dp))
            
            Text(
                "Menampilkan riwayat 30 hari terakhir",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun FilterTab(label: String, selected: Boolean) {
    Surface(
        color = if (selected) Color(0xFF003B73) else Color(0xFFE2E8F0),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else Color(0xFF4A5568),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun TransactionItem(title: String, date: String, amount: String, isIncome: Boolean, iconType: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconBg = if (isIncome) Color(0xFFE6F0FF) else Color(0xFFFFECEC)
            val iconColor = if (isIncome) Color(0xFF003B73) else Color(0xFFD32F2F)
            val iconVec = if (isIncome) Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward

            Box(modifier = Modifier.size(48.dp).background(iconBg, CircleShape), contentAlignment = Alignment.Center) {
                Icon(iconVec, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                Spacer(Modifier.height(4.dp))
                Text(date, fontSize = 13.sp, color = Color.Gray)
            }
            Text(
                amount,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isIncome) Color(0xFF003B73) else Color(0xFFD32F2F)
            )
        }
    }
}
