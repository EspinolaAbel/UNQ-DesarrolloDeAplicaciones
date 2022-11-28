package ar.edu.unq.desapp.grupoN.desapp.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OperationTest {

    fun when_created_it_should_be_IN_PROGRESS_active_with_creation_and_update_timestamps_set_to_now() {
        val ad = Advertisement(null, null, OperationType.BUY, Symbol.AAVEUSDT, 0.0, 0.0, 0.0)
        Operation(null, User(), ad)
    }
}