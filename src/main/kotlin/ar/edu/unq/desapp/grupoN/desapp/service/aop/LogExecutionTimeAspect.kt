package ar.edu.unq.desapp.grupoN.desapp.service.aop

import org.aspectj.lang.ProceedingJoinPoint

import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch


@Aspect
@Component
class LogExecutionTimeAspect {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Around("@annotation(ar.edu.unq.desapp.grupoN.desapp.service.aop.LogExecutionTime)")
    @Throws(Throwable::class)
    fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any {
        val stopWatch = StopWatch()
        stopWatch.start()
        val proceed = joinPoint.proceed()
        stopWatch.stop()
        logger.info("\"{}\" executed in {} ms", joinPoint.signature, stopWatch.totalTimeMillis)
        return proceed
    }

}
