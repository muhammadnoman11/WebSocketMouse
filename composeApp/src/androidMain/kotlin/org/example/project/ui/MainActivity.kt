package org.example.project.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.navigator.Navigator
import org.example.project.ui.screen.MainScreen
import org.example.project.ui.screen.MouseScreen
import org.example.project.util.WebSocketHolder


class MainActivity : ComponentActivity() {

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {

            val isConnected by WebSocketHolder.isConnected.collectAsState()

            val startScreen = if (isConnected) {
                MouseScreen()
            } else {
                MainScreen()
            }

            Navigator(
                startScreen
            )
        }
    }
}
