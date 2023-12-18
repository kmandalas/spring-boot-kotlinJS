import auth.AuthApi
import kotlinx.browser.sessionStorage
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.get
import org.w3c.dom.set
import product.ProductsTab
import util.ModalUtil

@JsModule("navigo")
@JsNonModule
external class Navigo(root: String, resolveOptions: ResolveOptions = definedExternally) {
    fun on(route: String, handler: () -> Unit)
    fun resolve()
    fun navigate(s: String)
}

external interface ResolveOptions {
    var strategy: String? // ONE or ALL
    var hash: Boolean?
    var noMatchWarning: Boolean?
}

class Router(root: String = "/") {

    private val resolveOptions = object : ResolveOptions {
        override var strategy: String? = "ONE"
        override var hash: Boolean? = true
        override var noMatchWarning: Boolean? = true
    }

    private val navigo: Navigo = Navigo(root, resolveOptions)

    init {
        configureRoutes()
        navigo.resolve()
    }

    private fun configureRoutes() {

        navigo.on("/") {
            println("Navigated to /")
            navigo.navigate("/login")
        }

        navigo.on("/login") {
            println("Navigated to /login")
            window.location.href = "http://localhost:8090/login.html"
        }

        navigo.on("/homepage") {
            println("Navigated to /homepage")
            sessionStorage["userIn"] = "true"
            navigo.navigate("/products")
        }

        navigo.on("/products") {
            println("Navigated to /products")
            ProductsTab.init()
        }

        navigo.on("/orders") {
            println("Navigated to /orders")
            val userInFlag = sessionStorage["userIn"]
            println("User In Flag: $userInFlag")
            ModalUtil.alert("Under construction")
        }

        navigo.on("/logout") {
            println("Navigated to /logout")
            MainScope().launch {
                AuthApi.logout()
            }
        }
    }

    fun start() {
        // Start listening for changes
        navigo.resolve()
    }
}