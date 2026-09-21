package com.meridian.androidai.core.selfmodel

data class Capability(
    val id: String,
    val description: String,
    val state: CapabilityState,
    val requiredPermissions: Set<String> = emptySet()
)
