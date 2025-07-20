package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import org.example.project.ui.MainScreen
import org.example.project.util.WebSocketHolder.PORT
import org.example.project.websocketserver.MouseWebSocketServer

fun main() = application {

    val server = MouseWebSocketServer(PORT)
    server.start()

    Window(
        onCloseRequest = ::exitApplication,
        title = "WebSocketMouse",
    ) {

        Navigator( MainScreen())
    }
}