package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlobalHeader(
    modifier: Modifier = Modifier,
    title: String? = null,
    onNavigateToNotifications: (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null
) {
    Surface(
        color = Color.White,
        shadowElevation = 2.dp, // Subtle drop shadow
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Spacer(modifier = Modifier.statusBarsPadding())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Navigation icon if available (e.g., back arrow)
                if (navigationIcon != null) {
                    navigationIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                } else if (title != null) {
                    // Balanced spacer for centered title if no nav icon
                    Spacer(modifier = Modifier.size(40.dp))
                }

                // Brand Logo or Custom Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (title != null) Arrangement.Center else Arrangement.Start,
                    modifier = Modifier.weight(1f)
                ) {
                    if (title == null) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.logo_ikan),
                            contentDescription = "Logo IkanKu",
                            modifier = Modifier.size(34.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "IkanKu",
                            color = Color(0xFF006684),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    } else {
                        Text(
                            text = title,
                            color = Color(0xFF006684),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Header Action Buttons
                IconButton(
                    onClick = { onNavigateToNotifications?.invoke() },
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("btn_top_notifications")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifikasi",
                        tint = Color(0xFF006684),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFE2F1F8), thickness = 1.dp)
        }
    }
}
