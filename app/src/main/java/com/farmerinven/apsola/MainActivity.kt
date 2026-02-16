package com.farmerinven.apsola

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.farmerinven.apsola.ui.components.BottomNavigationBar
import com.farmerinven.apsola.ui.navigation.NavGraph
import com.farmerinven.apsola.ui.theme.FarmerInventoryTheme
import com.farmerinven.apsola.ui.viewmodel.SettingsViewModel
import com.farmerinven.apsola.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val viewModelFactory = ViewModelFactory(this)
            val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
            val theme by settingsViewModel.uiState.collectAsState()
            
            val isDarkTheme = when (theme.theme) {
                "dark" -> true
                else -> false
            }
            
            FarmerInventoryTheme(themeMode = theme.theme) {
                val view = window
                SideEffect {
                    view.statusBarColor = if (isDarkTheme) {
                        Color(0xFF1C1B1F).toArgb()
                    } else {
                        Color.Transparent.toArgb()
                    }
                    WindowCompat.getInsetsController(view, view.decorView).apply {
                        isAppearanceLightStatusBars = !isDarkTheme
                    }
                }
                
                FarmerInventoryApp()
            }
        }
    }
}

@Composable
fun FarmerInventoryApp() {
    val navController = rememberNavController()
    val viewModelFactory = ViewModelFactory(navController.context)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            viewModelFactory = viewModelFactory,
            modifier = Modifier.padding(innerPadding)
        )
    }
}