package ar.edu.unq.desapp.grupoN.desapp.service.exeption

import ar.edu.unq.desapp.grupoN.desapp.model.OperationStatus
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import java.util.*

class CryptoExchangeException private constructor(msg: String) : RuntimeException(msg) {

    companion object {
        fun advertisementDoesNotExists(uuid: UUID?): CryptoExchangeException {
            return entityDoesNotExists(uuid, "Advertisement")
        }
        fun operationDoesNotExists(uuid: UUID?): CryptoExchangeException {
            return entityDoesNotExists(uuid, "Operation")
        }
        private fun entityDoesNotExists(uuid: UUID?, entityName: String): CryptoExchangeException {
            return if (uuid != null)
                CryptoExchangeException("$entityName with uuid=$uuid does not exist")
            else
                CryptoExchangeException("$entityName uuid is null")
        }

        fun advertisementNotFoundOrInUse(uuid: UUID?): CryptoExchangeException {
            return CryptoExchangeException("Advertisement $uuid does not exists, is inactive or is in use by an operation")
        }

        fun advertisementPriceIsOutOfRange(symbol: Symbol, cryptoPrice: Double, rangePercentage: Double): CryptoExchangeException {
            return CryptoExchangeException(
                "Advertisement suggested price $$cryptoPrice for crypto $symbol is out of range. " +
                "It should be +-$rangePercentage% of the current crypto price"
            )
        }

    }

}
