package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JastiperServiceScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    
    // Collapsible FAQ states
    var faq1Expanded by remember { mutableStateOf(false) }
    var faq2Expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Jastiper Service",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = jastipNavy,
                        letterSpacing = (-0.5).sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = jastipNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Interactive Header with Hero Banner Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F5B9B), // Ocean Blue Accent
                                Color(0xFF102A43)  // Deep Navy
                            )
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Halo Jastiper!\nApa yang bisa kami bantu?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )

                    // Search field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = {
                            Text(
                                "Cari Solusi (misal: 'status pengiriman')",
                                fontSize = 12.sp,
                                color = jastipTextGray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Cari",
                                tint = jastipTextGray,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = jastipBlueAccent
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Kategori Masalah Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Kategori Masalah",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = jastipNavy,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                // Horizontal Carousel / Scrollable Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Category Card 1
                    CategorySelectionCard(
                        title = "Masalah Pembayaran",
                        subtitle = "Refund, Top-up, & Transaksi",
                        icon = Icons.Default.Payment,
                        iconBgColor = Color(0xFFE4F2FE),
                        iconTintColor = jastipBlueAccent,
                        onClick = {
                            Toast.makeText(context, "Membuka FAQ Pembayaran...", Toast.LENGTH_SHORT).show()
                        }
                    )

                    // Category Card 2
                    CategorySelectionCard(
                        title = "Akun & Keamanan",
                        subtitle = "Verifikasi & Lupa Password",
                        icon = Icons.Default.Security,
                        iconBgColor = Color(0xFFFFEBEE),
                        iconTintColor = Color(0xFFD32F2F),
                        onClick = {
                            Toast.makeText(context, "Membuka FAQ Keamanan...", Toast.LENGTH_SHORT).show()
                        }
                    )

                    // Category Card 3
                    CategorySelectionCard(
                        title = "Kendala Aplikasi",
                        subtitle = "Error, Bug, & Performa",
                        icon = Icons.Default.Smartphone,
                        iconBgColor = Color(0xFFECEFF1),
                        iconTintColor = Color(0xFF546E7A),
                        onClick = {
                            Toast.makeText(context, "Membuka FAQ Aplikasi...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Frequently Asked Questions (FAQ) Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE4E9F0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header Row inside Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Frequently Asked Questions",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = jastipNavy
                        )
                        Text(
                            text = "Lihat Semua FAQ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = jastipBlueAccent,
                            modifier = Modifier.clickable {
                                Toast.makeText(context, "Menampilkan daftar seluruh FAQ...", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // FAQ Accordion 1
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { faq1Expanded = !faq1Expanded }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Bagaimana cara membatalkan pesanan yang sudah dibayar?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = jastipNavy,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (faq1Expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = jastipNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        AnimatedVisibility(visible = faq1Expanded) {
                            Text(
                                text = "Anda dapat membatalkan pesanan sebelum jastiper memproses pembelian ikan segar di TPI/Pasar. Hubungi pelanggan via Chat untuk persetujuan pembatalan, lalu ajukan pengembalian limit langsung dari sistem untuk pencairan dana instan.",
                                fontSize = 11.sp,
                                color = jastipTextGray,
                                modifier = Modifier.padding(bottom = 8.dp),
                                lineHeight = 16.sp
                            )
                        }
                        Divider(color = Color(0xFFF1F5F9))
                    }

                    // FAQ Accordion 2
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { faq2Expanded = !faq2Expanded }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Apakah produk seafood dijamin kesegarannya?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = jastipNavy,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (faq2Expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = jastipNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AnimatedVisibility(visible = faq2Expanded) {
                            Text(
                                text = "Ya, seluruh produk seafood dibeli langsung secara live dari TPI (Tempat Pelelangan Ikan) terdekat oleh jastiper kami dan disimpan menggunakan cool-box khusus berbatu es selama rute pengantaran jastip untuk menjamin kesegaran rasa ikan yang prima.",
                                fontSize = 11.sp,
                                color = jastipTextGray,
                                modifier = Modifier.padding(bottom = 8.dp),
                                lineHeight = 16.sp
                            )
                        }
                        Divider(color = Color(0xFFF1F5F9))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Saluran Bantuan Support Cards (Stacked Vertically)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // WhatsApp Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = BorderStroke(1.dp, Color(0xFFC8E6C9))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF25D366)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "WhatsApp Support",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = jastipNavy
                                )
                                Text(
                                    text = "Respon cepat via pesan WhatsApp untuk bantuan instan.",
                                    fontSize = 11.sp,
                                    color = jastipTextGray
                                )
                            }
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "Membuka WhatsApp Support...", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Text(
                                "Chat via WhatsApp",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Live Chat Support Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    border = BorderStroke(1.dp, Color(0xFFBBDEFB))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(jastipBlueAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forum,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Live Chat",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = jastipNavy
                                )
                                Text(
                                    text = "Bicara langsung dengan tim support kami di dalam aplikasi.",
                                    fontSize = 11.sp,
                                    color = jastipTextGray
                                )
                            }
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "Memulai sesi Live Chat...", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = jastipBlueAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Text(
                                "Mulai Live Chat",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CategorySelectionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(190.dp)
            .height(130.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE4E9F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = jastipNavy
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = jastipTextGray,
                    lineHeight = 13.sp
                )
            }
        }
    }
}
