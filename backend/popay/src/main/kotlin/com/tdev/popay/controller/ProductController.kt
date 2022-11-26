package com.tdev.popay.controller

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

    @GetMapping("/products")
    fun getAllProducts(): List<Product> =
            productRepository.findAll()

    @PostMapping("/product")
    fun createProduct(@Valid @RequestBody product: Product): Product =
            productRepository.save(product)

    @GetMapping("/product/{id}")
    fun getProductById(@PathVariable(value = "id") productId: Long): ResponseEntity<Product> {
        return productRepository.findById(productId).map { product ->
            ResponseEntity.ok(product)
        }.orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/product/{id}")
    fun updateProductById(@PathVariable(value = "id") productId: Long,
                          @Valid @RequestBody newProduct: Product): ResponseEntity<Product> {
        return productRepository.findById(productId).map { existingProduct ->
            val updatedProduct: Product = existingProduct
                    .copy(
                        price = newProduct.price,
                        caption = newProduct.caption,
                        description = newProduct.description,
                        quantity = newProduct.quantity
                    )
            ResponseEntity.ok().body(productRepository.save(updatedProduct))
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/product/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeProductById(@PathVariable(value = "id") productId: Long): ResponseEntity<Void> {
        return productRepository.findById(productId).map { product ->
            productRepository.delete(product)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())
    }
}
