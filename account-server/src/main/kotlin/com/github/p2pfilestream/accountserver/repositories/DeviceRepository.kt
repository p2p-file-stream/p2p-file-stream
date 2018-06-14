package com.github.p2pfilestream.accountserver.repositories

import com.github.p2pfilestream.Device
import org.hibernate.exception.ConstraintViolationException
import org.springframework.data.repository.CrudRepository

interface DeviceRepository : CrudRepository<Device, Long> {
    fun findByNickname(nickname: String)
}

/**
 * Save device
 * @return null if nickname isn't unique
 */
fun DeviceRepository.saveOrNull(device: Device): Device? {
    try {
        return save(device)
    } catch (e: Exception) {
        if (e.cause is ConstraintViolationException) {
            // Nickname already exits (violation of unique constraint)
            return null
        }
        throw e
    }
}