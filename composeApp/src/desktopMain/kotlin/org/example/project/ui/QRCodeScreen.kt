package org.example.project.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.onClick
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.util.WebSocketHolder
import org.example.project.util.Helper
import qrgenerator.generateQrCode

class QRCodeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        QRCodeScreenContent(navigator)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QRCodeScreenContent(navigator: Navigator?) {

    val ipAddress = remember { mutableStateOf(Helper.getLocalIpAddress() ?: "Not found") }
    val generatedQRCode = remember { mutableStateOf<ImageBitmap?>(null) }
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val isConnected by WebSocketHolder.isConnected.collectAsState()


    LaunchedEffect(ipAddress.value){
        println("Desktop Ip: ${ipAddress.value}")
        generateQrCode(
            ipAddress.value,
            onSuccess = { info, qrCode ->
                generatedQRCode.value = qrCode
            },
            onFailure = {
                scope.launch {
                    snackBarHostState.showSnackbar("Something went wrong")
                }
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
            .background(color = Color.White)
    ){

       Icon(
           modifier = Modifier
               .padding(10.dp)
               .size(25.dp)
               .onClick {
                   navigator?.pop()
               },
           painter = painterResource("images/ic_back.svg"),
           contentDescription = "Back",
       )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){


            Text(
                modifier = Modifier
                    .padding(
                        vertical = 25.dp,
                        horizontal = 16.dp),
                text = "Scan the QR code to connect",
                fontSize = 24.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )


            generatedQRCode.value?.let { qrCode ->
                QRCodeViewer(qrCode)
            }

            Spacer( modifier = Modifier.padding(vertical = 20.dp))


            Text(
                text = if(isConnected) "Connected Enjoy!" else "Not connected",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}


@Composable
fun QRCodeViewer(qrCode: ImageBitmap) {
    var isLoading by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(500)
        isLoading = false
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(vertical = 15.dp)
            .background(Color.White)
            .border(BorderStroke(3.dp, Color.Black))
            .size(250.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Image(
                bitmap = qrCode,
                contentScale = ContentScale.Fit,
                contentDescription = "QR Code",
                modifier = Modifier.fillMaxSize()
            )
        }

    }
}

