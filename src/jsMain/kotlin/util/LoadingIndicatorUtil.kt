package util

import kotlinx.browser.document
import org.w3c.dom.HTMLElement

object LoadingIndicatorUtil {

    fun showLoadingIndicator() {
        val loadingIndicator = document.getElementById("loadingIndicator") as? HTMLElement
        loadingIndicator?.style?.display = "flex"
    }

    fun hideLoadingIndicator() {
        val loadingIndicator = document.getElementById("loadingIndicator") as? HTMLElement
        loadingIndicator?.style?.display = "none"
    }

}
