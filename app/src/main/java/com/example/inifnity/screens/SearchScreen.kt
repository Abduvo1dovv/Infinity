package com.example.inifnity.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.inifnity.data.model.Wallpaper
import com.example.inifnity.ui.theme.DarkBackground
import com.example.inifnity.ui.theme.DarkSurface
import com.example.inifnity.ui.theme.PrimaryAccent
import com.example.inifnity.ui.theme.TextWhite
import com.example.inifnity.viewmodel.SearchViewModel
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    initialQuery: String,
    viewModel: SearchViewModel = viewModel(),
    onWallpaperClick: (Wallpaper) -> Unit,
    onBack: () -> Unit
) {
    // Viewmodelga qidiruv so'zini beramiz (birinchi marta)
    LaunchedEffect(initialQuery) {
        viewModel.updateSearchQuery(initialQuery)
    }

    val wallpapers = viewModel.searchResults.collectAsLazyPagingItems()
    var searchText by remember { mutableStateOf(initialQuery) }
    val focusManager = LocalFocusManager.current

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
                    .padding(bottom = 16.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search...", color = TextWhite.copy(0.5f)) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .padding(horizontal = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = PrimaryAccent,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchText.isNotEmpty()) {
                                    viewModel.updateSearchQuery(searchText)
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { searchText = "" }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear", tint = TextWhite.copy(0.5f))
                                }
                            }
                        }
                    )
                }

                Text(
                    text = "Results for \"$initialQuery\"",
                    color = TextWhite.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            if (wallpapers.itemCount == 0 && wallpapers.loadState.refresh !is LoadState.Loading) {
                // Bo'sh natija
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = TextWhite.copy(0.3f), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No wallpapers found", color = TextWhite.copy(0.5f))
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(wallpapers.itemCount) { index ->
                        val wallpaper = wallpapers[index]
                        if (wallpaper != null) {
                            SearchWallpaperCard(wallpaper, index, onWallpaperClick)
                        }
                    }

                    // Loading State
                    wallpapers.apply {
                        when {
                            loadState.refresh is LoadState.Loading -> {
                                item(span = { GridItemSpan(2) }) {
                                    Box(modifier = Modifier.fillMaxWidth().height(400.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = PrimaryAccent)
                                    }
                                }
                            }
                            loadState.append is LoadState.Loading -> {
                                item(span = { GridItemSpan(2) }) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = PrimaryAccent)
                                    }
                                }
                            }
                            loadState.refresh is LoadState.Error -> {
                                item(span = { GridItemSpan(2) }) {
                                    Text("Error loading results", color = Color.Red, modifier = Modifier.padding(16.dp))
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
fun SearchWallpaperCard(
    wallpaper: Wallpaper,
    index: Int,
    onWallpaperClick: (Wallpaper) -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 50L)
        startAnimation = true
    }
    val alpha by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f, animationSpec = tween(400), label = "")
    val scale by animateFloatAsState(targetValue = if (startAnimation) 1f else 0.9f, animationSpec = tween(400), label = "")

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .height(260.dp)
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