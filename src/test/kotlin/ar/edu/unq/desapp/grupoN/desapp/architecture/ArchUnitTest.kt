package ar.edu.unq.desapp.grupoN.desapp.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ArchUnitTest {

    @Test
    fun givenPresentationLayerClasses_thenWrongCheckFails() {
        val packageName = "ar.edu.unq.desapp.grupoN.desapp.webservice"
        val javaClasses: JavaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages(packageName)
        val rule: ArchRule = classes()
            .that()
            .resideInAPackage("..webservice..")
            .should().onlyDependOnClassesThat()
            .resideInAPackage("..service..")
        Assertions.assertThrows(AssertionError::class.java) {
            rule.check( javaClasses )
        }
    }

    @Test
    fun givenPresentationLayerClasses_thenCheckWithFrameworkDependenciesSuccess() {
        val packageName = "ar.edu.unq.desapp.grupoN.desapp.webservice"
        val javaClasses: JavaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages(packageName)
        val rule: ArchRule = classes()
            .that()
            .resideInAPackage("..webservice..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(
                "..service..",
                "java..", "javax..",
                "kotlin..", "org.jetbrains..", // estas son agregadas por el IDE o kotlin
                "org.springframework..",
                "io.swagger.annotations..",
                "ar.edu.unq.desapp.grupoN.desapp..",
            )
        rule.check(javaClasses)
    }

    @Test
    fun givenPresentationLayerClasses_thenNoPersistenceLayerAccess() {
        val packageName = "ar.edu.unq.desapp.grupoN.desapp.webservice"
        val javaClasses: JavaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages(packageName)
        val rule: ArchRule = noClasses()
            .that()
            .resideInAPackage("..webservice..")
            .should().dependOnClassesThat()
            .resideInAPackage("..persistence..")
        rule.check(javaClasses)
    }

}
