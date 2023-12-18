package util

import io.ktor.client.fetch.*
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.random.Random

object ModalUtil {

    enum class Severity {
        INFO, ERROR, WARNING
    }

    fun alert(message: String) {
        window.alert(message)
    }

    fun showMessage(message: String, severity: Severity, callback: (() -> Unit)? = null) {
        val modal = document.createElement("div") as HTMLDivElement
        modal.className = "modal fade"
        modal.id = "messageModal_${Random.nextInt(100)}"

        val modalDialog = document.createElement("div") as HTMLDivElement
        modalDialog.className = "modal-dialog"

        val modalContent = document.createElement("div") as HTMLDivElement
        modalContent.className = "modal-content"

        // Modal header
        val modalHeader = document.createElement("div") as HTMLDivElement
        modalHeader.className = "modal-header"

        // Set the modal title and colored icon based on severity
        when (severity) {
            Severity.INFO, Severity.ERROR, Severity.WARNING -> {
                val severityIcon = when (severity) {
                    Severity.ERROR -> "x-circle"
                    Severity.WARNING -> "exclamation-triangle"
                    else -> "info-circle"
                }
                val severityClass = when (severity) {
                    Severity.ERROR -> "text-danger"
                    Severity.WARNING -> "text-warning"
                    else -> "text-primary"
                }
                modalHeader.innerHTML = """
                    <div class="d-flex align-items-center">
                        <i class="bi bi-${severityIcon} $severityClass me-2"></i>
                        <h5 class="modal-title">${severity.name}</h5>
                        <button type="button" class="btn-close position-absolute top-0 end-0 m-2" 
                            data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                """
            }
        }

        // Modal body
        val modalBody = document.createElement("div") as HTMLDivElement
        modalBody.className = "modal-body"
        modalBody.textContent = message

        // Modal footer
        val modalFooter = document.createElement("div") as HTMLDivElement
        modalFooter.className = "modal-footer"
        modalFooter.innerHTML = """
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
        """

        modalContent.appendChild(modalHeader)
        modalContent.appendChild(modalBody)
        modalContent.appendChild(modalFooter)

        modalDialog.appendChild(modalContent)
        modal.appendChild(modalDialog)

        // Add an event listener for the 'hidden.bs.modal' event
        modal.addEventListener("hidden.bs.modal", {
            // Check if a callback is provided and execute it
            callback?.invoke()

            // Remove the modal element from the DOM after it's hidden
            document.body?.removeChild(modal)
        })
        document.body?.appendChild(modal)

        val modalElement = document.getElementById(modal.id) as? HTMLDivElement
        modalElement?.let {
            js("new bootstrap.Modal(it).show()")
        }
    }

    fun showForm(formIdentifier: String, title: String, setupForm: () -> Unit, callback: (() -> Unit)? = null) {
        val modal = document.createElement("div") as HTMLDivElement
        modal.className = "modal fade"
        modal.id = formIdentifier + "Modal"

        val modalDialog = document.createElement("div") as HTMLDivElement
        modalDialog.className = "modal-dialog modal-dialog-centered"

        val modalContent = document.createElement("div") as HTMLDivElement
        modalContent.className = "modal-content"

        // Modal header
        val modalHeader = document.createElement("div") as HTMLDivElement
        modalHeader.className = "modal-header"

        modalHeader.innerHTML = """
            <div class="d-flex align-items-center">
                <h5 class="modal-title">$title</h5>
                <button type="button" class="btn-close position-absolute top-0 end-0 m-2" 
                    data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
        """

        // Modal body
        val modalBody = document.createElement("div") as HTMLDivElement
        modalBody.className = "modal-body"
        modalBody.id = formIdentifier + "ModalBody"

        // Load form into the modal body
        loadAndInjectHtml("$formIdentifier.html", modalBody.id)
        setupForm()

        // Modal footer
        val modalFooter = document.createElement("div") as HTMLDivElement
        modalFooter.className = "modal-footer"
        val submitButtonId = formIdentifier + "SubmitBtn"
        modalFooter.innerHTML = """
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            <button type="button" class="btn btn-primary" id="$submitButtonId">Submit</button>
        """
        modalContent.appendChild(modalHeader)
        modalContent.appendChild(modalBody)
        modalContent.appendChild(modalFooter)

        modalDialog.appendChild(modalContent)
        modal.appendChild(modalDialog)

        // Add an event listener for the 'hidden.bs.modal' event
        modal.addEventListener("hidden.bs.modal", {
            // Check if a callback is provided and execute it
            callback?.invoke()

            // Remove the modal element from the DOM after it's hidden
            document.body?.removeChild(modal)
        })
        document.body?.appendChild(modal)

        val modalElement = document.getElementById(modal.id) as? HTMLDivElement
        modalElement?.let {
            js("new bootstrap.Modal(it).show()")
        }
    }

    fun close(formIdentifier: String) {
        val modalElement = document.getElementById(formIdentifier + "Modal") as? HTMLElement
        modalElement?.let {
            js("$(it).modal('hide')")
        }
    }

    private fun loadAndInjectHtml(url: String, targetId: String) {
        fetch(url)
            .then { response ->
                if (!response.ok) {
                    throw Exception("Failed to load HTML")
                }
                return@then response.text()
            }
            .then { htmlContent ->
                val container = document.createElement("div")
                container.innerHTML = htmlContent

                // Append child nodes of the container to the target element
                val targetElement = document.getElementById(targetId)
                targetElement?.appendChild(container.childNodes.item(0)!!)
            }
            .catch { error ->
                console.error("Error loading HTML:", error)
            }
    }

}
