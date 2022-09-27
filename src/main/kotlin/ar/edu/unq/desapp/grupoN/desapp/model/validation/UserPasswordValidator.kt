package ar.edu.unq.desapp.grupoN.desapp.model.validation

import java.util.stream.Collectors.joining
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class UserPasswordValidator: ConstraintValidator<UserPassword, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        val errors = HashSet<String>()
        /** Contraseña: al menos 1 minuscula, 1 mayuscula, 1 carac especial y min 6 */
        if (value == null)
            errors.add("cannot be null")
        else {
            if (!hasAtLeast6CharactersLong(value))
                errors.add("has less than 6 characters")
            if (!atLeastOneLowerCaseCharacter(value))
                errors.add("should have at least 1 lowercase character")
            if (!atLeastOneUpperCaseCharacter(value))
                errors.add("should have at least 1 uppercase character")
            if (!atLeastOneSpecialCharacter(value))
                errors.add("should have at least 1 special character")
        }
        if (errors.size != 0)
            context.disableDefaultConstraintViolation();
            context
                .buildConstraintViolationWithTemplate(errors.stream().collect(joining(", ")))
                .addConstraintViolation();
        return errors.size == 0
    }

    private fun atLeastOneUpperCaseCharacter(value: String): Boolean = value.contains(Regex("[A-Z]"))
    private fun atLeastOneLowerCaseCharacter(value: String): Boolean = value.contains(Regex("[a-z]"))
    private fun atLeastOneSpecialCharacter(value: String): Boolean = value.contains(Regex("[^A-Za-z0-9]"))
    private fun hasAtLeast6CharactersLong(value: String): Boolean = value.length >= 6

}
