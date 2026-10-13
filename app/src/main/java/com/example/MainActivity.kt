package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.DirectionsBike
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.JastipViewModel
import com.example.viewmodel.JastipViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val app = context.applicationContext as Application
                val viewModel: JastipViewModel = viewModel(
                    factory = JastipViewModelFactory(app)
                )

                IkanKuJastiperApp(viewModel = viewModel)
            }
        }
    }
}

data class NavigationTab(
    val route: String,
    val label: String,
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector,
    val tag: String
)

@Composable
fun IkanKuJastiperApp(viewModel: JastipViewModel) {
    android.util.Log.d("IkanKuApp", "IkanKuJastiperApp Composing")
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var currentUserRole by remember { mutableStateOf("pembeli") }

    val tabs = remember(currentUserRole) {
        when (currentUserRole) {
            "admin" -> emptyList() // ✅ Admin tidak pakai bottom nav
            "jastiper" -> {
                listOf(
                    NavigationTab("jastiper_dashboard", "Beranda", Icons.Outlined.Home, Icons.Filled.Home, "nav_home"),
                    NavigationTab("jastiper_pesanan", "Pesanan", Icons.AutoMirrored.Outlined.Assignment, Icons.AutoMirrored.Filled.Assignment, "nav_pesanan"),
                    NavigationTab("jastiper_live", "Live", Icons.Outlined.LiveTv, Icons.Filled.LiveTv, "nav_live"),
                    NavigationTab("jastiper_chat", "Chat", Icons.Outlined.Forum, Icons.Filled.Forum, "nav_chat"),
                    NavigationTab("jastiper_profile", "Profil", Icons.Outlined.Person, Icons.Filled.Person, "nav_profile")
                )
            }
            else -> {
                listOf(
                    NavigationTab("beranda", "Beranda", Icons.Outlined.Home, Icons.Filled.Home, "nav_beranda"),
                    NavigationTab("dashboard?scrollToAll={scrollToAll}", "Jastip", Icons.AutoMirrored.Outlined.DirectionsBike, Icons.AutoMirrored.Filled.DirectionsBike, "nav_dashboard"),
                    NavigationTab("chat_list", "Chat", Icons.Outlined.Forum, Icons.Filled.Forum, "nav_chats"),
                    NavigationTab("user_profile", "Profil", Icons.Outlined.Person, Icons.Filled.Person, "nav_profile")
                )
            }
        }
    }

    val bottomTabRoutes = remember(tabs) { tabs.map { it.route } }
    val isChatDetailOpen by viewModel.isChatDetailOpen.collectAsState()
    val shouldShowBottomBar = currentRoute in bottomTabRoutes && !isChatDetailOpen

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White, // Pastikan background putih, jangan biarkan transparan ke sistem
        bottomBar = {
            if (tabs.isNotEmpty() && (shouldShowBottomBar || (currentUserRole == "jastiper" && currentRoute == "jastiper_notifications"))) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    tabs.forEach { tab ->
                        val isSelected = currentRoute == tab.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    // Refresh and Scroll to top if active tab is clicked
                                    viewModel.refreshAllData()
                                    viewModel.emitScrollToTop()
                                } else {
                                    // Trigger refresh data when switching tabs (standard behavior)
                                    viewModel.refreshAllData()

                                    val navigateRoute = if (tab.route.startsWith("dashboard")) "dashboard?scrollToAll=false" else tab.route
                                    navController.navigate(navigateRoute) {
                                        val rootRoute = if (currentUserRole == "jastiper") "jastiper_dashboard" else "beranda"
                                        popUpTo(rootRoute) {
                                            saveState = (tab.route != rootRoute)
                                        }
                                        launchSingleTop = true
                                        restoreState = (tab.route != rootRoute)
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.iconFilled else tab.iconOutlined,
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label) },
                            modifier = Modifier.testTag(tab.tag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            // Login Screen
            composable("login") {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { role ->
                        currentUserRole = role
                        val nextDest = when (role) {
                            "admin" -> "admin_dashboard"
                            "jastiper" -> "jastiper_dashboard"
                            else -> "beranda"
                        }
                        navController.navigate(nextDest) {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onSignUpClick = {
                        navController.navigate("signup")
                    }
                )
            }

            // Signup / Register Screen
            composable("signup") {
                SignUpScreen(
                    viewModel = viewModel,
                    onSignUpSuccess = {
                        // Navigate to login after registration instead of dashboard
                        navController.navigate("login") {
                            popUpTo("signup") { inclusive = true }
                        }
                    },
                    onJastipSignUpClick = {
                        navController.navigate("jastip_register") {
                            popUpTo("signup") { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navController.navigate("login") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                )
            }

            // Jastip multi-step registration screen
            composable("jastip_register") {
                JastipRegisterScreen(
                    viewModel = viewModel,
                    onRegistrationCompleted = {
                        // Navigate to login after registration instead of dashboard
                        navController.navigate("login") {
                            popUpTo("jastip_register") { inclusive = true }
                        }
                    },
                    onBackToLogin = {
                        navController.navigate("login") {
                            popUpTo("jastip_register") { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // Jastiper Dashboard Screen
            composable("jastiper_dashboard") {
                JastiperDashboardScreen(
                    viewModel = viewModel,
                    onLogoutClick = {
                        currentUserRole = "pembeli"
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToNotifications = {
                        navController.navigate("jastiper_notifications")
                    },
                    onNavigateToLive = {
                        navController.navigate("jastiper_live")
                    }
                )
            }

            composable("jastiper_pesanan") {
                JastiperPesananScreen(
                    viewModel = viewModel,
                    onNavigateToNotifications = {
                        navController.navigate("jastiper_notifications")
                    }
                )
            }

            composable("jastiper_chat") {
                JastiperChatScreen(
                    viewModel = viewModel,
                    onNavigateToNotifications = {
                        navController.navigate("jastiper_notifications")
                    }
                )
            }

            composable("jastiper_live") {
                JastiperLiveScreen(
                    viewModel = viewModel,
                    onNavigateToNotifications = {
                        navController.navigate("jastiper_notifications")
                    }
                )
            }

            composable("jastiper_notifications") {
                JastiperNotificationScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("jastiper_pendapatan") {
                JastiperPendapatanScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToTarikSaldo = {
                        navController.navigate("jastiper_tarik_saldo")
                    }
                )
            }

            composable("jastiper_profile") {
                JastiperProfileScreen(
                    viewModel = viewModel,
                    onLogoutClick = {
                        currentUserRole = "pembeli"
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToPendapatan = {
                        navController.navigate("jastiper_pendapatan")
                    },
                    onNavigateToTarikSaldo = {
                        navController.navigate("jastiper_tarik_saldo")
                    },
                    onNavigateToJastiperService = {
                        navController.navigate("jastiper_service")
                    },
                    onNavigateToPanduanJastiper = {
                        navController.navigate("jastiper_panduan")
                    },
                    onNavigateToEditProfile = {
                        navController.navigate("edit_profile")
                    },
                    onNavigateToAlamat = {
                        navController.navigate("alamat_saya")
                    },
                    onNavigateToPin = {
                        navController.navigate("pin_transaksi")
                    },
                    onNavigateToChangePassword = {
                        navController.navigate("change_password")
                    },
                    onNavigateToNotifications = {
                        navController.navigate("jastiper_notifications")
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("jastiper_panduan") {
                JastiperPanduanScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("jastiper_service") {
                JastiperServiceScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("jastiper_tarik_saldo") {
                JastiperTarikSaldoScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Beranda screen
            composable("beranda") {
                val liveMitras by viewModel.liveMitras.collectAsState()
                BerandaScreen(
                    viewModel = viewModel,
                    onNavigateToJastip = {
                        navController.navigate("dashboard?scrollToAll=true") {
                            popUpTo("beranda") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToLive = {
                        if (liveMitras.size == 1) {
                            // Jika terdapat 1 live yang sedang berlangsung, tampilkan live tersebut secara otomatis
                            navController.navigate("live/${liveMitras.first().id}")
                        } else {
                            // Jika terdapat beberapa live atau tidak ada (0), arahkan ke halaman penemuan live
                            // Halaman ini akan menangani daftar live aktif atau state kosong (empty state)
                            navController.navigate("live_discovery")
                        }
                    },
                    onNavigateToNotifications = {
                        navController.navigate("user_notifications")
                    }
                )
            }

            composable("live_discovery") {
                LiveDiscoveryScreen(
                    viewModel = viewModel,
                    onNavigateToLive = { mitraId ->
                        navController.navigate("live/$mitraId")
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable("user_notifications") {
                UserNotificationScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToOrderDetail = { orderId ->
                        navController.navigate("detail_pesanan/$orderId")
                    }
                )
            }

            composable("cart") {
                CartScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToJastip = {
                        // Navigate back to previous screen or dashboard
                        if (!navController.popBackStack()) {
                            navController.navigate("dashboard") {
                                popUpTo("beranda") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onNavigateToCheckout = {
                        navController.navigate("checkout")
                    }
                )
            }

            // Checkout screen
            composable("checkout") {
                CheckoutScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onCheckoutSuccess = { orderNumber, totalAmount, orderId ->
                        navController.navigate("payment_success/$orderNumber/$totalAmount/$orderId") {
                            popUpTo("checkout") { inclusive = true }
                        }
                    },
                    onNavigateToTopUp = {
                        navController.navigate("saldo_wavepay")
                    },
                    onNavigateToOrders = {
                        navController.navigate("pesanan_saya") {
                            popUpTo("beranda") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToOrderDetail = { orderId ->
                        navController.navigate("detail_pesanan/$orderId") {
                            popUpTo("checkout") { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate("beranda") {
                            popUpTo("beranda") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // News screen
            composable("news") {
                NewsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Customer Live screen
            composable(
                route = "live/{mitraId}",
                arguments = listOf(navArgument("mitraId") { type = NavType.StringType })
            ) { backStackEntry ->
                val mitraId = backStackEntry.arguments?.getString("mitraId") ?: "jastip_joko"
                LiveScreen(
                    viewModel = viewModel,
                    mitraId = mitraId,
                    onNavigateToCart = { navController.navigate("cart") },
                    onNavigateToCheckout = { navController.navigate("checkout") },
                    onNavigateToTopUp = { navController.navigate("saldo_wavepay") },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Dashboard (Jastip marketplace list) Screen
            composable(
                route = "dashboard?scrollToAll={scrollToAll}",
                arguments = listOf(navArgument("scrollToAll") { defaultValue = "false"; type = NavType.StringType })
            ) { backStackEntry ->
                val scrollToAll = backStackEntry.arguments?.getString("scrollToAll") == "true"
                DashboardScreen(
                    viewModel = viewModel,
                    scrollToAll = scrollToAll,
                    onNavigateToOrderDetail = { orderId -> navController.navigate("order_detail/$orderId") },
                    onNavigateToChat = { targetOrderId ->
                        navController.navigate("chat_detail/$targetOrderId")
                    },
                    onNavigateToLive = { mitraId ->
                        navController.navigate("live/$mitraId")
                    },
                    onNavigateToNotifications = {
                        navController.navigate("user_notifications")
                    }
                )
            }

            // About Us screen
            composable("about_us") {
                AboutUsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("edit_profile") {
                EditProfileScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("alamat_saya") {
                AlamatSayaScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { addressId -> navController.navigate("edit_alamat/$addressId") },
                    onAddClick = { navController.navigate("tambah_alamat") }
                )
            }

            composable("tambah_alamat") {
                TambahAlamatScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "edit_alamat/{addressId}",
                arguments = listOf(navArgument("addressId") { type = NavType.StringType })
            ) { backStackEntry ->
                val addressId = backStackEntry.arguments?.getString("addressId") ?: ""
                EditAlamatScreen(
                    viewModel = viewModel,
                    addressId = addressId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("pin_transaksi") {
                PinTransaksiScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToUbahPin = { navController.navigate("ubah_pin") },
                    onNavigateToLupaPin = { navController.navigate("lupa_pin") }
                )
            }

            composable("ubah_pin") {
                UbahPinScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("lupa_pin") {
                LupaPinScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("user_profile") {
                UserProfileScreen(
                    viewModel = viewModel,
                    onLogoutClick = {
                        currentUserRole = "pembeli"
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToAboutUs = {
                        navController.navigate("about_us")
                    },
                    onNavigateToNews = {
                        navController.navigate("news")
                    },
                    onNavigateToEditProfile = {
                        navController.navigate("edit_profile")
                    },
                    onNavigateToAlamat = {
                        navController.navigate("alamat_saya")
                    },
                    onNavigateToPin = {
                        navController.navigate("pin_transaksi")
                    },
                    onNavigateToChangePassword = {
                        navController.navigate("change_password")
                    },
                    onNavigateToCustomerService = {
                        navController.navigate("customer_service")
                    },
                    onNavigateToPesanan = {
                        navController.navigate("pesanan_saya")
                    },
                    onNavigateToSaldoWavepay = {
                        navController.navigate("saldo_wavepay")
                    },
                    onNavigateToJastipRegister = {
                        navController.navigate("jastip_register")
                    },
                    onNavigateToNotifications = {
                        navController.navigate("user_notifications")
                    }
                )
            }

            composable("saldo_wavepay") {
                SaldoWavepayScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToIsiSaldo = { navController.navigate("isi_saldo") }
                )
            }

            composable("isi_saldo") {
                IsiSaldoScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToInstruction = { method, amount, transactionId ->
                        navController.navigate("instruction_payment/$method/$amount/$transactionId")
                    }
                )
            }

            composable(
                route = "instruction_payment/{method}/{amount}/{transactionId}",
                arguments = listOf(
                    navArgument("method") { type = NavType.StringType },
                    navArgument("amount") { type = NavType.StringType },
                    navArgument("transactionId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val method = backStackEntry.arguments?.getString("method") ?: ""
                val amount = backStackEntry.arguments?.getString("amount") ?: "0"
                val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
                InstructionPaymentScreen(
                    method = method,
                    amount = amount,
                    onBackClick = { navController.popBackStack() },
                    onCheckStatusClick = {
                        val popUpDestination = if (currentUserRole == "jastiper") "jastiper_dashboard" else "beranda"
                        navController.navigate(popUpDestination) {
                            popUpTo(popUpDestination) { inclusive = true }
                        }
                    }
                )
            }

            composable("change_password") {
                ChangePasswordScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(
                route = "payment_success/{orderNumber}/{totalAmount}/{orderId}",
                arguments = listOf(
                    navArgument("orderNumber") { type = NavType.StringType },
                    navArgument("totalAmount") { type = NavType.StringType }, // Total amount as string to avoid precision issues in URL
                    navArgument("orderId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val orderNumber = backStackEntry.arguments?.getString("orderNumber") ?: ""
                val totalAmountStr = backStackEntry.arguments?.getString("totalAmount") ?: "0"
                val totalAmount = totalAmountStr.toDoubleOrNull() ?: 0.0
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""

                PaymentSuccessScreen(
                    orderNumber = orderNumber,
                    totalAmount = totalAmount,
                    onNavigateToOrderDetail = {
                        navController.navigate("detail_pesanan/$orderId") {
                            popUpTo("beranda")
                        }
                    },
                    onNavigateToOrders = {
                        navController.navigate("pesanan_saya") {
                            popUpTo("beranda")
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate("beranda") {
                            popUpTo("beranda") { inclusive = true }
                        }
                    }
                )
            }

            composable("customer_service") {
                CustomerServiceScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable("pesanan_saya") {
                PesananSayaScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToDetail = { orderId -> navController.navigate("detail_pesanan/$orderId") },
                    onNavigateToLacak = { navController.navigate("lacak_pesanan") }
                )
            }

            composable("lacak_pesanan") {
                LacakPesananScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "detail_pesanan/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                DetailPesananScreen(
                    orderId = orderId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToChat = { targetOrderId ->
                        navController.navigate("chat_detail/$targetOrderId")
                    }
                )
            }

            composable("kurir_dashboard") {
                KurirDashboardScreen(
                    viewModel = viewModel,
                    onBackClick = { 
                        currentUserRole = "pembeli"
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("admin_dashboard") {
                AdminDashboardScreen(
                    viewModel = viewModel,
                    onLogout = {
                        currentUserRole = "pembeli"
                        navController.navigate("login") {
                            popUpTo("admin_dashboard") { inclusive = true }
                        }
                    }
                )
            }

            // Order Detail Screen (accepts orderId string)
            composable(
                route = "order_detail/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderDetailScreen(
                    orderId = orderId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToChat = { targetOrderId -> navController.navigate("chat_detail/$targetOrderId") }
                )
            }

            // Chat Threads list root Screen
            composable("chat_list") {
                ChatListScreen(
                    viewModel = viewModel,
                    onNavigateToChat = { orderId -> navController.navigate("chat_detail/$orderId") },
                    onNavigateToNotifications = { navController.navigate("user_notifications") }
                )
            }

            // Specific active chat detailing Screen (accepts orderId string)
            composable(
                route = "chat_detail/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                ChatDetailScreen(
                    orderId = orderId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToOrderDetail = { id ->
                        navController.navigate("detail_pesanan/$id")
                    },
                    onNavigateToPaymentSuccess = { orderNumber, totalAmount, id ->
                        navController.navigate("payment_success/$orderNumber/$totalAmount/$id") {
                            popUpTo("chat_detail/$id") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
