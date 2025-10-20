package com.kreggscode.waisttohip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kreggscode.waisttohip.data.ThemeManager
import com.kreggscode.waisttohip.ui.components.FloatingBottomNav
import com.kreggscode.waisttohip.ui.screens.*
import com.kreggscode.waisttohip.ui.theme.CurveAndFuelTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var themeManager: ThemeManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Install splash screen
        var keepSplashScreen = true
        installSplashScreen().setKeepOnScreenCondition { keepSplashScreen }
        
        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val isDarkMode by themeManager.isDarkMode.collectAsState(initial = false)
            
            CurveAndFuelTheme(darkTheme = isDarkMode) {
                var showSplash by remember { mutableStateOf(true) }
                
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(100)
                    keepSplashScreen = false
                }
                
                if (showSplash) {
                    SplashScreen(
                        onSplashComplete = { showSplash = false }
                    )
                } else {
                    CurveAndFuelApp(
                        themeManager = themeManager,
                        isDarkMode = isDarkMode
                    )
                }
            }
        }
    }
}

@Composable
fun CurveAndFuelApp(
    themeManager: ThemeManager,
    isDarkMode: Boolean
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()
    
    // Determine if bottom nav should be shown (hide on Chat for keyboard)
    val showBottomNav = currentRoute in listOf(
        Screen.Home.route,
        Screen.Measure.route,
        Screen.Scanner.route,
        Screen.History.route
    )
    
    // Determine selected bottom nav index
    val selectedNavIndex = when (currentRoute) {
        Screen.Home.route -> 0
        Screen.Measure.route -> 1
        Screen.Scanner.route -> 2
        Screen.History.route -> 3
        else -> 0
    }
    
    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNav,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(400)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(400)
                ) + fadeOut()
            ) {
                FloatingBottomNav(
                    selectedIndex = selectedNavIndex,
                    onItemSelected = { index ->
                        val route = when (index) {
                            0 -> Screen.Home.route
                            1 -> Screen.Measure.route
                            2 -> Screen.Scanner.route
                            3 -> Screen.History.route
                            else -> Screen.Home.route
                        }
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(
                route = Screen.Home.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                HomeScreen(
                    onMeasureClick = {
                        navController.navigate(Screen.Measure.route)
                    },
                    onScanClick = {
                        navController.navigate(Screen.Scanner.route)
                    },
                    onQuickAddClick = {
                        navController.navigate(Screen.Scanner.route)
                    },
                    onChatClick = {
                        navController.navigate(Screen.Chat.route)
                    },
                    themeManager = themeManager,
                    isDarkMode = isDarkMode
                )
            }
            
            composable(
                route = Screen.Measure.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                MeasureScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onResultsClick = {
                        navController.navigate(Screen.AIAnalysis.route)
                    }
                )
            }
            
            composable(
                route = Screen.AIAnalysis.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                AIAnalysisScreen(
                    onBackClick = {
                        // Navigate to home instead of just popping back
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onChatClick = {
                        navController.navigate(Screen.Chat.route)
                    }
                )
            }
            
            composable(
                route = Screen.Scanner.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                ScannerScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(
                route = Screen.Chat.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                ChatScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(
                route = Screen.History.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                HistoryScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Measure : Screen("measure")
    object AIAnalysis : Screen("ai_analysis")
    object Scanner : Screen("scanner")
    object Chat : Screen("chat")
    object History : Screen("history")
}
