package ar.edu.unq.desapp.grupoN.desapp.model

import ar.edu.unq.desapp.grupoN.desapp.model.OperationStatus
import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import java.util.*

class OperationException private constructor(msg: String) : RuntimeException(msg) {

    companion object {
        fun operationInvalidStatus(opUUID: UUID, currentStatus: OperationStatus, newStatus: OperationStatus): OperationException {
            return OperationException("Operation $opUUID with status $currentStatus cannot be set to status $newStatus")
        }

        fun operationStatusCannotBeUpdatedByUser(opUUID: UUID, status: OperationStatus, userId: Int): OperationException {
            return OperationException("Operation $opUUID with status $status cannot be updated by user with id $userId")
        }
    }

}
