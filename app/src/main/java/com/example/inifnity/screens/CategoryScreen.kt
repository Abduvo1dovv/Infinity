package com.example.inifnity.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.inifnity.R
import com.example.inifnity.data.model.Wallpaper
import com.example.inifnity.ui.theme.DarkBackground
import com.example.inifnity.ui.theme.DarkSurface
import com.example.inifnity.ui.theme.PrimaryAccent
import com.example.inifnity.ui.theme.TextWhite
import com.example.inifnity.viewmodel.CategoryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Kategoriyalar ro'yxati
val categoriesList = listOf(
    "Abstract", "Nature", "Space", "Cars", "Minimalism", "Neon",
    "Technology", "Dark", "Animals", "Cyberpunk", "Mountains",
    "Forest", "Cartoon", "Cityscape", "Geometric", "Architecture",
    "Snow", "Winter"
)
val infinityFontCategory = FontFamily.Default

@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = viewModel(),
    onWallpaperClick: (Wallpaper) -> Unit
) {
    val wallpapers = viewModel.wallpapers.collectAsLazyPagingItems()
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            // --- PREMIUM HEADER ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0D0D0D),
                                DarkBackground
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(bottom = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Collections",
                    fontFamily = infinityFontCategory,
                    color = TextWhite,
                    fontSize = 36.sp, // Kattalashtirdik
                    fontWeight = FontWeight.Black, // Qalinroq
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )


                Text(
                    fontFamily = infinityFontCategory,
                    text = "Curated lists just for you",
                    color = TextWhite.copy(alpha = 0.5f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                PremiumCategoryTabs(
                    categories = categoriesList,
                    selectedIndex = selectedIndex,
                    onCategorySelected = { index, category ->
                        if (selectedIndex != index) {
                            selectedIndex = index
                            viewModel.onCategorySelected(category)
                            scope.launch {
                                gridState.scrollToItem(0)
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                state = gridState,
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 100.dp,
                    top = 0.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    count = wallpapers.itemCount,
                    key = { index ->
                        val item = wallpapers[index]
                        "${item?.id ?: index}_${categoriesList[selectedIndex]}"
                    }
                ) { index ->
                    val wallpaper = wallpapers[index]
                    if (wallpaper != null) {
                        GridWallpaperCardWithAnimation(wallpaper, index % 12, onWallpaperClick)
                    }
                }

                // Loading State
                wallpapers.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item(span = { GridItemSpan(3) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(400.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = PrimaryAccent)
                                }
                            }
                        }

                        loadState.append is LoadState.Loading -> {
                            item(span = { GridItemSpan(3) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = PrimaryAccent,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        loadState.refresh is LoadState.Error -> {
                            item(span = { GridItemSpan(3) }) {
                                Box(modifier = Modifier.padding(40.dp)) {
                                    Text("Check internet connection",
                                        color = Color.Red,
                                        fontFamily = infinityFontCategory)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PremiumCategoryTabs(
    categories: List<String>,
    selectedIndex: Int,
    onCategorySelected: (Int, String) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        edgePadding = 20.dp,
        indicator = {},
        divider = {},
        modifier = Modifier.fillMaxWidth()
    ) {
        categories.forEachIndexed { index, category ->
            val isSelected = selectedIndex == index

            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .shadow(
                        elevation = if (isSelected) 10.dp else 0.dp,
                        shape = RoundedCornerShape(50),
                        spotColor = if (isSelected) PrimaryAccent.copy(alpha = 0.6f) else Color.Transparent
                    )
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isSelected) PrimaryAccent else Color.Transparent
                    )
                    .border(

                        border = if (isSelected) BorderStroke(0.dp, Color.Transparent)
                        else BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(50)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onCategorySelected(index, category)
                    }
                    .padding(
                        horizontal = 24.dp,
                        vertical = 12.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color.Black else TextWhite.copy(alpha = 0.9f),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun GridWallpaperCardWithAnimation(
    wallpaper: Wallpaper,
    index: Int,
    onWallpaperClick: (Wallpaper) -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 30L)
        startAnimation = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.9f,
        animationSpec = tween(durationMillis = 400),
        label = "scale"
    )

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .height(220.dp)
            .graphicsLayer {
                this.alpha = alpha
                this.scaleX = scale
                this.scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp))
            .clickable { onWallpaperClick(wallpaper) },
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(wallpaper.imageUrl)
                .crossfade(true)
                .size(400, 600)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}