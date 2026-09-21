package com.meridian.androidai.core.cognition

interface CognitiveCore {
    val id: CoreId

    fun accept(message: CognitiveMessage): List<CognitiveMessage>
}
