package ar.edu.unq.desapp.grupoN.desapp.model

import java.time.Instant

data class P2PTransaction(
    val buyer: User,
    val seller: User,
    val coin: Symbol,
    val price: Double,
    val quantity: Double
) {
    var state: State = State.IN_PROCESS; private set
    var isClosed: Boolean = false; private set
    var endTimestamp: Instant? = null; private set
    val startTimestamp: Instant = Instant.now()

    enum class State {
        IN_PROCESS,
        SUCCESSFUL,
        CANCELLED_BY_SYSTEM,
        CANCELLED_BY_SELLER,
        CANCELLED_BY_BUYER
    }
}