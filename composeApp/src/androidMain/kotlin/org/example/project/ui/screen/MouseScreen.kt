package org.example.project.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.component.TouchPad
import org.example.project.util.WebSocketHolder
import org.example.project.model.MouseEventMessage
import org.java_websocket.client.WebSocketClient


class MouseScreen : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current
        MouseScreenContent(navigator)
    }
}

@Composable
fun MouseScreenContent(navigator: Navigator?) {
    val socketClient = WebSocketHolder.client
    val json = Json
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
            .background(color = Color.White)
    ){
        Column(
            modifier = Modifier.fillMaxSize()
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){

            Text(
                modifier = Modifier
                    .padding(
                        vertical = 25.dp,
                        horizontal = 16.dp),
                text = "Remote Mouse",
                fontSize = 24.sp,
                color = Color.Black,
            )

            Row (
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){

                Button(
                    onClick = {
                        if (socketClient != null) {
                            if (socketClient.isOpen) {
                                socketClient.send(
                                    json.encodeToString(MouseEventMessage("click", button = "left"))
                                )
                            }
                        }

                    }
                ) {

                    Text("Left Click")

                }

                Button(
                    onClick = {
                        if (socketClient != null) {
                            if (socketClient.isOpen) {
                                socketClient.send(
                                    json.encodeToString(MouseEventMessage("click", button = "right"))
                                )
                            }
                        }

                    }
                ) {

                    Text("Right Click")

                }

            }


            socketClient?.let {
                TouchPad(json = json, socketClient = socketClient)
            }


            Button(
                modifier = Modifier.padding(vertical = 15.dp),
                onClick = {
                    socketClient?.close()
                    WebSocketHolder.setConnectionStatus(false)

                    coroutineScope.launch {
                        delay(300)
                        navigator?.replaceAll(MainScreen())
                    }
                }
            ) {

                Text("Disconnect")
            }

        }
    }
}






@Composable
@Preview(showBackground = true)
fun MouseScreenPreview(){
    MouseScreenContent(
        navigator = null
    )
}
