package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun AlamatSayaScreen(
    viewModel: JastipViewModel,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    val addresses by viewModel.userAddresses.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF7FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Alamat Saya",
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003049)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tambah Alamat Baru", color = Color.White, fontWeight = FontWeight.Normal, fontSize = 16.sp)
                }
            }

            items(addresses) { address ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    // Top border for the primary address
                    if (address.isPrimary) {
                        Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(Color(0xFF0F5B9B)))
                    }
                    
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (address.isPrimary) {
                                Surface(color = Color(0xFFD6E4FF), shape = RoundedCornerShape(6.dp)) {
                                    Text("Utama", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF002255), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp)) {
                                Text(address.label, fontSize = 12.sp, fontWeight = FontWeight.Normal, color = Color.DarkGray, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(address.receiverName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(address.phoneNumber, fontSize = 14.sp, color = Color.DarkGray)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = address.fullAddress,
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            lineHeight = 22.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text("Edit", color = Color(0xFF0F5B9B), fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { onEditClick(address.id) })
                            Spacer(modifier = Modifier.width(28.dp))
                            Text("Hapus", color = Color(0xFFD32F2F), fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { viewModel.deleteAddress(address.id) })
                        }
                    }
                }
            }
        }
    }
}

