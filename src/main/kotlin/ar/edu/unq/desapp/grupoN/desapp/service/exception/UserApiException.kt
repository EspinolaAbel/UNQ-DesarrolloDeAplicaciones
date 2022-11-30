package ar.edu.unq.desapp.grupoN.desapp.service.exception

class UserApiException : RuntimeException {

    private constructor(msg: String) : super(msg)

    companion object {
        fun emailAlreadyInUse(email: String): UserApiException {
            return UserApiException("There is already a registered user with email $email")
        }
        fun userWithIdDoesNotExists(userId: Int): UserApiException {
            return UserApiException("User with id=$userId does not exist")
        }
    }

}
