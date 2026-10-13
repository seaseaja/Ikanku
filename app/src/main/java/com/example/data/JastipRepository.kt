package com.example.data

import com.example.data.api.RetrofitClient
import com.example.data.api.UpdateProfileRequest
import com.example.data.api.LoginRequest
import com.example.data.api.RegisterRequest
import com.example.data.api.AuthResponse
import com.example.data.api.ApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import android.util.Log

class JastipRepository(private val jastipDao: JastipDao) {
    private val apiService = RetrofitClient.instance

    val allOrders: Flow<List<JastipOrder>> = jastipDao.getAllOrders()
    val allMessages: Flow<List<ChatMessage>> = jastipDao.getAllMessages()

    suspend fun refreshOrders() {
        Log.d("JastipDB", "Mencoba mengambil data dari server MySQL...")
        try {
            val orders = apiService.getOrders()
            Log.d("JastipDB", "Berhasil! Mendapatkan ${orders.size} pesanan dari MySQL")
            orders.forEach { jastipDao.insertOrder(it) }
        } catch (e: Exception) {
            Log.e("JastipDB", "Gagal konek ke database: ${e.message}")
        }
    }

    suspend fun getOrderById(id: String): JastipOrder? = jastipDao.getOrderById(id)

    suspend fun createOrder(order: JastipOrder) {
        jastipDao.insertOrder(order)
        try {
            apiService.createOrder(order)
        } catch (e: Exception) {
            Log.e("JastipAPI", "Gagal upload order: ${e.message}")
        }
    }

    suspend fun updateOrderStatus(id: String, status: OrderStatus) {
        jastipDao.updateOrderStatus(id, status.name)
        // Idealnya ada API update status juga di sini
    }

    suspend fun updateOrderRating(id: String, rating: Int) {
        jastipDao.updateOrderRating(id, rating)
    }

    suspend fun submitOrderComplaint(id: String, complaint: String) {
        jastipDao.updateOrderComplaintStatus(id, "DIPROSES")
    }

    fun getMessagesForOrder(orderId: String): Flow<List<ChatMessage>> = 
        jastipDao.getMessagesForOrder(orderId)

    suspend fun refreshMessages(orderId: String) {
        try {
            val messages = apiService.getMessages(orderId)
            messages.forEach { jastipDao.insertMessage(it) }
        } catch (e: Exception) {
            Log.e("JastipAPI", "Gagal refresh pesan: ${e.message}")
        }
    }

    suspend fun sendMessage(message: ChatMessage) {
        jastipDao.insertMessage(message)
        try {
            apiService.sendMessage(message)
        } catch (e: Exception) {
            Log.e("JastipAPI", "Gagal kirim pesan ke server: ${e.message}")
        }
    }

