package ar.edu.unq.desapp.grupoN.desapp.service.exeption

import java.util.*

class CryptoExchangeException: RuntimeException {
    private constructor(msg: String) : super(msg)

    companion object {
        fun advertisementDoesNotExists(uuid: UUID?): CryptoExchangeException {
            return if (uuid != null)
                CryptoExchangeException("Advertisement with uuid=$uuid does not exist")
            else
                CryptoExchangeException("Advertisement uuid is null")
        }
        fun advertisementAlreadyInAnOperation(uuid: UUID?): Throwable {
            return CryptoExchangeException("Advertisement $uuid is currently used in other operation")
        }
    }

}
