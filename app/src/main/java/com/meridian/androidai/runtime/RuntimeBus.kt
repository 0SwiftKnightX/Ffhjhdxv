package com.meridian.androidai.runtime

import java.util.concurrent.CopyOnWriteArrayList

class RuntimeBus {
    private val listeners = CopyOnWriteArrayList<(RuntimeEvent) -> Unit>()
    fun subscribe(listener: (RuntimeEvent) -> Unit) { listeners += listener }
    fun publish(event: RuntimeEvent) { listeners.forEach { it(event) } }
    fun clear() { listeners.clear() }
}
