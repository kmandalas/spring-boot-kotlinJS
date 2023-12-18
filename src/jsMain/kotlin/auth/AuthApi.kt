package auth

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import util.ModalUtil
import util.ModalUtil.showMessage

object AuthApi {

    private const val LOGOUT_URL: String = "http://localhost:8090/api/logout"

    private val client = HttpClient(Js) {
        install(ContentNegotiation) {
            json(Json)
        }
        install(HttpCookies) {
        }
    }

    suspend fun logout() {
        try {
            val response: HttpResponse = client.post(LOGOUT_URL)
            if (response.status.isSuccess()) {
                window.location.href="/"
            } else {
                showMessage("Error: ${response.status.value}, during logout.",
                    ModalUtil.Severity.ERROR)
            }
        } catch (e: HttpRequestTimeoutException) {
            // Timeout exception, show an error message
            showMessage("Logout Request timeout. Please try again.", ModalUtil.Severity.ERROR)
        } catch (e: Exception) {
            // Other exceptions, show a generic error message
            showMessage("An error occurred. Please try again.", ModalUtil.Severity.ERROR)
        }
    }

}