    suspend fun login(email: String, password: String, role: String? = null): AuthResponse {
        return try {
            Log.d("JastipAPI", "Mencoba login ke server: $email")
            val response = apiService.login(LoginRequest(email, password, role))
            
            if (response.success) {
                // Berhasil login di server, update data lokal agar sinkron.
                // PRIORITAS: Jika salah satu (Server atau Lokal) menyatakan user adalah 'jastiper', 
                // maka kita tetapkan sebagai 'jastiper'.
                val localUser = jastipDao.getUserByEmail(email)
                val finalRole = if (response.role == "jastiper" || localUser?.role == "jastiper") {
                    "jastiper"
                } else {
                    // Jika server mengembalikan role kosong atau null, gunakan data lokal.
                    val serverRole = if (response.role.isNullOrBlank()) null else response.role
                    serverRole ?: localUser?.role ?: "pembeli"
                }
                
                // Ambil Nama Lengkap dan Nomor HP dari server jika ada
                val finalFullName = response.full_name ?: localUser?.fullName ?: ""
                val finalPhoneNumber = response.no_hp ?: localUser?.phoneNumber ?: ""
                
                jastipDao.insertUser(UserAccount(
                    email = email, 
                    password = password, 
                    role = finalRole, 
                    fullName = finalFullName, 
                    phoneNumber = finalPhoneNumber
                ))
                
                // Kembalikan response dengan role yang valid agar UI dapat melakukan validasi dengan benar
                response.copy(role = finalRole, full_name = finalFullName, no_hp = finalPhoneNumber)
            } else {
                // Server menolak login (password salah / email tidak ada)
                response
            }
        } catch (e: Exception) {
            Log.e("JastipAPI", "LOGIN ERROR (Connection Failed): ${e.message}")
            
            // FALLBACK KE LOKAL jika server mati
            val localUser = jastipDao.getUserByEmail(email)
            if (localUser != null) {
                if (localUser.password == password) {
                    AuthResponse(
                        status = "success", 
                        message = "Login Offline (Server tidak terjangkau)", 
                        role = localUser.role,
                        full_name = localUser.fullName,
                        no_hp = localUser.phoneNumber
                    )
                } else {
                    AuthResponse(status = "error", message = "Password lokal salah!", role = null)
                }
            } else {
                AuthResponse(status = "error", message = "Gagal terhubung ke server dan akun tidak ditemukan di lokal.", role = null)
            }
        }
    }

    suspend fun register(email: String, password: String, role: String, fullName: String = "", phoneNumber: String = ""): ApiResponse {
        return try {
            Log.d("JastipAPI", "Mencoba daftar ke MySQL: $email dengan role $role")
            val response = apiService.register(RegisterRequest(email, password, role, no_hp = phoneNumber, full_name = fullName))
            
            if (response.success) {
                // Simpan ke Lokal HANYA jika berhasil di server
                val existingUser = jastipDao.getUserByEmail(email)
                if (existingUser != null) {
                    // Jika user sudah ada (upgrade), pertahankan data profile lain
                    jastipDao.insertUser(existingUser.copy(
                        role = role, 
                        password = password.ifEmpty { existingUser.password }, 
                        fullName = fullName.ifEmpty { existingUser.fullName },
                        phoneNumber = phoneNumber.ifEmpty { existingUser.phoneNumber }
                    ))
                } else {
                    jastipDao.insertUser(UserAccount(email, password, role, fullName = fullName, phoneNumber = phoneNumber))
                }
            } else if (response.message.contains("sudah ada", ignoreCase = true) || 
                       response.message.contains("terdaftar", ignoreCase = true)) {
                // LOGIKA UPGRADE: Jika email sudah ada di server, kita anggap ini permintaan upgrade.
                // Dalam aplikasi nyata, mungkin ada API khusus, tapi di sini kita sinkronkan role lokal.
                val existingUser = jastipDao.getUserByEmail(email)
                if (existingUser != null) {
                    jastipDao.insertUser(existingUser.copy(
                        role = role, 
                        fullName = if (fullName.isNotEmpty()) fullName else existingUser.fullName,
                        phoneNumber = if (phoneNumber.isNotEmpty()) phoneNumber else existingUser.phoneNumber
                    ))
                    return ApiResponse(status = "success", message = "Akun Anda berhasil diperbarui menjadi $role!")
                }
            }
            response
        } catch (e: Exception) {
            Log.e("JastipAPI", "ERROR REGISTER: ${e.message}")
            
            // FALLBACK REGISTER/UPGRADE LOKAL: Jika server tidak terjangkau, ijinkan pendaftaran/upgrade lokal
            val existingUser = jastipDao.getUserByEmail(email)
            if (existingUser != null) {
                jastipDao.insertUser(existingUser.copy(
                    role = role, 
                    fullName = if (fullName.isNotEmpty()) fullName else existingUser.fullName,
                    phoneNumber = if (phoneNumber.isNotEmpty()) phoneNumber else existingUser.phoneNumber
                ))
                return ApiResponse(status = "success", message = "Berhasil memperbarui status akun (Offline)")
            } else {
                // Buat user baru di lokal jika belum ada (berguna saat server mati/demo)
                jastipDao.insertUser(UserAccount(email, password, role, fullName = fullName, phoneNumber = phoneNumber))
                return ApiResponse(status = "success", message = "Pendaftaran berhasil (Mode Offline)")
            }
        }
    }

