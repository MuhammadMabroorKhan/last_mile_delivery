package com.example.lastmiledelivery.data.models.customer

data class StockItemRequest(
    val vendor_ID: Int,
    val shop_ID: Int,
    val branch_ID: Int,
    val item_detail_ID: Int
)

data class StockItemResponse(
    val vendor_ID: Int,
    val shop_ID: Int,
    val branch_ID: Int,
    val item_detail_ID: Int,
    val stock_qty: Int?,
    val test_stock_qty: Int?,
    val source: String,
    val error: String?
)
data class StockResponseWrapper(
    val status: Boolean,
    val message: String,
    val data: List<StockItemResponse>
)
data class StockItemRequestWrapper(
    val items: List<StockItemRequest>
)
