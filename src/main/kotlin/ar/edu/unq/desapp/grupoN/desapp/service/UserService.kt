package ar.edu.unq.desapp.grupoN.desapp.service

import ar.edu.unq.desapp.grupoN.desapp.persistence.entity.User
import ar.edu.unq.desapp.grupoN.desapp.persistence.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(private val userRepo: UserRepository) {

    fun saveUser(user: User): User = userRepo.save(user)
    fun getUser(id: Int): Optional<User> = userRepo.findById(id);
    fun getUsers(): List<User> = userRepo.findAll()

}
