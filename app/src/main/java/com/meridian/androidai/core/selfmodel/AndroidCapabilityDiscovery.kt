package com.meridian.androidai.core.selfmodel

import android.Manifest
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

        val networkPermissionGranted =
            packageManager.checkPermission(
                Manifest.permission.INTERNET,
                appContext.packageName
            ) == PackageManager.PERMISSION_GRANTED

        capabilities += Capability(
            id = "android.network",
            description = "Use network services when permitted and connectivity is available",
            state = if (networkPermissionGranted) {
                CapabilityState.AVAILABLE
            } else {
                CapabilityState.REQUIRES_PERMISSION
            },
            requiredPermissions = setOf(Manifest.permission.INTERNET)
        )

        capabilities += Capability(
            id = "android.camera",
            description = "Access the device camera when available and permission is granted",
            state = when {
                !packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ->
                    CapabilityState.UNAVAILABLE
                appContext.checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED ->
                    CapabilityState.AVAILABLE
                else ->
                    CapabilityState.REQUIRES_PERMISSION
            },
            requiredPermissions = setOf(Manifest.permission.CAMERA)
        )

        capabilities += Capability(
            id = "android.microphone",
            description = "Access the device microphone when available and permission is granted",
            state = when {
                !packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE) ->
                    CapabilityState.UNAVAILABLE
                appContext.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED ->
                    CapabilityState.AVAILABLE
                else ->
                    CapabilityState.REQUIRES_PERMISSION
            },
            requiredPermissions = setOf(Manifest.permission.RECORD_AUDIO)
        )

        return capabilities
    }
}
