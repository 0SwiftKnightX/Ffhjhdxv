package com.meridian.androidai.runtime

import android.content.Context
import java.util.concurrent.atomic.AtomicReference

class MeridianRuntime(context: Context) {
    private val appContext = context.applicationContext
    private val bus = RuntimeBus()
    private val state = AtomicReference(MeridianState())

    fun start() {
        val current = state.get()
        if (current.mode == MeridianMode.SHUTTING_DOWN) return
        state.set(current.copy(mode = MeridianMode.READY, autonomousWorkAllowed = true))
        bus.publish(RuntimeEvent.Started)
    }

    fun stopAutonomousWork() {
        val current = state.get()
        state.set(current.copy(mode = MeridianMode.STOPPED, activeTaskId = null, autonomousWorkAllowed = false))
        bus.publish(RuntimeEvent.StopRequested)
    }

    fun beginTask(taskId: String): Boolean {
        val current = state.get()
        if (current.mode == MeridianMode.STOPPED || !current.autonomousWorkAllowed) return false
        state.set(current.copy(mode = MeridianMode.RUNNING, activeTaskId = taskId))
        bus.publish(RuntimeEvent.TaskStarted(taskId))
        return true
    }

    fun finishTask(taskId: String) {
        val current = state.get()
        if (current.activeTaskId == taskId) {
            state.set(current.copy(mode = MeridianMode.READY, activeTaskId = null))
            bus.publish(RuntimeEvent.TaskFinished(taskId))
        }
    }

    fun snapshot(): MeridianState = state.get()
    fun events(): RuntimeBus = bus

    fun shutdown() {
        state.set(state.get().copy(mode = MeridianMode.SHUTTING_DOWN, activeTaskId = null, autonomousWorkAllowed = false))
        bus.publish(RuntimeEvent.ShutdownRequested)
        bus.clear()
    }
}
