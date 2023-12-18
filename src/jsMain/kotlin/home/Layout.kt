package home

import kotlinx.browser.document
import kotlinx.browser.localStorage
import org.w3c.dom.*

object Layout {

    private const val TABS_CONTAINER_ID: String = "myTabs"
    private const val HTML_THEME_ATTR = "data-bs-theme"

    fun init() {
        val tabsContainer = document.createElement("ul") as HTMLElement
        tabsContainer.className = "nav nav-tabs"
        tabsContainer.id = TABS_CONTAINER_ID

        // Products tab
        val productsTab = createTab("Products", "/products", true)
        tabsContainer.appendChild(productsTab)

        // Orders tab
        val ordersTab = createTab("Orders", "/orders")
        tabsContainer.appendChild(ordersTab)

        // Append the tabs to the root element
        val root = document.getElementById("root")
        root?.appendChild(tabsContainer)

        // Apply theme and handle change
        handleTheme()
    }

    private fun createTab(label: String, route: String, active: Boolean = false): HTMLElement {
        val tab = document.createElement("li") as HTMLElement
        tab.className = "nav-item"

        val link = document.createElement("a") as HTMLElement
        link.className = "nav-link" + if (active) " active" else ""
        link.textContent = label
        (link as HTMLAnchorElement).href = route
        link.setAttribute("data-navigo", "")

        // Add click event listener to update active attribute
        link.addEventListener("click", {
            // Remove 'active' class from all tabs
            val navLinks = document.querySelectorAll(".nav-link")
            navLinks.asList().forEach { navLink ->
                (navLink as? HTMLElement)?.classList?.remove("active")
            }
            // Add 'active' class to the clicked tab
            link.classList.add("active")
        })

        tab.appendChild(link)

        // Create a container div for the tab content
        val tabContent = document.createElement("div") as HTMLElement
        tabContent.id = "${route.substring(1)}TabContent" // Create a unique ID for each tab's content
        tabContent.className = "tab-content"
        tabContent.style.paddingBottom = "20px"

        // Append the container to the document body or any desired container
        document.body?.appendChild(tabContent)

        return tab
    }

    private fun handleTheme() {
        localStorage["userTheme"]?.let { document.body?.setAttribute(HTML_THEME_ATTR, it) }
        val themeToggleButton = document.getElementById("themeToggleButton") as? HTMLButtonElement
        themeToggleButton?.onclick = { toggleTheme() }
    }

    private fun toggleTheme() {
        val body = document.body
        val currentTheme = body?.getAttribute(HTML_THEME_ATTR)
        val themeIcon = document.getElementById("themeIcon") as? HTMLElement

        fun setThemeAttributes(theme: String, iconClass: String) {
            body?.setAttribute(HTML_THEME_ATTR, theme)
            themeIcon?.classList?.apply {
                remove("fa-sun", "fa-moon")
                add(iconClass)
            }
            localStorage["userTheme"] = theme
        }

        if (currentTheme == "dark") {
            setThemeAttributes("light", "fa-moon")
        } else {
            setThemeAttributes("dark", "fa-sun")
        }
    }

}