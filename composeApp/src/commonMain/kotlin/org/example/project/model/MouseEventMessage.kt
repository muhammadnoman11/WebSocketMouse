package org.example.project.model

import kotlinx.serialization.Serializable

@Serializable
data class MouseEventMessage(
    val type: String, /** "move", "click" , "scroll" , "touchPadTap" */
    val dx: Int = 0,
    val dy: Int = 0,
    val button: String? = null /** "left", "right" */
)