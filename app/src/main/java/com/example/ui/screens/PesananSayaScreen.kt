package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.viewmodel.JastipViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PesananSayaScreen(
    viewModel: JastipViewModel,
    onBackClick: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToLacak: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Aktif, 1: Riwayat
    val myActiveOrders by viewModel.myActiveOrders.collectAsState()
    val completedOrders by viewModel.completedOrders.collectAsState()

    var showRatingDialog by remember { mutableStateOf(false) }
    var showComplaintDialog by remember { mutableStateOf(false) }
    var selectedOrderId by remember { mutableStateOf("") }
    var selectedOrderName by remember { mutableStateOf("") }
    var ratingValue by remember { mutableIntStateOf(0) }
    var complaintText by remember { mutableStateOf("") }

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
                title = {
                    Text(
                        text = "Pesanan Saya",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003049)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF003049)
                        )
                    }
                },
                actions = {
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF7FAFC)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(Color(0xFFE2E8F0), shape = RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                // Aktif Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (selectedTab == 0) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .let {
                            if (selectedTab == 0) {
                                it.background(Color.White, RoundedCornerShape(8.dp))
                            } else {
                                it
                            }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = { selectedTab = 0 }, contentPadding = PaddingValues(0.dp)) {
                        Text(
                            text = "Aktif",
                            color = if (selectedTab == 0) Color(0xFF003049) else Color(0xFF64748B),
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    }
                }

                // Riwayat Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (selectedTab == 1) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = { selectedTab = 1 }, contentPadding = PaddingValues(0.dp)) {
                        Text(
                            text = "Riwayat",
                            color = if (selectedTab == 1) Color(0xFF003049) else Color(0xFF64748B),
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (selectedTab == 0) {
                // Aktif Content - Show orders from ViewModel
                if (myActiveOrders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingBag,
                                contentDescription = "Empty",
                                tint = Color(0xFF9FB3C8),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Belum ada pesanan aktif", color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        myActiveOrders.forEach { order ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToDetail(order.id) },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(52.dp)
                                                    .background(Color.LightGray, CircleShape)
                                                    .clip(CircleShape)
                                            ) {
                                                // Empty Box as placeholder for Image
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(order.jastiperName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF003049), modifier = Modifier.size(14.dp))
                                                    Spacer(Modifier.width(4.dp))
                                                    Text("4.8 • Top Courier", fontSize = 13.sp, color = Color(0xFF003049), fontWeight = FontWeight.SemiBold)
                                                }
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    when(order.status) {
                                                        "ACCEPTED" -> Color(0xFF2C5282)
                                                        "BUYING" -> Color(0xFFD97706)
                                                        "DELIVERING" -> Color(0xFF059669)
                                                        else -> Color.Gray
                                                    },
                                                    RoundedCornerShape(16.dp)
                                                )
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                when(order.status) {
                                                    "ACCEPTED" -> "Diterima"
                                                    "BUYING" -> "Belanja"
                                                    "DELIVERING" -> "Dikirim"
                                                    else -> order.status
                                                },
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(Icons.Outlined.ShoppingBag, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(order.fishType, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(Icons.Outlined.LocalShipping, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            when(order.status) {
                                                "ACCEPTED" -> "Pesanan diterima, menunggu proses"
                                                "BUYING" -> "Jastiper sedang berbelanja di ${order.marketName}"
                                                "DELIVERING" -> "Menuju Lokasi Anda (Estimasi 15 Menit)"
                                                else -> "Sedang diproses"
                                            },
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0xFFF1F5F9))
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column {
                                            Text("TOTAL PEMBAYARAN", fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
                                            // Tambahkan biaya layanan 2000 agar sesuai dengan detail pesanan
                                            Text(currencyFormatter.format(order.budgetEst + order.jastipFee + 2000.0), fontSize = 18.sp, color = Color(0xFF003049), fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = onNavigateToLacak,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003049)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Lacak", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            } else {
                // Riwayat - Show completed orders
                if (completedOrders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada riwayat pesanan.", color = Color.Gray)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        completedOrders.forEach { order ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToDetail(order.id) },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(order.jastiperName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text("Selesai", color = Color(0xFF059669), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(order.fishType, fontSize = 14.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    // Tambahkan biaya layanan 2000 agar sesuai dengan detail pesanan
                                    Text(currencyFormatter.format(order.budgetEst + order.jastipFee + 2000.0), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF003049))

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // LOGIKA KOMPLAIN
                                        if (order.complaintStatus == null) {
                                            OutlinedButton(
                                                onClick = {
                                                    selectedOrderId = order.id
                                                    selectedOrderName = order.jastiperName
                                                    complaintText = ""
                                                    showComplaintDialog = true
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                                contentPadding = PaddingValues(vertical = 8.dp)
                                            ) {
                                                Text(
                                                    "Ajukan Komplain",
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF64748B),
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        } else {
                                            Surface(
                                                modifier = Modifier.weight(1f),
                                                color = Color(0xFFFFF5F5),
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, Color(0xFFFEB2B2))
                                            ) {
                                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text("Komplain", fontSize = 10.sp, color = Color(0xFFC53030), fontWeight = FontWeight.Bold)
                                                    Text(order.complaintStatus, fontSize = 12.sp, color = Color(0xFFE53E3E), fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        // LOGIKA RATING
                                        if (order.userRating == 0) {
                                            Button(
                                                onClick = {
                                                    selectedOrderId = order.id
                                                    selectedOrderName = order.jastiperName
                                                    ratingValue = 0
                                                    showRatingDialog = true
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003049)),
                                                contentPadding = PaddingValues(vertical = 8.dp)
                                            ) {
                                                Text(
                                                    "Beri Rating",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        } else {
                                            Surface(
                                                modifier = Modifier.weight(1f),
                                                color = Color(0xFFFFFBEB),
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(vertical = 10.dp),
                                                    horizontalArrangement = Arrangement.Center,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                                                    Spacer(Modifier.width(4.dp))
                                                    Text("Rating: ${order.userRating}", fontSize = 13.sp, color = Color(0xFFB45309), fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }

    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = {
                Text(
                    "Beri Rating untuk $selectedOrderName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003049)
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Bagaimana pengalaman Anda dengan Jastiper ini?",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        (1..5).forEach { index ->
                            IconButton(
                                onClick = { ratingValue = index },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index <= ratingValue) Color(0xFFFFB800) else Color(0xFFE2E8F0),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        viewModel.submitRating(selectedOrderId, ratingValue)
                        showRatingDialog = false 
                    },
                    enabled = ratingValue > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003049)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Kirim")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showComplaintDialog) {
        AlertDialog(
            onDismissRequest = { showComplaintDialog = false },
            title = {
                Text(
                    "Ajukan Komplain",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003049)
                )
            },
            text = {
                Column {
                    Text(
                        "Laporkan kendala atau ketidaksesuaian pesanan dari $selectedOrderName.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = complaintText,
                        onValueChange = { complaintText = it },
                        placeholder = { Text("Ceritakan kendala Anda...", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF003049),
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        viewModel.submitComplaint(selectedOrderId, complaintText)
                        showComplaintDialog = false 
                    },
                    enabled = complaintText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD00000)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Kirim Komplain", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showComplaintDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
