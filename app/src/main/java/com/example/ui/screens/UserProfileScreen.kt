package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.JastipViewModel

import com.example.ui.components.GlobalHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: JastipViewModel,
    onLogoutClick: () -> Unit,
    onNavigateToAboutUs: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAlamat: () -> Unit,
    onNavigateToPin: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToCustomerService: () -> Unit,
    onNavigateToPesanan: () -> Unit,
    onNavigateToSaldoWavepay: () -> Unit,
    onNavigateToJastipRegister: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Spacer(modifier = Modifier.statusBarsPadding())
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.size(40.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Profil",
                                color = Color(0xFF006684),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        IconButton(
                            onClick = { onNavigateToNotifications() },
                            modifier = Modifier
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifikasi",
                                tint = Color(0xFF006684),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    if (isRefreshing) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().height(2.dp),
                            color = Color(0xFF006684),
                            trackColor = Color.Transparent
                        )
                    }
                    HorizontalDivider(color = Color(0xFFE2F1F8), thickness = 1.dp)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FC))
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0F2FE))
                                .clickable { Toast.makeText(context, "Ubah Foto Profil", Toast.LENGTH_SHORT).show() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color(0xFF81D4FA))
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF003049)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = userProfile.fullName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF003049)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = userProfile.phoneNumber,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Stats Cards
            item {
                 // INTEGRASI SALDO WAVEPAY
                 // Saldo ini terintegrasi dengan SaldoWavepayScreen dan akan ter-update secara real-time
                 // ketika user melakukan top-up atau checkout pembayaran
                 val userBalance by viewModel.userBalance.collectAsState()
                 val currencyFormatter = remember {
                     java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).apply {
                         maximumFractionDigits = 0
                     }
                 }
                 
                 Row(
                     modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                     horizontalArrangement = Arrangement.spacedBy(16.dp)
                 ) {
                     // Pesanan Saya
                     Card(
                         modifier = Modifier.weight(1f).clickable { onNavigateToPesanan() },
                         shape = RoundedCornerShape(12.dp),
                         colors = CardDefaults.cardColors(containerColor = Color.White),
                         elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                         border = BorderStroke(1.dp, Color(0xFFEBF0F4))
                     ) {
                         Column(
                             modifier = Modifier.padding(20.dp).fillMaxWidth(),
                             horizontalAlignment = Alignment.CenterHorizontally
                         ) {
                             Icon(imageVector = Icons.Outlined.ShoppingBag, contentDescription = null, tint = Color(0xFF003B5B))
                             Spacer(modifier = Modifier.height(8.dp))
                             Text("Pesanan", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                             Text("SAYA", fontSize = 12.sp, color = Color.Gray)
                         }
                     }

                     // Saldo Wavepay
                     Card(
                         modifier = Modifier.weight(1f).clickable { onNavigateToSaldoWavepay() },
                         shape = RoundedCornerShape(12.dp),
                         colors = CardDefaults.cardColors(containerColor = Color.White),
                         elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                         border = BorderStroke(1.dp, Color(0xFFEBF0F4))
                     ) {
                         Column(
                             modifier = Modifier.padding(20.dp).fillMaxWidth(),
                             horizontalAlignment = Alignment.CenterHorizontally
                         ) {
                             Icon(imageVector = Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF003B5B))
                             Spacer(modifier = Modifier.height(8.dp))
                             Text(
                                 "Rp ${currencyFormatter.format(userBalance)}", 
                                 fontWeight = FontWeight.Bold, 
                                 fontSize = 18.sp, 
                                 color = Color.Black
                             )
                             Text("SALDO WAVEPAY", fontSize = 12.sp, color = Color.Gray)
                         }
                     }
                 }
            }

            // Keamanan & Akun Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, Color(0xFFEBF0F4))
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "KEAMANAN & AKUN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        ProfileSimpleMenuRow(title = "Edit Profil", icon = Icons.Outlined.Person, onClick = { onNavigateToEditProfile() })
                        HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 48.dp))
                        ProfileSimpleMenuRow(title = "Alamat Saya", icon = Icons.Outlined.LocationOn, onClick = { onNavigateToAlamat() })
                        HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 48.dp))

                        if (userProfile.role != "jastiper") {
                            ProfileSimpleMenuRow(
                                title = "Daftar Jadi Jastiper", 
                                icon = Icons.Outlined.VerifiedUser, 
                                iconTint = Color(0xFFF57F17),
                                onClick = onNavigateToJastipRegister
                            )
                            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 48.dp))
                        }

                        ProfileSimpleMenuRow(title = "PIN Transaksi Jastip & Bayar", icon = Icons.Outlined.Dialpad, onClick = { onNavigateToPin() })
                        HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 48.dp))
                        ProfileSimpleMenuRow(title = "Ubah Kata Sandi", icon = Icons.Outlined.Lock, onClick = onNavigateToChangePassword)
                    }
                }
            }

            // Informasi & Bantuan
            item {
                 Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, Color(0xFFEBF0F4))
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "INFORMASI & BANTUAN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        ProfileSimpleMenuRow(title = "Berita Perikanan", icon = Icons.Outlined.Article, onClick = onNavigateToNews)
                        HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 48.dp))
                        ProfileSimpleMenuRow(title = "Tentang IkanKu", icon = Icons.Outlined.Info, onClick = onNavigateToAboutUs)
                        HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 48.dp))
                        ProfileSimpleMenuRow(title = "Hubungi Layanan CS", icon = Icons.Outlined.SupportAgent, onClick = onNavigateToCustomerService)
                    }
                }
            }

            // Logout Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onLogoutClick() },
                    border = BorderStroke(1.5.dp, Color(0xFFD32F2F)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_logout_profile")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = "Keluar Akun",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Keluar Akun",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ProfileSimpleMenuRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color = Color(0xFF003049),
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Text(title, fontSize = 15.sp, color = textColor, fontWeight = FontWeight.Medium)
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = if (textColor == Color(0xFFD32F2F)) Color(0xFFD32F2F) else Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ProfileSimpleSwitchRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF003049),
                modifier = Modifier.size(24.dp)
            )
            Text(title, fontSize = 15.sp, color = Color.Black, fontWeight = FontWeight.Medium)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF003049),
            ),
            modifier = Modifier.scale(0.8f) // Optional: scale switch slightly smaller to fit better
        )
    }
}
