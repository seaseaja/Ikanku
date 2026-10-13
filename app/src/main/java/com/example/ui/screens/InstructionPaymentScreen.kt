package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructionPaymentScreen(
    method: String,
    amount: String,
    onBackClick: () -> Unit,
    onCheckStatusClick: () -> Unit
) {
    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
    val formattedAmount = NumberFormat.getCurrencyInstance(Locale("id", "ID")).run {
        maximumFractionDigits = 0
        format(parsedAmount)
    }.replace("Rp", "Rp ")

    val methodType = when {
        method.contains("Transfer") -> "VA"
        method in listOf("Alfamart", "Indomaret") -> "RETAIL"
        else -> "EWALLET"
    }

    val iconText = when (method) {
        "BCA Transfer" -> "BCA"
        "Mandiri Transfer" -> "M"
        "BNI Transfer" -> "BNI"
        "GoPay" -> "G"
        "OVO" -> "OVO"
        "Dana" -> "D"
        "Alfamart" -> "A"
        "Indomaret" -> "I"
        else -> ""
    }

    val iconColor = when (method) {
        "BCA Transfer" -> Color(0xFF0066AE)
        "Mandiri Transfer" -> Color(0xFF0B5591)
        "BNI Transfer" -> Color(0xFF005E6A)
        "GoPay" -> Color(0xFF00AED6)
        "OVO" -> Color(0xFF4C3494)
        "Dana" -> Color(0xFF118EE9)
        "Alfamart" -> Color(0xFFE2202C)
        "Indomaret" -> Color(0xFF0072C6)
        else -> Color.Gray
    }

    val accountName = when (method) {
        "BCA Transfer" -> "BCA Virtual Account"
        "Mandiri Transfer" -> "Mandiri Virtual Account"
        "BNI Transfer" -> "BNI Virtual Account"
        "GoPay" -> "GoPay"
        "OVO" -> "OVO"
        "Dana" -> "Dana"
        "Alfamart" -> "Alfamart Payment Code"
        "Indomaret" -> "Indomaret Payment Code"
        else -> method
    }

    val accountNo = when (method) {
        "BCA Transfer" -> "1234 5678 \n9012"
        "Mandiri Transfer" -> "8950 8000 \n1234"
        "BNI Transfer" -> "8273 6451 \n9081"
        "GoPay" -> "GP-9238472301"
        "OVO" -> "OV-8347293847"
        "Dana" -> "DN-9847583495"
        "Alfamart" -> "8347598237"
        "Indomaret" -> "3948572019"
        else -> "1234567890"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF7FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Instruksi Pembayaran", fontWeight = FontWeight.Bold, color = Color(0xFF003049), fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFF003049)) }
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Outlined.HelpOutline, contentDescription = "Help", tint = Color.Gray) }
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Batas Waktu Pembayaran", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Text("23 : 59 : 59", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0066AE), letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            Text("Jatuh tempo pada besok, 14:30 WIB", fontSize = 14.sp, color = Color.Gray)

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Total Bayar", fontSize = 13.sp, color = Color.Gray)
                        Spacer(Modifier.height(4.dp))
                        Text(formattedAmount, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF003049))
                    }
                    Text("DETAIL ⌄", fontSize = 12.sp, color = Color(0xFF0066AE), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (methodType == "VA" || methodType == "RETAIL") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.background(iconColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text(iconText, color = iconColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(accountName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        Spacer(Modifier.height(16.dp))

                        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp)).border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp)).padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(if (methodType == "VA") "Nomor Virtual Account" else "Kode Pembayaran", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(4.dp))
                                    Text(accountNo, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, letterSpacing = 2.sp, lineHeight = 28.sp)
                                }
                                Button(
                                    onClick = { },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003B73)),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Salin", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Penyedia Layanan", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(4.dp))
                                Text(accountName, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = iconColor)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(8.dp)) {
                                Box(modifier = Modifier.size(24.dp).background(iconColor, CircleShape), contentAlignment = Alignment.Center) {
                                    Text(iconText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(accountName.uppercase(), fontSize = 10.sp, color = iconColor, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("ID Transaksi", fontSize = 14.sp, color = Color.Gray)
                            Text(accountNo, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onCheckStatusClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = iconColor)
                ) {
                    Text("⚡ Bayar dengan $accountName", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                
                Spacer(Modifier.height(16.dp))
                
                Text("— atau —", fontSize = 14.sp, color = Color.Gray)
                
                Spacer(Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.wrapContentWidth().height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF003B73))
                ) {
                    Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("TAMPILKAN QR CODE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Cara Membayar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(Modifier.height(16.dp))
                
                if (methodType == "VA") {
                    InstructionStepBank(1, "Buka aplikasi m-banking atau internet banking Anda")
                    InstructionStepBank(2, "Pilih menu Transfer Virtual Account")
                    InstructionStepBank(3, "Masukkan nomor VA ${accountNo.replace("\n", "")}")
                    InstructionStepBank(4, "Periksa detail transaksi dan Konfirmasi pembayaran")
                } else if (methodType == "RETAIL") {
                    InstructionStepBank(1, "Kunjungi gerai $accountName terdekat")
                    InstructionStepBank(2, "Tunjukkan kode pembayaran ke kasir")
                    InstructionStepBank(3, "Lakukan pembayaran senilai $formattedAmount")
                    InstructionStepBank(4, "Simpan struk sebagai bukti pembayaran")
                } else {
                    InstructionStepEwallet(1, "Klik tombol 'Bayar dengan $accountName'", "Pastikan aplikasi $accountName sudah terinstall di ponsel Anda.")
                    InstructionStepEwallet(2, "Otomatis dialihkan ke $accountName", "Aplikasi akan terbuka secara otomatis untuk melanjutkan transaksi.")
                    InstructionStepEwallet(3, "Periksa rincian pembayaran", "Pastikan nama merchant dan jumlah pembayaran ($formattedAmount) sudah benar.")
                    InstructionStepEwallet(4, "Konfirmasi & Masukkan PIN", "Masukkan PIN $accountName Anda untuk menyelesaikan.")
                }
            }

            if (methodType == "EWALLET") {
                Spacer(Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)).padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Outlined.Shield, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Pembayaran Anda dilindungi. Jangan pernah memberikan OTP atau PIN Anda kepada siapapun.", fontSize = 12.sp, color = Color.DarkGray, lineHeight = 18.sp)
                }
            }

            Spacer(Modifier.height(32.dp))
            
            if (methodType != "EWALLET") {
                Button(
                    onClick = onCheckStatusClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C5282))
                ) {
                    Text("Cek Status Pembayaran", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }
                
                Spacer(Modifier.height(12.dp))
            }
            
            Button(
                onClick = { onBackClick() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color(0xFF4A5568))
            ) {
                Text("Ganti Metode Pembayaran", fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun InstructionStepBank(step: Int, title: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(24.dp).background(Color(0xFF2C5282), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$step", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(16.dp))
        Text(title, fontSize = 15.sp, color = Color.DarkGray, lineHeight = 22.sp, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
fun InstructionStepEwallet(step: Int, title: String, description: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(24.dp).background(Color(0xFF003049), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$step", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(4.dp))
            Text(description, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
        }
    }
}
