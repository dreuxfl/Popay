package com.tdev.popay.controller

import com.tdev.popay.dto.LoginDto
import com.tdev.popay.dto.ResponseMessage
import com.tdev.popay.model.User
import com.tdev.popay.service.HashService
import com.tdev.popay.service.TokenService
import com.tdev.popay.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api")
class UserController(
    private val hashService: HashService,
    private val tokenService: TokenService,
    private val userService: UserService
) {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult.fieldErrors.forEach { error -> errors[error.field] = error.defaultMessage!! }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody newUser: User): ResponseEntity<Any> {
        if (!userService.existsByEmail(newUser.email)) {
            val user = User(
                first_name = newUser.first_name,
                last_name = newUser.last_name,
                email = newUser.email,
                password = hashService.hashBcrypt(newUser.password),
                wallet = newUser.wallet
            )
            userService.save(user)
            return ResponseEntity(ResponseMessage(true, "User registered successfully"), HttpStatus.CREATED)
        }
        return ResponseEntity(ResponseMessage(false, "User already exist"), HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/login")
    fun loginUser(@Valid @RequestBody loginDto: LoginDto): ResponseEntity<Any> {
        val user = userService.findByEmail(loginDto.email)
        if (user != null) {
            if (hashService.checkBcrypt(loginDto.password, user.password)) {
                val token = tokenService.generateToken(user)
                val jsonToken = mapOf("token" to token)
                return ResponseEntity(jsonToken, HttpStatus.OK)
            }
        }
        return ResponseEntity(ResponseMessage(false, "Invalid credentials"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/users")
    fun getAllUsers(): List<User> = userService.findAll()

    @GetMapping("/user/{id}")
    fun getUserById(@PathVariable(value = "id") userId: Long): ResponseEntity<Any> {
        val checkUser = userService.findById(userId)
        if (checkUser != null) {
            return ResponseEntity(checkUser, HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.NOT_FOUND)
    }

    @PutMapping("/user/{id}")
    fun updateUserById(
        @PathVariable(value = "id") userId: Long,
        @Valid @RequestBody updatedUser: User
    ): ResponseEntity<Any> {
        val checkUser = userService.findById(userId)
        if (checkUser != null) {
            val user = checkUser.copy(
                first_name = updatedUser.first_name,
                last_name = updatedUser.last_name,
                email = updatedUser.email,
                password = hashService.hashBcrypt(updatedUser.password),
                wallet = updatedUser.wallet
            )
            userService.save(user)
            return ResponseEntity(ResponseMessage(true, "User updated successfully"), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("/user/{id}")
    fun removeUserById(@PathVariable(value = "id") userId: Long): ResponseEntity<Any> {
        val checkUser = userService.findById(userId)
        if (checkUser != null) {
            userService.deleteById(userId)
            return ResponseEntity(ResponseMessage(true, "User deleted successfully"), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.NOT_FOUND)
    }
}
