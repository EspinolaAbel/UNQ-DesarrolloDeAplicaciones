package ar.edu.unq.desapp.grupoN.desapp.service.aop

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.CodeSignature
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest


@Aspect
@Component
class RestControllerAspect(val mapper: ObjectMapper) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RestControllerAspect::class.java)
    }

    @Pointcut("within(ar.edu.unq.desapp.grupoN.desapp.webservice..*) && within(@org.springframework.web.bind.annotation.RestController *)" )
    fun pointcut() { }

    @Before("pointcut()")
    fun logMethod(joinPoint: JoinPoint) {
        val signature: MethodSignature = joinPoint.signature as MethodSignature
        val parameters = getParameters(joinPoint)
        try {
            logger.info("==> method(s): {}, arguments: {} ",
                signature.method.name, mapper.writeValueAsString(parameters)
            )
        } catch (e: JsonProcessingException) {
            logger.error("Error while converting", e)
        }
    }

    @AfterReturning(pointcut = "pointcut()", returning = "entity")
    fun logMethodAfter(joinPoint: JoinPoint, entity: ResponseEntity<*>?) {
        val signature: MethodSignature = joinPoint.signature as MethodSignature
        try {
            logger.info("<== method(s): {}, retuning: {}",
                signature.method, mapper.writeValueAsString(entity)
            )
        } catch (e: JsonProcessingException) {
            logger.error("Error while converting", e)
        }
    }

    private fun getParameters(joinPoint: JoinPoint): Map<String, Any> {
        val signature = joinPoint.signature as CodeSignature
        val map: HashMap<String, Any> = HashMap()
        val parameterNames = signature.parameterNames
        for (i in parameterNames.indices) {
            map[parameterNames[i]] = joinPoint.args[i]
        }
        return map
    }
}
