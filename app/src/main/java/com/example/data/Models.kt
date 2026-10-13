package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class OrderStatus {
    PENDING,     // Open for claim by Jastiper
    ACCEPTED,    // Jastiper accepted, planning
    BUYING,      // Jastiper is buying at the Market
    DELIVERING,  // Jastiper is on the way to user's address
    COMPLETED    // Delivered and done!
}

@Entity(tableName = "jastip_orders")
data class JastipOrder(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val customerName: String,
    val jastiperName: String = "Pak Joko",
    val fishType: String,
    val weightKg: Double,
    val marketName: String,
    val budgetEst: Double,
    val jastipFee: Double,
    val deliveryAddress: String,
    val notes: String = "",
    val status: String = "PENDING",
    val timestamp: Long = System.currentTimeMillis(),
    val userRating: Int = 0,               // 0 means not rated yet
    val complaintStatus: String? = null    // null means no complaint
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val orderId: String,
    val senderName: String,
    val isFromMe: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val attachmentUrl: String? = null // For simulated fish photos
)

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val market: String,
    val price: Double,
    val imageType: String,
    val quantity: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey val email: String,
    val password: String,
    val role: String,
    val fullName: String = "",
    val phoneNumber: String = ""
)

data class UserProfile(
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val role: String = "pembeli"
)

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val category: String,        // "PESANAN", "PROMO", "INFO", "SISTEM"
    val title: String,
    val description: String,
    val isUnread: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val orderId: String? = null  // Optional link to order
)

@Entity(tableName = "balance_transactions")
data class BalanceTransaction(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: String,            // "TOP_UP", "PAYMENT", "REFUND"
    val method: String,          // "BCA Transfer", "GoPay", "Alfamart", etc.
    val amount: Double,
    val description: String,
    val status: String = "PENDING", // "PENDING", "SUCCESS", "FAILED"
    val timestamp: Long = System.currentTimeMillis()
)

data class UserAddress(
    val id: String = UUID.randomUUID().toString(),
    val label: String, // "Rumah", "Kantor", etc.
    val receiverName: String,
    val phoneNumber: String,
    val fullAddress: String,
    val isPrimary: Boolean = false,
    val note: String = ""
)

