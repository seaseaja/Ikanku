package com.example.ui.screens

import com.example.ui.components.GlobalHeader

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutUsScreen(
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

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
            // "Kami percaya, laut milik semua."
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF191C1E))) {
                        append("Kami percaya,\n")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF006684))) {
                        append("laut milik semua.")
                    }
                },
                style = MaterialTheme.typography.displaySmall.copy(
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "IKANKU adalah rumah informasi perikanan & marketplace jastip ikan lokal. Misi kami sederhana: bikin dunia perikanan lebih mudah dipahami, dan lebih adil untuk semua yang terlibat di dalamnya.",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = Color(0xFF4C626B),
                modifier = Modifier.padding(bottom = 28.dp)
            )

            Text(
                text = "NILAI UTAMA KAMI",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF006684),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // 1. Untuk pemula Card
            CoreValueCard(
                title = "Untuk pemula",
                description = "Nggak ada istilah terlalu awam. Kami percaya perikanan bisa dikemas santai, rapi, dan gampang dicerna siapa pun.",
                bgColor = Color(0xFFFFF0F2),
                icon = Icons.Default.Favorite,
                iconColor = Color(0xFFEC407A)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Akar lokal Card
            CoreValueCard(
                title = "Akar lokal",
                description = "Fokus kami adalah pasar tradisional, nelayan pesisir, dan jastiper kota lokal. Mereka garda utama dapur kita.",
                bgColor = Color(0xFFE0F2F1),
                icon = Icons.Default.Anchor,
                iconColor = Color(0xFF00897B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Kerja bareng Card
            CoreValueCard(
                title = "Kerja bareng",
                description = "IKANKU bukan sekadar aplikasi searah. Ini kolaborasi pembaca, penulis perikanan, dosen, & kurir jastip harian.",
                bgColor = Color(0xFFE3F2FD),
                icon = Icons.Default.Groups,
                iconColor = Color(0xFF1E88E5)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
        // Unified Footer
        UnifiedFooter()
    }
}
}

@Composable
fun CoreValueCard(
    title: String,
    description: String,
    bgColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, iconColor.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF001F2A)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = Color(0xFF4C626B)
            )
        }
    }
}
