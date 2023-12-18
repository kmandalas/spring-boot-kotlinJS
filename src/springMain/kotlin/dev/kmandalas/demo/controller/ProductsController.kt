package dev.kmandalas.demo.controller

import io.github.serpro69.kfaker.Faker
import model.Category
import model.Product
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.TimeUnit

@RestController
@CrossOrigin
@RequestMapping("/api")
class ProductsController {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val faker = Faker()

    @Value("\${response.delay}")
    private val delay: Long = 0

    @GetMapping("/products")
    fun getProducts(@RequestParam(defaultValue = "20") numberOfItems: Int): List<Product> {
        TimeUnit.MILLISECONDS.sleep(delay)
        return (1..numberOfItems).map {
            Product(
                it.toLong(),
                faker.commerce.productName(),
                Category(it.toLong(), faker.commerce.department()),
                faker.money.amount(IntRange(100, 999)).replace("$", "").toDouble()
            )
        }
    }

    @PostMapping("/products")
    fun addProduct(@RequestBody product: Product) {
        log.info("Adding new product: {}", product)
    }

    @GetMapping("/categories")
    fun getCategories(@RequestParam(defaultValue = "10") numberOfItems: Int): List<Category> {
        TimeUnit.MILLISECONDS.sleep(delay)
        return (1..numberOfItems).map {
            Category(it.toLong(), faker.commerce.department())
        }
    }

    @GetMapping("/subcategories")
    fun getSubCategories(@RequestParam(defaultValue = "5") numberOfItems: Int): ResponseEntity<List<String>> {
        val cacheControl = CacheControl.maxAge(60, TimeUnit.SECONDS)
            .noTransform()
            .mustRevalidate()
        TimeUnit.MILLISECONDS.sleep(delay)
        val subs = (1..numberOfItems).map { _ -> faker.commerce.department() }
        return ResponseEntity.ok()
            .cacheControl(cacheControl)
            .body(subs)
    }

}