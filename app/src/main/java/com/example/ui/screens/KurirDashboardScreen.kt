package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurirDashboardScreen(
    viewModel: JastipViewModel,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    
    val pendingDeliveries = remember {
        mutableStateListOf(
            DeliveryItem("DLV-001", "Kakap Merah 2kg", "Pasar Induk", "Kebayoran, Jakarta", "Diperjalanan"),
            DeliveryItem("DLV-002", "Udang Vaname 5kg", "TPI Muara Angke", "Tebet, Jakarta", "Menunggu Pickup")
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF5FAFD),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Dashboard Kurir",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F5B9B),
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color(0xFF0F5B9B)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F5B9B))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsBike, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Budi Kurniawan", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                            Text("ID Kurir: KUR-2819", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Target hari ini: 12 Pengiriman", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Daftar Tugas Pengiriman",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B3B5A)
                )
            }

            items(pendingDeliveries) { delivery ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE4F2FE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(delivery.id, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (delivery.status == "Diperjalanan") Color(0xFFFFF3E0) else Color(0xFFE3F2FD))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    delivery.status, 
                                    fontSize = 10.sp, 
                                    fontWeight = FontWeight.Bold,
                                    color = if (delivery.status == "Diperjalanan") Color(0xFFE65100) else Color(0xFF1565C0)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dari: ${delivery.pickup}", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ke: ${delivery.destination}", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Barang: ${delivery.itemInfo}", fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Button for Reporting Issues
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "Membuka form pelaporan kendala (Kecelakaan/Ban Bocor/Nyasar)...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Lapor Kendala", fontSize = 12.sp)
                            }

                            if (delivery.status == "Menunggu Pickup") {
                                Button(
                                    onClick = {
                                        val idx = pendingDeliveries.indexOf(delivery)
                                        if (idx != -1) {
                                            pendingDeliveries[idx] = delivery.copy(status = "Diperjalanan")
                                            Toast.makeText(context, "Status diupdate: Diperjalanan", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F5B9B)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Mulai Perjalanan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        // Show Dialog to Handover & Upload Proof
                                        Toast.makeText(context, "Membuka kamera untuk foto & input kode validasi...", Toast.LENGTH_SHORT).show()
                                        val idx = pendingDeliveries.indexOf(delivery)
                                        if (idx != -1) {
                                            pendingDeliveries.removeAt(idx)
                                            Toast.makeText(context, "Bukti Selesai Terkirim! Notifikasi otomatis terkirim ke User.", Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Selesaikan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            
            if (pendingDeliveries.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Semua tugas selesai!", fontWeight = FontWeight.Bold, color = Color(0xFF1B3B5A))
                    }
                }
            }
        }
    }
}

data class DeliveryItem(
    val id: String,
    val itemInfo: String,
    val pickup: String,
    val destination: String,
    val status: String
)
