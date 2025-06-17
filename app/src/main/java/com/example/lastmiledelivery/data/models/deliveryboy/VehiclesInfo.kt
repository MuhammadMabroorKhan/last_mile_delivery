package com.example.lastmiledelivery.data.models.deliveryboy

// --- 1. Data Models ---
data class Vehicle(
    val vehicle_id: Int,
    val plate_no: String,
    val color: String?,
    val model: String?,
    val category_id: Int,
    val category_name: String,
    val per_km_charge: String,
    val category_description: String?
)

data class VehicleCategory(
    val id: Int,
    val name: String,
    val per_km_charge: String,
    val description: String?
)

data class VehicleRequest(
    val plate_no: String,
    val color: String?,
    val model: String?,
    val vehicle_type: Int // ID of vehicle category
)


//////
// --- 3. Response Wrappers ---
data class VehicleResponse(
    val status: Boolean,
    val message: String,
    val data: VehicleData?
)

data class VehicleData(
    val delivery_boy_id: Int,
    val vehicles: List<Vehicle>
)

data class VehicleCategoryResponse(
    val status: Boolean,
    val message: String,
    val data: List<VehicleCategory>
)

data class BasicResponse(
    val message: String,
    val vehicle: Vehicle?
)