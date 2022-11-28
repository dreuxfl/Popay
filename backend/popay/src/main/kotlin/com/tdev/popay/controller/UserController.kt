package com.tdev.popay.controller

import com.tdev.popay.dtos.LoginUser
import com.tdev.popay.model.User
import com.tdev.popay.repository.UserRepository
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

    @GetMapping("/users")
    fun getAllUsers(): List<User> =
            userRepository.findAll()

    @PostMapping("/register")
    fun createUser(@Valid @RequestBody user: User): ResponseEntity<User> {
        return userRepository.findByEmail(user.email).map { ResponseEntity(it, HttpStatus.CONFLICT) }
            .orElseGet {
                val newUser = user.copy(password = BCryptPasswordEncoder().encode(user.password))
                ResponseEntity(userRepository.save(newUser), HttpStatus.CREATED)
            }
    }

    @PostMapping("/login")
    fun loginUser(@Valid @RequestBody user: LoginUser): ResponseEntity<User>? {
        return userRepository.findByEmail(user.email).map { ResponseEntity(it, HttpStatus.OK) }
            .orElseGet { ResponseEntity(HttpStatus.NOT_FOUND) }
    }

    @GetMapping("/user/{id}")
    fun getUserById(@PathVariable(value = "id") userId: Long): ResponseEntity<User> {
        return userRepository.findById(userId).map { user ->
            ResponseEntity.ok(user)
        }.orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/user/{id}")
    fun updateUserById(@PathVariable(value = "id") userId: Long,
                       @Valid @RequestBody newUser: User): ResponseEntity<User> {
        return userRepository.findById(userId).map { existingUser ->
            val updatedUser: User = existingUser
                    .copy(
                        first_name = newUser.first_name,
                        last_name = newUser.last_name,
                        email = newUser.email,
                        password = BCryptPasswordEncoder().encode(newUser.password),
                        wallet = newUser.wallet
                    )
            ResponseEntity.ok().body(userRepository.save(updatedUser))
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/user/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeUserById(@PathVariable(value = "id") userId: Long): ResponseEntity<Void> {
        return userRepository.findById(userId).map { user  ->
            userRepository.delete(user)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())
    }
}
