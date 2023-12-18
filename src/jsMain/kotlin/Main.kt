import home.Layout
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun main() {

    // Use the MainScope to launch coroutines
    MainScope().launch {
        window.onload = {
            Layout.init()
            val router = Router()
            router.start()
        }
    }

}



