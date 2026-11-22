package com.abitusd.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.abitusd.app.core.designsystem.theme.AbitusTheme
import com.abitusd.app.core.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Light theme by default (05b); the theme preference arrives with settings.
            AbitusTheme(darkTheme = false) {
                AppNavigation()
            }
        }
    }
}
