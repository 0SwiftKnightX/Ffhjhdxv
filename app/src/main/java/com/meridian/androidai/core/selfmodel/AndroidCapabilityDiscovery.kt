package com.meridian.androidai.core.selfmodel

import android.content.Context
import android.content.pm.PackageManager

class AndroidCapabilityDiscovery(
    context: Context
) : CapabilityDiscovery {
    private val appContext = context.applicationContext

    override fun discover(): List<Capability> {
        val packageManager = appContext.packageManager
        val capabilities = mutableListOf<Capability>()

        capabilities += Capability(
            id = "android.application",
            description = "Run as an Android application",
            state = CapabilityState.AVAILABLE
        )

        capabilities += Capability(
            id = "android.network",
            description = "Access network services when the application has network permission and connectivity",
            state = if (packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)) {
                CapabilityState.AVAILABLE
            } else {
                CapabilityState.UNAVAILABLE
            },
            requiredPermissions = setOf("android.permission.INTERNET")
        )

        capabilities += Capability(
            id = "android.camera",
            description = "Access the device camera when available and permission is granted",
            state = if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                CapabilityState.REQUIRES_PERMISSION
            } else {
                CapabilityState.UNAVAILABLE
            },
            requiredPermissions = setOf("android.permission.CAMERA")
        )

        capabilities += Capability(
            id = "android.microphone",
            description = "Access the device microphone when available and permission is granted",
            state = if (packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
                CapabilityState.REQUIRES_PERMISSION
            } else {
                CapabilityState.UNAVAILABLE
            },
            requiredPermissions = setOf("android.permission.RECORD_AUDIO")
        )

        return capabilities
    }
}
