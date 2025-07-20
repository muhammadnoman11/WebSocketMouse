package org.example.project.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import  androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import org.example.project.R


class MainScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        ConnectDeviceScreenContent(navigator)
    }
}

@Composable
fun ConnectDeviceScreenContent(navigator: Navigator?){
    Box(
        modifier = Modifier
            .fillMaxSize().statusBarsPadding()
            .background(color = Color.White),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_qrcode_mouse),
                contentDescription = "Sample Image",
                modifier = Modifier
                    .padding(top = 25.dp)
                    .size(350.dp)

            )

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = "Welcome to Remote\nMouse",
                fontSize = 24.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )


            Spacer(modifier = Modifier.padding(vertical = 20.dp))

            Button(
                onClick = {
                    navigator?.push(QrScannerScreen())
                },
            ) {
                Text(text = "Start Scan")
            }
        }
    }
}


@Preview
@Composable
fun ConnectDeviceScreenPreview(){
    ConnectDeviceScreenContent(
        navigator = null
    )
}

