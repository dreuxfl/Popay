package com.tdev.popay.controller

import com.tdev.popay.dto.LoginDto
import com.tdev.popay.dto.ResponseMessage
import com.tdev.popay.model.User
import com.tdev.popay.repository.UserRepository
import com.tdev.popay.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class UserController(private val userRepository: UserRepository) {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult.fieldErrors.forEach { error -> errors[error.field] = error.defaultMessage!! }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody newUser: User): ResponseEntity<Any> {
        val userAlreadyExist = userRepository.findByEmail(newUser.email)
        if (!userAlreadyExist.isPresent) {
            val user = User(
                first_name = newUser.first_name,
                last_name = newUser.last_name,
                email = newUser.email,
                password = BCryptPasswordEncoder().encode(newUser.password),
                wallet = newUser.wallet
            )
            userRepository.save(user)
            return ResponseEntity(ResponseMessage(true, "User registered successfully"), HttpStatus.CREATED)
        }
        return ResponseEntity(ResponseMessage(false, "User already exist"), HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/login")
    fun loginUser(@Valid @RequestBody loginDto: LoginDto): ResponseEntity<Any> {
        val checkUser = userRepository.findByEmail(loginDto.email)
        if (checkUser.isPresent) {
            val user = checkUser.get()
            if (user.comparePassword(loginDto.password)) {
                val token = TokenService().generateToken(user)
                val jsonToken = mapOf("token" to token)
                return ResponseEntity(jsonToken, HttpStatus.OK)
            }
        }
        return ResponseEntity(ResponseMessage(false, "Invalid credentials"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/users")
    fun getAllUsers(): List<User> = userRepository.findAll()

    @GetMapping("/user/{id}")
    fun getUserById(@PathVariable(value = "id") userId: Long): ResponseEntity<Any> {
        val checkUser = userRepository.findById(userId)
        if (checkUser.isPresent) {
            return ResponseEntity(checkUser.get(), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.NOT_FOUND)
    }

    @PutMapping("/user/{id}")
    fun updateUserById(
        @PathVariable(value = "id") userId: Long,
        @Valid @RequestBody updatedUser: User
    ): ResponseEntity<Any> {
        val checkUser = userRepository.findById(userId)
        if (checkUser.isPresent) {
            val user = checkUser.get().copy(
                first_name = updatedUser.first_name,
                last_name = updatedUser.last_name,
                email = updatedUser.email,
                password = BCryptPasswordEncoder().encode(updatedUser.password),
                wallet = updatedUser.wallet
            )
            userRepository.save(user)
            return ResponseEntity(ResponseMessage(true, "User updated successfully"), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("/user/{id}")
    fun removeUserById(@PathVariable(value = "id") userId: Long): ResponseEntity<Any> {
        val checkUser = userRepository.findById(userId)
        if (checkUser.isPresent) {
            userRepository.deleteById(userId)
            return ResponseEntity(ResponseMessage(true, "User deleted successfully"), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.NOT_FOUND)
    }
}
