package product

import kotlinx.browser.document
import model.Product
import org.w3c.dom.HTMLTableElement

object ProductTable {

    private const val PRODUCTS_TABLE_ID: String = "productsTable"

    private const val DATATABLE_OPTIONS: String = """
        {
            paging: true,
            lengthChange: true,
            searching: true,
            ordering: true,
            info: true,
            autoWidth: true,
            dom: 'Bfrtip',
            buttons: ['copy', 'excel', 'pdf', {
                text: 'Add product',
                attr:  {
                    id: 'addProductBtn'
                }
            }]
        }
    """

    @Deprecated("simple approach - no loading mask, no DataTables")
    suspend fun init() {
        val products = ProductApi.getProducts()
        val root = document.getElementById("root")!!
        root.appendChild(buildTable(products))
    }

    suspend fun init(tabContentId: String) {
        try {
            val products = ProductApi.getProducts()

            // Render table
            val table = buildTable(products)
            table.id = PRODUCTS_TABLE_ID // Set an id for DataTable initialization

            // Append the table to the specific tab content
            val tabContent = document.getElementById(tabContentId) ?: throw IllegalStateException("Tab content not found")
            tabContent.innerHTML = ""
            tabContent.appendChild(table)

            initializeDataTable()
        } catch (e: Throwable) {
            console.error("An error occurred while initializing the table:", e)
        }
    }

    private fun buildTable(products: List<Product>): HTMLTableElement {
        val table = document.createElement("table") as HTMLTableElement
        table.className = "table table-striped table-hover"

        // Header
        val thead = table.createTHead()
        val headerRow = thead.insertRow()
        headerRow.appendChild(document.createElement("th").apply { textContent = "ID" })
        headerRow.appendChild(document.createElement("th").apply { textContent = "Name" })
        headerRow.appendChild(document.createElement("th").apply { textContent = "Category" })
        headerRow.appendChild(document.createElement("th").apply { textContent = "Price" })

        // Body
        val tbody = table.createTBody()
        for (product in products) {
            val row = tbody.insertRow()
            row.appendChild(document.createElement("td").apply { textContent = product.id.toString() })
            row.appendChild(document.createElement("td").apply { textContent = product.name })
            row.appendChild(document.createElement("td").apply { textContent = product.category.name })
            row.appendChild(document.createElement("td").apply { textContent = product.price.toString() })
        }

        document.getElementById("root")?.appendChild(table)
        return table
    }

    private fun initializeDataTable() {
        js("new DataTable('#$PRODUCTS_TABLE_ID', $DATATABLE_OPTIONS)")
    }

}