package com.vi.teamconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vi.teamconnect.presentation.navigation.MainAppNavigation
import com.vi.teamconnect.presentation.theme.ViTeamConnectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ViTeamConnectTheme {
                MainAppNavigation()
            }
        }
    }
}
