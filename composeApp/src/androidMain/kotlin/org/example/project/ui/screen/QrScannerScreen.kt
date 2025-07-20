package org.example.project.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.launch
import org.example.project.component.FullScreenLoading
import org.example.project.websocket.WebSocketManager
import org.example.project.util.WebSocketHolder
import org.example.project.util.WebSocketHolder.PORT
import qrscanner.QrScanner
import java.net.URI


class QrScannerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        QrScannerScreenContent(navigator)
    }
}

@Composable
fun QrScannerScreenContent(navigator: Navigator?){

    var qrCodeURL by remember { mutableStateOf("") }
    val flashlightOn by remember { mutableStateOf(false) }
    var openImagePicker by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }
    val isConnected by WebSocketHolder.isConnected.collectAsState()

    // Navigate when connected
    LaunchedEffect(isConnected) {
        if (isConnected) {
            navigator?.replaceAll(MouseScreen())
        }
    }

    // Handle QR scan result and connect WebSocket
    LaunchedEffect(qrCodeURL) {
        if (qrCodeURL.isNotEmpty()) {
            isLoading = true
            try {
                val uri = URI("ws://$qrCodeURL:$PORT")
                WebSocketManager(
                    context = context,
                    uri = uri,
                    onConnected = {
                        isLoading = false
                        WebSocketHolder.setConnectionStatus(true)
                    },
                    onDisconnected = {
                        isLoading = false
                        WebSocketHolder.setConnectionStatus(false)
                    },
                    onError = {

                        isLoading = false
                        WebSocketHolder.setConnectionStatus(false)
                        navigator?.pop()
                    }
                ).connect()
            } catch (e: Exception) {
                isLoading = false
                Toast.makeText(context, "Invalid QR or Connection Error", Toast.LENGTH_SHORT).show()
                navigator?.pop()
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()) {

        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                QrScanner(
                    modifier = Modifier.clip(RoundedCornerShape(14.dp)),
                    flashlightOn = flashlightOn,
                    openImagePicker = openImagePicker,
                    onCompletion = { qrCodeURL = it },
                    imagePickerHandler = { openImagePicker = it },
                    onFailure = {
                        coroutineScope.launch {
                            val message = if (it.isEmpty()) "Invalid QR code" else it
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp, top = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "close",
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        navigator?.pop()
                    },
                tint = Color.Black
            )

            Text(
                text = "QRScanner",
                modifier = Modifier.padding(start = 10.dp),
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )


        }

        if (isLoading) {
            FullScreenLoading(isLoading = isLoading)
        }
    }
}


@Composable
@Preview
fun QrScannerPreview(){
    QrScannerScreenContent(
        navigator = null
    )
}