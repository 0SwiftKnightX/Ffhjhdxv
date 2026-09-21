package com.meridian.androidai.runtime

enum class MeridianMode { STARTING, READY, RUNNING, STOPPED, SHUTTING_DOWN }

data class MeridianState(
    val mode: MeridianMode = MeridianMode.STARTING,
    val activeTaskId: String? = null,
    val autonomousWorkAllowed: Boolean = true
) {
    fun summary(): String =
        "Meridian\nMode: $mode\nActive task: ${activeTaskId ?: "none"}\nAutonomous work: ${if (autonomousWorkAllowed) "allowed" else "stopped"}"
}
