package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

// Data model representing a Jastip Partner (Mitra)
data class JastipMitra(
    val id: String,
    val name: String,
    val rating: Double,
    val city: String,
    val market: String,
    val fishTypes: String,
    val baseFee: Double,
    val ordersCompleted: Int,
    val initial: String,
    val initialBg: Color,
    val imageType: String,
    val specialties: List<String>,
    val isLive: Boolean,
    val isInstant: Boolean,
    val distanceKm: Double
)

class JastipViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: JastipRepository
    private val prefs = application.getSharedPreferences("ikanku_prefs", android.content.Context.MODE_PRIVATE)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = JastipRepository(database.jastipDao())
        
        viewModelScope.launch {
            try {
                repository.prepopulateDatabaseIfEmpty()
                repository.refreshOrders()
            } catch (e: Exception) {
                android.util.Log.e("JastipVM", "Error during init: ${e.message}")
            }
        }
    }

    // === PROFILE & BALANCE ===
    private val _loggedInEmail = MutableStateFlow(prefs.getString("logged_in_email", "admin@mail.com") ?: "admin@mail.com")
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val userProfile: StateFlow<UserProfile> = _loggedInEmail
        .flatMapLatest { email ->
            repository.getUserProfileFlow(email)
        }
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile("admin@mail.com", "Admin Utama", "081234567890")
        )

    private val _userBalance = MutableStateFlow(500000.0) // Default Rp 500.000
    val userBalance: StateFlow<Double> = _userBalance.asStateFlow()

    // === CART ===
    val allCartItems: StateFlow<List<CartItem>> = repository.allCartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === ORDERS ===
    val allOrders: StateFlow<List<JastipOrder>> = repository.allOrders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allMessages: StateFlow<List<ChatMessage>> = repository.allMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val availableOrders: StateFlow<List<JastipOrder>> = allOrders
        .map { list -> list.filter { it.status == OrderStatus.PENDING.name } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myActiveOrders: StateFlow<List<JastipOrder>> = combine(allOrders, userProfile) { orders, profile ->
        orders.filter { 
            (it.customerName.contains(profile.fullName, ignoreCase = true) || it.customerName.contains("Andi Pratama")) && 
            it.status in listOf(OrderStatus.ACCEPTED.name, OrderStatus.BUYING.name, OrderStatus.DELIVERING.name) 
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedOrders: StateFlow<List<JastipOrder>> = combine(allOrders, userProfile) { orders, profile ->
        orders.filter { 
            (it.customerName.contains(profile.fullName, ignoreCase = true) || it.customerName.contains("Andi Pratama")) && 
            it.status == OrderStatus.COMPLETED.name 
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === MITRA DATA ===
    private val _allMitras = MutableStateFlow<List<JastipMitra>>(
        listOf(
            JastipMitra(
                id = "jastip_joko",
                name = "Pak Joko",
                rating = 4.9,
                city = "Jakarta",
                market = "Muara Angke",
                fishTypes = "Salmon, Tuna, Kakap",
                baseFee = 15000.0,
                ordersCompleted = 312,
                initial = "J",
                initialBg = Color(0xFFE3F2FD),
                imageType = "tuna",
                specialties = listOf("Ikan Laut"),
                isLive = true,
                isInstant = true,
                distanceKm = 1.2
            ),
            JastipMitra(
                id = "jastip_yati",
                name = "Bu Yati",
                rating = 4.8,
                city = "Surabaya",
                market = "Pasar Pabean",
                fishTypes = "Udang, Cumi, Bandeng",
                baseFee = 12000.0,
                ordersCompleted = 256,
                initial = "Y",
                initialBg = Color(0xFFFFDCC5),
                imageType = "udang",
                specialties = listOf("Ikan Laut", "Udang & Kepiting", "Kerang & Cumi"),
                isLive = false,
                isInstant = true,
                distanceKm = 2.5
            ),
            JastipMitra(
                id = "jastip_adit",
                name = "Mas Adit",
                rating = 4.9,
                city = "Bandung",
                market = "Pasar Ciroyom",
                fishTypes = "Gurame, Lele, Mujair",
                baseFee = 10000.0,
                ordersCompleted = 198,
                initial = "A",
                initialBg = Color(0xFFE8F5E9),
                imageType = "gurame",
                specialties = listOf("Ikan Air Tawar"),
                isLive = false,
                isInstant = false,
                distanceKm = 3.1
            ),
            JastipMitra(
                id = "jastip_made",
                name = "Pak Made",
                rating = 5.0,
                city = "Bali",
                market = "Pasar Kedonganan",
                fishTypes = "Tuna, Lobster, Snapper",
                baseFee = 20000.0,
                ordersCompleted = 421,
                initial = "M",
                initialBg = Color(0xFFEDE7F6),
                imageType = "lobster",
                specialties = listOf("Ikan Laut", "Udang & Kepiting"),
                isLive = true,
                isInstant = false,
                distanceKm =  0.8
            ),
            JastipMitra(
                id = "jastip_sari",
                name = "Bu Sari",
                rating = 4.7,
                city = "Makassar",
                market = "Paotere",
                fishTypes = "Cakalang, Sunu, Baronang",
                baseFee = 13000.0,
                ordersCompleted = 178,
                initial = "S",
                initialBg = Color(0xFFFFF8E1),
                imageType = "snapper",
                specialties = listOf("Ikan Laut"),
                isLive = false,
                isInstant = true,
                distanceKm = 4.0
            ),
            JastipMitra(
                id = "jastip_rio",
                name = "Mas Rio",
                rating = 4.8,
                city = "Medan",
                market = "Pasar Bengkel",
                fishTypes = "Kerapu, Kakap, Udang",
                baseFee = 11000.0,
                ordersCompleted = 145,
                initial = "R",
                initialBg = Color(0xFFE0F2F1),
                imageType = "kakap",
                specialties = listOf("Ikan Laut", "Udang & Kepiting"),
                isLive = true,
                isInstant = true,
                distanceKm = 1.7
            )
        )
    )
    val allMitras: StateFlow<List<JastipMitra>> = _allMitras.asStateFlow()

    val liveMitras: StateFlow<List<JastipMitra>> = allMitras
        .map { list -> list.filter { it.isLive } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === CHAT ===
    private val _activeOrderIdForChat = MutableStateFlow<String?>(null)
    val activeOrderIdForChat: StateFlow<String?> = _activeOrderIdForChat.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val activeChatMessages: StateFlow<List<ChatMessage>> = combine(_activeOrderIdForChat, allOrders, allMitras) { orderId, orders, mitras ->
        Triple(orderId, orders, mitras)
    }.flatMapLatest { (orderId, orders, mitras) ->
        if (orderId == null) return@flatMapLatest flowOf(emptyList())
        
        // Find Jastiper name to consolidate messages
        val currentOrder = orders.find { it.id == orderId }
        val currentMitra = mitras.find { it.id == orderId }
        
        val jastiperName = currentOrder?.jastiperName ?: currentMitra?.name
        
        if (jastiperName == null) {
            // Fallback to just this orderId if name cannot be determined
            return@flatMapLatest repository.getMessagesForOrder(orderId)
        }

        // Consolidate: Find all order IDs for this jastiper + the jastiper's ID itself (for chat-only messages)
        val relatedOrderIds = mutableListOf<String>()
        
        // 1. Add all actual order IDs
        relatedOrderIds.addAll(orders.filter { it.jastiperName == jastiperName }.map { it.id })
        
        // 2. Add the mitra ID (room ID for chat-only)
        val mitraId = mitras.find { it.name == jastiperName }?.id
        if (mitraId != null && mitraId !in relatedOrderIds) {
            relatedOrderIds.add(mitraId)
        }
        
        // 3. Fallback: always include the requested orderId if not already present
        if (orderId !in relatedOrderIds) {
            relatedOrderIds.add(orderId)
        }
        
        val flows = relatedOrderIds.distinct().map { repository.getMessagesForOrder(it) }
        combine(flows) { messagesList ->
            messagesList.flatMap { it }.distinctBy { it.id }.sortedBy { it.timestamp }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectActiveChat(orderId: String?) {
        _activeOrderIdForChat.value = orderId
    }


    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            val statusText = when(status) {
                OrderStatus.BUYING -> "mengonfirmasi mulai belanja di pasar."
                OrderStatus.DELIVERING -> "sedang mengantarkan ikan pesanan ke alamat Anda."
                OrderStatus.COMPLETED -> "menyelesaikan pengiriman. Pesanan telah diterima!"
                else -> ""
            }
            if (statusText.isNotEmpty()) {
                repository.sendMessage(ChatMessage(orderId = orderId, senderName = "Sistem", isFromMe = true, text = "[INFO] Jastiper $statusText"))
            }
        }
    }

    fun submitRating(orderId: String, rating: Int) {
        viewModelScope.launch {
            repository.updateOrderRating(orderId, rating)
        }
    }

    fun submitComplaint(orderId: String, complaint: String) {
        viewModelScope.launch {
            repository.submitOrderComplaint(orderId, complaint)
        }
    }

    fun sendChatMessage(orderId: String, text: String) {
        if (text.trim().isEmpty()) return
        viewModelScope.launch {
            repository.sendMessage(ChatMessage(orderId = orderId, senderName = "Saya", isFromMe = true, text = text))
            delay(1000)
            val order = repository.getOrderById(orderId)
            val mitra = allMitras.value.find { it.id == orderId }
            val jastiperName = order?.jastiperName ?: mitra?.name ?: "Jastiper"
            repository.sendMessage(ChatMessage(orderId = orderId, senderName = jastiperName, isFromMe = false, text = "Siap kak, sedang diproses!"))
        }
    }

    // === BALANCE OPERATIONS ===
    fun topUpBalance(amount: Double) {
        viewModelScope.launch {
            _userBalance.value += amount
        }
    }

    fun deductBalance(amount: Double): Boolean {
        return if (_userBalance.value >= amount) {
            _userBalance.value -= amount
            true
        } else {
            false
        }
    }

    // === CART OPERATIONS ===
    fun addToCart(title: String, market: String, price: Double, imageType: String) {
        viewModelScope.launch {
            val items = allCartItems.value
            val existingItem = items.find { it.title == title && it.market == market }
            if (existingItem != null) {
                repository.updateCartItemQuantity(existingItem.id, existingItem.quantity + 1)
            } else {
                repository.insertCartItem(CartItem(title = title, market = market, price = price, imageType = imageType, quantity = 1))
            }
        }
    }

    fun updateCartQuantity(id: Int, quantity: Int) {
        viewModelScope.launch {
            if (quantity <= 0) repository.deleteCartItemById(id) else repository.updateCartItemQuantity(id, quantity)
        }
    }

    fun removeFromCart(id: Int) {
        viewModelScope.launch {
            repository.deleteCartItemById(id)
        }
    }

    fun clearCart() { 
        viewModelScope.launch { repository.clearCart() } 
    }

    fun checkoutCart(deliveryAddress: String, notes: String, customerName: String, onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            val items = allCartItems.value
            if (items.isEmpty()) return@launch
            val fishTypes = items.joinToString(", ") { "${it.title} (${it.quantity}kg)" }
            val totalWeight = items.sumOf { it.quantity }.toDouble()
            val newOrder = JastipOrder(
                customerName = customerName,
                fishType = fishTypes,
                weightKg = totalWeight,
                marketName = items.first().market,
                budgetEst = items.sumOf { it.price * it.quantity },
                jastipFee = 15000.0,
                deliveryAddress = deliveryAddress,
                notes = notes,
                status = OrderStatus.ACCEPTED.name
            )
            repository.createOrder(newOrder)
            
            // Create notification
            val notification = Notification(
                category = "PESANAN",
                title = "Pesanan Berhasil Dibuat!",
                description = "Pesanan ${fishTypes} telah berhasil dibuat dan menunggu konfirmasi Jastiper.",
                orderId = newOrder.id
            )
            repository.createNotification(notification)
            
            repository.clearCart()
            onSuccess(newOrder.id)
        }
    }

    // === PROFILE OPERATIONS ===
    fun updateProfile(email: String, fullName: String, phoneNumber: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.updateProfile(email, fullName, phoneNumber)
            onResult(success)
        }
    }

    fun login(email: String, password: String, role: String? = null, onResult: (Boolean, String?, String?) -> Unit) {
        viewModelScope.launch {
            val response = repository.login(email, password, role)
            if (response.success) {
                // Simpan email ke SharedPreferences agar persist saat restart
                prefs.edit().putString("logged_in_email", email).apply()
                _loggedInEmail.value = email

                // PENTING: Gunakan response.role (peran asli dari DB), 
                // jangan gunakan role dari UI sebagai fallback agar validasi tidak bocor.
                onResult(true, response.role, null)
            } else {
                onResult(false, null, response.message)
            }
        }
    }

    fun register(email: String, password: String, role: String, fullName: String = "", phoneNumber: String = "", onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val response = repository.register(email, password, role, fullName, phoneNumber)
            onResult(response.success, response.message)
        }
    }

    // === NOTIFICATIONS ===
    val allNotifications: StateFlow<List<Notification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch { repository.markNotificationAsRead(id) }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch { repository.markAllNotificationsAsRead() }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch { repository.deleteNotification(id) }
    }

    // === REFRESH DATA ===
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isChatDetailOpen = MutableStateFlow(false)
    val isChatDetailOpen: StateFlow<Boolean> = _isChatDetailOpen.asStateFlow()

    // Event for Scroll to Top functionality
    private val _scrollToTopEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val scrollToTopEvent = _scrollToTopEvent.asSharedFlow()

    fun emitScrollToTop() {
        viewModelScope.launch {
            _scrollToTopEvent.emit(Unit)
        }
    }


    fun setChatDetailOpen(isOpen: Boolean) {
        _isChatDetailOpen.value = isOpen
    }

    fun refreshAllData() {
        android.util.Log.d("JastipVM", "Refreshing all data...")
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                // Refresh data profile & orders
                repository.refreshOrders()
                // Simulate some delay for better UX visibility if needed, or just let it be fast
                delay(500) 
            } catch (e: Exception) {
                android.util.Log.e("JastipVM", "Error refreshing data: ${e.message}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    // === BALANCE TRANSACTIONS ===
    val allTransactions: StateFlow<List<BalanceTransaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createTopUpRequest(method: String, amount: Double, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val transaction = repository.createTopUpTransaction(method, amount)
                onSuccess(transaction.id)
            } catch (e: Exception) {
                android.util.Log.e("JastipVM", "Error creating top-up: ${e.message}")
            }
        }
    }

    fun confirmTopUp(transactionId: String, amount: Double) {
        viewModelScope.launch {
            try {
                repository.confirmTopUpTransaction(transactionId)
                topUpBalance(amount)
                
                val notification = Notification(
                    category = "SISTEM",
                    title = "Top Up Berhasil!",
                    description = "Saldo WavePay Anda telah bertambah Rp ${String.format("%,.0f", amount)}"
                )
                repository.createNotification(notification)
            } catch (e: Exception) {
                android.util.Log.e("JastipVM", "Error confirming top-up: ${e.message}")
            }
        }
    }

    // === ADDRESS MANAGEMENT ===
    private val _userAddresses = MutableStateFlow<List<UserAddress>>(
        listOf(
            UserAddress(
                id = "addr_1",
                label = "Rumah",
                receiverName = "Budi Santoso",
                phoneNumber = "0812-3456-7890",
                fullAddress = "Jl. Muara Karang Blok A Utara No. 15, RT.05/RW.02, Pluit, Penjaringan, Jakarta Utara, DKI Jakarta 14450",
                isPrimary = true
            ),
            UserAddress(
                id = "addr_2",
                label = "Kantor",
                receiverName = "Siti Aminah",
                phoneNumber = "0856-7890-1234",
                fullAddress = "Gedung Oceanic Tower, Lt. 12, Unit A, Jl. Sudirman No. 45, Kuningan, Jakarta Selatan, DKI Jakarta 12950",
                isPrimary = false
            )
        )
    )
    val userAddresses: StateFlow<List<UserAddress>> = _userAddresses.asStateFlow()

    fun addAddress(address: UserAddress) {
        _userAddresses.value = _userAddresses.value + address
    }

    fun updateAddress(updatedAddress: UserAddress) {
        _userAddresses.value = _userAddresses.value.map {
            if (it.id == updatedAddress.id) updatedAddress else it
        }
    }

    fun deleteAddress(addressId: String) {
        _userAddresses.value = _userAddresses.value.filter { it.id != addressId }
    }

    fun setPrimaryAddress(addressId: String) {
        _userAddresses.value = _userAddresses.value.map {
            it.copy(isPrimary = it.id == addressId)
        }
    }
}

class JastipViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JastipViewModel::class.java)) return JastipViewModel(application) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
