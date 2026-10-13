package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JastiperTarikSaldoScreen(
    viewModel: JastipViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val localeId = Locale("id", "ID")
    val formatter = NumberFormat.getCurrencyInstance(localeId).apply {
        maximumFractionDigits = 0
    }

    // Dynamic Balance and input states
    var currentBalance by remember { mutableStateOf(12450000L) }
    var inputAmountStr by remember { mutableStateOf("5000000") }
    var selectedBankIndex by remember { mutableStateOf(0) } // 0 for BCA, 1 for Mandiri
    
    // Banks list
    val banks = remember {
        listOf(
            BankOption("BCA", "Kapten Bahari", "1234567890"),
            BankOption("Mandiri", "Kapten Bahari", "9876543210")
        )
    }

    // Add Recipient Bank Dialog
    var showAddBankDialog by remember { mutableStateOf(false) }
    var newBankName by remember { mutableStateOf("") }
    var newAccountNo by remember { mutableStateOf("") }
    var userBanksList = remember { mutableStateListOf<BankOption>().apply { addAll(banks) } }

    val parsedAmount = inputAmountStr.toLongOrNull() ?: 0L
    val adminFee = if (parsedAmount > 0) 2500L else 0L
    val totalDiterima = if (parsedAmount >= adminFee) parsedAmount - adminFee else 0L

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF5FAFD),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tarik Saldo",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = jastipNavy,
                        letterSpacing = (-0.5).sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = jastipNavy
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { Toast.makeText(context, "Pusat Bantuan Tarik Saldo", Toast.LENGTH_SHORT).show() }) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Bantuan",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Balance Header with gradient for premium modern feel matching guidelines
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.White, Color(0xFFE4F2FE))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = jastipBlueAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "SALDO TERSEDIA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = jastipBlueAccent,
                                letterSpacing = 0.5.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text(
                            text = formatter.format(currentBalance),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = jastipNavy,
                            letterSpacing = (-0.5).sp
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Pendapatan Anda siap dicairkan ke rekening terdaftar.",
                            fontSize = 12.sp,
                            color = jastipTextGray
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Alert Info Block (Informasi Penarikan) styled with info icon
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(BorderStroke(1.dp, Color(0xFFD6E4FF)), RoundedCornerShape(10.dp))
                                .background(Color(0xFFEDF3FF))
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = Color(0xFF2F80ED),
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text = "Informasi Penarikan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2F80ED)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Estimasi waktu proses 1x24 jam hari kerja. Pastikan nomor rekening sudah sesuai untuk menghindari kegagalan transaksi.",
                                    fontSize = 10.sp,
                                    color = jastipNavy.copy(alpha = 0.8f),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                // Main Form Content Areas
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title info Form
                    Column {
                        Text(
                            text = "Formulir Tarik Dana",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = jastipNavy
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Lengkapi detail di bawah untuk mencairkan saldo Anda.",
                            fontSize = 12.sp,
                            color = jastipTextGray
                        )
                    }

                    // 2a. Nominal input
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE4E9F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Nominal Tarik Dana",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = jastipNavy
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Custom Input field presenting structured "Rp" prefix and text
                            OutlinedTextField(
                                value = inputAmountStr,
                                onValueChange = { newValue ->
                                    // Accept only digits
                                    val filtered = newValue.filter { it.isDigit() }
                                    if (filtered.length <= 10) {
                                        inputAmountStr = filtered
                                    }
                                },
                                leadingIcon = {
                                    Text(
                                        text = "Rp",
                                        fontWeight = FontWeight.Bold,
                                        color = jastipNavy,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                                    )
                                },
                                textStyle = LocalTextStyle.current.copy(
                                    color = jastipNavy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                placeholder = { Text("0", fontSize = 18.sp, color = jastipTextGray) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("payout_amount_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF9FBFD),
                                    unfocusedContainerColor = Color(0xFFF9FBFD),
                                    unfocusedBorderColor = Color(0xFFE4E9F0),
                                    focusedBorderColor = jastipBlueAccent
                                ),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "⚠️ Minimal penarikan Rp 50.000",
                                fontSize = 11.sp,
                                color = Color(0xFFE65100),
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Preset Quick Amount Suggestion Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val presets = listOf("1000000", "2000000", "5000000")
                                presets.forEach { p ->
                                    val formattedPreset = formatter.format(p.toLong()).replace("Rp", "").trim()
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(
                                                BorderStroke(
                                                    1.dp,
                                                    if (inputAmountStr == p) jastipBlueAccent else Color(0xFFE4E9F0)
                                                ),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .background(if (inputAmountStr == p) Color(0xFFE4F2FE) else Color.White)
                                            .clickable {
                                                inputAmountStr = p
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = formattedPreset,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (inputAmountStr == p) jastipBlueAccent else jastipNavy
                                        )
                                    }
                                }

                                // Quick All chips
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            BorderStroke(
                                                1.dp,
                                                if (inputAmountStr == currentBalance.toString()) jastipBlueAccent else Color(0xFFE4E9F0)
                                            ),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .background(if (inputAmountStr == currentBalance.toString()) Color(0xFFE4F2FE) else Color.White)
                                        .clickable {
                                            inputAmountStr = currentBalance.toString()
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Semua",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (inputAmountStr == currentBalance.toString()) jastipBlueAccent else jastipNavy
                                    )
                                }
                            }
                        }
                    }

                    // 2b. Rekening Tujuan
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rekening Tujuan",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = jastipNavy
                            )
                            
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { showAddBankDialog = true }
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = jastipBlueAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Tambah Rekening",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = jastipBlueAccent
                                )
                            }
                        }

                        // Render banks beautifully matching design Radio selector
                        userBanksList.forEachIndexed { index, bank ->
                            val isSelected = selectedBankIndex == index
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedBankIndex = index },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) jastipBlueAccent else Color(0xFFE4E9F0)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Bank Avatar
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFE4F2FE)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = bank.name,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = jastipBlueAccent
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = bank.ownerName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = jastipNavy
                                            )
                                            Text(
                                                text = bank.accountNo,
                                                fontSize = 11.sp,
                                                color = jastipTextGray
                                            )
                                        }
                                    }

                                    // Radio Indicator
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                        contentDescription = null,
                                        tint = if (isSelected) jastipBlueAccent else jastipTextGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 3. Rincian Transaksi
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE4E9F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Rincian Transaksi",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = jastipNavy
                            )

                            Divider(color = Color(0xFFF0F4F8))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Nominal Tarik", fontSize = 12.sp, color = jastipTextGray)
                                Text(
                                    text = formatter.format(parsedAmount),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = jastipNavy
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Biaya Admin", fontSize = 12.sp, color = jastipTextGray)
                                Text(
                                    text = formatter.format(adminFee),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = jastipNavy
                                )
                            }

                            Divider(color = Color(0xFFF0F4F8))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Diterima",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = jastipNavy
                                )
                                Text(
                                    text = formatter.format(totalDiterima),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = jastipBlueAccent
                                )
                            }
                        }
                    }

                    // 4. Promo Banner block styled exactly like design
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFB3E5FC))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF003E7E), Color(0xFF006684))
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Pasar Ikan Segar, Keuntungan Terbesar.",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Nikmati kemudahan pencairan dana jastip ikan kapan saja gratis tanpa nunggu lama.",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(
                                    imageVector = Icons.Default.DirectionsBoat,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // 5. Sticky CTA Bottom Panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE4E9F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Penarikan tunduk pada Syarat & Ketentuan Jastiper IkanKu.",
                        fontSize = 11.sp,
                        color = jastipTextGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (parsedAmount < 50000L) {
                                Toast.makeText(context, "Minimal penarikan adalah Rp 50.000", Toast.LENGTH_SHORT).show()
                            } else if (parsedAmount > currentBalance) {
                                Toast.makeText(context, "Saldo tidak mencukupi!", Toast.LENGTH_SHORT).show()
                            } else {
                                currentBalance -= parsedAmount
                                Toast.makeText(
                                    context,
                                    "Permintaan penarikan Rp ${formatter.format(parsedAmount).replace("Rp", "").trim()} sedang diproses!",
                                    Toast.LENGTH_LONG
                                ).show()
                                onBackClick()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = jastipNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("tarik_dana_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.SendToMobile,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Tarik Dana Sekarang",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Recipient Bank Account dialog dialogbox
    if (showAddBankDialog) {
        AlertDialog(
            onDismissRequest = { showAddBankDialog = false },
            title = {
                Text(
                    text = "Tambah Rekening Baru",
                    color = jastipNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Masukkan detail bank penerima pencairan saldo.",
                        fontSize = 12.sp,
                        color = jastipTextGray
                    )

                    OutlinedTextField(
                        value = newBankName,
                        onValueChange = { newBankName = it },
                        label = { Text("Nama Bank (e.g. BNI, BRI, Mandiri)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newAccountNo,
                        onValueChange = { newAccountNo = it },
                        label = { Text("No. Rekening") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = jastipBlueAccent),
                    onClick = {
                        if (newBankName.isNotBlank() && newAccountNo.isNotBlank()) {
                            userBanksList.add(BankOption(newBankName.uppercase(), "Kapten Bahari", newAccountNo))
                            Toast.makeText(context, "Rekening $newBankName berhasil ditambah!", Toast.LENGTH_SHORT).show()
                            showAddBankDialog = false
                            newBankName = ""
                            newAccountNo = ""
                        } else {
                            Toast.makeText(context, "Lengkapi semua isian!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Tambah", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBankDialog = false }) {
                    Text("Batal", color = jastipTextGray)
                }
            }
        )
    }
}

data class BankOption(
    val name: String,
    val ownerName: String,
    val accountNo: String
)
