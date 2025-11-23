package com.example.inifnity.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.inifnity.data.model.Wallpaper
import com.example.inifnity.ui.theme.DarkBackground
import com.example.inifnity.ui.theme.DarkSurface
import com.example.inifnity.ui.theme.LightText
import com.example.inifnity.ui.theme.PrimaryAccent
import com.example.inifnity.ui.theme.TextWhite
import com.example.inifnity.utils.VisualSearchHelper
import com.example.inifnity.viewmodel.CategoryViewModel
import com.example.inifnity.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

val infinityFont = FontFamily.Default

data class Category(val name: String, val description: String, val imageUrl: String)

val mockCategories = listOf(
    Category("Abstract", "Colorful", "https://images.pexels.com/photos/2693212/pexels-photo-2693212.png?auto=compress&cs=tinysrgb&w=400"),
    Category("Nature", "Relax", "https://images.pexels.com/photos/3225517/pexels-photo-3225517.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Tech", "Future", "https://images.pexels.com/photos/2582937/pexels-photo-2582937.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Cars", "Speed", "https://images.pexels.com/photos/337909/pexels-photo-337909.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Animals", "Wild Life", "https://images.pexels.com/photos/145939/pexels-photo-145939.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Space", "Cosmic", "https://images.pexels.com/photos/2156/sky-space-dark-galaxy.jpg?auto=compress&cs=tinysrgb&w=400"),
    Category("Sports", "Dynamic", "https://images.pexels.com/photos/114296/pexels-photo-114296.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Cities", "Urban", "https://images.pexels.com/photos/373912/pexels-photo-373912.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Minimal", "Clean", "https://images.pexels.com/photos/1181325/pexels-photo-1181325.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Anime", "Characters", "https://images.pexels.com/photos/7882009/pexels-photo-7882009.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Girls", "Aesthetic", "https://images.pexels.com/photos/3693901/pexels-photo-3693901.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Boys", "Aesthetic", "https://images.pexels.com/photos/3785079/pexels-photo-3785079.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Fantasy", "Magic", "https://images.pexels.com/photos/355465/pexels-photo-355465.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Flowers", "Beautiful", "https://images.pexels.com/photos/541405/pexels-photo-541405.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Neon", "Cyberpunk", "https://images.pexels.com/photos/4065179/pexels-photo-4065179.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Patterns", "Geometry", "https://images.pexels.com/photos/6985040/pexels-photo-6985040.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Dark", "Mood", "https://images.pexels.com/photos/7770019/pexels-photo-7770019.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Light", "Bright", "https://images.pexels.com/photos/1122627/pexels-photo-1122627.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Gaming", "Esport", "https://images.pexels.com/photos/907486/pexels-photo-907486.jpeg?auto=compress&cs=tinysrgb&w=400"),
    Category("Motivation", "Inspire", "https://images.pexels.com/photos/204139/pexels-photo-204139.jpeg?auto=compress&cs=tinysrgb&w=400"),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    onWallpaperClick: (Wallpaper) -> Unit,
    onNavigateToCategory: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSearch: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val editorsChoiceWallpapers = categoryViewModel.wallpapers.collectAsLazyPagingItems()
    val trendingWallpapers by homeViewModel.trendingWallpapers.collectAsState()
    val selectedCategory by categoryViewModel.selectedCategory.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isProcessingImage by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            isProcessingImage = true
            scope.launch {
                val resultKeyword = VisualSearchHelper.analyzeImage(bitmap)
                isProcessingImage = false
                if (!resultKeyword.isNullOrEmpty()) onNavigateToSearch(resultKeyword)
                else Toast.makeText(context, "Could not identify object.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        bottomBar = {
            InfinityBottomBar(
                onNavigateToCategory = onNavigateToCategory,
                onNavigateToFavorites = onNavigateToFavorites,
                onNavigateToSettings = onNavigateToSettings
            )
        },
        containerColor = DarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item(span = { GridItemSpan(2) }) {
                    Column {
                        AppHeader(
                            onSearchTriggered = onNavigateToSearch,
                            onScannerClick = { cameraLauncher.launch(null) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    CategorySection(
                        categories = mockCategories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { categoryViewModel.onCategorySelected(it) }
                    )
                }

                if (trendingWallpapers.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        WallpaperSection(
                            title = "Trending Now",
                            wallpapers = trendingWallpapers,
                            isVertical = false,
                            onWallpaperClick = onWallpaperClick
                        )
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    Column(modifier = Modifier.padding(top = 24.dp, start = 8.dp, bottom = 8.dp)) {
                        Text(
                            text = selectedCategory.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase() else it.toString()
                            } + " Wallpapers",
                            color = TextWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                items(
                    count = editorsChoiceWallpapers.itemCount,
                    key = editorsChoiceWallpapers.itemKey { it.id }
                ) { index ->
                    editorsChoiceWallpapers[index]?.let {
                        PremiumWallpaperCard(
                            wallpaper = it,
                            index = index,
                            onWallpaperClick = onWallpaperClick
                        )
                    }
                }

                editorsChoiceWallpapers.apply {
                    when {
                        loadState.append is LoadState.Loading ->
                            item(span = { GridItemSpan(2) }) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = PrimaryAccent, strokeWidth = 3.dp)
                                }
                            }

                        loadState.refresh is LoadState.Loading && editorsChoiceWallpapers.itemCount == 0 ->
                            item(span = { GridItemSpan(2) }) {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .height(400.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = PrimaryAccent, strokeWidth = 4.dp)
                                }
                            }

                        loadState.refresh is LoadState.Error || loadState.append is LoadState.Error ->
                            item(span = { GridItemSpan(2) }) {
                                Text("Connection Error", color = Color.Red, modifier = Modifier.padding(16.dp))
                            }
                    }
                }
            }

            if (isProcessingImage) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.CenterFocusStrong,
                            contentDescription = null,
                            tint = PrimaryAccent,
                            modifier = Modifier.size(70.dp)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text("AI Scanning...", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AppHeader(onSearchTriggered: (String) -> Unit, onScannerClick: () -> Unit) {
    Text(
        text = "INFINITY",
        style = TextStyle(
            brush = Brush.linearGradient(
                colors = listOf(Color.Cyan, PrimaryAccent, Color.White)
            ),
            fontSize = 34.sp,
            fontFamily = infinityFont,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        ),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp)
    )
    SearchInput(onSearch = onSearchTriggered, onScannerClick = onScannerClick)
}

@Composable
fun SearchInput(onSearch: (String) -> Unit, onScannerClick: () -> Unit) {
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    TextField(
        value = searchText,
        onValueChange = { searchText = it },
        placeholder = { Text("Search 4K wallpapers...", color = LightText.copy(alpha = 0.5f)) },
        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = null, tint = PrimaryAccent.copy(alpha = 0.8f))
        },
        trailingIcon = {
            IconButton(onClick = onScannerClick) {
                Icon(Icons.Filled.CenterFocusStrong, contentDescription = null, tint = PrimaryAccent)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(54.dp)
            .shadow(12.dp, RoundedCornerShape(28.dp), spotColor = PrimaryAccent.copy(alpha = 0.15f))
            .clip(RoundedCornerShape(28.dp)),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            if (searchText.isNotBlank()) {
                onSearch(searchText)
                focusManager.clearFocus()
            }
        }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DarkSurface,
            unfocusedContainerColor = DarkSurface,
            cursorColor = PrimaryAccent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        )
    )
}

