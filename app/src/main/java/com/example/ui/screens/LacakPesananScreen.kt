package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LacakPesananScreen(onBackClick: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF7FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lacak Pesanan", fontWeight = FontWeight.Bold, color = Color(0xFF003049), fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFF003049)) }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Outlined.HelpOutline, contentDescription = "Help", tint = Color(0xFF003049)) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFF7FAFC))
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Map Placeholder Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF336C70))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        // Drawing fake map routes for aesthetics
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            drawLine(
                                color = Color(0xFFFFD54F).copy(alpha = 0.5f),
                                start = Offset(size.width * 0.2f, size.height * 0.1f),
                                end = Offset(size.width * 0.8f, size.height * 0.8f),
                                strokeWidth = 5f,
                                pathEffect = pathEffect
                            )
                        }
                        // Dummy nodes
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.offset(y = (-40).dp, x = (-20).dp)) {
                            Box(modifier = Modifier.size(30.dp).background(Color(0xFF2C5282), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.height(4.dp))
                            Surface(color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 4.dp) {
                                Text("Bu Yati (Jastip)", fontSize = 11.sp, color = Color(0xFF003049), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.offset(y = 40.dp, x = 60.dp)) {
                            Box(modifier = Modifier.size(30.dp).background(Color(0xFFE53E3E), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.height(4.dp))
                            Surface(color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 4.dp) {
                                Text("Muara Karang", fontSize = 11.sp, color = Color(0xFF003049), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }
                }

                // Timeline Card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Status Pengiriman", fontSize = 16.sp, color = Color(0xFF003049))
                            Surface(color = Color(0xFFE2E8F0), shape = RoundedCornerShape(16.dp)) {
                                Text("#IKN-92831", fontSize = 14.sp, color = Color(0xFF2C5282), modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        TimelineItem(
                            title = "Pesanan Diterima",
                            subtitle = "08:30 • Admin memverifikasi pesanan Anda",
                            status = TimelineStatus.COMPLETED
                        )
                        TimelineItem(
                            title = "Mencari Ikan",
                            subtitle = "09:15 • Jastip sedang memilih ikan segar di TPI",
                            status = TimelineStatus.COMPLETED
                        )
                        TimelineItem(
                            title = "Dalam Perjalanan",
                            subtitle = "Estimasi tiba: 15 menit",
                            status = TimelineStatus.ACTIVE
                        )
                        TimelineItem(
                            title = "Tiba di Lokasi",
                            subtitle = "Pesanan sampai di Muara Karang",
                            status = TimelineStatus.PENDING,
                            isLast = true
                        )
                    }
                }
            }

            // Bottom Profile Card
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C5282))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box {
                                Box(
                                    modifier = Modifier.size(52.dp).background(Color.LightGray, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Placeholder image
                                }
                                Box(
                                    modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-4).dp, y = (-4).dp).size(16.dp).background(Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color(0xFF2C5282), modifier = Modifier.size(12.dp))
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Bu Yati", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF90CDF4), modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("4.9 • Top Courier", fontSize = 13.sp, color = Color(0xFFE2E8F0))
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            IconButton(
                                onClick = {},
                                modifier = Modifier.size(40.dp).background(Color(0xFF4A5568).copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(Icons.Outlined.Call, contentDescription = null, tint = Color.White)
                            }
                            IconButton(
                                onClick = {},
                                modifier = Modifier.size(40.dp).background(Color(0xFF63B3ED), CircleShape)
                            ) {
                                Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class TimelineStatus { COMPLETED, ACTIVE, PENDING }

@Composable
fun TimelineItem(title: String, subtitle: String, status: TimelineStatus, isLast: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when (status) {
                TimelineStatus.COMPLETED -> Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color(0xFF2C5282))
                TimelineStatus.ACTIVE -> Icon(Icons.Outlined.RadioButtonChecked, contentDescription = null, tint = Color(0xFF003049))
                TimelineStatus.PENDING -> Icon(Icons.Outlined.RadioButtonUnchecked, contentDescription = null, tint = Color.LightGray)
            }
            if (!isLast) {
                Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(if (status == TimelineStatus.COMPLETED) Color(0xFF2C5282) else Color(0xFFE2E8F0)))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = 24.dp).offset(y = (-4).dp)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = if (status == TimelineStatus.PENDING) Color.Gray else Color(0xFF003049),
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                subtitle,
                color = if (status == TimelineStatus.PENDING) Color(0xFFCBD5E1) else Color(0xFF4A5568),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}
