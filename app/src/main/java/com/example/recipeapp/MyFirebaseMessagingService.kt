package com.example.recipeapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    /**
     * Called when a new FCM token is generated for the device.
     * This is the perfect place to save the token to your user's document in Firestore.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token generated: $token")
        // If a user is logged in, save the new token to their profile
        auth.currentUser?.uid?.let { userId ->
            sendTokenToFirestore(userId, token)
        }
    }

    /**
     * Called when a message is received.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // The message contains a 'notification' payload.
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "New Recipe!"
            val body = notification.body ?: "Check out this delicious new recipe."
            Log.d("FCM", "Message Notification Body: ${notification.body}")

            // Display the notification to the user
            sendNotification(title, body)
        }
    }

    /**
     * Creates and displays a system notification.
     */
    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "recipe_channel" // A unique ID for your notification channel
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.pot) // Your app's notification icon
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true) // Automatically removes the notification when the user taps it
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since Android Oreo, notification channels are required.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recipe Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    /**
     * Saves the FCM token to the user's document in Firestore.
     */
    fun sendTokenToFirestore(userId: String, token: String) {
        if (userId.isEmpty()) return

        val tokenData = hashMapOf("fcmToken" to token)
        db.collection("users").document(userId)
            .update(tokenData as Map<String, Any>)
            .addOnSuccessListener { Log.d("FCM", "Token successfully saved to Firestore.") }
            .addOnFailureListener { e -> Log.w("FCM", "Error saving token", e) }
    }
}