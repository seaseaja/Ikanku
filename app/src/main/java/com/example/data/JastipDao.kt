package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JastipDao {
    @Query("SELECT * FROM jastip_orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<JastipOrder>>

    @Query("SELECT * FROM jastip_orders WHERE id = :id")
    suspend fun getOrderById(id: String): JastipOrder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: JastipOrder)

    @Query("UPDATE jastip_orders SET status = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: String, status: String)

    @Query("UPDATE jastip_orders SET userRating = :rating WHERE id = :id")
    suspend fun updateOrderRating(id: String, rating: Int)

    @Query("UPDATE jastip_orders SET complaintStatus = :status WHERE id = :id")
    suspend fun updateOrderComplaintStatus(id: String, status: String)

    @Query("SELECT * FROM chat_messages WHERE orderId = :orderId ORDER BY timestamp ASC")
    fun getMessagesForOrder(orderId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM jastip_orders")
    suspend fun clearAllOrders()

    @Query("SELECT * FROM cart_items ORDER BY timestamp DESC")
    fun getAllCartItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateCartItemQuantity(id: Int, quantity: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItemById(id: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // === USER ACCOUNTS ===
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount)

    @Query("SELECT * FROM user_accounts WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE email = :email")
    fun getUserByEmailFlow(email: String): Flow<UserAccount?>

    // === NOTIFICATIONS ===
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Query("UPDATE notifications SET isUnread = :isUnread WHERE id = :id")
    suspend fun updateNotificationReadStatus(id: String, isUnread: Boolean)

    @Query("UPDATE notifications SET isUnread = 0")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)

    // === BALANCE TRANSACTIONS ===
    @Query("SELECT * FROM balance_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<BalanceTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: BalanceTransaction)

    @Query("UPDATE balance_transactions SET status = :status WHERE id = :id")
    suspend fun updateTransactionStatus(id: String, status: String)

    @Query("SELECT * FROM balance_transactions WHERE type = 'TOP_UP' AND status = 'SUCCESS' ORDER BY timestamp DESC LIMIT 10")
    fun getRecentTopUps(): Flow<List<BalanceTransaction>>
}
