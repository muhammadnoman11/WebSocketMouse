package org.example.project

import kotlinx.coroutines.flow.StateFlow

interface Platform {
    val name: String
}


expect fun getPlatform(): Platform