@Composable
fun CategorySection(categories: List<Category>, selectedCategory: String, onCategorySelected: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { category ->
            CategoryCard(category, isSelected = category.name == selectedCategory) {
                onCategorySelected(category.name)
            }
        }
    }
}

@Composable
fun CategoryCard(category: Category, isSelected: Boolean, onClick: () -> Unit) {
    val cardColor = if (isSelected) PrimaryAccent else DarkSurface
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.92f else 1f, label = "")

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(220.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(if (isSelected) 18.dp else 6.dp, RoundedCornerShape(24.dp), spotColor = PrimaryAccent.copy(alpha = 0.6f))
            .clip(RoundedCornerShape(24.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        colors = CardDefaults.cardColors(cardColor)
    ) {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(
                model = category.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().graphicsLayer { alpha = if (isSelected) 0.85f else 0.6f }
            )
            Box(
                Modifier.fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )
            Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text(category.name, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(category.description, color = TextWhite.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun WallpaperSection(title: String, wallpapers: List<Wallpaper>, isVertical: Boolean, onWallpaperClick: (Wallpaper) -> Unit) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(title, color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(wallpapers) { wp ->
                PremiumWallpaperCard(wp, 0, onWallpaperClick, isHorizontal = true)
            }
        }
    }
}
@Composable
fun PremiumWallpaperCard(
    wallpaper: Wallpaper,
    index: Int,
    onWallpaperClick: (Wallpaper) -> Unit,
    isHorizontal: Boolean = false
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "")
    val rowIndex = index / 2
    val isLeftItem = index % 2 == 0


    val cardHeight = when {
        isHorizontal -> 220.dp
        rowIndex % 2 == 0 && isLeftItem -> 300.dp
        rowIndex % 2 == 0 && !isLeftItem -> 287.dp
        rowIndex % 2 != 0 && isLeftItem -> 287.dp
        else -> 300.dp
    }

    val modifier = if (isHorizontal)
        Modifier.width(150.dp).height(cardHeight)
    else
        Modifier.fillMaxWidth().height(cardHeight)

    Card(
        modifier = modifier
            .graphicsLayer { scaleX = pressScale; scaleY = pressScale }
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(20.dp))
            .clickable(interactionSource = interaction, indication = null) {
                onWallpaperClick(wallpaper)
            },
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
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

@Composable
fun InfinityBottomBar(
    onNavigateToCategory: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var selectedItem by remember { mutableStateOf("home") }

    Box(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 34.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(70.dp)
                .shadow(20.dp, RoundedCornerShape(35.dp), spotColor = PrimaryAccent.copy(alpha = 0.4f))
                .clip(RoundedCornerShape(35.dp)),
            color = DarkSurface.copy(alpha = 0.95f),
            tonalElevation = 4.dp
        ) {
            Row(
                Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    BottomNavItemView(
                        item = item,
                        isSelected = selectedItem == item.route
                    ) {
                        selectedItem = item.route
                        when (item.route) {
                            "categories" -> onNavigateToCategory()
                            "favorites" -> onNavigateToFavorites()
                            "settings" -> onNavigateToSettings()
                        }
                    }
                }
            }
        }
    }
}

data class BottomNavItem(val title: String, val icon: ImageVector, val route: String)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Outlined.Home, "home"),
    BottomNavItem("Categories", Icons.Outlined.Category, "categories"),
    BottomNavItem("Favorites", Icons.Outlined.FavoriteBorder, "favorites"),
    BottomNavItem("Settings", Icons.Outlined.Settings, "settings"),
)

@Composable
fun BottomNavItemView(item: BottomNavItem, isSelected: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f, label = "")

    Column(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = if (isSelected) PrimaryAccent else LightText.copy(alpha = 0.6f),
            modifier = Modifier.size(26.dp)
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.size(4.dp).background(PrimaryAccent, CircleShape))
        }
    }
}
