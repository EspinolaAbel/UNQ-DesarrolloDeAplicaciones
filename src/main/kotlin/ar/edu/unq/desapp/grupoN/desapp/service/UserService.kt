package ar.edu.unq.desapp.grupoN.desapp.service

import ar.edu.unq.desapp.grupoN.desapp.model.Operation
import ar.edu.unq.desapp.grupoN.desapp.model.OperationStatus
import ar.edu.unq.desapp.grupoN.desapp.model.User
import ar.edu.unq.desapp.grupoN.desapp.persistence.UserRepository
import ar.edu.unq.desapp.grupoN.desapp.service.exeption.UserApiException
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(private val userRepo: UserRepository) {

    fun saveUser(user: User): User {
        // TODO: testear usuario con email existente
        if (userRepo.existsByEmail(user.email))
            throw UserApiException.emailAlreadyInUse(user.email);
        return userRepo.save(user)
    }

    fun findUser(id: Int): Optional<User> = userRepo.findById(id);

    fun findUserOrThrow(userId: Int): User = findUser(userId)
        .orElseThrow { UserApiException.userWithIdDoesNotExists(userId) }

    fun getUsers(): List<User> = userRepo.findAll()

    fun updateUserPunctuationOperation(userId: Int, points: Int, operationWasClosed: Boolean) {
        val user = findUserOrThrow(userId)
        user.reputation += points
        if (operationWasClosed)
            user.closedOperations += 1
        userRepo.save(user)
    }

}
