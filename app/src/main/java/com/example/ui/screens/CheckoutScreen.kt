package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CartItem
import com.example.viewmodel.JastipViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: JastipViewModel,
    onNavigateBack: () -> Unit,
    onCheckoutSuccess: (String, Double, String) -> Unit,
    onNavigateToTopUp: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToOrderDetail: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cartItems by viewModel.allCartItems.collectAsState()
    val userBalance by viewModel.userBalance.collectAsState()

    // Formatting currency in Indonesian Rupiah
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    // Default Shipping Address State
    var customerName by remember { mutableStateOf("Andi Pratama") }
    var customerPhone by remember { mutableStateOf("+62 812 3456 7890") }
    var deliveryAddress by remember { mutableStateOf("Jl. Sudirman No. 123, Blok C4, Senayan, Kebayoran Baru, Jakarta Selatan, 12190") }
    var showAddressDialog by remember { mutableStateOf(false) }

    // Shipping Courier & Protection Option
    val shippingFee = 45000.0
    var isIceboxProtected by remember { mutableStateOf(false) }
    val iceboxFee = if (isIceboxProtected) 20000.0 else 0.0

    // Payment method grouping (Accordions)
    var isVaExpanded by remember { mutableStateOf(true) }
    var isWalletExpanded by remember { mutableStateOf(false) }
    
    // 1 -> BCA, 2 -> BNI, 3 -> BRI, 4 -> Mandiri, 5 -> GoPay, 6 -> Dana, 7 -> OVO, 8 -> ShopeePay, 9 -> QRIS
    var selectedPaymentMethod by remember { mutableStateOf(9) } 

    // Payment Processing States
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentError by remember { mutableStateOf<String?>(null) }
    var showInsufficientBalanceDialog by remember { mutableStateOf(false) }
    var showTopUpDialog by remember { mutableStateOf(false) }
    
    // State to capture final transaction details to avoid "mock data fallback" anomaly
    var confirmedOrderId by remember { mutableStateOf("") }
    var confirmedOrderNumber by remember { mutableStateOf("") }
    var confirmedTotalAmount by remember { mutableStateOf(0.0) }
    
    val coroutineScope = rememberCoroutineScope()

    // Calculations
    val subtotal = remember(cartItems) {
        cartItems.sumOf { it.price * it.quantity }
    }
    
    val baseJastipFee = 15000.0
    val itemsJastipFee = remember(cartItems) { if (cartItems.isEmpty()) 0.0 else (cartItems.size - 1) * 5000.0 }
    val totalJastipFee = baseJastipFee + itemsJastipFee
    
    val totalPayment = subtotal + shippingFee + iceboxFee + totalJastipFee

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Checkout",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF001F2A)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("btn_checkout_back")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color(0xFF006684)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            // Sticky Bottom Bar replacing bottom navigation (isolated)
            Surface(
                color = Color.White,
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total Pembayaran",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = currencyFormatter.format(totalPayment),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF006684) // Main checkout highlight brand color
                        )
                    }

                    Button(
                        onClick = {
                            // Check if balance is sufficient
                            if (userBalance < totalPayment) {
                                showInsufficientBalanceDialog = true
                                return@Button
                            }

                            coroutineScope.launch {
                                isProcessingPayment = true
                                paymentError = null
                                // Simulate network / payment gateway delay
                                kotlinx.coroutines.delay(2000)
                                
                                // Randomly fail to fulfill the "Menampilkan Pesan Pembayaran Gagal / Timeout" use case
                                val isSuccess = Math.random() > 0.3
                                
                                if (isSuccess) {
                                    // Deduct balance
                                    val deducted = viewModel.deductBalance(totalPayment)
                                    if (deducted) {
                                        isProcessingPayment = false
                                        
                                        // Capture transaction details BEFORE clearing cart
                                        confirmedOrderNumber = "#IKN-${System.currentTimeMillis().toString().takeLast(5)}"
                                        confirmedTotalAmount = totalPayment
                                        
                                        // Create order
                                        viewModel.checkoutCart(
                                            deliveryAddress = deliveryAddress,
                                            notes = "Kurir Jastip: Kilat Sameday" + 
                                                    (if (isIceboxProtected) " + Proteksi Fresh Icebox" else "") + 
                                                    ". Metode: " + getPaymentMethodName(selectedPaymentMethod),
                                            customerName = "$customerName ($customerPhone)",
                                            onSuccess = { orderId ->
                                                onCheckoutSuccess(confirmedOrderNumber, confirmedTotalAmount, orderId)
                                            }
                                        )
                                    } else {
                                        isProcessingPayment = false
                                        showInsufficientBalanceDialog = true
                                    }
                                } else {
                                    isProcessingPayment = false
                                    paymentError = "Pembayaran Gagal / Timeout.\nSistem gagal memverifikasi pembayaran. Silakan coba lagi."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .widthIn(min = 160.dp)
                            .testTag("btn_checkout_pay"),
                        enabled = !isProcessingPayment
                    ) {
                        if (isProcessingPayment) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Bayar Sekarang",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5FAFD))
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5FAFD)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // 1. Clickable Shipping Address Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2F1F8)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAddressDialog = true }
                        .testTag("card_shipping_address")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Alamat Pengiriman",
                                    tint = Color(0xFF006684),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Alamat Pengiriman",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF001F2A)
                                )
                            }
                            
                            // Visual affordance - Clickable indicator
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Ubah",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF006684)
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Ubah Alamat",
                                    tint = Color(0xFF006684),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "$customerName ($customerPhone)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF001F2A)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = deliveryAddress,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4C626B),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status badge "Alamat Utama" matching visual palette
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE0F2F1))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Alamat Utama",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF004D40)
                            )
                        }
                    }
                }
            }

            // 2. Ringkasan Pesanan (Order Items)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2F1F8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = "Order Summary",
                                tint = Color(0xFF006684),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ringkasan Pesanan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF001F2A)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (cartItems.isEmpty()) {
                            // Show the gorgeous mock items matching the user's reference mockup image
                            CheckoutItemRow(
                                title = "Kakap Merah Segar (Lokal)",
                                detail = "Berat: 1.5 kg",
                                priceText = currencyFormatter.format(187500) + " / 1.5kg",
                                iconFallback = Icons.Default.WaterDrop
                            )
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFECEFF1))
                            CheckoutItemRow(
                                title = "Udang Vaname Grade A",
                                detail = "Berat: 2.0 kg",
                                priceText = currencyFormatter.format(240000) + " / 2kg",
                                iconFallback = Icons.Default.Home
                            )
                        } else {
                            cartItems.forEachIndexed { index, item ->
                                CheckoutItemRow(
                                    title = item.title,
                                    detail = "Berat: ${item.quantity}.0 kg",
                                    priceText = currencyFormatter.format(item.price * item.quantity) + " / ${item.quantity}kg",
                                    iconFallback = if (index % 2 == 0) Icons.Default.WaterDrop else Icons.Default.Home
                                )
                                if (index < cartItems.size - 1) {
                                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFECEFF1))
                                }
                            }
                        }
                    }
                }
            }

            // 3. Shipping Options Group & Fresh Protection
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2F1F8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = "Pilihan Ekspedisi",
                                tint = Color(0xFF006684),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pengiriman & Proteksi Segar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF001F2A)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stack Option 1 - Jastip Kilat Sameday (Essential for fish)
                        ShippingOptionRow(
                            title = "Jastip Kilat Sameday",
                            eta = "Estimasi tiba hari ini sebelum jam 16:00 WIB (Terjamin)",
                            badgeText = "Sangat Disarankan untuk Seafood Basah",
                            price = currencyFormatter.format(45000),
                            isSelected = true,
                            onClick = {}
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color(0xFFECEFF1))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Protection Option: Styrofoam box with Ice Packs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isIceboxProtected) Color(0xFFE0F7FA).copy(alpha = 0.4f) else Color.Transparent)
                                .border(
                                    BorderStroke(
                                        width = 1.dp,
                                        color = if (isIceboxProtected) Color(0xFF006684) else Color(0xFFECEFF1)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { isIceboxProtected = !isIceboxProtected }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE0F2F1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📦", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Proteksi Icebox",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF001F2A)
                                )
                                Text(
                                    text = "Gunakan Box Styrofoam + Ice Pack beku agar kesegaran ikan optimal.",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    lineHeight = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = currencyFormatter.format(20000),
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF006684)
                                )
                                Switch(
                                    checked = isIceboxProtected,
                                    onCheckedChange = { isIceboxProtected = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF006684),
                                        uncheckedThumbColor = Color.Gray,
                                        uncheckedTrackColor = Color(0xFFECEFF1)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 4. Metode Pembayaran - (Hick's Law Accordion Dropdown style to combat visual clutter)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2F1F8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = "Metode Pembayaran",
                                tint = Color(0xFF006684),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Metode Pembayaran",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF001F2A)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Category A: Transfer Bank (Virtual Account) Accordion
                        PaymentAccordionHeader(
                            title = "Transfer Bank (Virtual Account)",
                            isExpanded = isVaExpanded,
                            onToggle = { isVaExpanded = !isVaExpanded }
                        )
                        
                        AnimatedVisibility(visible = isVaExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                PaymentItemRow(id = 1, name = "BCA Virtual Account", selectedId = selectedPaymentMethod, onSelect = { selectedPaymentMethod = it })
                                PaymentItemRow(id = 2, name = "BNI Virtual Account", selectedId = selectedPaymentMethod, onSelect = { selectedPaymentMethod = it })
                                PaymentItemRow(id = 3, name = "BRI Virtual Account", selectedId = selectedPaymentMethod, onSelect = { selectedPaymentMethod = it })
                                PaymentItemRow(id = 4, name = "Mandiri Virtual Account", selectedId = selectedPaymentMethod, onSelect = { selectedPaymentMethod = it })
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color(0xFFECEFF1))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Category B: E-Wallet Accordion
                        PaymentAccordionHeader(
                            title = "E-Wallet",
                            isExpanded = isWalletExpanded,
                            onToggle = { isWalletExpanded = !isWalletExpanded }
                        )

                        AnimatedVisibility(visible = isWalletExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                PaymentItemRow(id = 5, name = "GoPay", selectedId = selectedPaymentMethod, onSelect = { selectedPaymentMethod = it })
                                PaymentItemRow(id = 6, name = "Dana", selectedId = selectedPaymentMethod, onSelect = { selectedPaymentMethod = it })
                                PaymentItemRow(id = 7, name = "OVO", selectedId = selectedPaymentMethod, onSelect = { selectedPaymentMethod = it })
                                PaymentItemRow(id = 8, name = "ShopeePay", selectedId = selectedPaymentMethod, onSelect = { selectedPaymentMethod = it })
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color(0xFFECEFF1))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Standalone Option C: QRIS (E-Wallet consolidated instant scan)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedPaymentMethod == 9) Color(0xFFE0F7FA).copy(alpha = 0.5f) else Color.Transparent)
                                .border(
                                    BorderStroke(
                                        width = if (selectedPaymentMethod == 9) 1.5.dp else 1.dp,
                                        color = if (selectedPaymentMethod == 9) Color(0xFF006684) else Color(0xFFECEFF1)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedPaymentMethod = 9 }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = null,
                                tint = Color(0xFF006684),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "QRIS",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF001F2A)
                                )
                                Text(
                                    text = "Bayar instan via GoPay, OVO, Dana, LinkAja",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            RadioButton(
                                selected = selectedPaymentMethod == 9,
                                onClick = { selectedPaymentMethod = 9 },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF006684))
                            )
                        }
                    }
                }
            }

            // 5. Rincian Pembayaran
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2F1F8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = "Rincian Pembayaran",
                                tint = Color(0xFF006684),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Rincian Pelunasan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF001F2A)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal Seafood", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                            Text(currencyFormatter.format(subtotal), fontWeight = FontWeight.SemiBold, color = Color(0xFF001F2A))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Fee Jastip (Kalkulasi Otomatis)", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                            Text(currencyFormatter.format(totalJastipFee), fontWeight = FontWeight.SemiBold, color = Color(0xFF001F2A))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ongkir Kilat Sameday", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                            Text(currencyFormatter.format(shippingFee), fontWeight = FontWeight.SemiBold, color = Color(0xFF001F2A))
                        }

                        if (isIceboxProtected) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Proteksi Box Styrofoam + Ice Pack", color = Color(0xFF00838F), style = MaterialTheme.typography.bodyMedium)
                                Text(currencyFormatter.format(iceboxFee), fontWeight = FontWeight.Bold, color = Color(0xFF00838F))
                            }
                        }

                        Divider(color = Color(0xFFECEFF1), modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Pembayaran", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF001F2A))
                            Text(currencyFormatter.format(totalPayment), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge, color = Color(0xFF006684))
                        }
                    }
                }
            }
        }
    }

    // Dynamic Address Modifier Dialog (Modifies Recipient name + address on clicking address card)
    if (paymentError != null) {
        AlertDialog(
            onDismissRequest = { paymentError = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pembayaran Gagal", fontWeight = FontWeight.ExtraBold, color = Color.Red)
                }
            },
            text = {
                Text(paymentError ?: "")
            },
            confirmButton = {
                Button(
                    onClick = { paymentError = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Tutup", color = Color.White)
                }
            }
        )
    }

    if (showAddressDialog) {
        var tempName by remember { mutableStateOf(customerName) }
        var tempPhone by remember { mutableStateOf(customerPhone) }
        var tempAddr by remember { mutableStateOf(deliveryAddress) }

        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF006684))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ubah Alamat Pengiriman", fontWeight = FontWeight.ExtraBold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Nama Lengkap") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { tempPhone = it },
                        label = { Text("No. Telepon / WhatsApp") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tempAddr,
                        onValueChange = { tempAddr = it },
                        label = { Text("Alamat Lengkap") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.trim().isNotEmpty() && tempAddr.trim().isNotEmpty()) {
                            customerName = tempName
                            customerPhone = tempPhone
                            deliveryAddress = tempAddr
                            showAddressDialog = false
                            Toast.makeText(context, "Alamat berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Nama dan Alamat tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }

    // Insufficient Balance Dialog
    if (showInsufficientBalanceDialog) {
        AlertDialog(
            onDismissRequest = { showInsufficientBalanceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFFFF6F00))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Saldo Tidak Mencukupi", fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF6F00))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Saldo WavePay Anda tidak mencukupi untuk menyelesaikan transaksi ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4C626B)
                    )
                    Divider(color = Color(0xFFECEFF1))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Saldo Anda:", fontWeight = FontWeight.Medium, color = Color.Gray)
                        Text(currencyFormatter.format(userBalance), fontWeight = FontWeight.Bold, color = Color(0xFF001F2A))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Pembayaran:", fontWeight = FontWeight.Medium, color = Color.Gray)
                        Text(currencyFormatter.format(totalPayment), fontWeight = FontWeight.Bold, color = Color(0xFF006684))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Kekurangan:", fontWeight = FontWeight.Bold, color = Color.Red)
                        Text(currencyFormatter.format(totalPayment - userBalance), fontWeight = FontWeight.ExtraBold, color = Color.Red)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInsufficientBalanceDialog = false
                        onNavigateToTopUp()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Top Up Saldo", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showInsufficientBalanceDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun CheckoutItemRow(
    title: String,
    detail: String,
    priceText: String,
    iconFallback: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFE0F7FA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconFallback,
                contentDescription = null,
                tint = Color(0xFF00838F),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF001F2A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = detail,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

        Text(
            text = priceText,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF006684),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ShippingOptionRow(
    title: String,
    eta: String,
    badgeText: String?,
    price: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color(0xFFE0F7FA).copy(alpha = 0.5f) else Color.Transparent)
            .border(
                BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) Color(0xFF006684) else Color(0xFFECEFF1)
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF001F2A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = eta,
                fontSize = 11.sp,
                color = Color.Gray
            )
            if (badgeText != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = price,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF001F2A)
            )
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF006684))
            )
        }
    }
}

@Composable
fun PaymentAccordionHeader(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "rotation")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF001F2A)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Sembunyikan" else "Tampilkan",
            tint = Color(0xFF006684),
            modifier = Modifier.rotate(rotationState)
        )
    }
}

@Composable
fun PaymentItemRow(
    id: Int,
    name: String,
    selectedId: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(id) }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                tint = Color(0xFF006684),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF001F2A)
            )
        }

        RadioButton(
            selected = selectedId == id,
            onClick = { onSelect(id) },
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF006684))
        )
    }
}

private fun getPaymentMethodName(id: Int): String {
    return when (id) {
        1 -> "BCA Virtual Account"
        2 -> "BNI Virtual Account"
        3 -> "BRI Virtual Account"
        4 -> "Mandiri Virtual Account"
        5 -> "GoPay"
        6 -> "Dana"
        7 -> "OVO"
        8 -> "ShopeePay"
        else -> "QRIS"
    }
}
