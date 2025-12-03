package com.erdemyesilcicek.contactapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.navigation.AppNavigation
import com.erdemyesilcicek.contactapp.ui.theme.ContactAppTheme
import com.erdemyesilcicek.contactapp.util.ProvideAppDimensions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContactAppTheme {
                ProvideAppDimensions {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = AppColors.Background
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}