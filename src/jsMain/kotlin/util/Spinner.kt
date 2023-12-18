package util

import kotlinx.browser.document

object Spinner {

    fun startSpinner(targetElementId: String): dynamic {
        val spinnerElement = document.getElementById(targetElementId)
        return spinnerElement?.let {
            js("new Spin.Spinner({color: '#cbc3c3'}).spin(it)")
        }
    }

    fun stopSpinner(spinner: dynamic) {
        spinner?.stop()
    }

}