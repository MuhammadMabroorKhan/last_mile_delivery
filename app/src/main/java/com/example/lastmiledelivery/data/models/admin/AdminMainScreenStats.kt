package com.example.lastmiledelivery.data.models.admin

data class AdminStatsResponse(
    val total_users: Int,
    val users_by_role: UsersByRole,
    val total_orders: Int,
    val orders_by_status: OrdersByStatus,
    val total_shops: Int,
    val total_branches: Int,
    val branches_by_approval: BranchesByApproval
)

data class UsersByRole(
    val customer: Int,
    val vendor: Int,
    val organization: Int,
    val deliveryboy: Int,
    val admin: Int
)

data class OrdersByStatus(
    val cancelled: Int,
    val pending: Int,
    val confirmed: Int
)

data class BranchesByApproval(
    val pending: Int,
    val approved: Int,
    val rejected: Int
)
