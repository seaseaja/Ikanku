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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun JastiperPanduanScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Akademi Jastiper",
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
            
            // 1. Header Section - Greeting matching screenshot style but mobile optimized
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Selamat Siang, Jastiper!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = jastipBlueAccent,
                    letterSpacing = (-0.5).sp
                )
                
                Text(
                    text = "Selamat datang di akademi IkanKu. Pelajari semua yang Anda butuhkan untuk menjadi Jastiper ikan profesional dengan rating tinggi dan omzet maksimal.",
                    fontSize = 13.sp,
                    color = jastipTextGray,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Langkah Awal Memulai Section (Vertical Timeline)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)), // Matching background tint of guide banner
                border = BorderStroke(1.dp, Color(0xFFD2E5F7))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Section title inside card
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = jastipBlueAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Langkah Awal Memulai",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = jastipNavy
                        )
                    }

                    // Step 1
                    VerticalStepRow(
                        stepNumber = "1",
                        title = "Siapkan Peralatan",
                        description = "Siapkan ponsel pintar dengan kamera jernih, tripod mini, dan koneksi internet yang stabil di lokasi pasar.",
                        isLast = false
                    )

                    // Step 2
                    VerticalStepRow(
                        stepNumber = "2",
                        title = "Survei Lokasi",
                        description = "Kunjungi pelabuhan atau pasar ikan lokal. Pastikan Anda punya hubungan baik dengan pedagang terpercaya.",
                        isLast = false
                    )

                    // Step 3
                    VerticalStepRow(
                        stepNumber = "3",
                        title = "Mulai Siaran Langsung",
                        description = "Buka aplikasi IkanKu, pilih 'Mulai Live', dan tunjukkan kesegaran tangkapan hari ini kepada calon pembeli.",
                        isLast = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Tips Memilih Ikan Segar Section (Vertical Stacking)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveRedEye,
                        contentDescription = null,
                        tint = jastipNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Tips Memilih Ikan Segar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = jastipNavy
                    )
                }

                // Card 1: Segar (Terbaik)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Visual Segment of Fresh Fish Card with green gradient
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFE6F4EA), Color(0xFFB7E1CD))
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Tag Header
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(jastipGreenAccent)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "SEGAR (TERBAIK)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                // Visual Fish Silhouette or text outline
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = jastipGreenAccent.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .align(Alignment.End)
                                )
                            }
                        }

                        // Fresh Fish Details List
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconicBulletItem(
                                icon = Icons.Default.CheckCircle,
                                tint = jastipGreenAccent,
                                text = "Mata bening, jernih, dan menonjol."
                            )
                            IconicBulletItem(
                                icon = Icons.Default.CheckCircle,
                                tint = jastipGreenAccent,
                                text = "Insang berwarna merah darah atau merah muda cerah."
                            )
                            IconicBulletItem(
                                icon = Icons.Default.CheckCircle,
                                tint = jastipGreenAccent,
                                text = "Daging kenyal dan elastis saat ditekan."
                            )
                        }
                    }
                }

                // Card 2: Hindari (Tidak Segar)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Visual Segment of Stale Fish Card with red/gray gradient
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFFCE8E6), Color(0xFFF5C2C1))
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Tag Header
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFD32F2F))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "HINDARI (TIDAK SEGAR)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                // Visual Warning Outline
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F).copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .align(Alignment.End)
                                )
                            }
                        }

                        // Stale Fish Details List
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconicBulletItem(
                                icon = Icons.Default.Cancel,
                                tint = Color(0xFFD32F2F),
                                text = "Mata kusam, putih susu, atau tenggelam."
                            )
                            IconicBulletItem(
                                icon = Icons.Default.Cancel,
                                tint = Color(0xFFD32F2F),
                                text = "Insang berwarna cokelat tua atau kelabu."
                            )
                            IconicBulletItem(
                                icon = Icons.Default.Cancel,
                                tint = Color(0xFFD32F2F),
                                text = "Bau amis menyengat yang tidak sedap."
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Trik Siaran Langsung Interaktif Section (Stacked Vertically)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Podcasts,
                        contentDescription = null,
                        tint = jastipNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Trik Siaran Langsung Interaktif",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = jastipNavy
                    )
                }

                // Subcard 1: Interaksi Aktif (Hero Banner style)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = jastipNavy),
                    border = BorderStroke(1.dp, jastipNavy)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF0F5B9B), // Royal blue accent
                                        Color(0xFF102A43)  // Deep navy
                                    )
                                )
                            )
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forum,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Interaksi Aktif",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        
                        Text(
                            text = "Sapa penonton yang baru bergabung dengan menyebut nama mereka. Jawab pertanyaan tentang harga dan jenis ikan secara langsung untuk membangun kepercayaan.",
                            fontSize = 12.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 18.sp
                        )
                    }
                }

                // Subcard 2: Pencahayaan Alami (Dark blue) & Suara Jernih (Light blue) stacked nicely
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pencahayaan Alami (Dark Blue Block Card)
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = jastipBlueAccent),
                        border = BorderStroke(1.dp, jastipBlueAccent)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LightMode,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Pencahayaan Alami",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Gunakan cahaya matahari pagi untuk menonjolkan kilau sisik ikan segar.",
                                fontSize = 10.sp,
                                color = Color(0xFFECEFF1),
                                lineHeight = 14.sp
                            )
                        }
                    }

                    // Suara Jernih (Light Blue Block Card)
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE4F2FE)),
                        border = BorderStroke(1.dp, Color(0xFFBCE0FD))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = jastipBlueAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Suara Jernih",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = jastipNavy
                            )
                            Text(
                                text = "Gunakan mikrofon tambahan agar suara Anda tidak kalah dengan kebisingan pasar.",
                                fontSize = 10.sp,
                                color = jastipTextGray,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Aturan Main & Etika Jastiper (Flat List without heavy borders)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    
                    // Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = jastipBlueAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Aturan Main & Etika Jastiper",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = jastipNavy
                        )
                    }

                    Divider(color = Color(0xFFECEFF1))

                    // Flat List Item 1: Kejujuran Produk
                    RuleListItem(
                        icon = Icons.Outlined.Verified,
                        title = "Kejujuran Produk",
                        description = "Dilarang menyembunyikan cacat pada produk atau menggunakan efek kamera yang mengubah warna asli ikan secara berlebihan."
                    )
                    
                    Divider(color = Color(0xFFF1F5F9))

                    // Flat List Item 2: Ketepatan Waktu
                    RuleListItem(
                        icon = Icons.Outlined.Schedule,
                        title = "Ketepatan Waktu",
                        description = "Pesanan harus dikirim maksimal 2 jam setelah sesi siaran berakhir untuk menjaga kesegaran ikan."
                    )
                    
                    Divider(color = Color(0xFFF1F5F9))

                    // Flat List Item 3: Larangan Transaksi Luar
                    RuleListItem(
                        icon = Icons.Outlined.Block,
                        title = "Larangan Transaksi Luar",
                        description = "Dilarang mengarahkan pembeli untuk bertransaksi di luar platform IkanKu demi keamanan dan jaminan bersama."
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun VerticalStepRow(
    stepNumber: String,
    title: String,
    description: String,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left Column (Numbers Timeline node)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            // Circle Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(jastipBlueAccent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
            
            // Connective Line to next
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(64.dp)
                        .background(jastipBlueAccent.copy(alpha = 0.3f))
                )
            }
        }

        // Right Column (Text blocks)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = jastipNavy
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = jastipTextGray,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun IconicBulletItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Text(
            text = text,
            fontSize = 11.sp,
            color = jastipTextGray,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun RuleListItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFE4F2FE)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = jastipBlueAccent,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = jastipNavy
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = jastipTextGray,
                lineHeight = 16.sp
            )
        }
    }
}