    suspend fun getUserByEmail(email: String): UserAccount? = jastipDao.getUserByEmail(email)

    fun getUserProfileFlow(email: String): Flow<UserProfile?> = jastipDao.getUserByEmailFlow(email).map { user ->
        user?.let {
            UserProfile(it.email, it.fullName, it.phoneNumber, it.role)
        }
    }

    suspend fun prepopulateDatabaseIfEmpty() {
        // Prepopulate orders
        val currentOrders = jastipDao.getAllOrders().first()
        val holdsOldData = currentOrders.isNotEmpty() && currentOrders.any { it.id.contains("budi") }
        if (currentOrders.isEmpty() || holdsOldData) {
            val joko = JastipOrder(
                id = "jastip_joko",
                customerName = "Saya",
                jastiperName = "Pak Joko",
                fishType = "Salmon, Tuna, Kakap",
                weightKg = 2.0,
                marketName = "Muara Angke, Jakarta",
                budgetEst = 150000.0,
                jastipFee = 15000.0,
                deliveryAddress = "Sunter Jaya Indah No. 12, Jakarta",
                notes = "Pilih salmon segar potongan fillet tebal rata.",
                status = OrderStatus.BUYING.name,
                timestamp = System.currentTimeMillis() - 3600000 * 2
            )

            val yati = JastipOrder(
                id = "jastip_yati",
                customerName = "Saya",
                jastiperName = "Bu Yati",
                fishType = "Udang, Cumi, Bandeng",
                weightKg = 1.5,
                marketName = "Pasar Pabean, Surabaya",
                budgetEst = 120000.0,
                jastipFee = 12000.0,
                deliveryAddress = "Dharmahusada Mas Blok C-4, Surabaya",
                notes = "Udang yang sedang, cumi tolong dibuang tintanya.",
                status = OrderStatus.ACCEPTED.name,
                timestamp = System.currentTimeMillis() - 3600000
            )
            
            jastipDao.insertOrder(joko)
            jastipDao.insertOrder(yati)

            // DUMMY DATA FOR COMPLETED ORDERS (HISTORY)
            val udin = JastipOrder(
                id = "jastip_udin",
                customerName = "Saya",
                jastiperName = "Bang Udin",
                fishType = "Kepiting Bakau (2kg)",
                weightKg = 2.0,
                marketName = "TPI Muara Angke",
                budgetEst = 280000.0,
                jastipFee = 25000.0,
                deliveryAddress = "Sunter Jaya Indah No. 12, Jakarta",
                notes = "Pilih yang capitnya masih lengkap.",
                status = OrderStatus.COMPLETED.name,
                timestamp = System.currentTimeMillis() - 86400000 // Kemarin
            )
            
            val sarah = JastipOrder(
                id = "jastip_sarah",
                customerName = "Saya",
                jastiperName = "Mbak Sarah",
                fishType = "Ikan Gurame (3 ekor)",
                weightKg = 1.8,
                marketName = "Pasar Modern",
                budgetEst = 95000.0,
                jastipFee = 10000.0,
                deliveryAddress = "Sunter Jaya Indah No. 12, Jakarta",
                notes = "Tolong dibersihkan sisiknya.",
                status = OrderStatus.COMPLETED.name,
                timestamp = System.currentTimeMillis() - 86400000 * 2 // 2 hari lalu
            )

            jastipDao.insertOrder(udin)
            jastipDao.insertOrder(sarah)
            
            // Initial messages for Bu Yati
            jastipDao.insertMessage(ChatMessage(
                orderId = "jastip_yati",
                senderName = "Saya",
                isFromMe = true,
                text = "Bu Yati, titip udang sama cumi segar di Pasar Pabean ya.",
                timestamp = System.currentTimeMillis() - 600000
            ))
        }

        // Prepopulate default admin user if not exists
        val existingAdmin = jastipDao.getUserByEmail("admin@mai.com")
        if (existingAdmin == null) {
            jastipDao.insertUser(UserAccount(
                email = "admin@mai.com",
                password = "admin#123",
                role = "admin",
                fullName = "Admin Utama",
                phoneNumber = "081234567890"
            ))
        } else if (existingAdmin.role != "admin") {
            // Fix legacy admin role if already inserted with wrong role
            jastipDao.insertUser(existingAdmin.copy(role = "admin", password = "admin#123"))
        }
    }

