package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: JastipViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToJastip: () -> Unit,
    onNavigateToCheckout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cartItems by viewModel.allCartItems.collectAsState()

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showClearCartDialog by remember { mutableStateOf(false) }

    // Aggregate values
    val subtotal = remember(cartItems) { cartItems.sumOf { it.price * it.quantity } }
    val baseJastipFee = 15000.0
    val itemsJastipFee = remember(cartItems) { if (cartItems.isEmpty()) 0.0 else (cartItems.size - 1) * 5000.0 }
    val totalJastipFee = baseJastipFee + itemsJastipFee
    val totalPayment = subtotal + totalJastipFee

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5FAFD))
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5FAFD))
        ) {
            if (cartItems.isEmpty()) {
                // Empty state view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color(0xFFE0F2F1), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Keranjang Kosong",
                            tint = Color(0xFF006684),
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Keranjangmu Masih Kosong",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF001F2A),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Pesan seafood laut & tawar paling segar langsung dari pasar dibantu oleh mitra Jastip andalan kami.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4C626B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = onNavigateToJastip,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.81f)
                            .height(48.dp)
                            .testTag("btn_go_to_shop")
                    ) {
                        Text(
                            text = "Mulai Belanja Seafood",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                // Cart contents and check out block
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "${cartItems.size} Item terpilih di keranjang:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF006684),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        items(cartItems, key = { it.id }) { item ->
                            CartItemRow(
                                item = item,
                                currencyFormatter = currencyFormatter,
                                onQuantityChange = { newQty ->
                                    viewModel.updateCartQuantity(item.id, newQty)
                                },
                                onRemoveClick = {
                                    viewModel.removeFromCart(item.id)
                                    Toast.makeText(context, "${item.title} dihapus", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                    // Global Informational Banner - Moved from list to be always visible and non-repetitive
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA).copy(alpha = 0.9f)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFB2EBF2).copy(alpha = 0.8f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info Jastip",
                                tint = Color(0xFF006684),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Mitra jastip kami akan membelikan pesanan segar dari pasar yang sama secara terstandardisasi.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF004D40),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Order Summary Panel
                    Surface(
                        color = Color.White,
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .padding(18.dp)
                        ) {
                            Text(
                                text = "Ringkasan Pesanan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF001F2A),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal Seafood", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = currencyFormatter.format(subtotal),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF001F2A)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Biaya Jastip Pasar", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = currencyFormatter.format(totalJastipFee),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF001F2A)
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFECEFF1))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Estimasi", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFF001F2A))
                                Text(
                                    text = currencyFormatter.format(totalPayment),
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color(0xFF006684)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onNavigateToCheckout,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("btn_cart_checkout")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBike,
                                    contentDescription = "Pesan Jastip",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Lanjut ke Pembayaran",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Clear Cart Confirmation Dialog
    if (showClearCartDialog) {
        AlertDialog(
            onDismissRequest = { showClearCartDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kosongkan Keranjang",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Text(
                    text = "Yakin ingin mengosongkan semua item dari keranjang Anda?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearCartDialog = false
                        viewModel.clearCart() // Call clear action
                        Toast.makeText(context, "Keranjang berhasil dikosongkan", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ya, Kosongkan", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearCartDialog = false }
                ) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }

    // Checkout Details Dialog
    if (showCheckoutDialog) {
        var customerName by remember { mutableStateOf("Gwyneth Eunice") }
        var deliveryAddress by remember { mutableStateOf("Rungkut Asri Timur No 45, Surabaya") }
        var inputNotes by remember { mutableStateOf("Pilih ikan yang matanya masih bening ya pak.") }

        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "Detail Pengiriman",
                        tint = Color(0xFF006684)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Konfirmasi Jastip",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Konfirmasi detail pengiriman Anda agar mitra jastip kami bisa langsung berbelanja ke pasar setempat.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Nama Pemesan") },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = Color(0xFF006684)) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = { deliveryAddress = it },
                        label = { Text("Alamat Pengiriman") },
                        leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color(0xFF006684)) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputNotes,
                        onValueChange = { inputNotes = it },
                        label = { Text("Catatan Spesial untuk Jastiper") },
                        leadingIcon = { Icon(Icons.Outlined.Notes, contentDescription = null, tint = Color(0xFF006684)) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (deliveryAddress.trim().isEmpty() || customerName.trim().isEmpty()) {
                            Toast.makeText(context, "Nama dan alamat wajib diisi!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.checkoutCart(deliveryAddress, inputNotes, customerName)
                        showCheckoutDialog = false
                        Toast.makeText(context, "Permintaan Jastip Berhasil Dibuat!", Toast.LENGTH_LONG).show()
                        onNavigateToJastip()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006684)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_confirm_checkout_dialog")
                ) {
                    Text("Kirim Jastip", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCheckoutDialog = false }
                ) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun getProductEmojiAndColor(imageType: String, title: String): Pair<String, Color> {
    val lowerType = imageType.lowercase()
    val lowerTitle = title.lowercase()
    return when {
        lowerType.contains("salmon") || lowerTitle.contains("salmon") -> Pair("🐟", Color(0xFFFFEBEE))
        lowerType.contains("tuna") || lowerTitle.contains("tuna") -> Pair("🐠", Color(0xFFE0F7FA))
        lowerType.contains("udang") || lowerTitle.contains("udang") || lowerType == "p4" || lowerType.contains("vaname") -> Pair("🦐", Color(0xFFFFF3E0))
        lowerType.contains("kakap") || lowerTitle.contains("kakap") || lowerType == "p3" -> Pair("🐠", Color(0xFFFFEBEE))
        lowerType == "p1" || lowerTitle.contains("kembung") -> Pair("🐟", Color(0xFFE0F2F1))
        lowerType == "p2" || lowerTitle.contains("cumi") -> Pair("🦑", Color(0xFFEDE7F6))
        lowerType == "p5" || lowerTitle.contains("kepiting") || lowerTitle.contains("bakau") -> Pair("🦀", Color(0xFFFFF8E1))
        lowerType == "p6" || lowerTitle.contains("kerang") -> Pair("🐚", Color(0xFFE0F7FA))
        else -> Pair("🐟", Color(0xFFF1F5F9))
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    currencyFormatter: NumberFormat,
    onQuantityChange: (Int) -> Unit,
    onRemoveClick: () -> Unit
) {
    val (emoji, bgColor) = getProductEmojiAndColor(item.imageType, item.title)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2F1F8)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cart_item_card_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left icon block representation of fish choice with dynamic emoji & color
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Main info & operations Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Top Row: Title, Market, and independent Delete button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF001F2A),
                            softWrap = true
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = "Pasar",
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = item.market,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Top-right Delete button with optimized 44dp Touch Target
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("btn_remove_item_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Hapus item ini",
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Row: Price and Custom Symmetrical Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currencyFormatter.format(item.price)} /Kg",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF006684)
                    )

                    // Symmetrical and balanced quantitative stepper controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Symmetrical Minus button in darker grey for better contrast
                        IconButton(
                            onClick = {
                                if (item.quantity > 1) {
                                    onQuantityChange(item.quantity - 1)
                                }
                            },
                            enabled = item.quantity > 1,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (item.quantity > 1) Color(0xFFCFD8DC) else Color(0xFFECEFF1))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Kurang berat",
                                tint = if (item.quantity > 1) Color(0xFF001F2A) else Color(0xFF90A4AE),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Weight label count
                        Text(
                            text = "${item.quantity} kg",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF001F2A)
                        )

                        // Symmetrical Plus button in darker grey for better contrast
                        IconButton(
                            onClick = { onQuantityChange(item.quantity + 1) },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFCFD8DC))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah berat",
                                tint = Color(0xFF001F2A),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
