package com.chat.homework.common.application

import org.redisson.api.RedissonClient
import org.springframework.dao.CannotAcquireLockException
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedissonDistributedLockService(
    private val redissonClient: RedissonClient
) {
    fun <T> executeLockOperation(lockKey: String, timeoutSeconds: Long, task: () -> T): T {
        val lock = redissonClient.getLock(lockKey)

        try {
            val isLocked = lock.tryLock(timeoutSeconds, TimeUnit.SECONDS)
            if (isLocked) {
                return task()
            } else {
                throw CannotAcquireLockException("Failed to acquire lock: $lockKey")
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException("Lock interrupted", e)
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}
