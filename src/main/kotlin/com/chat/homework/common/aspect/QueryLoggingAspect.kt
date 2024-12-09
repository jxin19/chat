package com.chat.homework.common.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class QueryLoggingAspect {

    private val logger = LoggerFactory.getLogger(QueryLoggingAspect::class.java)

    @Around("execution(* org.springframework.data.repository.Repository+.*(..))")
    fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any {
        val start = System.currentTimeMillis()
        val result = joinPoint.proceed()
        val elapsedTime = System.currentTimeMillis() - start
        logger.info("${joinPoint.signature} executed in ${elapsedTime}ms")
        return result
    }
}
