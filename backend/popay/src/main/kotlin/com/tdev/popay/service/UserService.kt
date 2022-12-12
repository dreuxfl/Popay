package com.tdev.popay.service

import com.tdev.popay.model.User
import com.tdev.popay.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun findById(id: Long): User? {
        return userRepository.findByIdOrNull(id)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    fun save(user: User): User {
        return userRepository.save(user)
    }

    fun deleteById(id: Long) {
        userRepository.deleteById(id)
    }
}
