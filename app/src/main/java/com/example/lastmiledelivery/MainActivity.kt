package com.example.lastmiledelivery

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lastmiledelivery.data.models.deliveryboy.AssignedSuborder
import com.example.lastmiledelivery.ui.admin.AddIntegrationScreen
import com.example.lastmiledelivery.ui.admin.AdminMainScreen
import com.example.lastmiledelivery.ui.admin.ApiVendorWebsiteMappingInfo
import com.example.lastmiledelivery.ui.admin.ApiVendorsConnectionScreen
import com.example.lastmiledelivery.ui.admin.BranchDetailScreen
import com.example.lastmiledelivery.ui.admin.IntegrationDetailScreen
import com.example.lastmiledelivery.ui.admin.MappingScreen
import com.example.lastmiledelivery.ui.admin.PendingBranchesScreen
import com.example.lastmiledelivery.ui.admin.VendorApprovalScreen
import com.example.lastmiledelivery.ui.admin.VendorDetailScreen
import com.example.lastmiledelivery.ui.common.LoginScreen
import com.example.lastmiledelivery.ui.common.MapPickerScreen
import com.example.lastmiledelivery.ui.common.RoleSelectionScreen
import com.example.lastmiledelivery.ui.common.SplashScreen
import com.example.lastmiledelivery.ui.customer.AddAddressScreen
import com.example.lastmiledelivery.ui.customer.CartScreen
import com.example.lastmiledelivery.ui.customer.CustomerMainScreen
import com.example.lastmiledelivery.ui.customer.CustomerOrders
import com.example.lastmiledelivery.ui.customer.CustomerProfileScreen
import com.example.lastmiledelivery.ui.customer.CustomerSignupScreen
import com.example.lastmiledelivery.ui.customer.EditPersonalInfoScreen
import com.example.lastmiledelivery.ui.customer.OrderConfirmationScreen
import com.example.lastmiledelivery.ui.customer.OrderDetailScreen
import com.example.lastmiledelivery.ui.customer.OrderRatingScreen
import com.example.lastmiledelivery.ui.customer.ShopDetailsScreen
import com.example.lastmiledelivery.ui.customer.TrackOrderScreen
import com.example.lastmiledelivery.ui.deliveryboy.AssignedOrdersScreen
import com.example.lastmiledelivery.ui.deliveryboy.DeliveryBoyMainScreen
import com.example.lastmiledelivery.ui.deliveryboy.DeliveryBoyProfileScreen
import com.example.lastmiledelivery.ui.deliveryboy.ReadySubordersScreen
import com.example.lastmiledelivery.ui.deliveryboy.ReadySubordersScreenFromMarker
import com.example.lastmiledelivery.ui.deliveryboy.SuborderTrackingDeliveryBoyScreen
import com.example.lastmiledelivery.ui.deliveryboy.VehicleScreen
import com.example.lastmiledelivery.ui.organization.DeliveryBoyListScreen
import com.example.lastmiledelivery.ui.organization.OrganizationDeliveryBoySignupScreen
import com.example.lastmiledelivery.ui.organization.OrganizationMainScreen
import com.example.lastmiledelivery.ui.organization.OrganizationSignup
import com.example.lastmiledelivery.ui.organization.VendorConnectionRequest
import com.example.lastmiledelivery.ui.organization.VendorRequestDetailScreen
import com.example.lastmiledelivery.ui.vendor.API_Vendor.API_VendorItemsScreen
import com.example.lastmiledelivery.ui.vendor.IN_APP.IN_APP_VendorItemsScreen
import com.example.lastmiledelivery.ui.vendor.SuborderDetailsScreen
import com.example.lastmiledelivery.ui.vendor.VendorBranchOrdersScreen
import com.example.lastmiledelivery.ui.vendor.VendorBranchesScreen
//import com.example.lastmiledelivery.ui.vendor.VendorMainScreen
import com.example.lastmiledelivery.ui.vendor.VendorMainScreenWrapper
import com.example.lastmiledelivery.ui.vendor.VendorOrdersScreen
import com.example.lastmiledelivery.ui.vendor.VendorOrganizationsConnection
import com.example.lastmiledelivery.ui.vendor.VendorProfileScreen
import com.example.lastmiledelivery.ui.vendor.VendorShopsScreen
import com.example.lastmiledelivery.ui.vendor.VendorSignupScreen
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.admin.VendorApprovalViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel() // Single instance for the app
    val context = LocalContext.current

    NavHost(navController, startDestination = "splash") {
        //Login Route and Main Screens
        composable("login") { LoginScreen(navController) }
        composable("splash") { SplashScreen(navController) }
        composable("admin") { AdminMainScreen(navController = navController) }
        composable("customer") { CustomerMainScreen(navController) }
        composable("deliveryboy") { DeliveryBoyMainScreen(navController = navController) }
        composable("vendor") { VendorMainScreenWrapper(navController) }
        composable("Organization") { OrganizationMainScreen(navController) }

        //SignUP Routes
        composable("role_selection") { RoleSelectionScreen(navController) }
        composable("vendor_signup") { VendorSignupScreen(navController = navController) }
        composable("Organization_signup") { OrganizationSignup(navController = navController) }
//        composable(
//            "organization_deliveryBoys/{organizationId}",
//            arguments = listOf(
//                navArgument("organizationId") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val organizationId = backStackEntry.arguments?.getString("organizationId") ?: ""
//            OrganizationDeliveryBoySignupScreen(navController = navController, organizationId = organizationId)
//        }

        composable("deliveryboys/{orgId}") { backStackEntry ->
            val orgId = backStackEntry.arguments?.getString("orgId")?.toIntOrNull() ?: 0
            DeliveryBoyListScreen(orgId = orgId, navController = navController)
        }


        composable("vendorConnectionScreenForOrganization/{orgId}") { backStackEntry ->
            val orgId = backStackEntry.arguments?.getString("orgId")?.toIntOrNull() ?: 0
            VendorConnectionRequest(organizationId = orgId, navController = navController)
        }

        composable(
            route = "vendor_request_detail/{vendorName}/{vendorEmail}/{vendorPhone}/{orgUserName}/{approvalStatus}/{vendorProfilePicture}/{organizationID}/{requestID}/{vendorID}",
            arguments = listOf(
                navArgument("vendorName") { type = NavType.StringType },
                navArgument("vendorEmail") { type = NavType.StringType },
                navArgument("vendorPhone") { type = NavType.StringType },
                navArgument("orgUserName") { type = NavType.StringType },
                navArgument("approvalStatus") { type = NavType.StringType },
                navArgument("vendorProfilePicture") { type = NavType.StringType },
                navArgument("organizationID") { type = NavType.StringType },
                navArgument("requestID") { type = NavType.StringType },
                navArgument("vendorID") { type = NavType.StringType }
            )

        ) { backStackEntry ->
            val args = backStackEntry.arguments!!
            VendorRequestDetailScreen(
                vendorName = args.getString("vendorName") ?: "",
                vendorEmail = args.getString("vendorEmail") ?: "",
                vendorPhone = args.getString("vendorPhone") ?: "",
                orgUserName = args.getString("orgUserName") ?: "",
                approvalStatus = args.getString("approvalStatus") ?: "",
                vendorProfilePicture = args.getString("vendorProfilePicture") ?: "",
                organizationId = args.getString("organizationID") ?: "",
                requestID = args.getString("requestID") ?: "",
                vendorId = args.getString("vendorID") ?: "",
                navController = navController
            )
        }


        composable(
            "organization_deliveryBoys/{organizationId}",
            arguments = listOf(
                navArgument("organizationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val organizationId = backStackEntry.arguments?.getString("organizationId") ?: ""
            OrganizationDeliveryBoySignupScreen(
                navController = navController,
                organizationId = organizationId
            )
        }




        composable("customer_signup") { CustomerSignupScreen(navController = navController) }
        //Map Picker for Signup
        composable("map_picker") {
            MapPickerScreen(navController = navController)
        }

//Admin Functionality ROUTES
        composable("vendorApproval") { VendorApprovalScreen(navController) }

        //Website Connectivity
        composable("vendorWebsiteConnection") { ApiVendorsConnectionScreen(navController) }
        composable(
            "apiVendorWebsiteDetail/{vendorId}/{lmdUserId}",
            arguments = listOf(
                navArgument("vendorId") { type = NavType.IntType },
                navArgument("lmdUserId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val vendorId = backStackEntry.arguments?.getInt("vendorId") ?: 0
            val lmdUserId = backStackEntry.arguments?.getInt("lmdUserId") ?: 0
            VendorDetailScreen(navController, vendorId, lmdUserId)
        }
        composable(
            "apiVendorWebsiteMappingInfo/{vendorID}/{shopID}/{branchID}",
            arguments = listOf(
                navArgument("vendorID") { type = NavType.IntType },
                navArgument("shopID") { type = NavType.IntType },
                navArgument("branchID") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val vendorId = backStackEntry.arguments?.getInt("vendorID") ?: 0
            val shopID = backStackEntry.arguments?.getInt("shopID") ?: 0
            val branchID = backStackEntry.arguments?.getInt("branchID") ?: 0
            ApiVendorWebsiteMappingInfo(navController, vendorId, shopID, branchID)
        }

        composable(
            route = "add_integration/{vendorId}/{shopId}/{branchId}",
            arguments = listOf(
                navArgument("vendorId") { type = NavType.IntType },
                navArgument("shopId") { type = NavType.IntType },
                navArgument("branchId") { type = NavType.IntType },
            )
        ) { backStackEntry ->
            val vendorId = backStackEntry.arguments?.getInt("vendorId") ?: 0
            val shopId = backStackEntry.arguments?.getInt("shopId") ?: 0
            val branchId = backStackEntry.arguments?.getInt("branchId") ?: 0

            AddIntegrationScreen(
                navController = navController,
                vendorId = vendorId,
                shopID = shopId,
                branchID = branchId
            )
        }

        composable(
            "integration_detail_api_methods/{apiVendorId}/{branchId}",
            arguments = listOf(
                navArgument("apiVendorId") { type = NavType.IntType },
                navArgument("branchId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val apiVendorId = backStackEntry.arguments?.getInt("apiVendorId") ?: 0
            val branchId = backStackEntry.arguments?.getInt("branchId") ?: 0
            IntegrationDetailScreen(apiVendorId, branchId, navController)
        }

        composable("variable_mapping_screen/{branchId}/{vendorId}") { backStackEntry ->
            val branchId = backStackEntry.arguments?.getString("branchId")?.toIntOrNull() ?: 0
            val vendorId = backStackEntry.arguments?.getString("vendorId")?.toIntOrNull() ?: 0
            MappingScreen(branchId = branchId, vendorId = vendorId,navController=navController)
        }





        composable("vendorDetail") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("vendorApproval")
            }
            val viewModel: VendorApprovalViewModel = hiltViewModel(parentEntry)
            VendorDetailScreen(navController, viewModel)
        }
        composable("pendingBranches") {
            PendingBranchesScreen(navController)
        }
        composable(
            "branchDetail/{branchId}",
            arguments = listOf(navArgument("branchId") { type = NavType.IntType })
        ) { backStackEntry ->
            val branchId = backStackEntry.arguments?.getInt("branchId") ?: 0
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("pendingBranches")
            }
            val viewModel: VendorApprovalViewModel = hiltViewModel(parentEntry)
            BranchDetailScreen(branchId, viewModel, navController)
        }

        composable(
            "shopscreen/{vendorId}",
            arguments = listOf(navArgument("vendorId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vendorId = backStackEntry.arguments?.getInt("vendorId") ?: 0
            VendorShopsScreen(vendorId, navController = navController)
        }

        composable(
            "branches/{shopcategory_name}/{shopcategory_ID}/{shopId}/{shopName}/{shopDescription}",
            arguments = listOf(
                navArgument("shopcategory_name") { type = NavType.StringType },
                navArgument("shopcategory_ID") { type = NavType.IntType },
                navArgument("shopId") { type = NavType.IntType },
                navArgument("shopName") { type = NavType.StringType },
                navArgument("shopDescription") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val shopcategory_name =
                backStackEntry.arguments?.getString("shopcategory_name") ?: "No SHOPCATEGORY NAME"
            val shopcategory_ID = backStackEntry.arguments?.getInt("shopcategory_ID") ?: 0
            val shopId = backStackEntry.arguments?.getInt("shopId") ?: 0
            val shopName = backStackEntry.arguments?.getString("shopName") ?: "Unknown"
            val shopDescription =
                backStackEntry.arguments?.getString("shopDescription") ?: "No Description"

            VendorBranchesScreen(
                shopcategory_name,
                shopcategory_ID,
                shopId,
                shopName,
                shopDescription,
                navController
            )
        }

        composable(
            "vendororderscreen/{vendorId}",
            arguments = listOf(navArgument("vendorId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vendorId = backStackEntry.arguments?.getInt("vendorId") ?: 0
            VendorOrdersScreen(vendorId, navController = navController)
        }

        composable(
            "vendororderscreen/{vendorId}/{branchId}",
            arguments = listOf(navArgument("vendorId") { type = NavType.IntType },
                navArgument("branchId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val vendorId = backStackEntry.arguments?.getInt("vendorId") ?: 0
            val branchId = backStackEntry.arguments?.getInt("branchId") ?: 0
            VendorBranchOrdersScreen(vendorId, branchId , navController = navController)
        }

        composable(
            "vendor_suborder_details/{vendorId}/{shopId}/{branchId}/{suborderId}",
            arguments = listOf(
                navArgument("vendorId") { type = NavType.IntType },
                navArgument("shopId") { type = NavType.IntType },
                navArgument("branchId") { type = NavType.IntType },
                navArgument("suborderId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val vendorId = backStackEntry.arguments?.getInt("vendorId") ?: 0
            val shopId = backStackEntry.arguments?.getInt("shopId") ?: 0
            val branchId = backStackEntry.arguments?.getInt("branchId") ?: 0
            val suborderId = backStackEntry.arguments?.getInt("suborderId") ?: 0
            SuborderDetailsScreen(
                vendorId,
                shopId,
                branchId,
                suborderId,
                navController = navController
            )
        }

        composable(
            "vendorProfile/{vendorId}",
            arguments = listOf(navArgument("vendorId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vendorId = backStackEntry.arguments?.getInt("vendorId") ?: 0
            VendorProfileScreen(vendorId = vendorId, navController = navController)
        }

        composable(
            "vendorOrganizationsConnection/{vendorId}",
            arguments = listOf(navArgument("vendorId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vendorId = backStackEntry.arguments?.getInt("vendorId") ?: 0
            VendorOrganizationsConnection(vendorId = vendorId, navController = navController)
        }

        //API Vendor
        composable(
            "API_VendorItemsScreen/{shopcategory_name}/{shopcategory_ID}/{vendorId}/{branchId}/{shopId}/{approvalStatus}",
            arguments = listOf(
                navArgument("shopcategory_name") { type = NavType.StringType },
                navArgument("shopcategory_ID") { type = NavType.StringType },
                navArgument("vendorId") { type = NavType.StringType },
                navArgument("branchId") { type = NavType.StringType },
                navArgument("shopId") { type = NavType.StringType },
                navArgument("approvalStatus") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val shopcategory_ID = backStackEntry.arguments?.getString("shopcategory_ID") ?: ""
            val shopcategory_name = backStackEntry.arguments?.getString("shopcategory_name") ?: ""
            val vendorId = backStackEntry.arguments?.getString("vendorId") ?: ""
            val branchId = backStackEntry.arguments?.getString("branchId") ?: ""
            val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
            val approvalStatus = backStackEntry.arguments?.getString("approvalStatus") ?: ""

            API_VendorItemsScreen(
                shopcategory_name,
                shopcategory_ID,
                vendorId,
                branchId,
                shopId,
                approvalStatus,
                onBackPressed = { navController.popBackStack() },
                navController = navController
            )
        }
        //In-APP Vendor
        composable(
            "IN_APP_VendorItemsScreen/{shopcategory_name}/{shopcategory_ID}/{vendorId}/{branchId}/{shopId}/{approvalStatus}",
            arguments = listOf(
                navArgument("shopcategory_name") { type = NavType.StringType },
                navArgument("shopcategory_ID") { type = NavType.StringType },
                navArgument("vendorId") { type = NavType.StringType },
                navArgument("branchId") { type = NavType.StringType },
                navArgument("shopId") { type = NavType.StringType },
                navArgument("approvalStatus") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val shopcategory_ID = backStackEntry.arguments?.getString("shopcategory_ID") ?: ""
            val shopcategory_name = backStackEntry.arguments?.getString("shopcategory_name") ?: ""
            val vendorId = backStackEntry.arguments?.getString("vendorId") ?: ""
            val branchId = backStackEntry.arguments?.getString("branchId") ?: ""
            val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
            val approvalStatus = backStackEntry.arguments?.getString("approvalStatus") ?: ""

            IN_APP_VendorItemsScreen(
                shopcategory_name,
                shopcategory_ID,
                vendorId,
                branchId,
                shopId,
                approvalStatus,
                onBackPressed = { navController.popBackStack() },
                navController = navController)
        }


//Customer  Functionality ROUTES
        composable("shop_details") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("customer") // Get the parent entry
            }
            val customerViewModel: CustomerViewModel = hiltViewModel(parentEntry)

            ShopDetailsScreen(navController, customerViewModel)
        }

        composable("customerProfile") { CustomerProfileScreen(navController) }
        composable("personal_info") { EditPersonalInfoScreen(navController) }

        composable("cart") {
            CartScreen(navController)
        }

        composable(
            route = "orderConfirmationScreen/{userId}/{customerId}/{cartJson}/{customerJson}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("customerId") { type = NavType.StringType },
                navArgument("cartJson") { type = NavType.StringType },
                navArgument("customerJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            val cartJson = backStackEntry.arguments?.getString("cartJson") ?: ""
            val customerJson = backStackEntry.arguments?.getString("customerJson") ?: ""

            OrderConfirmationScreen(navController, userId, customerId, cartJson, customerJson)
        }


        composable("customerOrders") { CustomerOrders(navController) }
        composable(
            route = "orderDetail/{orderId}/{customerId}/{addressId}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.IntType },
                navArgument("customerId") { type = NavType.IntType },
                navArgument("addressId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: -1
            val customerId = backStackEntry.arguments?.getInt("customerId") ?: -1
            val addressId = backStackEntry.arguments?.getInt("addressId") ?: -1
            if (orderId != -1 && customerId != -1 && addressId != -1) {
                OrderDetailScreen(
                    navController = navController,
                    orderId = orderId,
                    customerId = customerId,
                    addressId = addressId
                )
            }
        }
//        navController.navigate("track_order/${suborder.suborder_id}/$customerId/$addressId/${suborder.vendor_ID}/${suborder.shop_ID}/${suborder.branch_ID}")
        composable(
            "track_order/{suborderId}/{customerId}/{addressId}/{vendor_ID}/{shop_ID}/{branch_ID}",
            arguments = listOf(
                navArgument("suborderId") { type = NavType.IntType },
                navArgument("customerId") { type = NavType.IntType },
                navArgument("addressId") { type = NavType.IntType },
                navArgument("vendor_ID") { type = NavType.IntType },
                navArgument("shop_ID") { type = NavType.IntType },
                navArgument("branch_ID") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val suborderId = backStackEntry.arguments?.getInt("suborderId") ?: 0
            val customerId = backStackEntry.arguments?.getInt("customerId") ?: 0
            val addressId = backStackEntry.arguments?.getInt("addressId") ?: 0
            val vendor_ID = backStackEntry.arguments?.getInt("vendor_ID") ?: 0
            val shop_ID = backStackEntry.arguments?.getInt("shop_ID") ?: 0
            val branch_ID = backStackEntry.arguments?.getInt("branch_ID") ?: 0
            TrackOrderScreen(
                suborderId = suborderId,
                customerId = customerId,
                addressId = addressId,
                vendor_ID = vendor_ID,
                shop_ID = shop_ID,
                branch_ID = branch_ID,
                navController = navController
            )
        }


        composable(
            "add_address/{customerId}",
            arguments = listOf(navArgument("customerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getInt("customerId") ?: 0
            AddAddressScreen(navController = navController, customerId = customerId)
        }


        composable(
            route = "order_rating/{suborderId}",
            arguments = listOf(navArgument("suborderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val suborderId = backStackEntry.arguments?.getInt("suborderId") ?: 0
            OrderRatingScreen(suborderId = suborderId)
        }


//DeliveryBoy  Functionality ROUTES
        composable("deliveryBoy_Profile") { DeliveryBoyProfileScreen(navController) }

        composable(
            route = "vehicle_screen/{deliveryBoyId}",
            arguments = listOf(navArgument("deliveryBoyId") { type = NavType.IntType })
        ) { backStackEntry ->
            val deliveryBoyId = backStackEntry.arguments?.getInt("deliveryBoyId") ?: 0
            VehicleScreen(deliveryBoyId = deliveryBoyId, navController = navController)
        }

        composable(
            route = "ready_suborders_deliveryBoyScreen/{deliveryBoyID}/{lmdUserID}",
            arguments = listOf(
                navArgument("deliveryBoyID") { type = NavType.IntType },
                navArgument("lmdUserID") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val deliveryBoyID = backStackEntry.arguments?.getInt("deliveryBoyID") ?: 0
            val lmdUserID = backStackEntry.arguments?.getInt("lmdUserID") ?: 0
            ReadySubordersScreen(
                deliveryBoyID = deliveryBoyID,
                lmdUserID = lmdUserID,
                navController = navController
            )
        }
        composable(
            route = "ready_suborders_deliveryBoyScreenFromMapMarker/{deliveryBoyID}/{lmdUserID}/{branchID}",
            arguments = listOf(
                navArgument("deliveryBoyID") { type = NavType.IntType },
                navArgument("lmdUserID") { type = NavType.IntType },
                navArgument("branchID") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val deliveryBoyID = backStackEntry.arguments?.getInt("deliveryBoyID") ?: 0
            val lmdUserID = backStackEntry.arguments?.getInt("lmdUserID") ?: 0
            val branchID = backStackEntry.arguments?.getInt("branchID") ?: 0
            ReadySubordersScreenFromMarker(
                deliveryBoyID = deliveryBoyID,
                lmdUserID = lmdUserID,
                branchID = branchID,
                navController = navController
            )
        }

        composable(
            route = "deliveryBoySuborders/{deliveryBoyID}/{lmdUserID}",
            arguments = listOf(
                navArgument("deliveryBoyID") { type = NavType.IntType },
                navArgument("lmdUserID") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val deliveryBoyID = backStackEntry.arguments?.getInt("deliveryBoyID") ?: 0
            val lmdUserID = backStackEntry.arguments?.getInt("lmdUserID") ?: 0
            AssignedOrdersScreen(
                deliveryBoyID = deliveryBoyID,
                lmdUserID = lmdUserID,
                navController = navController
            )
        }
//        composable("tracking_screenDeliveryBoy") {
//            val order = remember {
//                navController.previousBackStackEntry?.savedStateHandle?.get<AssignedSuborder>("assigned_order")
//            }
//            order?.let {
//                SuborderTrackingDeliveryBoyScreen(order = it, navController = navController)
//            }
//        }
        composable("tracking_screenDeliveryBoy") {
            val previousEntry = navController.previousBackStackEntry
            val order =
                remember { previousEntry?.savedStateHandle?.get<AssignedSuborder>("assigned_order") }
            val deliveryBoyId =
                remember { previousEntry?.savedStateHandle?.get<Int>("delivery_boy_id") }

            if (order != null && deliveryBoyId != null) {
                SuborderTrackingDeliveryBoyScreen(
                    order = order,
                    deliveryBoyID = deliveryBoyId,
                    navController = navController
                )
            }
        }


    }


}







