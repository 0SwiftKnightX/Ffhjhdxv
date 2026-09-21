package com.meridian.androidai.core.selfmodel

data class MeridianSelfModel(
    val identity: String = "Meridian",
    val operatingMode: OperatingMode = OperatingMode.STARTING,
    val activeTaskId: String? = null,
    val capabilities: List<Capability> = emptyList(),
    val modelProvider: String? = null,
    val inputChannels: Set<String> = emptySet(),
    val outputChannels: Set<String> = emptySet(),
    val authorityState: String = "USER_CONTROLLED",
    val resourceState: Map<String, String> = emptyMap()
) {
    fun capability(id: String): Capability? = capabilities.firstOrNull { it.id == id }

    fun canUse(id: String): Boolean =
        capability(id)?.state == CapabilityState.AVAILABLE
}
