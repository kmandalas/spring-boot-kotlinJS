package product

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object ProductsTab {

    const val TAB_CONTENT_ID: String = "productsTabContent"
    const val CATEGORIES_DIV_ID: String = "catDiv"
    const val SUB_CATEGORIES_DIV_ID: String = "subCatDiv"

    private val mainScope = MainScope()

    fun init() {
        mainScope.launch {
            ProductTable.init(TAB_CONTENT_ID)
            ProductForm.init()
        }
    }

}