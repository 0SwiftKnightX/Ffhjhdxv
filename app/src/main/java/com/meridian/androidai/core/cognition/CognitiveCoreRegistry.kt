package com.meridian.androidai.core.cognition

class CognitiveCoreRegistry {
    private val cores = LinkedHashMap<CoreId, CognitiveCore>()

    fun register(core: CognitiveCore) {
        cores[core.id] = core
    }

    fun unregister(id: CoreId) {
        cores.remove(id)
    }

    fun get(id: CoreId): CognitiveCore? = cores[id]

    fun all(): List<CognitiveCore> = cores.values.toList()

    fun contains(id: CoreId): Boolean = cores.containsKey(id)
}
