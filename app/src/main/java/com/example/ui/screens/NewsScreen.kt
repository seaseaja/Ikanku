package com.example.ui.screens

import com.example.ui.components.GlobalHeader

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NewsArticle(
    val category: String,
    val title: String,
    val description: String,
    val readTime: String,
    val isMintBg: Boolean
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsScreen(
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    val scrollState = rememberScrollState()

    val articles = remember {
        listOf(
            NewsArticle(
                category = "Resep",
                title = "Pesmol kakap ala rumahan: rahasia bumbu kuningnya",
                description = "Pesmol yang enak itu bukan soal banyak bumbu — tapi soal urutan menumis. Ini...",
                readTime = "6 min baca",
                isMintBg = true
            ),
            NewsArticle(
                category = "Akademik",
                title = "Studi: pola migrasi tuna sirip kuning di Laut Banda",
                description = "Data tagging 2020-2025 menunjukkan pola pergerakan musiman yang konsisten —...",
                readTime = "9 min baca",
                isMintBg = false
            ),
            NewsArticle(
                category = "Tips",
                title = "Cara pilih ikan segar di pasar (tanda yang sering keliru)",
                description = "Mata jernih saja belum cukup. Ada empat tanda lain yang sering dilewatkan pembeli...",
                readTime = "4 min baca",
                isMintBg = true
            ),
            NewsArticle(
                category = "Berita",
                title = "Musim panen udang vannamei 2026 diprediksi naik 12%",
                description = "Cuaca yang stabil dan investasi baru di tambak Lampung mendorong proyeksi...",
                readTime = "3 min baca",
                isMintBg = false
            ),
            NewsArticle(
                category = "Cerita",
                title = "Sehari bareng nelayan Cilacap: dari dermaga ke meja makan",
                description = "Jam 3 pagi, perahu mulai berangkat. Kami ikut Pak Slamet melaut, dan menemukan rute tak...",
                readTime = "8 min baca",
                isMintBg = true
            ),
            NewsArticle(
                category = "Tips",
                title = "Menyimpan ikan di kulkas: berapa lama benar-benar aman?",
                description = "Panduan praktis berdasarkan suhu kulkas dan jenis ikan — plus trik membungkus yang...",
                readTime = "5 min baca",
                isMintBg = false
            )
        )
    }

    val categories = listOf("Semua", "Resep", "Tips", "Akademik", "Berita", "Cerita")

    // Filter articles based on category and search query
    val filteredArticles = remember(selectedCategory, searchQuery) {
        articles.filter { article ->
            val matchesCategory = selectedCategory == "Semua" || article.category.equals(selectedCategory, true)
            val matchesSearch = article.title.contains(searchQuery, true) || article.description.contains(searchQuery, true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5FAFD))
    ) {
        // App bar top block - Unified, Brand Focused, and Statically Anchored
        GlobalHeader(
            navigationIcon = if (onBackClick != null) {
                {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color(0xFF006684)
                        )
                    }
                }
            } else null
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
            // "Belajar perikanan, selow tapi dalam."
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF191C1E))) {
                        append("Belajar perikanan,\n")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF006684))) {
                        append("selow tapi dalam.")
                    }
                },
                style = MaterialTheme.typography.displaySmall.copy(
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Dari resep dapur sampai jurnal kelautan — pilih bacaanmu hari ini.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4C626B),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Search input box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari", tint = Color.Gray) },
                placeholder = { Text("Cari artikel, resep, atau topik...", color = Color.Gray) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF006684),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("tf_search_news")
            )

            // Category horizontally scrollable row of chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isActive = selectedCategory == category
                    Surface(
                        color = if (isActive) Color(0xFF006684) else Color(0xFFECEFF1),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clickable { selectedCategory = category }
                            .testTag("chip_cat_$category")
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color.White else Color(0xFF4C626B),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Articles Vertical Layout Grid adaptation
            if (filteredArticles.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔍", fontSize = 42.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Artikel tidak ditemukan",
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            } else {
                filteredArticles.forEach { article ->
                    NewsArticleItemCard(article = article)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        // Unified Footer
        UnifiedFooter()
    }
}
}

@Composable
fun NewsArticleItemCard(article: NewsArticle) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFCFE6F1).copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Top Preview Area representing image block from design
            val waveColor = if (article.isMintBg) Color(0xFFE0F2F1) else Color(0xFFE3F2FD)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(waveColor),
                contentAlignment = Alignment.Center
            ) {
                // Standard Icon component representing the article cover illustration.
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Book,
                    contentDescription = "Gambar Artikel",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    tint = Color.White.copy(alpha = 0.35f)
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "Bacaan",
                        tint = Color(0xFF006684),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Tag Label Top-Left
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = article.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF006684),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Body Area
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191C1E)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = article.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4C626B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                Divider(color = Color.LightGray.copy(alpha = 0.4f))

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Waktu Baca",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = article.readTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Baca",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF006684),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Go",
                            tint = Color(0xFF006684),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
