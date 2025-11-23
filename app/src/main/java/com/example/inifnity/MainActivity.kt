package com.example.inifnity

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.example.inifnity.data.local.SettingsManager
import com.example.inifnity.data.model.Wallpaper
import com.example.inifnity.screens.CategoryScreen
import com.example.inifnity.screens.FavoritesScreen
import com.example.inifnity.screens.HomeScreen
import com.example.inifnity.screens.SearchScreen
import com.example.inifnity.screens.SettingsScreen
import com.example.inifnity.screens.SplashScreen
import com.example.inifnity.screens.WallpaperDetailsScreen
import com.example.inifnity.ui.theme.DarkBackground
import com.example.inifnity.ui.theme.InifnityTheme

object Destinations {
    const val SPLASH_SCREEN = "splash_screen"
    const val HOME_SCREEN = "home_screen"
    const val DETAILS_SCREEN = "details_screen"
    const val CATEGORY_SCREEN = "category_screen"
    const val FAVORITES_SCREEN = "favorites_screen"
    const val SETTINGS_SCREEN = "settings_screen"
    const val SEARCH_SCREEN = "search_screen/{query}"

    fun searchRoute(query: String) = "search_screen/$query"
}

class NavigationViewModel : ViewModel() {
    var selectedWallpaper: Wallpaper? = null
}


fun getOptimizedImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.15)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizeBytes(50L * 1024 * 1024)
                .build()
        }
        .bitmapConfig(Bitmap.Config.RGB_565)
        .allowHardware(false)
        .networkCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .crossfade(true)
        .build()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SettingsManager.init(this)

        setContent {

            val isDarkMode by SettingsManager.isDarkModeFlow.collectAsState()


            InifnityTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    AppEntryPoint()
                }
            }
        }
    }
}

@Composable
fun AppEntryPoint() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navViewModel: NavigationViewModel = viewModel()

    CompositionLocalProvider(LocalImageLoader provides getOptimizedImageLoader(context)) {

        NavHost(navController = navController, startDestination = Destinations.SPLASH_SCREEN) {


            composable(Destinations.SPLASH_SCREEN) {
                SplashScreen(
                    onNavigateToHome = {

                        navController.navigate(Destinations.HOME_SCREEN) {
                            popUpTo(Destinations.SPLASH_SCREEN) { inclusive = true }
                        }
                    }
                )
            }


            composable(Destinations.HOME_SCREEN) {
                HomeScreen(
                    onWallpaperClick = { wallpaper ->
                        navViewModel.selectedWallpaper = wallpaper
                        navController.navigate(Destinations.DETAILS_SCREEN)
                    },
                    onNavigateToCategory = {
                        navController.navigate(Destinations.CATEGORY_SCREEN)
                    },
                    onNavigateToFavorites = {
                        navController.navigate(Destinations.FAVORITES_SCREEN)
                    },
                    onNavigateToSearch = { query ->
                        navController.navigate(Destinations.searchRoute(query))
                    },
                    onNavigateToSettings = {
                        navController.navigate(Destinations.SETTINGS_SCREEN)
                    }
                )
            }


            composable(Destinations.CATEGORY_SCREEN) {
                CategoryScreen(
                    onWallpaperClick = { wallpaper ->
                        navViewModel.selectedWallpaper = wallpaper
                        navController.navigate(Destinations.DETAILS_SCREEN)
                    }
                )
            }


            composable(Destinations.FAVORITES_SCREEN) {
                FavoritesScreen(
                    onWallpaperClick = { wallpaper ->
                        navViewModel.selectedWallpaper = wallpaper
                        navController.navigate(Destinations.DETAILS_SCREEN)
                    }
                )
            }


            composable(Destinations.SETTINGS_SCREEN) {
                SettingsScreen()
            }

            composable(
                route = Destinations.SEARCH_SCREEN,
                arguments = listOf(navArgument("query") { type = NavType.StringType })
            ) { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query") ?: ""
                SearchScreen(
                    initialQuery = query,
                    onWallpaperClick = { wallpaper ->
                        navViewModel.selectedWallpaper = wallpaper
                        navController.navigate(Destinations.DETAILS_SCREEN)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }


            composable(Destinations.DETAILS_SCREEN) {
                val selectedWallpaper = navViewModel.selectedWallpaper
                if (selectedWallpaper != null) {
                    WallpaperDetailsScreen(
                        wallpaper = selectedWallpaper,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate(Destinations.HOME_SCREEN) {
                            popUpTo(Destinations.HOME_SCREEN) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}