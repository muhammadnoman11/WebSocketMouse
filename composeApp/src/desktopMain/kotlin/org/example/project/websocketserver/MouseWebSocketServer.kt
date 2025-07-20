package org.example.project.websocketserver


import java.awt.Robot
import java.awt.event.InputEvent
import java.net.InetSocketAddress
import kotlinx.serialization.json.*
import org.example.project.model.MouseEventMessage
import org.example.project.util.WebSocketHolder
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.awt.MouseInfo

class MouseWebSocketServer(port: Int) : WebSocketServer(InetSocketAddress(port)) {
    private val robot = Robot()
    private val json = Json

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        println("Client connected: ${conn?.remoteSocketAddress}")
        WebSocketHolder.setConnectionStatus(true)
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        println("Client disconnected: ${conn?.remoteSocketAddress}")
        WebSocketHolder.setConnectionStatus(false)
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        println("Message: $message")
        message ?: return
        try {
            val event = json.decodeFromString<MouseEventMessage>(message)
            when (event.type) {
                "move" -> robot.mouseMove(
                    MouseInfo.getPointerInfo().location.x + event.dx,
                    MouseInfo.getPointerInfo().location.y + event.dy
                )
                "click" -> {
                    val button = when (event.button) {
                        "left" -> InputEvent.BUTTON1_DOWN_MASK
                        "right" -> InputEvent.BUTTON3_DOWN_MASK
                        else -> return
                    }
                    robot.mousePress(button)
                    robot.mouseRelease(button)
                }
                "scroll" -> robot.mouseWheel(event.dy)

                "touchPadTap" ->{
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
                }


            }
        } catch (e: Exception) {
            println("Invalid message: $e")
        }
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        ex?.printStackTrace()
    }

    override fun onStart() {
        println("Server started on ${address.hostName}:${address.port}")
    }
}

