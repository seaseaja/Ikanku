package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel
import android.app.DatePickerDialog
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JastiperPendapatanScreen(
    viewModel: JastipViewModel,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    onNavigateToTarikSaldo: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val localeId = Locale("id", "ID")
    val currencyFormatter = NumberFormat.getCurrencyInstance(localeId).apply {
        maximumFractionDigits = 0
    }

    var showDownloadBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var selectedRangeOption by remember { mutableStateOf("7 Hari Terakhir") } // "7 Hari Terakhir", "Bulan Ini", "Bulan Lalu", "Kustom"
    var customStartDate by remember { mutableStateOf("23 Mei 2026") }
    var customEndDate by remember { mutableStateOf("30 Mei 2026") }

    fun getMonthName(m: Int): String {
        return when (m) {
            0 -> "Jan"
            1 -> "Feb"
            2 -> "Mar"
            3 -> "Apr"
            4 -> "Mei"
            5 -> "Jun"
            6 -> "Jul"
            7 -> "Agu"
            8 -> "Sep"
            9 -> "Okt"
            10 -> "Nov"
            11 -> "Des"
            else -> ""
        }
    }

    val transactions = remember {
        listOf(
            JastipTransaction("#IK-99283", "Jastip Live: TPI Brondong", "Komisi Pelayanan", "12 Okt 2023", 150000L, true, "BERHASIL"),
            JastipTransaction("#IK-99275", "Ikan Kerapu Sunu (2kg)", "Penjualan Produk", "11 Okt 2023", 480000L, true, "BERHASIL"),
            JastipTransaction("#WD-00412", "Penarikan Saldo (Bank BCA)", "Penarikan Dana", "10 Okt 2023", 2000000L, false, "DIPROSES"),
            JastipTransaction("#IK-99120", "Udang Vaname Super", "Penjualan Produk", "09 Okt 2023", 1200000L, true, "BERHASIL")
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF5FAFD),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Dashboard Pendapatan",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = jastipNavy,
                        letterSpacing = (-0.5).sp
                    )
                },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Kembali",
                                tint = jastipNavy
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showDownloadBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Download,
                            contentDescription = "Unduh Laporan",
                            tint = jastipNavy
                        )
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
            // Header Title block matching description
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Dashboard Pendapatan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = jastipNavy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Pantau performa penjualan dan kelola saldo jastip Anda.",
                        fontSize = 12.sp,
                        color = jastipTextGray
                    )
                }
            }

            // Area Metrik side-by-side (50:50 ratio) Card
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Column Total Penjualan Ikan card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F5B9B))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Total Penjualan Ikan",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rp 8.240.000",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "📈 +12% dari minggu lalu",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF81C784)
                            )
                        }
                    }

                    // Right Column Komisi Jastip card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE4E9F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Komisi Jastip (Biaya Layanan)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = jastipTextGray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rp 4.210.000",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = jastipNavy
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = jastipBlueAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "42 Jasa Titip Berhasil",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = jastipTextGray
                                )
                            }
                        }
                    }
                }
            }

            // Tarik Saldo Main Thumb CTA Button
            item {
                Button(
                    onClick = {
                        if (onNavigateToTarikSaldo != null) {
                            onNavigateToTarikSaldo()
                        } else {
                            Toast.makeText(context, "Menuju Penarikan Saldo...", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = jastipNavy),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tarik Saldo",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Tren Pendapatan Mingguan Chart Component
            item {
                WeeklyTrendChart()
            }

            // Promotions & Quick Info Carousel Horizontal Row
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Info & Panduan Jastip",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = jastipNavy
                    )
                    PromoCarouselSection()
                }
            }

            // Riwayat Transaksi List Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Transaksi",
                        fontWeight = FontWeight.Bold,
                        color = jastipNavy,
                        fontSize = 15.sp
                    )
                    
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { showDownloadBottomSheet = true }
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Download,
                            contentDescription = null,
                            tint = jastipBlueAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Unduh Laporan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = jastipBlueAccent
                        )
                    }
                }
            }

            // Cards transaction items representation matching table
            items(transactions) { trx ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE4E9F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sisi Kiri: Title, ID & date info
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = trx.productName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = jastipNavy
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = trx.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = jastipTextGray
                                )
                                Box(
                                    modifier = Modifier
                                        .size(3.dp)
                                        .background(Color.LightGray, CircleShape)
                                )
                                Text(
                                    text = "${trx.id} • ${trx.date}",
                                    fontSize = 10.sp,
                                    color = jastipTextGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Sisi Kanan: Nominal and status badge
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "${if (trx.isCredit) "" else "- "}${currencyFormatter.format(trx.amount).replace("Rp", "Rp ")}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (trx.isCredit) jastipNavy else Color(0xFFD32F2F)
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Badge status
                            val isSelesai = trx.status == "BERHASIL"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelesai) Color(0xFFE8F5E9) else Color(0xFFE3F2FD))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = trx.status,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelesai) jastipGreenAccent else Color(0xFF1565C0)
                                )
                            }
                        }
                    }
                }
            }

            // Lihat Semua Transaksi link
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Lihat Semua Transaksi",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = jastipBlueAccent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { Toast.makeText(context, "Membuka riwayat lengkap...", Toast.LENGTH_SHORT).show() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }

    if (showDownloadBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showDownloadBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, bottom = 24.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title & Close Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Unduh Lapor Pendapatan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = jastipNavy
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Laporan transaksi akan dirangkum sesuai rentang waktu.",
                            fontSize = 12.sp,
                            color = jastipTextGray
                        )
                    }
                    IconButton(
                        onClick = { showDownloadBottomSheet = false }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = jastipTextGray
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFECEFF1))
                )

                // Quick Chips (Tombol Pintasan) Title
                Text(
                    text = "Tombol Pintasan Rentang Waktu",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = jastipNavy
                )

                // Quick Chips Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickOptions = listOf(
                        "7 Hari Terakhir" to "7 Hari",
                        "Bulan Ini" to "Bulan Ini",
                        "Bulan Lalu" to "Bulan Lalu"
                    )
                    quickOptions.forEach { (value, label) ->
                        val isSelected = selectedRangeOption == value
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color(0xFFE4F2FE) else Color(0xFFF8FAFC))
                                .border(
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) jastipBlueAccent else Color(0xFFECEFF1)
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    selectedRangeOption = value
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) jastipBlueAccent else jastipTextGray
                            )
                        }
                    }
                }

                // Custom Range choice or Trigger
                Spacer(modifier = Modifier.height(4.dp))
                
                val isKustom = selectedRangeOption == "Kustom"
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            selectedRangeOption = "Kustom"
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = isKustom,
                        onClick = { selectedRangeOption = "Kustom" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = jastipBlueAccent,
                            unselectedColor = jastipTextGray
                        )
                    )
                    Column {
                        Text(
                            text = "Pilih Tanggal Sendiri...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isKustom) jastipBlueAccent else jastipNavy
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Kustomisasi rentang tanggal secara presisi",
                            fontSize = 11.sp,
                            color = jastipTextGray
                        )
                    }
                }

                // Date selecting section
                if (isKustom) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Start Date Card
                        val calendar = Calendar.getInstance()
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val datePickerDialog = DatePickerDialog(
                                        context,
                                        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                                            customStartDate = "$selectedDayOfMonth ${getMonthName(selectedMonth)} $selectedYear"
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    )
                                    datePickerDialog.show()
                                },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = BorderStroke(1.dp, Color(0xFFE4E9F0))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Tanggal Mulai",
                                    fontSize = 10.sp,
                                    color = jastipTextGray,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = jastipBlueAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = customStartDate,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = jastipNavy
                                    )
                                }
                            }
                        }

                        // End Date Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val datePickerDialog = DatePickerDialog(
                                        context,
                                        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                                            customEndDate = "$selectedDayOfMonth ${getMonthName(selectedMonth)} $selectedYear"
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    )
                                    datePickerDialog.show()
                                },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = BorderStroke(1.dp, Color(0xFFE4E9F0))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Tanggal Selesai",
                                    fontSize = 10.sp,
                                    color = jastipTextGray,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = jastipBlueAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = customEndDate,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = jastipNavy
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Final Solid Action Button on very bottom
                Button(
                    onClick = {
                        val periodText = if (selectedRangeOption == "Kustom") {
                            "$customStartDate - $customEndDate"
                        } else {
                            selectedRangeOption
                        }
                        Toast.makeText(
                            context,
                            "Berhasil mengunduh laporan periode: $periodText",
                            Toast.LENGTH_LONG
                        ).show()
                        showDownloadBottomSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = jastipBlueAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Unduh File (.xlsx)",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyTrendChart() {
    var expandedFilter by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("7 Hari Terakhir") }
    val filters = listOf("7 Hari Terakhir", "30 Hari Terakhir", "Bulan Ini")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE4E9F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title and Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tren Pendapatan Mingguan",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = jastipNavy
                )
                
                // Dropdown Filter Pill
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .border(BorderStroke(1.dp, Color(0xFFE4E9F0)), RoundedCornerShape(20.dp))
                            .background(Color(0xFFF8FAFC))
                            .clickable { expandedFilter = true }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = selectedFilter,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = jastipBlueAccent
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = jastipBlueAccent,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expandedFilter,
                        onDismissRequest = { expandedFilter = false }
                    ) {
                        filters.forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter, fontSize = 12.sp, color = jastipNavy) },
                                onClick = {
                                    selectedFilter = filter
                                    expandedFilter = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val yLabels = listOf("Rp 3.5jt", "Rp 3jt", "Rp 2.5jt", "Rp 2jt", "Rp 1.5jt", "Rp 1jt", "Rp 0.5jt", "Rp 0jt")
            val points = listOf(0.35f, 0.55f, 0.32f, 0.72f, 0.65f, 0.95f, 0.82f) // relative heights representing Sen - Min
            val days = listOf("Sen", "Sel", "Rb", "Km", "Jm", "Sab", "Min")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Y-Axis labels
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(48.dp)
                        .padding(bottom = 24.dp), // align with coordinates canvas area
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    yLabels.forEach { label ->
                        Text(
                            text = label,
                            fontSize = 9.sp,
                            color = jastipTextGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Canvas area for precise Bezier graph line
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 24.dp)
                    ) {
                        val width = size.width
                        val height = size.height

                        // 1. Horizontal faint reference lines
                        val numGridlines = yLabels.size
                        for (i in 0 until numGridlines) {
                            val y = height * i / (numGridlines - 1)
                            drawLine(
                                color = Color(0xFFF1F5F9),
                                start = androidx.compose.ui.geometry.Offset(0f, y),
                                end = androidx.compose.ui.geometry.Offset(width, y),
                                strokeWidth = 1f
                            )
                        }

                        // 2. Draw smooth curved Bezier trend path
                        if (points.isNotEmpty()) {
                            val spacing = width / (points.size - 1)

                            val path = Path()
                            val fillPath = Path()

                            val coordinates = points.mapIndexed { index, factor ->
                                androidx.compose.ui.geometry.Offset(
                                    x = index * spacing,
                                    y = height * (1f - factor * 0.8f - 0.1f) // slight padding from top edge
                                )
                            }

                            // Begin path
                            path.moveTo(coordinates[0].x, coordinates[0].y)
                            fillPath.moveTo(0f, height)
                            fillPath.lineTo(coordinates[0].x, coordinates[0].y)

                            for (i in 1 until coordinates.size) {
                                val p0 = coordinates[i - 1]
                                val p1 = coordinates[i]
                                val controlPointX1 = p0.x + (p1.x - p0.x) / 2
                                val controlPointY1 = p0.y
                                val controlPointX2 = p0.x + (p1.x - p0.x) / 2
                                val controlPointY2 = p1.y

                                path.cubicTo(
                                    controlPointX1, controlPointY1,
                                    controlPointX2, controlPointY2,
                                    p1.x, p1.y
                                )
                                fillPath.cubicTo(
                                    controlPointX1, controlPointY1,
                                    controlPointX2, controlPointY2,
                                    p1.x, p1.y
                                )
                            }

                            fillPath.lineTo(width, height)
                            fillPath.lineTo(0f, height)
                            fillPath.close()

                            // Linear gradient background fills below line graph matching screenshot
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF0F5B9B).copy(alpha = 0.3f),
                                        Color(0xFF0F5B9B).copy(alpha = 0.0f)
                                    )
                                )
                            )

                            // Stroke trend line path
                            drawPath(
                                path = path,
                                color = Color(0xFF0F5B9B),
                                style = Stroke(
                                    width = 6f,
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                                )
                            )

                            // Outer of graph points
                            coordinates.forEach { pt ->
                                drawCircle(
                                    color = Color(0xFF0F5B9B),
                                    radius = 10f,
                                    center = pt
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 5f,
                                    center = pt
                                )
                            }
                        }
                    }

                    // 3. Draw X-axis labels (days of weekly trend)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        days.forEachIndexed { idx, day ->
                            val isSunday = idx == 6
                            Text(
                                text = day,
                                fontSize = 10.sp,
                                color = if (isSunday) Color(0xFFD32F2F) else jastipTextGray,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PromoCarouselSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Card 1: Jastip Live Sekarang
        Card(
            modifier = Modifier
                .width(260.dp)
                .height(160.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD32F2F))
                        )
                        Text(
                            text = "Jastip Live Sekarang",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF003E7E), Color(0xFF001A33))
                            )
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBoat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "LOKASI:",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = "TPI Muara Angke",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Penonton: 1,240",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = jastipTextGray
                    )
                    Text(
                        text = "Margin: 15%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = jastipGreenAccent
                    )
                }
            }
        }

        // Card 2: Tips Cuan Jastiper
        Card(
            modifier = Modifier
                .width(260.dp)
                .height(160.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFB3E5FC)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5FAFD))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TipsAndUpdates,
                            contentDescription = null,
                            tint = jastipBlueAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Tips Cuan Jastiper",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = jastipBlueAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "\"Gunakan fitur Siaran Langsung untuk menunjukkan kesegaran ikan secara langsung. Kualitas Live HD memiliki konversi 40% lebih tinggi.\"",
                        fontSize = 10.sp,
                        color = jastipNavy,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 14.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clickable { /* logic */ }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Pelajari Strategi",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = jastipBlueAccent
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = jastipBlueAccent,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

data class JastipTransaction(
    val id: String,
    val productName: String,
    val category: String,
    val date: String,
    val amount: Long,
    val isCredit: Boolean,
    val status: String
)
