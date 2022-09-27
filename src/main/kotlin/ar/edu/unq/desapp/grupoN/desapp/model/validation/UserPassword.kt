package ar.edu.unq.desapp.grupoN.desapp.model.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy=[UserPasswordValidator::class])
/** Contraseña: al menos 1 minuscula, 1 mayuscula, 1 carac especial y min 6 */
annotation class UserPassword(
        val message: String = "invalid password",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)
