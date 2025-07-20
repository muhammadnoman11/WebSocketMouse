package org.example.project.websocket

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import org.example.project.util.WebSocketHolder
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class WebSocketManager(
    private val context: Context,
    private val uri: URI,
    private val onConnected: () -> Unit,
    private val onDisconnected: () -> Unit,
    private val onError: () -> Unit
) {
    private var socketClient: WebSocketClient? = null

    fun connect() {
        socketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Handler(Looper.getMainLooper()).post {
                    WebSocketHolder.client = this
                    onConnected()
                }
            }

            override fun onMessage(message: String?) {}

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
                    onDisconnected()
                }
            }

            override fun onError(ex: Exception?) {
                Handler(Looper.getMainLooper()).post {
                    Log.d("TAG", "onError Connection: ${ex?.message}")
                    Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show()
                    onError()
                }
            }
        }

        socketClient?.connect()
    }

    fun disconnect() {
        socketClient?.close()
    }
}
