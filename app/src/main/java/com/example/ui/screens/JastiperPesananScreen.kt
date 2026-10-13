package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JastiperPesananScreen(
    viewModel: JastipViewModel,
    onNavigateToNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Selesai") } 
    var searchQuery by remember { mutableStateOf("") }
    var activePage by remember { mutableStateOf(1) }
    var showDownloadMenu by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var isLoadingMore by remember { mutableStateOf(false) }
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Handle Scroll to Top event from Bottom Navigation
    LaunchedEffect(Unit) {
        viewModel.scrollToTopEvent.collect {
            listState.animateScrollToItem(0)
        }
    }

    val completedOrders = remember {
        mutableStateListOf(
            JastipOrder(
                id = "#IK-99281",
                customerName = "Andi Saputra",
                avatar = "AS",
                avatarBg = Color(0xFFEBF3FC),
                productName = "Krapu Macan (2kg)", 
                detailIkan = "Kerapu Macan (2kg)",
                shippingAddress = "Live: TPI Muara Angke",
                status = "Selesai",
                date = "30 Mei 2026, 10:25",
                totalEarnings = 450000
            ),
            JastipOrder(
                id = "#IK-99275",
                customerName = "Maria Wijaya",
                avatar = "MW",
                avatarBg = Color(0xFFFFF3E0),
                productName = "Lobster Hijau (1.5kg)",
                detailIkan = "Lobster Hijau (1.5kg)",
                shippingAddress = "Live: Pasar Ikan Modern",
                status = "Selesai",
                date = "29 Mei 2026, 16:45",
                totalEarnings = 1250000
            )
        )
    }

    val activeOrders = remember {
        mutableStateListOf(
            JastipOrder(
                id = "#IK-99120",
                customerName = "Siti Aminah",
                avatar = "S",
                avatarBg = Color(0xFFE0F7FA),
                productName = "Kakap Merah Segar (1.2kg)",
                detailIkan = "Kakap Merah Segar",
                shippingAddress = "Live: TPI Muara Angke",
                status = "Menunggu Verifikasi",
                date = "Hari ini, 10:45 AM",
                totalEarnings = 115000
            )
        )
    }

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Pesanan", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = jastipNavy)
                    IconButton(
                        onClick = onNavigateToNotifications,
                        modifier = Modifier.align(Alignment.CenterEnd).size(36.dp).clip(CircleShape).background(Color(0xFFF0F4F8))
                    ) {
                        Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "Notifikasi", tint = jastipNavy, modifier = Modifier.size(20.dp))
                    }
                }
                Divider(color = Color(0xFFE4F2FE), thickness = 1.dp)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari ID Pesanan...") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFEDEFF2)).padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Semua Aktif", "Selesai").forEach { tab ->
                        val isSelected = selectedTab == if (tab == "Semua Aktif") "Aktif" else "Selesai"
                        Box(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(if (isSelected) jastipBlueAccent else Color.Transparent).clickable { selectedTab = if (tab == "Semua Aktif") "Aktif" else "Selesai" }.padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = tab, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else jastipNavy, fontSize = 13.sp)
                        }
                    }
                }
                Divider(color = Color(0xFFE4F2FE), thickness = 1.dp)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().background(Color(0xFFF5FAFD)).padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val currentList = if (selectedTab == "Selesai") completedOrders else activeOrders
            items(currentList) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = order.id, fontWeight = FontWeight.Bold, color = jastipBlueAccent)
                            Text(text = order.status, fontWeight = FontWeight.Bold, color = jastipGreenAccent)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = order.customerName, fontWeight = FontWeight.Bold)
                        Text(text = order.productName)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = currencyFormatter.format(order.totalEarnings), fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

data class JastipOrder(
    val id: String,
    val customerName: String,
    val avatar: String,
    val avatarBg: Color,
    val productName: String,
    val detailIkan: String,
    val shippingAddress: String,
    val status: String,
    val date: String,
    val totalEarnings: Int
)
