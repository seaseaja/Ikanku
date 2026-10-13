package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IsiSaldoScreen(
    viewModel: com.example.viewmodel.JastipViewModel,
    onBackClick: () -> Unit,
    onNavigateToInstruction: (String, String, String) -> Unit
) {
    var topUpAmount by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("") }
    var selectedNominal by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showQrisDialog by remember { mutableStateOf(false) }
    var qrisTransactionId by remember { mutableStateOf("") }
    
    val minTopUp = 10000
    val currencyFormatter = remember {
        java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }
    
    // Format input dengan titik pemisah ribuan
    val formattedAmount = remember(topUpAmount) {
        if (topUpAmount.isBlank()) "" else {
            try {
                val cleanAmount = topUpAmount.replace(".", "").replace(",", "")
                if (cleanAmount.isNotEmpty()) {
                    currencyFormatter.format(cleanAmount.toLong())
                } else ""
            } catch (e: Exception) {
                topUpAmount
            }
        }
    }
    
    // Validasi nominal
    val isValidAmount = remember(topUpAmount) {
        try {
            val cleanAmount = topUpAmount.replace(".", "").replace(",", "")
            cleanAmount.isNotEmpty() && cleanAmount.toLong() >= minTopUp
        } catch (e: Exception) {
            false
        }
    }
    
    // Update error state
    LaunchedEffect(topUpAmount) {
        if (topUpAmount.isNotBlank()) {
            try {
                val cleanAmount = topUpAmount.replace(".", "").replace(",", "")
                showError = cleanAmount.toLong() < minTopUp
            } catch (e: Exception) {
                showError = true
            }
        } else {
            showError = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF7FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Isi Saldo", fontWeight = FontWeight.Bold, color = Color(0xFF003049), fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFF003049)) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                modifier = Modifier.background(Color.White)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { 
                        if (selectedMethod.isNotBlank() && isValidAmount) {
                            val cleanAmount = topUpAmount.replace(".", "").replace(",", "")
                            val amountDouble = cleanAmount.toDouble()
                            
                            // Jika metode QRIS, tampilkan dialog QRIS
                            if (selectedMethod == "QRIS") {
                                viewModel.createTopUpRequest(selectedMethod, amountDouble) { transactionId ->
                                    qrisTransactionId = transactionId
                                    showQrisDialog = true
                                }
                            } else {
                                // Metode lain, navigasi ke instruksi
                                viewModel.createTopUpRequest(selectedMethod, amountDouble) { transactionId ->
                                    onNavigateToInstruction(selectedMethod, topUpAmount, transactionId)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = selectedMethod.isNotBlank() && isValidAmount,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF003B73),
                        disabledContainerColor = Color(0xFFCBD5E1)
                    )
                ) {
                    Text(
                        "Lanjut ke Pembayaran", 
                        fontWeight = FontWeight.SemiBold, 
                        fontSize = 16.sp, 
                        color = if (selectedMethod.isNotBlank() && isValidAmount) Color.White else Color.Gray
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Nominal Top Up Section
            Column {
                Text(
                    text = "NOMINAL TOP UP",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = formattedAmount,
                    onValueChange = { newValue ->
                        // Hanya terima angka, hapus semua karakter non-digit
                        val cleanValue = newValue.replace(".", "").replace(",", "").filter { it.isDigit() }
                        topUpAmount = cleanValue
                        selectedNominal = "" // Reset selected nominal saat user mengetik manual
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("0", color = Color.LightGray, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = {
                        Text(
                            text = "Rp",
                            color = Color(0xFF003049),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = if (showError) Color(0xFFD32F2F) else Color(0xFFCBD5E1),
                        unfocusedBorderColor = if (showError) Color(0xFFD32F2F) else Color(0xFFCBD5E1),
                        errorBorderColor = Color(0xFFD32F2F)
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                    singleLine = true,
                    isError = showError
                )
                
                // Error message dan info minimum
                if (showError) {
                    Text(
                        text = "Nominal minimal top-up adalah Rp ${currencyFormatter.format(minTopUp)}",
                        color = Color(0xFFD32F2F),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                } else {
                    Text(
                        text = "Minimal top-up Rp ${currencyFormatter.format(minTopUp)}",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NominalButton(
                        label = "Rp 50.000", 
                        isSelected = selectedNominal == "50000",
                        modifier = Modifier.weight(1f)
                    ) { 
                        topUpAmount = "50000"
                        selectedNominal = "50000"
                    }
                    NominalButton(
                        label = "Rp 100.000", 
                        isSelected = selectedNominal == "100000",
                        modifier = Modifier.weight(1.3f)
                    ) { 
                        topUpAmount = "100000"
                        selectedNominal = "100000"
                    }
                    Spacer(modifier = Modifier.weight(1f)) // Padding on right
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NominalButton(
                        label = "Rp 200.000", 
                        isSelected = selectedNominal == "200000",
                        modifier = Modifier.weight(1.3f)
                    ) { 
                        topUpAmount = "200000"
                        selectedNominal = "200000"
                    }
                    NominalButton(
                        label = "Rp 500.000", 
                        isSelected = selectedNominal == "500000",
                        modifier = Modifier.weight(1.3f)
                    ) { 
                        topUpAmount = "500000"
                        selectedNominal = "500000"
                    }
                    Spacer(modifier = Modifier.weight(0.7f))
                }
            }

            // Transfer Bank Section
            Column {
                Text(
                    text = "TRANSFER BANK",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column {
                        PaymentMethodItem(title = "BCA Transfer", iconPlaceholder = Color(0xFF0066AE), iconText = "BCA", isSelected = selectedMethod == "BCA Transfer") { selectedMethod = "BCA Transfer" }
                        HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 16.dp))
                        PaymentMethodItem(title = "Mandiri Transfer", iconPlaceholder = Color(0xFF0B5591), iconText = "M", isSelected = selectedMethod == "Mandiri Transfer") { selectedMethod = "Mandiri Transfer" }
                        HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 16.dp))
                        PaymentMethodItem(title = "BNI Transfer", iconPlaceholder = Color(0xFF005E6A), iconText = "BNI", isSelected = selectedMethod == "BNI Transfer") { selectedMethod = "BNI Transfer" }
                    }
                }
            }

            // E-Wallet Section
            Column {
                Text(
                    text = "E-WALLET",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column {
                        PaymentMethodItem(title = "GoPay", iconPlaceholder = Color(0xFF00AED6), iconText = "G", isSelected = selectedMethod == "GoPay") { selectedMethod = "GoPay" }
                        HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 16.dp))
                        PaymentMethodItem(title = "OVO", iconPlaceholder = Color(0xFF4C3494), iconText = "OVO", isSelected = selectedMethod == "OVO") { selectedMethod = "OVO" }
                        HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 16.dp))
                        PaymentMethodItem(title = "Dana", iconPlaceholder = Color(0xFF118EE9), iconText = "D", isSelected = selectedMethod == "Dana") { selectedMethod = "Dana" }
                    }
                }
            }

            // Gerai Retail Section
            Column {
                Text(
                    text = "GERAI RETAIL",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column {
                        PaymentMethodItem(title = "Alfamart", iconPlaceholder = Color(0xFFE2202C), iconText = "A", isSelected = selectedMethod == "Alfamart") { selectedMethod = "Alfamart" }
                        HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 16.dp))
                        PaymentMethodItem(title = "Indomaret", iconPlaceholder = Color(0xFF0072C6), iconText = "I", isSelected = selectedMethod == "Indomaret") { selectedMethod = "Indomaret" }
                    }
                }
            }

            // QRIS Section
            Column {
                Text(
                    text = "QRIS",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    PaymentMethodItem(
                        title = "QRIS (Semua E-Wallet & Bank)", 
                        iconPlaceholder = Color(0xFF1A1A1A), 
                        iconText = "QR", 
                        isSelected = selectedMethod == "QRIS"
                    ) { 
                        selectedMethod = "QRIS" 
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // QRIS Payment Dialog
        if (showQrisDialog) {
            QRISPaymentDialog(
                amount = topUpAmount,
                transactionId = qrisTransactionId,
                onDismiss = { showQrisDialog = false },
                onPaymentComplete = {
                    showQrisDialog = false
                    val cleanAmount = topUpAmount.replace(".", "").replace(",", "")
                    viewModel.confirmTopUp(qrisTransactionId, cleanAmount.toDouble())
                    onBackClick()
                }
            )
        }
    }
}

@Composable
fun NominalButton(label: String, isSelected: Boolean = false, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = if (isSelected) Color(0xFF003B73) else Color(0xFFF1F5F9),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PaymentMethodItem(title: String, iconPlaceholder: Color, iconText: String, isSelected: Boolean = false, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(if (isSelected) Color(0xFFF0F7FF) else Color.Transparent)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mock icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconPlaceholder, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
             Text(text = iconText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF003049))
        )
    }
}


