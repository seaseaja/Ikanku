package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JastipOrder
import com.example.data.OrderStatus
import com.example.viewmodel.JastipViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    viewModel: JastipViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.allOrders.collectAsState()
    val order = orders.find { it.id == orderId }

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Jastip Ikan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("btn_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Dashboard"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (order != null) {
                        IconButton(
                            onClick = { onNavigateToChat(order.id) },
                            modifier = Modifier.testTag("btn_chat_detail")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Chat,
                                contentDescription = "Tulis Pesan ke Customer"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Order tidak ditemukan!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            val statusType = try {
                OrderStatus.valueOf(order.status)
            } catch (e: Exception) {
                OrderStatus.PENDING
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Seafood summary card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when {
                                        order.fishType.contains("tuna", true) -> "🐟"
                                        order.fishType.contains("kakap", true) -> "🐠"
                                        order.fishType.contains("gurame", true) -> "🐟"
                                        order.fishType.contains("kepiting", true) -> "🦀"
                                        order.fishType.contains("udang", true) -> "🦐"
                                        else -> "⚓"
                                    },
                                    fontSize = 24.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = order.fishType,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Estimasi Berat: ${order.weightKg} kg",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (order.notes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "Catatan Request Pelanggan:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "\"${order.notes}\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }
                }

                // Tracking Map Vector Illustration
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Rute Pelelangan ➔ Pengantaran",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom vector draw illustrating the map route
                        val routeColor = MaterialTheme.colorScheme.primary
                        val dotColor = MaterialTheme.colorScheme.tertiary
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                // Draw simulated roads (mesh grids)
                                drawLine(
                                    color = Color.LightGray.copy(alpha = 0.4f),
                                    start = Offset(0f, h * 0.4f),
                                    end = Offset(w, h * 0.4f),
                                    strokeWidth = 4f
                                )
                                drawLine(
                                    color = Color.LightGray.copy(alpha = 0.4f),
                                    start = Offset(0f, h * 0.7f),
                                    end = Offset(w, h * 0.7f),
                                    strokeWidth = 4f
                                )
                                drawLine(
                                    color = Color.LightGray.copy(alpha = 0.4f),
                                    start = Offset(w * 0.3f, 0f),
                                    end = Offset(w * 0.3f, h),
                                    strokeWidth = 4f
                                )
                                drawLine(
                                    color = Color.LightGray.copy(alpha = 0.4f),
                                    start = Offset(w * 0.7f, 0f),
                                    end = Offset(w * 0.7f, h),
                                    strokeWidth = 4f
                                )

                                // Draw transit shipping trajectory path
                                val roadPath = androidx.compose.ui.graphics.Path().apply {
                                    moveTo(w * 0.15f, h * 0.5f)
                                    quadraticTo(
                                        w * 0.4f, h * 0.2f,
                                        w * 0.5f, h * 0.6f
                                    )
                                    quadraticTo(
                                        w * 0.6f, h * 1.0f,
                                        w * 0.85f, h * 0.5f
                                    )
                                }

                                drawPath(
                                    path = roadPath,
                                    color = routeColor,
                                    style = Stroke(
                                        width = 6f,
                                        pathEffect = PathEffect.dashPathEffect(
                                            floatArrayOf(15f, 15f),
                                            0f
                                        )
                                    )
                                )

                                // Draw Nodes
                                drawCircle(
                                    color = routeColor,
                                    radius = 16f,
                                    center = Offset(w * 0.15f, h * 0.5f)
                                )
                                drawCircle(
                                    color = dotColor,
                                    radius = 16f,
                                    center = Offset(w * 0.85f, h * 0.5f)
                                )

                                // Animated Courier marker (depending on state)
                                val courierRatio = when (statusType) {
                                    OrderStatus.ACCEPTED -> 0.15f
                                    OrderStatus.BUYING -> 0.35f
                                    OrderStatus.DELIVERING -> 0.65f
                                    OrderStatus.COMPLETED -> 0.85f
                                    else -> 0.15f
                                }
                                val courierX = w * (0.15f + (0.7f * courierRatio))
                                val courierY = h * (0.5f + (0.05f * if (courierRatio > 0.5f) 1f else -1f))
                                drawCircle(
                                    color = Color.Blue,
                                    radius = 12f,
                                    center = Offset(courierX, courierY)
                                )
                            }

                            // Icons overlaid on canvas
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.Store,
                                        contentDescription = "Pasar Sapi",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(MaterialTheme.colorScheme.tertiary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.Home,
                                        contentDescription = "Rumah User",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Titik Pembelian:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    order.marketName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Alamat Pengantaran:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    order.deliveryAddress,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }

                // Financial statement card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Rincian Keuangan Jastip",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimasi Harga Ikan", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(currencyFormatter.format(order.budgetEst), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ongkos / Komisi Jastip", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(currencyFormatter.format(order.jastipFee), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total yang Dibayarkan Pelanggan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                currencyFormatter.format(order.budgetEst + order.jastipFee),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Progress tracker timeline card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Status Pengiriman Jastip",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Timeline components
                        TimelineItem(
                            title = "Order Diterima",
                            desc = "Jastiper berkomitmen membeli dan mengirim pesanan ini.",
                            isDone = statusType >= OrderStatus.ACCEPTED,
                            isActive = statusType == OrderStatus.ACCEPTED
                        )
                        TimelineLine(isDone = statusType > OrderStatus.ACCEPTED)

                        TimelineItem(
                            title = "Membeli di Pasar",
                            desc = "Jastiper sedang menyeleksi seafood terbaik di ${order.marketName}.",
                            isDone = statusType >= OrderStatus.BUYING,
                            isActive = statusType == OrderStatus.BUYING
                        )
                        TimelineLine(isDone = statusType > OrderStatus.BUYING)

                        TimelineItem(
                            title = "Pengantaran Kurir",
                            desc = "Ikan dalam boks es pendingin dalam perjalanan ke ${order.deliveryAddress}.",
                            isDone = statusType >= OrderStatus.DELIVERING,
                            isActive = statusType == OrderStatus.DELIVERING
                        )
                        TimelineLine(isDone = statusType > OrderStatus.DELIVERING)

                        TimelineItem(
                            title = "Pesanan Diterima (Serah Terima)",
                            desc = "Seafood berhasil serah terima ke pelanggan dengan segar beserta validasi pesanan.",
                            isDone = statusType == OrderStatus.COMPLETED,
                            isActive = statusType == OrderStatus.COMPLETED
                        )
                    }
                }

                // If delivering, show Lacak Posisi Kurir detailed information
                if (statusType == OrderStatus.DELIVERING) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Moped, contentDescription = "Kurir", tint = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Lacak Posisi Kurir (Live)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Kurir Budi Kurniawan sedang dalam perjalanan menuju tujuan Anda.", fontSize = 12.sp, color = Color(0xFF1B5E20))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Estimasi tiba: 15 Menit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                }

                // Dynamic Status Action Button (ACCEPTED -> BUYING -> DELIVERING -> COMPLETED)
                if (statusType != OrderStatus.COMPLETED && statusType != OrderStatus.PENDING) {
                    val nextStatus = when (statusType) {
                        OrderStatus.ACCEPTED -> OrderStatus.BUYING
                        OrderStatus.BUYING -> OrderStatus.DELIVERING
                        OrderStatus.DELIVERING -> OrderStatus.COMPLETED
                        else -> OrderStatus.COMPLETED
                    }

                    val buttonLabel = when (statusType) {
                        OrderStatus.ACCEPTED -> "Mulai Berbelanja di Pasar"
                        OrderStatus.BUYING -> "Selesai Belanja & Kirim Ikan"
                        OrderStatus.DELIVERING -> "Konfirmasi Selesai Diantar"
                        else -> ""
                    }

                    val buttonColor = when (statusType) {
                        OrderStatus.ACCEPTED -> MaterialTheme.colorScheme.primary
                        OrderStatus.BUYING -> MaterialTheme.colorScheme.secondary
                        OrderStatus.DELIVERING -> Color(0xFF2E7D32) // Success Green
                        else -> MaterialTheme.colorScheme.primary
                    }

                    val icon = when (statusType) {
                        OrderStatus.ACCEPTED -> Icons.Default.ShoppingCart
                        OrderStatus.BUYING -> Icons.Default.LocalShipping
                        OrderStatus.DELIVERING -> Icons.Default.Home
                        else -> Icons.Default.Check
                    }

                    Button(
                        onClick = { viewModel.updateOrderStatus(order.id, nextStatus) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("btn_update_status"),
                        colors = ButtonDefaults.buttonColors(containerColor = courierStateButtonColor(statusType, buttonColor)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(icon, contentDescription = buttonLabel)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = buttonLabel,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Contact profile section
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = order.customerName.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    order.customerName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Pelanggan Tetap IkanKu",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Call dialer simulator
                        IconButton(
                            onClick = { /* Simulated Call action */ },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                .size(40.dp)
                                .testTag("btn_call_customer")
                        ) {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = "Call Customer",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TimelineItem(
    title: String,
    desc: String,
    isDone: Boolean,
    isActive: Boolean
) {
    val indicatorColor = when {
        isActive -> MaterialTheme.colorScheme.tertiary
        isDone -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Dot indicator
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(16.dp)
                .background(indicatorColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color.White, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isActive || isDone) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive || isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = if (isActive || isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun TimelineLine(isDone: Boolean) {
    val lineColor = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = Modifier
            .padding(start = 7.dp)
            .width(2.dp)
            .height(24.dp)
            .background(lineColor)
    )
}

fun courierStateButtonColor(status: OrderStatus, calculated: Color): Color {
    // Ensuring safety overrides for colors
    return when(status) {
        OrderStatus.BUYING -> Color(0xFF1E88E5)
        OrderStatus.DELIVERING -> Color(0xFF2E7D32)
        else -> calculated
    }
}
