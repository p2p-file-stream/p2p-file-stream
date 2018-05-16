package com.github.p2pfilestream.accountserver

import com.github.p2pfilestream.Device
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
        return null
    }
}