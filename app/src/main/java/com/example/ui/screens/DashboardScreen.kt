package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel
import java.text.NumberFormat
import java.util.Locale

import com.example.ui.components.GlobalHeader
import com.example.ui.components.JastipLiveCard
import com.example.viewmodel.JastipMitra

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    viewModel: JastipViewModel,
    onNavigateToOrderDetail: (String) -> Unit,
    onNavigateToChat: (String) -> Unit,
    onNavigateToLive: (String) -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    scrollToAll: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Handle Scroll to Top event from Bottom Navigation
    LaunchedEffect(Unit) {
        viewModel.scrollToTopEvent.collect {
            listState.animateScrollToItem(0)
        }
    }

    // Scroll to "Semua Mitra Jastip" if requested
    LaunchedEffect(scrollToAll) {
        if (scrollToAll) {
            kotlinx.coroutines.delay(100)
            listState.scrollToItem(index = 5) // Target index for "Semua Mitra Jastip" header
        }
    }

    // Standard ID Currency Formatter
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    // List of Mitras from ViewModel
    val originalMitras by viewModel.allMitras.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()

    // Interactive search, filter state
    var searchQuery by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("Semua Kota") }
    
    // Bottom Sheet filter state
    var showFilterSheet by remember { mutableStateOf(false) }
    
    // Active applied advanced filters
    var appliedSortBy by remember { mutableStateOf("Paling Relevan") }
    var appliedSpecialties by remember { mutableStateOf(setOf<String>()) }
    var appliedLiveOnly by remember { mutableStateOf(false) }
    var appliedInstantOnly by remember { mutableStateOf(false) }
    var appliedMinFee by remember { mutableStateOf(10000.0) }
    var appliedMaxFee by remember { mutableStateOf(50000.0) }

    // Backup temporary states inside the bottom sheet
    var tempSortBy by remember { mutableStateOf("Paling Relevan") }
    var tempSpecialties by remember { mutableStateOf(setOf<String>()) }
    var tempLiveOnly by remember { mutableStateOf(false) }
    var tempInstantOnly by remember { mutableStateOf(false) }
    var tempMinFee by remember { mutableStateOf(10000.0) }
    var tempMaxFee by remember { mutableStateOf(50000.0) }
    var tempCity by remember { mutableStateOf("Semua Kota") }

    // Filtered & sorted partners list using the interactive Bottom Sheet filters
    val filteredMitrasBySearchAndCity = remember(
        searchQuery, selectedCity, appliedSortBy, appliedSpecialties,
        appliedLiveOnly, appliedInstantOnly, appliedMinFee, appliedMaxFee
    ) {
        var result = originalMitras.filter { partner ->
            // Search criteria
            val matchesSearch = partner.name.contains(searchQuery, ignoreCase = true) ||
                    partner.fishTypes.contains(searchQuery, ignoreCase = true) ||
                    partner.market.contains(searchQuery, ignoreCase = true)
            
            // City filter criteria
            val matchesCity = if (selectedCity == "Semua Kota") {
                true
            } else {
                partner.city.equals(selectedCity, ignoreCase = true)
            }
            
            // Specialization criteria
            val matchesSpecialty = if (appliedSpecialties.isEmpty()) {
                true
            } else {
                appliedSpecialties.any { it in partner.specialties }
            }
            
            // Status operational
            val matchesLive = !appliedLiveOnly || partner.isLive
            val matchesInstant = !appliedInstantOnly || partner.isInstant
            
            // Price range check
            val matchesPrice = partner.baseFee >= appliedMinFee && partner.baseFee <= appliedMaxFee
            
            matchesSearch && matchesCity && matchesSpecialty && matchesLive && matchesInstant && matchesPrice
        }

        // Sorting options
        result = when (appliedSortBy) {
            "Rating Tertinggi" -> result.sortedByDescending { it.rating }
            "Pesanan Terbanyak" -> result.sortedByDescending { it.ordersCompleted }
            "Tarif Jasa Termurah" -> result.sortedBy { it.baseFee }
            "Jarak Terdekat" -> result.sortedBy { it.distanceKm }
            else -> result // "Paling Relevan" / default sequence
        }
        
        result
    }

    // Modal show for Jastiper Guidelines Info Sheet
    var showDaftarDialog by remember { mutableStateOf(false)    }
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GlobalHeader(
                title = "Jastip",
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
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5FAFD))
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            // 2. Interactive Search Bar & Filter Section (Sticky)
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF5FAFD),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari ikan atau jastip...", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.Gray
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear search query")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF006684),
                                unfocusedBorderColor = Color(0xFFCFE6F1).copy(alpha = 0.8f),
                                focusedContainerColor = Color(0xFFEFEFEF),
                                unfocusedContainerColor = Color(0xFFEFEFEF)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("jastip_search_field")
                        )

                        // Interactive Tune-Filter trigger button
                        IconButton(
                            onClick = {
                                tempSortBy = appliedSortBy
                                tempSpecialties = appliedSpecialties
                                tempLiveOnly = appliedLiveOnly
                                tempInstantOnly = appliedInstantOnly
                                tempMinFee = appliedMinFee
                                tempMaxFee = appliedMaxFee
                                tempCity = selectedCity
                                showFilterSheet = true
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (appliedSortBy != "Paling Relevan" || appliedSpecialties.isNotEmpty() || appliedLiveOnly || appliedInstantOnly || appliedMinFee != 10000.0 || appliedMaxFee != 50000.0 || selectedCity != "Semua Kota") {
                                        Color(0xFFBCEDFF)
                                    } else {
                                        Color.White
                                    },
                                    RoundedCornerShape(12.dp)
                                )
                                .testTag("btn_filter_rate")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Opsi Filter",
                                tint = Color(0xFF006684),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // City Chips Removed


            // Jastip Live Now Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Jastip",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF191C1E)
                    )
                    Text(
                        text = "Live Now",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF006684) // Blue
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ikut nimbrung langsung dari pelabuhan — lihat, tawar, dan pesan ikan segar saat masih basah.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            val liveMitras = filteredMitrasBySearchAndCity.filter { it.isLive }
            if (liveMitras.isEmpty()) {
                item {
                    Text(
                        text = "Belum ada mitra yang Live hari ini.",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                        color = Color.Gray
                    )
                }
            } else {
                items(liveMitras, key = { "live_${it.id}" }) { mitra ->
                    JastipLiveCard(
                        mitra = mitra,
                        onNavigateToLive = onNavigateToLive,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            item(key = "semua_mitra_header") {
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Semua Mitra Jastip",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF191C1E)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (filteredMitrasBySearchAndCity.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Mitra Jastip Tidak Ditemukan",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF001F2A)
                        )
                        Text(
                            "Coba ubah filter kota atau parameter pencarian Anda.",
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                items(filteredMitrasBySearchAndCity, key = { it.id }) { partner ->
                    PartnerJastipCardItem(
                        partner = partner,
                        currencyFormatter = currencyFormatter,
                        onPesanClicked = { 
                            // Cek apakah sudah ada pesanan aktif dengan jastiper ini
                            val existingOrder = allOrders.find { it.jastiperName == partner.name }
                            // Navigasi menggunakan ID Pesanan jika ada, jika tidak gunakan ID Jastiper
                            onNavigateToChat(existingOrder?.id ?: partner.id) 
                        }
                    )
                }
            }
        }
    }

    // Modal Sheet Dialog for registering as a Courier (Help Sheet)
    if (showDaftarDialog) {
        AlertDialog(
            onDismissRequest = { showDaftarDialog = false },
            confirmButton = {
                Button(
                    onClick = { showDaftarDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684))
                ) {
                    Text("OK, Saya Paham")
                }
            },
            title = {
                Text(
                    text = "Daftar Jadi Jastiper",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF001F2A)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Jastiper (Jasa Penitipan) adalah mitra resmi IkanKu yang membantu membelikan, menyeleksi, dan mengantarkan seafood segar langsung dari para nelayan atau pasar ikan pasar terdekat ke rumah pembeli.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Divider(color = Color(0xFFCFE6F1).copy(alpha = 0.5f))
                    
                    Text(
                        text = "Keuntungan bergabung:",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF006684)
                    )
                    Row(verticalAlignment = Alignment.Top) {
                        Text("✅ ", fontSize = 12.sp)
                        Text("Dapatkan tarif jastip bersih harian Rp 10.000 - Rp 30.000 per kilo pengiriman.", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.Top) {
                        Text("✅ ", fontSize = 12.sp)
                        Text("Fleksibilitas penuh - Atur hari dan pasar penugasan belanja Anda sendiri.", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.Top) {
                        Text("✅ ", fontSize = 12.sp)
                        Text("Sistem pencairan aman IkanKu Safe Pay cair instan begitu pesanan diterima.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Interactive Bottom Sheet for Advanced Search Filtering
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Pencarian",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF001F2A)
                    )
                    IconButton(onClick = { showFilterSheet = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                // 1. Sorting Parameter (Urutkan Berdasarkan)
                Text(
                    text = "Urutkan Berdasarkan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006684)
                )
                val sortOptions = listOf(
                    "Paling Relevan",
                    "Rating Tertinggi",
                    "Pesanan Terbanyak",
                    "Tarif Jasa Termurah"
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    sortOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { tempSortBy = option }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = tempSortBy == option,
                                onClick = { tempSortBy = option },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF006684))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF001F2A),
                                fontWeight = if (tempSortBy == option) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFCFE6F1).copy(alpha = 0.5f))

                // 2. Location Parameter (Pilihan Lokasi)
                Text(
                    text = "Pilihan Lokasi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006684)
                )
                val locationOptions = listOf("Semua Kota", "Jakarta", "Bandung", "Surabaya", "Bali", "Makassar", "Medan")
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    locationOptions.forEach { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { tempCity = city }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = tempCity == city,
                                onClick = { tempCity = city },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF006684))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = city,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF001F2A),
                                fontWeight = if (tempCity == city) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFCFE6F1).copy(alpha = 0.5f))

                // 2. Speciality Parameter (Spesialisasi Tangkapan)
                Text(
                    text = "Spesialisasi Tangkapan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006684)
                )
                val specialtyOptions = listOf(
                    "Ikan Laut" to "Ikan Laut (Kakap, Kerapu, Tuna)",
                    "Ikan Air Tawar" to "Ikan Air Tawar (Gurame, Lele)",
                    "Kerang & Cumi" to "Kerang & Cumi",
                    "Udang & Kepiting" to "Udang & Kepiting"
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    specialtyOptions.forEach { (key, label) ->
                        val isChecked = key in tempSpecialties
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    tempSpecialties = if (isChecked) {
                                        tempSpecialties - key
                                    } else {
                                        tempSpecialties + key
                                    }
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    tempSpecialties = if (isChecked) {
                                        tempSpecialties - key
                                    } else {
                                        tempSpecialties + key
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF006684))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF001F2A),
                                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFCFE6F1).copy(alpha = 0.5f))

                // 3. Operational status Parameter
                Text(
                    text = "Status Operasional",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006684)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { tempLiveOnly = !tempLiveOnly }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = tempLiveOnly,
                            onCheckedChange = { tempLiveOnly = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF006684))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sedang Live",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF001F2A)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { tempInstantOnly = !tempInstantOnly }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = tempInstantOnly,
                            onCheckedChange = { tempInstantOnly = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF006684))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pesanan Instan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF001F2A)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFCFE6F1).copy(alpha = 0.5f))

                // 4. Rentang Tarif Jasa (Price Range)
                Text(
                    text = "Rentang Tarif Jasa",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006684)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (tempMinFee <= 0.0) "" else tempMinFee.toInt().toString(),
                        onValueChange = {
                            val v = it.toDoubleOrNull() ?: 0.0
                            tempMinFee = v
                        },
                        label = { Text("Min Tarif (Rp)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF006684))
                    )
                    Text("—", fontWeight = FontWeight.Bold, color = Color.Gray)
                    OutlinedTextField(
                        value = if (tempMaxFee <= 0.0) "" else tempMaxFee.toInt().toString(),
                        onValueChange = {
                            val v = it.toDoubleOrNull() ?: 0.0
                            tempMaxFee = v
                        },
                        label = { Text("Maks Tarif (Rp)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF006684))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Decision Action Buttons (Reset, Terapkan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            tempSortBy = "Paling Relevan"
                            tempSpecialties = emptySet()
                            tempLiveOnly = false
                            tempInstantOnly = false
                            tempMinFee = 10000.0
                            tempMaxFee = 50000.0
                            tempCity = "Semua Kota"
                        },
                        border = BorderStroke(1.5.dp, Color(0xFF006684)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF006684)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Reset", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            appliedSortBy = tempSortBy
                            appliedSpecialties = tempSpecialties
                            appliedLiveOnly = tempLiveOnly
                            appliedInstantOnly = tempInstantOnly
                            appliedMinFee = tempMinFee
                            appliedMaxFee = tempMaxFee
                            selectedCity = tempCity
                            showFilterSheet = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Terapkan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PartnerJastipCardItem(
    partner: JastipMitra,
    currencyFormatter: NumberFormat,
    onPesanClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("partner_card_${partner.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)) // Changed to match JastipTerdekatCard
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(partner.initialBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = partner.initial,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF006684),
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = partner.name,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = Color(0xFF191C1E)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Lokasi",
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${partner.city} \u2014 ${partner.market}",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (partner.isLive) {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = "LIVE",
                                fontSize = 9.sp,
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFF8F00), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = partner.rating.toString(),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF006684),
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = partner.fishTypes,
                color = Color.DarkGray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEFEFEF))
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MULAI DARI",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currencyFormatter.format(partner.baseFee),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF191C1E),
                        fontSize = 16.sp
                    )
                }
                Button(
                    onClick = onPesanClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003B5B)), // Changed to match JastipTerdekatCard
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp).testTag("btn_pesan_partner_${partner.id}")
                ) {
                    Text("Pesan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun JastipCategoryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFAFDFE),
        border = BorderStroke(1.dp, Color(0xFFCFE6F1))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE3F2FD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF006684),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191C1E),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun JastipTerdekatCard(
    mitra: JastipMitra,
    currencyFormatter: NumberFormat,
    onPesanClicked: () -> Unit
) {
    Card(
        modifier = Modifier.width(280.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(mitra.initialBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mitra.initial,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF006684),
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = mitra.name,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = Color(0xFF191C1E)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Lokasi",
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${mitra.city} \u2014 ${mitra.market}",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFF8F00), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = mitra.rating.toString(),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF006684),
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = mitra.fishTypes,
                color = Color.DarkGray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEFEFEF))
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MULAI DARI",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currencyFormatter.format(mitra.baseFee),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF191C1E),
                        fontSize = 16.sp
                    )
                }
                Button(
                    onClick = onPesanClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003B5B)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Pesan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