    val allCartItems: Flow<List<CartItem>> = jastipDao.getAllCartItems()
    suspend fun insertCartItem(cartItem: CartItem) = jastipDao.insertCartItem(cartItem)
    suspend fun updateCartItemQuantity(id: Int, quantity: Int) = jastipDao.updateCartItemQuantity(id, quantity)
    suspend fun deleteCartItemById(id: Int) = jastipDao.deleteCartItemById(id)
    suspend fun clearCart() = jastipDao.clearCart()

    suspend fun updateProfile(email: String, fullName: String, phoneNumber: String): Boolean {
        return try {
            val response = apiService.updateProfile(UpdateProfileRequest(email, fullName, phoneNumber))
            if (response.success) {
                // Update local database too to keep it in sync
                val existingUser = jastipDao.getUserByEmail(email)
                if (existingUser != null) {
                    jastipDao.insertUser(existingUser.copy(fullName = fullName, phoneNumber = phoneNumber))
                }
            }
            response.success
        } catch (e: Exception) {
            Log.e("JastipAPI", "Update Profile Error (Falling back to local): ${e.message}")
            
            // FALLBACK TO LOCAL: Jika server tidak terjangkau, tetap simpan di database lokal
            val existingUser = jastipDao.getUserByEmail(email)
            if (existingUser != null) {
                jastipDao.insertUser(existingUser.copy(fullName = fullName, phoneNumber = phoneNumber))
                true // Berhasil secara lokal
            } else {
                // Jika user tidak ada di lokal (sangat jarang), buat baru dengan password kosong
                jastipDao.insertUser(UserAccount(email = email, password = "", role = "user", fullName = fullName, phoneNumber = phoneNumber))
                true
            }
        }
    }

    // === NOTIFICATIONS ===
    val allNotifications: Flow<List<Notification>> = jastipDao.getAllNotifications()

    suspend fun createNotification(notification: Notification) {
        jastipDao.insertNotification(notification)
    }

    suspend fun markNotificationAsRead(id: String) {
        jastipDao.updateNotificationReadStatus(id, false)
    }

    suspend fun markAllNotificationsAsRead() {
        jastipDao.markAllNotificationsAsRead()
    }

    suspend fun deleteNotification(id: String) {
        jastipDao.deleteNotification(id)
    }

    // === BALANCE TRANSACTIONS ===
    val allTransactions: Flow<List<BalanceTransaction>> = jastipDao.getAllTransactions()

    suspend fun createTopUpTransaction(method: String, amount: Double): BalanceTransaction {
        val transaction = BalanceTransaction(
            type = "TOP_UP",
            method = method,
            amount = amount,
            description = "Top Up via $method",
            status = "PENDING"
        )
        jastipDao.insertTransaction(transaction)
        return transaction
    }

    suspend fun confirmTopUpTransaction(transactionId: String) {
        jastipDao.updateTransactionStatus(transactionId, "SUCCESS")
    }

    suspend fun failTopUpTransaction(transactionId: String) {
        jastipDao.updateTransactionStatus(transactionId, "FAILED")
    }

    fun getRecentTopUps(): Flow<List<BalanceTransaction>> = jastipDao.getRecentTopUps()
}
