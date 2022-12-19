package com.tdev.popay.controller

import com.tdev.popay.dto.ResponseMessage
import com.tdev.popay.model.Product
import com.tdev.popay.service.ProductService
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
class ProductController(
    private val productService: ProductService,
    private val tokenService: TokenService,
    private val userService: UserService
) {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult.fieldErrors.forEach { error -> errors[error.field] = error.defaultMessage!! }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/product")
    fun createProduct(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody product: Product
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val newProduct = Product(
                    price = product.price,
                    caption = product.caption,
                    description = product.description,
                    stock = product.stock,
                )
                productService.save(newProduct)
                return ResponseEntity(ResponseMessage(true, "Product created successfully"), HttpStatus.CREATED)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/products")
    fun getAllProducts(@RequestHeader("Authorization") token: String): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val products = productService.findAll()
                return ResponseEntity(products, HttpStatus.OK)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/product/{id}")
    fun getProductById(
        @RequestHeader("Authorization") token: String,
        @PathVariable(value = "id") productId: Long
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val product = productService.findById(productId)
                if (product != null) {
                    return ResponseEntity(product, HttpStatus.OK)
                }
                return ResponseEntity(ResponseMessage(false, "Product not found"), HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.NOT_FOUND)
    }

    @PutMapping("/product/{id}")
    fun updateProductById(
        @RequestHeader("Authorization") token: String,
        @PathVariable(value = "id") productId: Long,
        @Valid @RequestBody newProduct: Product
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val checkProduct = productService.findById(productId)
                if (checkProduct != null) {
                    val product = checkProduct.copy(
                        price = newProduct.price,
                        caption = newProduct.caption,
                        description = newProduct.description,
                        stock = newProduct.stock,
                    )
                    productService.save(product)
                    return ResponseEntity(ResponseMessage(true, "Product updated successfully"), HttpStatus.OK)
                }
                return ResponseEntity(ResponseMessage(false, "Product not found"), HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("/product/{id}")
    fun removeProductById(
        @RequestHeader("Authorization") token: String,
        @PathVariable(value = "id") productId: Long
    ): ResponseEntity<Any> {
        val userId = tokenService.getUserIdFromToken(token)
        if (userId != null) {
            val checkUser = userService.findById(userId)
            if (checkUser != null) {
                val checkProduct = productService.findById(productId)
                if (checkProduct != null) {
                    productService.deleteById(productId)
                    return ResponseEntity(ResponseMessage(true, "Product deleted successfully"), HttpStatus.OK)
                }
                return ResponseEntity(ResponseMessage(false, "Product not found"), HttpStatus.BAD_REQUEST)
            }
            return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ResponseMessage(false, "User not found"), HttpStatus.NOT_FOUND)
    }
}
