package com.example.inifnity.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.imageLoader
import com.example.inifnity.data.local.SettingsManager
import com.example.inifnity.ui.theme.DarkBackground
import com.example.inifnity.ui.theme.DarkSurface
import com.example.inifnity.ui.theme.PrimaryAccent
import com.example.inifnity.ui.theme.TextWhite

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var notificationsEnabled by remember {
        mutableStateOf(
            SettingsManager.isNotificationEnabled(
                context
            )
        )
    }
    var darkModeEnabled by remember { mutableStateOf(SettingsManager.isDarkModeEnabled(context)) }

    val versionName = remember {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0D0D0D), DarkBackground)
                        )
                    )
                    .statusBarsPadding()
                    .padding(bottom = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Settings",
                    color = TextWhite,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Text(
                    text = "Customize your experience",
                    color = TextWhite.copy(alpha = 0.5f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. GENERAL SECTION
            item { SectionTitle("General") }

            item {
                SettingsItem(
                    icon = Icons.Filled.Notifications,
                    title = "Notifications",
                    subtitle = "Get daily wallpaper updates",
                    hasSwitch = true,
                    switchState = notificationsEnabled,
                    onSwitchChange = { isChecked ->
                        notificationsEnabled = isChecked
                        SettingsManager.setNotificationEnabled(context, isChecked)
                        val msg = if (isChecked) "Notifications ON" else "Notifications OFF"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Always on for Infinity",
                    hasSwitch = true,
                    switchState = darkModeEnabled,
                    onSwitchChange = { isChecked ->
                        darkModeEnabled = isChecked
                        SettingsManager.setDarkModeEnabled(context, isChecked)
                        // Toast olib tashlansa ham bo'ladi, chunki vizual o'zgarish darhol ko'rinadi
                    }
                )
            }

            item { SectionTitle("Data & Storage") }

            item {
                SettingsItem(
                    icon = Icons.Filled.CleaningServices,
                    title = "Clear Cache",
                    subtitle = "Free up space & fix loading issues",
                    onClick = {
                        // Coil keshini tozalash
                        context.imageLoader.diskCache?.clear()
                        context.imageLoader.memoryCache?.clear()
                        Toast.makeText(context, "Cache cleared successfully!", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }

            item { SectionTitle("About") }

            item {
                SettingsItem(
                    icon = Icons.Filled.Star,
                    title = "Rate Us",
                    subtitle = "Support our work on Play Store",
                    onClick = {
                        val packageName = context.packageName
                        try {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=$packageName")
                                )
                            )
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                                )
                            )
                        }
                    }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.Share,
                    title = "Share App",
                    subtitle = "Share with friends",
                    onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Check out Infinity Wallpapers app! Download now."
                            )
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Filled.Info,
                    title = "Version",
                    subtitle = "$versionName (Premium Build)",
                    showArrow = false
                )
            }

            item { Spacer(modifier = Modifier.height(100.dp)) } // Bottom nav uchun joy
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        color = PrimaryAccent,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    hasSwitch: Boolean = false,
    switchState: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {},
    showArrow: Boolean = true,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (isPressed) 0.97f else 1f,
        animationSpec = tween(100),
        label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                enabled = !hasSwitch && enabled, // Switch bo'lsa click ishlamaydi (faqat switch ishlaydi)
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PrimaryAccent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))


            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = TextWhite.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            if (hasSwitch) {
                Switch(
                    checked = switchState,
                    onCheckedChange = onSwitchChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = PrimaryAccent,
                        uncheckedThumbColor = TextWhite.copy(0.6f),
                        uncheckedTrackColor = DarkBackground.copy(alpha = 0.5f),
                        uncheckedBorderColor = TextWhite.copy(0.2f)
                    ),
                    enabled = enabled
                )
            } else if (showArrow) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = TextWhite.copy(alpha = 0.3f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}