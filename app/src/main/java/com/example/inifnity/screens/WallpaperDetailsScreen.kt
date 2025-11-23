package com.example.inifnity.screens

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.inifnity.data.local.FavoritesManager
import com.example.inifnity.data.model.Wallpaper
import com.example.inifnity.ui.theme.DarkBackground
import com.example.inifnity.ui.theme.PrimaryAccent
import com.example.inifnity.ui.theme.TextWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

@Composable
fun WallpaperDetailsScreen(
    wallpaper: Wallpaper,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isSettingWallpaper by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var showApplyDialog by remember { mutableStateOf(false) }


    var isFavorite by remember { mutableStateOf(false) }


    LaunchedEffect(wallpaper.id) {
        isFavorite = FavoritesManager.isFavorite(context, wallpaper.id)
    }

    BackHandler(enabled = true) {
        if (!isSettingWallpaper && !isDownloading) onBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(wallpaper.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )


        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(wallpaper.fullUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Full screen wallpaper",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                    )
                )
        )


        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 16.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextWhite
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // Sal balandroq qildim
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                    )
                )
        )


        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp, start = 24.dp, end = 24.dp)
        ) {

            Text(
                text = wallpaper.category,
                color = TextWhite.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                ActionButton(
                    icon = Icons.Outlined.ArrowDropDown,
                    text = "Save",
                    onClick = {
                        if (!isDownloading) {
                            isDownloading = true
                            scope.launch {
                                saveImageToGallery(context, wallpaper.fullUrl)
                                isDownloading = false
                            }
                        }
                    },
                    isLoading = isDownloading
                )

                Button(
                    onClick = { showApplyDialog = true },
                    modifier = Modifier
                        .height(56.dp)
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryAccent,
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(8.dp)
                ) {
                    if (isSettingWallpaper) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Filled.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Apply", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // FAV (SEVIMLILAR) TUGMASI
                val favIcon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                val favColor = if (isFavorite) Color.Red else TextWhite

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        // 1. Bazaga saqlash/o'chirish
                        isFavorite = FavoritesManager.toggleFavorite(context, wallpaper)
                        // 2. Xabar chiqarish
                        val msg = if (isFavorite) "Added to Favorites" else "Removed from Favorites"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = favIcon,
                            contentDescription = "Fav",
                            tint = favColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Fav", color = TextWhite.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
        }
    }

    if (showApplyDialog) {
        AlertDialog(
            onDismissRequest = { showApplyDialog = false },
            containerColor = Color(0xFF252525),

            titleContentColor = TextWhite,
            textContentColor = TextWhite.copy(alpha = 0.9f),

            title = { Text("Set Wallpaper") },
            text = { Text("Choose where you want to apply this wallpaper.") },
            confirmButton = {},
            dismissButton = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    DialogButton("Home Screen") {
                        showApplyDialog = false; isSettingWallpaper = true
                        scope.launch {
                            setWallpaper(
                                context,
                                wallpaper.fullUrl,
                                1
                            ); isSettingWallpaper = false
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    DialogButton("Lock Screen") {
                        showApplyDialog = false; isSettingWallpaper = true
                        scope.launch {
                            setWallpaper(
                                context,
                                wallpaper.fullUrl,
                                2
                            ); isSettingWallpaper = false
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    DialogButton("Both Screens") {
                        showApplyDialog = false; isSettingWallpaper = true
                        scope.launch {
                            setWallpaper(
                                context,
                                wallpaper.fullUrl,
                                3
                            ); isSettingWallpaper = false
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun DialogButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF353535)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, color = TextWhite, modifier = Modifier.padding(vertical = 4.dp))
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(enabled = !isLoading, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = TextWhite,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = TextWhite,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text, color = TextWhite.copy(alpha = 0.6f), fontSize = 12.sp)
    }
}


suspend fun setWallpaper(context: Context, imageUrl: String, target: Int) {
    withContext(Dispatchers.IO) {
        try {
            val loader = context.imageLoader
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()
            val result = (loader.execute(request) as? SuccessResult)?.drawable
            val bitmap = result?.toBitmap()

            if (bitmap != null) {
                val wm = WallpaperManager.getInstance(context)

                when (target) {
                    1 -> wm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                    2 -> wm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                    3 -> {
                        wm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                        wm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Successfully applied!", Toast.LENGTH_SHORT).show()
                }
            } else throw Exception("Failed to load image")
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

suspend fun saveImageToGallery(context: Context, imageUrl: String) {
    withContext(Dispatchers.IO) {
        try {
            val loader = context.imageLoader
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()
            val result = (loader.execute(request) as? SuccessResult)?.drawable
            val bitmap = result?.toBitmap()

            if (bitmap != null) {
                val filename = "Infinity_${System.currentTimeMillis()}.jpg"
                var fos: OutputStream? = null

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageUri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    fos = imageUri?.let { context.contentResolver.openOutputStream(it) }
                } else {
                    val imagesDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val image = java.io.File(imagesDir, filename)
                    fos = java.io.FileOutputStream(image)
                }

                fos?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error saving: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}