package com.tdev.popay.controller

import com.tdev.popay.dto.ResponseMessage
import com.tdev.popay.model.Product
import com.tdev.popay.repository.ProductRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class ProductController(private val productRepository: ProductRepository) {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult.fieldErrors.forEach { error -> errors[error.field] = error.defaultMessage!! }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/product")
    fun createProduct(@Valid @RequestBody product: Product): ResponseEntity<Any> {
        val newProduct = Product(
            price = product.price,
            caption = product.caption,
            description = product.description,
            quantity = product.quantity,
        )
        productRepository.save(newProduct)
        return ResponseEntity(ResponseMessage(true, "Product created successfully"), HttpStatus.CREATED)
    }

    @GetMapping("/products")
    fun getAllProducts(): List<Product> =
        productRepository.findAll()

    @GetMapping("/product/{id}")
    fun getProductById(@PathVariable(value = "id") productId: Long): ResponseEntity<Any> {
        val checkProduct = productRepository.findById(productId)
        if (checkProduct.isPresent) {
            return ResponseEntity(checkProduct.get(), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "Product not found"), HttpStatus.NOT_FOUND)
    }

    @PutMapping("/product/{id}")
    fun updateProductById(@PathVariable(value = "id") productId: Long,
                          @Valid @RequestBody newProduct: Product): ResponseEntity<Any> {
        val checkProduct = productRepository.findById(productId)
        if (checkProduct.isPresent) {
            val product = checkProduct.get().copy(
                price = newProduct.price,
                caption = newProduct.caption,
                description = newProduct.description,
                quantity = newProduct.quantity,
            )
            productRepository.save(product)
            return ResponseEntity(ResponseMessage(true, "Product updated successfully"), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "Product not found"), HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("/product/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeProductById(@PathVariable(value = "id") productId: Long): ResponseEntity<Any> {
        val checkProduct = productRepository.findById(productId)
        if (checkProduct.isPresent) {
            productRepository.deleteById(productId)
            return ResponseEntity(ResponseMessage(true, "Product deleted successfully"), HttpStatus.OK)
        }
        return ResponseEntity(ResponseMessage(false, "Product not found"), HttpStatus.NOT_FOUND)
    }
}
