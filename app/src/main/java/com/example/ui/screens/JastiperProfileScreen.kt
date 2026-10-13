package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JastiperProfileScreen(
    viewModel: JastipViewModel,
    onLogoutClick: () -> Unit,
    onNavigateToPendapatan: () -> Unit,
    onNavigateToTarikSaldo: () -> Unit,
    onNavigateToJastiperService: () -> Unit,
    onNavigateToPanduanJastiper: () -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToAlamat: () -> Unit = {},
    onNavigateToPin: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val listState = rememberLazyListState()

    // Handle Scroll to Top event from Bottom Navigation
    LaunchedEffect(Unit) {
        viewModel.scrollToTopEvent.collect {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF102A43))
                    }
                    
                    Text(
                        text = "Profil",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF102A43), // jastipNavy
                        letterSpacing = (-0.5).sp
                    )
                    
                    // Notification Icon card
                    IconButton(
                        onClick = onNavigateToNotifications,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0F4F8))
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifikasi",
                                tint = Color(0xFF102A43),
                                modifier = Modifier.size(20.dp)
                            )
                            // Red dot notification badge
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                            )
                        }
                    }
                }
                if (isRefreshing) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                        color = Color(0xFF102A43),
                        trackColor = Color.Transparent
                    )
                }
                HorizontalDivider(color = Color(0xFFE4F2FE), thickness = 1.dp)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5FAFD))
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar & Info section
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(3.dp, Color.White),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFE0F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF81D4FA),
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        
                        // Verified badge
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF003049),
                            border = BorderStroke(2.dp, Color.White),
                            modifier = Modifier.size(24.dp).offset(x = (-2).dp, y = (-2).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.padding(3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = userProfile.fullName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003049)
                    )
                    Text(
                        text = userProfile.phoneNumber,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFFE5E7EB).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Mitra Jastiper",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF003049),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Stats section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Outlined.Star, contentDescription = "Rating", tint = Color(0xFF1976D2))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("4.9", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("Rating", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Outlined.CalendarToday, contentDescription = "Joined", tint = Color(0xFF1976D2))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("6 Bln", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("Bergabung", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // Keamanan & Akun section
            item {
                Column {
                    Text(
                        text = "KEAMANAN & AKUN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column {
                            MenuItemRow(icon = Icons.Outlined.Person, title = "Edit Profil", onClick = onNavigateToEditProfile)
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            MenuItemRow(icon = Icons.Outlined.LocationOn, title = "Alamat Saya", onClick = onNavigateToAlamat)
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            MenuItemRow(icon = Icons.Outlined.Password, title = "PIN Transaksi", onClick = onNavigateToPin)
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            MenuItemRow(icon = Icons.Outlined.Lock, title = "Ubah Kata Sandi", onClick = onNavigateToChangePassword)
                        }
                    }
                }
            }
            
            // Layanan Jastip section
            item {
                Column {
                    Text(
                        text = "LAYANAN JASTIP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column {
                            MenuItemRow(icon = Icons.Outlined.AccountBalanceWallet, title = "Saldo Jastiper", onClick = onNavigateToPendapatan)
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            MenuItemRow(icon = Icons.Outlined.MenuBook, title = "Panduan Jastiper", onClick = onNavigateToPanduanJastiper)
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            MenuItemRow(icon = Icons.Outlined.SupportAgent, title = "Jastiper Service", onClick = onNavigateToJastiperService)
                        }
                    }
                }
            }

            // Logout Button
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onLogoutClick() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Logout, contentDescription = "Logout", tint = Color(0xFFD32F2F))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Keluar Akun", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFD32F2F))
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun MenuItemRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFF1F5F9),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF003049),
                modifier = Modifier.padding(10.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

