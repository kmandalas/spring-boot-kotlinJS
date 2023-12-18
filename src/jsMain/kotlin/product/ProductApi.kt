package product

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import model.Category
import model.Product
import util.ModalUtil
import util.ModalUtil.showMessage
import util.Spinner.startSpinner
import util.Spinner.stopSpinner

object ProductApi {

    private const val PRODUCTS_URL: String = "http://localhost:8090/api/products"
    private const val CATEGORIES_URL: String = "http://localhost:8090/api/categories"
    private const val SUB_CATEGORIES_URL: String = "http://localhost:8090/api/subcategories"

    private const val GENERIC_ERROR_MESSAGE: String = "An error occurred. Please try again."

    private val client = HttpClient(Js) {
        install(ContentNegotiation) {
            json(Json)
        }
        install(HttpCookies) {
        }
        install(HttpCache)
    }

    // Temporary hack, see: https://youtrack.jetbrains.com/issue/KTOR-539/Ability-to-use-browser-cookie-storage
    init {
        js("""
                window.originalFetch = window.fetch;
                window.fetch = function (resource, init) {
                    init = Object.assign({}, init);
                    init.credentials = init.credentials !== undefined ? init.credentials : 'include';
                    return window.originalFetch(resource, init);
                };
            """)
    }

    suspend fun getProducts(): List<Product> {
        // Show loading indicator
        val spinnerInstance = startSpinner(ProductsTab.TAB_CONTENT_ID)
        try {
            val response: HttpResponse = client.get(PRODUCTS_URL) {
                contentType(ContentType.Application.Json)
            }

            if (response.status.isSuccess()) {
                // Successful response, return the body
                return response.body()
            } else {
                // Non-200 response, show an error message
                showMessage("Error: ${response.status.value}, while fetching products.",
                    ModalUtil.Severity.ERROR)
            }
        } catch (e: HttpRequestTimeoutException) {
            // Timeout exception, show an error message
            showMessage("Request timeout while fetching products.", ModalUtil.Severity.ERROR)
        } catch (e: Exception) {
            // Other exceptions, show a generic error message
            showMessage(GENERIC_ERROR_MESSAGE, ModalUtil.Severity.ERROR)
        } finally {
            stopSpinner(spinnerInstance)
        }

        // Return an empty list in case of errors
        return emptyList()
    }

    suspend fun addProduct(product : Product) {
        try {
            val response: HttpResponse = client.post(PRODUCTS_URL) {
                contentType(ContentType.Application.Json)
                setBody(product)
            }

            if (response.status.isSuccess()) {
                showMessage("Product added successfully.", ModalUtil.Severity.INFO) {
                   ProductsTab.init()
                }
            } else {
                showMessage("Error: ${response.status.value}, while adding product.",
                    ModalUtil.Severity.ERROR)
            }
        } catch (e: HttpRequestTimeoutException) {
            showMessage("Request timeout while adding product.", ModalUtil.Severity.ERROR)
        } catch (e: Exception) {
            showMessage(GENERIC_ERROR_MESSAGE, ModalUtil.Severity.ERROR)
        }
    }

    suspend fun getCategories(): List<Category> {
        val spinnerInstance = startSpinner(ProductsTab.CATEGORIES_DIV_ID)
        try {
            val response: HttpResponse = client.get(CATEGORIES_URL) {
                contentType(ContentType.Application.Json)
            }

            if (response.status.isSuccess()) {
                return response.body()
            } else {
                showMessage("Error: ${response.status.value}, while fetching categories.",
                    ModalUtil.Severity.ERROR)
            }
        } catch (e: HttpRequestTimeoutException) {
            showMessage("Request timeout while fetching categories.", ModalUtil.Severity.ERROR)
        } catch (e: Exception) {
            showMessage(GENERIC_ERROR_MESSAGE, ModalUtil.Severity.ERROR)
        } finally {
            stopSpinner(spinnerInstance)
        }

        return emptyList()
    }

    suspend fun getSubcategories(category: String): List<String> {
        val spinnerInstance = startSpinner(ProductsTab.SUB_CATEGORIES_DIV_ID)
        try {
            val response: HttpResponse = client.get(SUB_CATEGORIES_URL) {
                contentType(ContentType.Application.Json)
            }

            if (response.status.isSuccess()) {
                return response.body()
            } else {
                showMessage("Error: ${response.status.value}, while fetching sub-categories.",
                    ModalUtil.Severity.ERROR)
            }
        } catch (e: HttpRequestTimeoutException) {
            showMessage("Request timeout while fetching sub-categories.", ModalUtil.Severity.ERROR)
        } catch (e: Exception) {
            showMessage(GENERIC_ERROR_MESSAGE, ModalUtil.Severity.ERROR)
        } finally {
            stopSpinner(spinnerInstance)
        }

        return emptyList()
    }

}