package model

import kotlinx.serialization.Serializable

@Serializable
data class Product(val id: Long, val name: String, val category: Category, val price: Double)