package ar.edu.unq.desapp.grupoN.desapp.service

import ar.edu.unq.desapp.grupoN.desapp.model.User
import ar.edu.unq.desapp.grupoN.desapp.persistence.UserRepository
import ar.edu.unq.desapp.grupoN.desapp.service.exception.UserApiException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
        val userRepo: UserRepository,
        val passwordEncoder: PasswordEncoder
    ) {

    fun saveUser(user: User): User {
        if (userRepo.existsByEmail(user.email))
            throw UserApiException.emailAlreadyInUse(user.email);
        user.password = passwordEncoder.encode(user.password)
        return userRepo.save(user)
    }

    fun findUser(id: Int): Optional<User> = userRepo.findById(id);

    fun findUserOrThrow(userId: Int): User = findUser(userId)
        .orElseThrow { UserApiException.userWithIdDoesNotExists(userId) }

    fun getUsers(): List<User> = userRepo.findAll()

    fun updateUserPunctuationOperation(userId: Int, points: Int, operationWasClosed: Boolean) {
        val user = findUserOrThrow(userId)
        user.points += points
        if (operationWasClosed)
            user.closedOperations += 1
        if (user.closedOperations != 0)
            user.reputation = user.points / user.closedOperations
        userRepo.save(user)
    }

}
