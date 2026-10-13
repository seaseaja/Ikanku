package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

import com.example.ui.components.GlobalHeader

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BerandaScreen(
    onNavigateToJastip: () -> Unit,
    onNavigateToLive: () -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    viewModel: com.example.viewmodel.JastipViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    // Handle Scroll to Top event from Bottom Navigation
    LaunchedEffect(Unit) {
        viewModel.scrollToTopEvent.collect {
            scrollState.animateScrollTo(0)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5FAFD)) // Light water-tint background from design
    ) {
        // App bar top block - Unified, Brand Focused, and Statically Anchored
        GlobalHeader(onNavigateToNotifications = onNavigateToNotifications)

        if (isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = Color(0xFF006684),
                trackColor = Color.Transparent
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            // Main Hero Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
            // Whale Hero Banner Card positioned AT THE TOP in mobile layout
            WhaleHeroBanner()

            Spacer(modifier = Modifier.height(20.dp))

            // "Dunia perikanan, selengkap lautan."
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF191C1E))) {
                        append("Dunia perikanan,\n")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF006684))) { // Accent Blue
                        append("selengkap lautan.")
                    }
                },
                style = MaterialTheme.typography.displaySmall.copy(
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Baca artikel, resep, dan tips dari ahli. Atau pesan ikan segar lewat jastip lokal di kota kamu semuanya di satu tempat.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF4C626B), // M3 neutral variant
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Buttons stacked vertically (full-width)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToJastip,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_pesan_jastip_now")
                ) {
                    Text("Pesan Jastip Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("›", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                }

                OutlinedButton(
                    onClick = onNavigateToLive,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF006684)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF006684)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_lihat_live_beranda")
                ) {
                    Text("Lihat Live Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Info Terkini — "Ikan favorit minggu ini."
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 32.dp)
        ) {
            Text(
                text = "Info Terkini",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF006684),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Ikan favorit minggu ini.",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF191C1E),
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // FlowRow or Column arrangement of the 4 favorite items
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FavoriteFishCard(
                            title = "Salmon Norwegia",
                            tag = "Best Seller",
                            tagColor = Color(0xFFFFA726),
                            market = "Pasar Muara Angke",
                            price = 130000,
                            currencyFormatter = currencyFormatter
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FavoriteFishCard(
                            title = "Tunu Sashimi Grade", // preserved typo "Tunu" or we can leave as is
                            tag = "Segar Hari Ini",
                            tagColor = Color(0xFF2E7D32),
                            market = "Pasar Pabean",
                            price = 145000,
                            currencyFormatter = currencyFormatter
                        )
                    }
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FavoriteFishCard(
                            title = "Udang Vannamei",
                            tag = "Baru Hadir",
                            tagColor = Color(0xFF29B6F6),
                            market = "Pasar Ciroyom",
                            price = 98000,
                            currencyFormatter = currencyFormatter
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FavoriteFishCard(
                            title = "Kakap Merah Utuh",
                            tag = "Rekomendasi",
                            tagColor = Color(0xFFEC407A),
                            market = "Pasar Kedongan",
                            price = 78000,
                            currencyFormatter = currencyFormatter
                        )
                    }
                }
            }
        }
        }

        // Unified Footer content
        UnifiedFooter()
    }
}

@Composable
fun WhaleHeroBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF00838F),
                            Color(0xFF004D40)
                        )
                    )
                )
        ) {
            // Draw beautiful underwater scene with canvas!
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw water glowing rays
                drawCircle(
                    color = Color(0xFF4DD0E1).copy(alpha = 0.3f),
                    radius = w * 0.4f,
                    center = Offset(w * 0.5f, 0f)
                )
                drawCircle(
                    color = Color(0xFF80DEEA).copy(alpha = 0.15f),
                    radius = w * 0.7f,
                    center = Offset(w * 0.5f, -h * 0.1f)
                )

                // Drawn bubbles
                drawCircle(Color.White.copy(alpha = 0.5f), radius = 6f, center = Offset(w * 0.2f, h * 0.7f))
                drawCircle(Color.White.copy(alpha = 0.4f), radius = 10f, center = Offset(w * 0.24f, h * 0.65f))
                drawCircle(Color.White.copy(alpha = 0.3f), radius = 14f, center = Offset(w * 0.18f, h * 0.5f))
                drawCircle(Color.White.copy(alpha = 0.5f), radius = 8f, center = Offset(w * 0.75f, h * 0.4f))
                drawCircle(Color.White.copy(alpha = 0.4f), radius = 12f, center = Offset(w * 0.79f, h * 0.32f))

                // Whale silhouette/drawing path
                val bodyPath = Path().apply {
                    // Start from whale tail
                    moveTo(w * 0.15f, h * 0.6f)
                    // Tail flukes
                    lineTo(w * 0.1f, h * 0.45f)
                    quadraticTo(w * 0.13f, h * 0.52f, w * 0.15f, h * 0.55f)
                    lineTo(w * 0.13f, h * 0.7f)
                    quadraticTo(w * 0.17f, h * 0.62f, w * 0.2f, h * 0.6f)
                    // Massive body
                    quadraticTo(w * 0.4f, h * 0.58f, w * 0.55f, h * 0.38f)
                    // Broad head curving up
                    quadraticTo(w * 0.7f, h * 0.18f, w * 0.82f, h * 0.3f)
                    // Mouth line round down
                    quadraticTo(w * 0.86f, h * 0.4f, w * 0.82f, h * 0.5f)
                    // Underbelly going back down
                    quadraticTo(w * 0.65f, h * 0.75f, w * 0.48f, h * 0.72f)
                    // Pectoral fin
                    lineTo(w * 0.42f, h * 0.85f)
                    quadraticTo(w * 0.38f, h * 0.8f, w * 0.42f, h * 0.71f)
                    // Underbody trailing to tail
                    quadraticTo(w * 0.3f, h * 0.68f, w * 0.15f, h * 0.6f)
                }

                // Fill the majestic whale silhouette with translucent dark color
                drawPath(
                    path = bodyPath,
                    color = Color(0xFF001F2A).copy(alpha = 0.65f)
                )

                // Fin outlines or whale body details
                drawPath(
                    path = bodyPath,
                    color = Color(0xFFBCEDFF).copy(alpha = 0.3f),
                    style = Stroke(width = 3f)
                )
            }

            // Top-left label tag: "Hari ini: 24 ikan segar"
            Surface(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopStart)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF2E7D32), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "24 ikan segar hari ini",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF001F2A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bottom-right tag: "Rating Jastip 4.9/5"
            Surface(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐", fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Rating Jastip: 4.9 / 5",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF001F2A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteFishCard(
    title: String,
    tag: String,
    tagColor: Color,
    market: String,
    price: Int,
    currencyFormatter: NumberFormat
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFCFE6F1).copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            // Draw a simplified beautiful fish or shrimp container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color(0xFFBCEDFF).copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                // Standard Icon component representing the product photo.
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    tint = Color(0xFF006684).copy(alpha = 0.25f)
                )

                // Tiny overlay badge like "Best Seller" on top left of preview area
                Surface(
                    color = tagColor,
                    shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 6.dp, bottomEnd = 6.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Card details text representation
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Pasar",
                        tint = Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = market,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191C1E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = currencyFormatter.format(price),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF006684)
                        )
                        Text(
                            text = "/Kg",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UnifiedFooter() {
    // Footer removed per user request
}
