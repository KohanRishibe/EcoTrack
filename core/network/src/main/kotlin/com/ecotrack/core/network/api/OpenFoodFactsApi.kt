package com.ecotrack.core.network.api

import com.ecotrack.core.network.dto.ProductDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class OpenFoodFactsApi(private val client: HttpClient) {

    suspend fun lookupBarcode(barcode: String): ProductDto? {
        return runCatching {
            val response: OffResponse = client.get(
                "https://world.openfoodfacts.org/api/v2/product/$barcode.json",
            ).body()
            response.product?.let {
                ProductDto(
                    barcode = barcode,
                    name = it.productName ?: return null,
                    category = it.categories?.split(",")?.firstOrNull()?.trim(),
                    imageUrl = it.imageUrl,
                )
            }
        }.getOrNull()
    }

    @Serializable
    private data class OffResponse(
        val product: OffProduct? = null,
        val status: Int = 0,
    )

    @Serializable
    private data class OffProduct(
        @SerialName("product_name") val productName: String? = null,
        val categories: String? = null,
        @SerialName("image_url") val imageUrl: String? = null,
    )
}
