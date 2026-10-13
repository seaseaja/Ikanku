package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserAddress
import com.example.viewmodel.JastipViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahAlamatScreen(
    viewModel: JastipViewModel,
    onBackClick: () -> Unit
) {
    var label by remember { mutableStateOf("Rumah") }
    var receiverName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var cityKecamatan by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var fullAddress by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF7FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tambah Alamat Baru",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF7FAFC)
                )
            )
        },
        bottomBar = {
            Surface(color = Color(0xFFF7FAFC)) {
                Button(
                    onClick = {
                        if (receiverName.isNotBlank() && phoneNumber.isNotBlank() && fullAddress.isNotBlank()) {
                            viewModel.addAddress(
                                UserAddress(
                                    label = label,
                                    receiverName = receiverName,
                                    phoneNumber = phoneNumber,
                                    fullAddress = if (cityKecamatan.isNotBlank()) "$fullAddress, $cityKecamatan, $postalCode" else fullAddress,
                                    note = note
                                )
                            )
                            onBackClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003049)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Simpan Alamat", fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Kategori Alamat
            Text(
                "KATEGORI ALAMAT",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                val categories = listOf("Rumah", "Kantor")
                categories.forEach { cat ->
                    val isSelected = label == cat
                    if (isSelected) {
                        Button(
                            onClick = { label = cat },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4F2FE)),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF003049)),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(
                                imageVector = if (cat == "Rumah") Icons.Default.Home else Icons.Default.Business,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF003049)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(cat, fontSize = 14.sp, color = Color(0xFF003049), fontWeight = FontWeight.Medium)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { label = cat },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(
                                imageVector = if (cat == "Rumah") Icons.Default.Home else Icons.Default.Business,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(cat, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Kontak
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = Color(0xFF003049), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kontak", fontSize = 16.sp, color = Color(0xFF003049), fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(Modifier.height(16.dp))
                    
                    Text("Nama Lengkap", fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = receiverName,
                        onValueChange = { receiverName = it },
                        placeholder = { Text("Misal: Budi Santoso", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    Text("Nomor Handphone", fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        placeholder = { Text("Misal: 081234567890", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Detail Alamat
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color(0xFF003049), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Detail Alamat", fontSize = 16.sp, color = Color(0xFF003049), fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(Modifier.height(16.dp))
                    
                    Text("Kota / Kecamatan", fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = cityKecamatan,
                        onValueChange = { cityKecamatan = it },
                        placeholder = { Text("Ketik Kota/Kecamatan", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        trailingIcon = {
                            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Color.Gray)
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    Text("Kode Pos", fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = postalCode,
                        onValueChange = { postalCode = it },
                        placeholder = { Text("Misal: 12345", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))
                    
                    Text("Alamat Lengkap", fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = fullAddress,
                        onValueChange = { fullAddress = it },
                        placeholder = { Text("Nama Jalan, Gedung, No.\nRumah/Unit, RT/RW, dll.", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().height(90.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(Modifier.height(16.dp))
                    
                    Text("Catatan untuk Kurir (Opsional)", fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("Misal: Warna rumah abu-abu, pagar\nhitam", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().height(90.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Pin Lokasi di Peta
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFD6E4FF), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Map, contentDescription = null, tint = Color(0xFF003049), modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pin Lokasi di Peta", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(Modifier.height(4.dp))
                        Text("Bantu kurir temukan alamatmu lebih cepat", fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp)
                    }
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Color.Gray)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
