package ar.edu.unq.desapp.grupoN.desapp.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import ar.edu.unq.desapp.grupoN.desapp.model.OperationStatus.*

class OperationTest {

    @Test
    fun validate_initialization() {
        val user = newUser()
        val adUser = newUser()
        val ad = newAdvertisement(adUser)

        val op = Operation(null, user, ad)

        assertNull(op.id)
        assertEquals(user, op.user)
        assertEquals(ad, op.advertisement)
        assertEquals(adUser, op.advertisement.user)
        assertEquals(op.creationTimestamp, op.updateTimestamp)
        assertEquals(0, op.duration().toSeconds())
        assertEquals(STARTED, op.status)
        assertNull(op.cryptoPriceClose)
    }

    @Test
    fun validate_that_status_is_completed_and_isClosed() {
        val op = Operation(null, newUser(), newAdvertisement(newUser()))
        validateIsClosedAndStatus(COMPLETED, op) { ope -> ope.wasSuccessfullyCompleted() }

    }

    @Test
    fun validate_that_status_is_cancelled_and_isClosed() {
        val op = Operation(null, newUser(), newAdvertisement(newUser()))
        validateIsClosedAndStatus(CANCELLED, op) { ope -> ope.wasCancelled() }
    }

    @Test
    fun user_interested_updates_operation_STARTED_to_INTERESTED_USER_DEPOSIT() {
        val userInterested = newUser()
        userInterested.id = 999
        val op = Operation(null, userInterested, newAdvertisement(newUser()))
        val newStatus = INTERESTED_USER_DEPOSIT

        assertEquals(STARTED, op.status)
        op.updateStatus(userInterested.id!!, newStatus)
        assertEquals(newStatus, op.status)
    }

    @Test
    fun user_ad_owner_updates_operation_INTERESTED_USER_DEPOSIT_to_COMPLETED() {
        val userAdOwner = newUser()
        userAdOwner.id = 999
        val op = Operation(null, newUser(), newAdvertisement(userAdOwner))
        op.status = INTERESTED_USER_DEPOSIT
        val newStatus = COMPLETED

        assertEquals(INTERESTED_USER_DEPOSIT, op.status)
        op.updateStatus(userAdOwner.id!!, newStatus)
        assertEquals(newStatus, op.status)
    }

    @Test
    fun user_ad_owner_updates_operation_STARTED_to_CANCELLED() {
        val userAdOwner = newUser()
        userAdOwner.id = 999
        val op = Operation(null, newUser(), newAdvertisement(userAdOwner))
        val newStatus = CANCELLED

        assertEquals(STARTED, op.status)
        op.updateStatus(userAdOwner.id!!, newStatus)
        assertEquals(newStatus, op.status)
    }

    @Test
    fun user_interested_updates_operation_STARTED_to_CANCELLED() {
        val userInterested = newUser()
        userInterested.id = 999
        val op = Operation(null, userInterested, newAdvertisement(newUser()))
        val newStatus = CANCELLED

        assertEquals(STARTED, op.status)
        op.updateStatus(userInterested.id!!, newStatus)
        assertEquals(newStatus, op.status)
    }

    private fun validateIsClosedAndStatus(status: OperationStatus, op: Operation, fn: (Operation) -> Boolean) {
        assertEquals(STARTED, op.status)
        assertFalse(fn(op))
        assertFalse(op.isClosed())
        op.status = status
        assertTrue(fn(op))
        assertTrue(op.isClosed())
    }

    private fun newAdvertisement(user: User): Advertisement {
        return Advertisement(
            null,
            user,
            OperationType.BUY,
            Symbol.AAVEUSDT,
            0.0,
            CurrencyAmount(CurrencyCode.USD,0.0),
        )
    }

    private fun newUser(): User {
        return User(
            null,
            "name",
            "lastName",
            "user@test.com",
            "route 66",
            "1234",
            "2222222222222222222222",
            "88888888"
        )
    }

}