@Composable
fun QRISPaymentDialog(
    amount: String,
    transactionId: String,
    onDismiss: () -> Unit,
    onPaymentComplete: () -> Unit
) {
    var timeRemaining by remember { mutableStateOf(300) } // 5 minutes = 300 seconds
    
    // Countdown timer
    LaunchedEffect(Unit) {
        while (timeRemaining > 0) {
            kotlinx.coroutines.delay(1000)
            timeRemaining--
        }
        // Auto dismiss when time runs out
        onDismiss()
    }
    
    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)
    
    val currencyFormatter = remember {
        java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Close button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF1A1A1A),
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // QR Code Container
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Header with QRIS logo and fish icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // QRIS Logo
                                Column {
                                    Text(
                                        text = "QRIS",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF1A1A1A),
                                        letterSpacing = 2.sp
                                    )
                                    Text(
                                        text = "QR Standar",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Text(
                                        text = "Pembayaran Nasional",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFF1A1A1A)
                                    )
                                }
                                
                                // Fish icon placeholder
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4DB6AC)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "🐟",
                                        fontSize = 32.sp
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            // Instruction text
                            Text(
                                text = "Scan Kode QRIS ini untuk membayar",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF666666),
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // QR Code placeholder
                            Box(
                                modifier = Modifier
                                    .size(280.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(
                                        2.dp, 
                                        Color(0xFFE0E0E0), 
                                        RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                // Mock QR Code pattern
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Simple QR-like pattern
                                    repeat(7) { row ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            repeat(7) { col ->
                                                val isCorner = (row < 2 && col < 2) || 
                                                              (row < 2 && col > 4) || 
                                                              (row > 4 && col < 2)
                                                Box(
                                                    modifier = Modifier
                                                        .size(if (isCorner) 32.dp else 24.dp)
                                                        .padding(2.dp)
                                                        .background(
                                                            if (isCorner || (row + col) % 2 == 0) 
                                                                Color(0xFF1A1A1A) 
                                                            else 
                                                                Color.Transparent,
                                                            RoundedCornerShape(if (isCorner) 4.dp else 2.dp)
                                                        )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Expiry timer
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "QR Kedaluwarsa dalam ",
                                    fontSize = 13.sp,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    text = timeString,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (timeRemaining < 60) Color(0xFFD32F2F) else Color(0xFF1A1A1A)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Amount info
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Pembayaran",
                            fontSize = 13.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp ${currencyFormatter.format(amount.replace(".", "").replace(",", "").toLongOrNull() ?: 0)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Instructions
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Cara Pembayaran:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "1. Buka aplikasi e-wallet atau mobile banking\n" +
                                      "2. Pilih menu Scan QR atau QRIS\n" +
                                      "3. Scan kode QR di atas\n" +
                                      "4. Konfirmasi pembayaran",
                                fontSize = 12.sp,
                                color = Color(0xFF1976D2),
                                lineHeight = 18.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Simulate payment button (for testing)
                    Button(
                        onClick = onPaymentComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Simulasi Pembayaran Berhasil",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
