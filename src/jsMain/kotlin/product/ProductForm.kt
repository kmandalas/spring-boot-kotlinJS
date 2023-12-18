package product

import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import model.Category
import model.Product
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import product.ProductApi.getCategories
import util.ModalUtil
import util.ModalUtil.showMessage

object ProductForm {

    private const val ADD_PRODUCT_BUTTON: String = "addProductBtn"
    private const val PRODUCTS_FORM_ID: String = "productForm"
    private const val PRODUCTS_FORM_TITLE: String = "Add a product"
    private const val CATEGORY_DROPDOWN: String = "categoryDropdown"
    private const val SUB_CATEGORY_DROPDOWN: String = "subCategoryDropdown"

    private val mainScope = MainScope()

    fun init() {
        val addProductBtn = document.getElementById(ADD_PRODUCT_BUTTON) as? HTMLButtonElement
        addProductBtn?.onclick = {
            ModalUtil.showForm(PRODUCTS_FORM_ID, PRODUCTS_FORM_TITLE, ::setupForm)
        }
    }

    private fun setupForm() {
        // Other form setup code...
        mainScope.launch {
            handleDropdowns()
            handleFormSubmit()
        }
    }

    private suspend fun handleDropdowns() {
        val categories = getCategories()
        val categoryDropdown = document.getElementById(CATEGORY_DROPDOWN) as? HTMLSelectElement

        categories.forEach { category ->
            val option = document.createElement("option") as HTMLOptionElement
            option.text = category.name
            option.value = category.id.toString()
            categoryDropdown?.add(option)
        }

        categoryDropdown?.addEventListener("change", {
            val selectedCategory = categoryDropdown.value
            // Fetch sub-categories based on the selected category
            mainScope.launch {
                populateSubCategories(selectedCategory)
            }
        })
    }

    private fun handleFormSubmit() {
        val submitButton = document.getElementById(PRODUCTS_FORM_ID + "SubmitBtn") as? HTMLButtonElement
        submitButton?.onclick = {
            mainScope.launch {
                submitProduct()
            }
        }
    }

    private suspend fun populateSubCategories(category: String) {
        val subCategories = ProductApi.getSubcategories(category)
        // Populate the sub-category dropdown with the fetched sub-categories
        val subCategoryDropdown = document.getElementById(SUB_CATEGORY_DROPDOWN) as? HTMLSelectElement
        subCategoryDropdown?.apply {
            clearOptions()
            subCategories.forEach { subcat ->
                val option = document.createElement("option") as HTMLOptionElement
                option.text = subcat
                subCategoryDropdown.add(option)
            }
        }
    }

     private suspend fun submitProduct() {
        val descriptionInput = document.getElementById("description") as HTMLInputElement
        val priceInput = document.getElementById("price") as HTMLInputElement
        val categoryChoice = document.getElementById(CATEGORY_DROPDOWN) as HTMLSelectElement

        val description = descriptionInput.value
        val price = priceInput.value.toDoubleOrNull()
        val categoryId = categoryChoice.value

        if (description.isNotBlank() && price != null) { // add more checks here...
            // Perform submission logic, e.g., make an API call
            println("Product submitted: Description=$description, CategoryId=$categoryId, Price=$price")
            val product = Product(0, description, Category(categoryId.toLong(), ""), price)
            ProductApi.addProduct(product)
            ModalUtil.close(PRODUCTS_FORM_ID)
        } else {
            showMessage("Invalid input. Please fill in all fields.", ModalUtil.Severity.WARNING)
        }
    }

    private fun HTMLSelectElement.clearOptions() {
        while (options.length > 0) {
            remove(0)
        }
    }


}