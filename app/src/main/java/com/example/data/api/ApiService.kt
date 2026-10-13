package com.example.data.api

import com.example.data.JastipOrder
import com.example.data.ChatMessage
import com.example.data.CartItem
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query

interface ApiService {
    // === AUTHENTICATION ===
    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("register.php")
    suspend fun register(@Body request: RegisterRequest): ApiResponse

    // === PROFILE ===
    @POST("update_profile.php")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponse

    // === ORDERS ===
    @GET("get_pesanan.php")
    suspend fun getOrders(): List<JastipOrder>

    @POST("create_pesanan.php")
    suspend fun createOrder(@Body order: JastipOrder): ApiResponse

    // === CHAT ===
    @GET("get_messages.php")
    suspend fun getMessages(@Query("orderId") orderId: String): List<ChatMessage>

    @POST("send_message.php")
    suspend fun sendMessage(@Body message: ChatMessage): ApiResponse

    // === CART ===
    @GET("get_cart.php")
    suspend fun getCart(@Query("userEmail") userEmail: String): List<CartItem>

    @POST("sync_cart.php")
    suspend fun syncCart(@Body cartItems: List<CartItem>): ApiResponse

    // === ADMIN ===
    @GET("admin/get_stats.php")
    suspend fun getAdminStats(): AdminStatsResponse

    @GET("admin/get_users.php")
    suspend fun getAllUsers(): List<com.example.data.UserAccount>

    @GET("admin/get_complaints.php")
    suspend fun getComplaints(): List<ComplaintItem>
}

// Helper Data Classes
data class AdminStatsResponse(
    val totalUsers: Int,
    val totalOrders: Int,
    val pendingVerifikasi: Int,
    val disputeOpen: Int,
    val danaEscrow: String
)

data class ComplaintItem(
    val id: String,
    val orderId: String,
    val userName: String,
    val message: String,
    val timestamp: Long,
    val status: String
)

data class LoginRequest(val email: String, val password: String, val role: String? = null)
data class RegisterRequest(
    val email: String, 
    val password: String, 
    val role: String,
    val no_hp: String = "",
    val full_name: String = ""
)
data class UpdateProfileRequest(val email: String, val fullName: String, val no_hp: String)

data class AuthResponse(
    val status: String?, // "success" or "error"
    val message: String,
    val role: String?,
    val full_name: String? = null,
    val no_hp: String? = null
) {
    val success: Boolean get() = status == "success"
}

data class ApiResponse(
    val status: String?, // "success" or "error"
    val message: String
) {
    val success: Boolean get() = status == "success"
}
