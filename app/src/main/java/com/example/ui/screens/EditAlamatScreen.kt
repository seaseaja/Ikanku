package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
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
fun EditAlamatScreen(
    viewModel: JastipViewModel,
    addressId: String,
    onBackClick: () -> Unit
) {
    val addresses by viewModel.userAddresses.collectAsState()
    val address = remember(addressId, addresses) { addresses.find { it.id == addressId } }

    if (address == null) {
        // Handle address not found
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Alamat tidak ditemukan")
        }
        return
    }

    var label by remember { mutableStateOf(address.label) }
    var receiverName by remember { mutableStateOf(address.receiverName) }
    var phoneNumber by remember { mutableStateOf(address.phoneNumber) }
    var fullAddress by remember { mutableStateOf(address.fullAddress) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF7FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Alamat",
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
                        viewModel.updateAddress(
                            address.copy(
                                label = label,
                                receiverName = receiverName,
                                phoneNumber = phoneNumber,
                                fullAddress = fullAddress
                            )
                        )
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003049)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
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
            // Kontainer Kontak Penerima
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("KONTAK PENERIMA", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF003B5B))
                    Spacer(Modifier.height(16.dp))

                    Text("Nama Lengkap", fontSize = 14.sp, color = Color.DarkGray)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = receiverName,
                        onValueChange = { receiverName = it },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.Gray) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFF0F5B9B)
                        ),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    Text("Nomor Telepon", fontSize = 14.sp, color = Color.DarkGray)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = Color.Gray) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFF0F5B9B)
                        ),
                        singleLine = true
                    )
                }
            }

            // Kontainer Detail Lokasi
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("DETAIL LOKASI", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF003B5B))
                    Spacer(Modifier.height(16.dp))

                    Text("Label Alamat", fontSize = 14.sp, color = Color.DarkGray)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val labels = listOf("Rumah", "Kantor", "Lainnya")
                        labels.forEach { l ->
                            val isSelected = label == l
                            if (isSelected) {
                                Button(
                                    onClick = { label = l },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF265A96)),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(
                                        imageVector = when(l) {
                                            "Rumah" -> Icons.Default.Home
                                            "Kantor" -> Icons.Default.Business
                                            else -> Icons.Default.LocationOn
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(l, fontSize = 13.sp)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { label = l },
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(
                                        imageVector = when(l) {
                                            "Rumah" -> Icons.Default.Home
                                            "Kantor" -> Icons.Default.Business
                                            else -> Icons.Default.LocationOn
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(l, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Alamat Lengkap", fontSize = 14.sp, color = Color.DarkGray)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fullAddress,
                        onValueChange = { fullAddress = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFF0F5B9B)
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Titik Lokasi (Pinpoint)", fontSize = 14.sp, color = Color.DarkGray)
                        Text(
                            "Ubah Titik",
                            fontSize = 13.sp,
                            color = Color(0xFF0F5B9B),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable {}
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFEFEFEF), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            tint = Color(0xFF265A96),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Pastikan titik lokasi sesuai agar kurir mudah menemukan alamat Anda.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
