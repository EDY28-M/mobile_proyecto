package com.example.avisonline.data.model

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    @SerializedName("product_tranding_new")
    val newProducts: ProductResponse,
    @SerializedName("categories_randoms")
    val categories: List<Category>
)

data class ProductResponse(
    val data: List<Product>
)

data class Product(
    val id: Int,
    val title: String,
    val resumen: String,
    val imagen: String,
    @SerializedName("price_pen")
    val price: Double,
)

data class Category(
    val id: Int,
    val name: String,
    val imagen: String,
    @SerializedName("products_count")
    val productCount: Int
)

data class CreateProductResponse(
    val message: Int,
    @SerializedName("product_id")
    val productId: Int?,
    @SerializedName("message_text")
    val messageText: String?
)