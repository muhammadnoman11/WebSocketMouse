package org.example.project.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.model.MouseEventMessage
import org.java_websocket.client.WebSocketClient

@Composable
fun TouchPad(json: Json, socketClient: WebSocketClient) {
    var lastPosition by remember { mutableStateOf<Offset?>(null) }
    var currentPosition by remember { mutableStateOf<Offset?>(null) }

    /** For animated gesture trail */
    val animatedStart = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val animatedEnd = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    LaunchedEffect(currentPosition) {
        if (lastPosition != null && currentPosition != null) {
            animatedStart.snapTo(lastPosition!!)
            animatedEnd.snapTo(currentPosition!!)
            launch {
                animatedStart.animateTo(
                    targetValue = currentPosition!!,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE0E0E0))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (socketClient.isOpen) {
                                socketClient.send(
                                    json.encodeToString(
                                        MouseEventMessage(
                                            type = "touchPadTap"
                                        )
                                    )
                                )
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            lastPosition = offset
                            currentPosition = offset
                        },
                        onDrag = { change, dragAmount ->
                            val newPos = currentPosition?.plus(dragAmount)
                            if (newPos != null && lastPosition != null && socketClient.isOpen) {
                                /** Send mouse move event to server */
                                socketClient.send(
                                    json.encodeToString(
                                        MouseEventMessage(
                                            type = "move",
                                            dx = dragAmount.x.toInt(),
                                            dy = dragAmount.y.toInt()
                                        )
                                    )
                                )
                                lastPosition = currentPosition
                                currentPosition = newPos
                            }
                        },
                        onDragEnd = {
                            lastPosition = null
                            currentPosition = null
                        },
                        onDragCancel = {
                            lastPosition = null
                            currentPosition = null
                        }
                    )
                }
        ) {

            if (lastPosition != null && currentPosition != null) {
                drawLine(
                    color = Color.Black.copy(alpha = 0.5f),
                    start = animatedStart.value,
                    end = animatedEnd.value,
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
            }
        }

        Box(
            modifier = Modifier
                .width(20.dp)
                .height(300.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFBDBDBD))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { _, dragAmount ->
                            if (socketClient.isOpen) {
                                /** Send mouse scroll event to server */
                                socketClient.send(
                                    json.encodeToString(
                                        MouseEventMessage(
                                            type = "scroll",
                                            dy = dragAmount.y.toInt()
                                        )
                                    )
                                )
                            }
                        }
                    )
                }
        )
    }
}