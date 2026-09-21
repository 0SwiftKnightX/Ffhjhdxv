package com.meridian.androidai.runtime

sealed interface RuntimeEvent {
    data object Started : RuntimeEvent
    data object StopRequested : RuntimeEvent
    data object ShutdownRequested : RuntimeEvent
    data class TaskStarted(val taskId: String) : RuntimeEvent
    data class TaskFinished(val taskId: String) : RuntimeEvent
}
