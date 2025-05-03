package com.example.lastmiledelivery.viewmodels.customer


import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.customer.AddAddressRequest
import com.example.lastmiledelivery.data.models.customer.AddCartResponse
import com.example.lastmiledelivery.data.models.customer.AddToCartRequest
import com.example.lastmiledelivery.data.models.customer.Address
import com.example.lastmiledelivery.data.models.customer.CartMenuItem
import com.example.lastmiledelivery.data.models.customer.CartResponse
import com.example.lastmiledelivery.data.models.customer.CategoryResponse
import com.example.lastmiledelivery.data.models.customer.ClearCartResponse
import com.example.lastmiledelivery.data.models.customer.CustomerData
import com.example.lastmiledelivery.data.models.customer.CustomerMainScreenResponse
import com.example.lastmiledelivery.data.models.customer.CustomerSignupResponse
import com.example.lastmiledelivery.data.models.customer.GenericResponse
import com.example.lastmiledelivery.data.models.customer.LiveLocationData
import com.example.lastmiledelivery.data.models.customer.MenuResponse
import com.example.lastmiledelivery.data.models.customer.Order
import com.example.lastmiledelivery.data.models.customer.OrderDetailsResponse
import com.example.lastmiledelivery.data.models.customer.OrderRequest
import com.example.lastmiledelivery.data.models.customer.PaymentStatusResponse
import com.example.lastmiledelivery.data.models.customer.RouteInfoResponse
import com.example.lastmiledelivery.data.repository.customer.CustomerRepository
import com.example.lastmiledelivery.ui.common.uriToFile
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val repository: CustomerRepository,
    private val context: Application
) : ViewModel() {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _signupState = MutableLiveData<Result<CustomerSignupResponse>?>()
    val signupState: MutableLiveData<Result<CustomerSignupResponse>?> get() = _signupState

    fun customerSignup(
        name: RequestBody, email: RequestBody, phoneNo: RequestBody,
        password: RequestBody, cnic: RequestBody, addressType: RequestBody,
        street: RequestBody, city: RequestBody, zipCode: RequestBody?,
        country: RequestBody, latitude: RequestBody?, longitude: RequestBody?,
        profilePicture: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            val result = repository.customerSignup(
                name,
                email,
                phoneNo,
                password,
                cnic,
                addressType,
                street,
                city,
                zipCode,
                country,
                latitude,
                longitude,
                profilePicture
            )
            _signupState.value = result
        }
    }

    fun clearSignupState() {
        _signupState.value = null  // Reset the state
    }

    // Holds the fetched customer data
    var customerState by mutableStateOf<CustomerData?>(null)
        private set

    // Holds an error message if something goes wrong
    var errorMessage by mutableStateOf<String?>(null)
        private set


    fun fetchCustomerData(id: Int) {
        viewModelScope.launch {
            try {
                val customer = repository.getCustomerData(id)
                if (customer != null) {
                    customerState = customer
                    with(sharedPreferences.edit()) {
                        putInt("customer_id", customerState!!.customerId)
                        apply()
                    }
                    Log.d("CUSTOMERID", "${customerState!!.customerId}")
                } else {
                    errorMessage = "Customer not found"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred"
            }
        }
    }


    fun getCustomerId(): Int? {
        return sharedPreferences.getInt("customer_id", -1)
    }
    // Create a mapping of shop_id to shop_name


    private val _customerData =
        MutableStateFlow<List<CustomerMainScreenResponse>?>(null)
    val customerData: StateFlow<List<CustomerMainScreenResponse>?> = _customerData

    private val _errorMessages = MutableStateFlow<String?>(null)
    val errorMessages: StateFlow<String?> = _errorMessages

    fun fetchCustomerMainScreen(customerId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getCustomerMainScreen(customerId)
                if (response.isSuccessful) {
                    _customerData.value = response.body()
                } else {
                    _errorMessages.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessages.value = "Exception: ${e.message}"
            }
        }
    }
//    val shopIdToNameMap: Map<Int, String>
//        get() = _customerData.value?.associate { it.shopId to it.shopName } ?: emptyMap()


    private val _selectedShop = MutableStateFlow<CustomerMainScreenResponse?>(null)
    val selectedShop: StateFlow<CustomerMainScreenResponse?> = _selectedShop

    fun setSelectedShop(shop: CustomerMainScreenResponse) {
        _selectedShop.value = shop
    }


    private val _categories = MutableStateFlow<List<CategoryResponse>>(emptyList())
    val categories: StateFlow<List<CategoryResponse>> = _categories

    private val _errorsMessages = MutableStateFlow<String?>(null)
    val errorsMessages: StateFlow<String?> = _errorsMessages

    fun fetchCategories(vendorId: Int, shopId: Int, branchId: Int) {
        viewModelScope.launch {
            repository.getCategories(vendorId, shopId, branchId)
                .onSuccess { _categories.value = it }
                .onFailure { _errorsMessages.value = it.message }
        }
    }


    sealed class MenuState {
        object Loading : MenuState()
        data class Success(val menu: MenuResponse) : MenuState()
        data class Error(val message: String) : MenuState()
    }

    //    private val _menuState = MutableStateFlow<MenuState>(MenuState.Loading)
//    val menuState: StateFlow<MenuState> get() = _menuState
    private val _menuState = MutableStateFlow<MenuState>(MenuState.Loading)
    val menuState: StateFlow<MenuState> get() = _menuState


    fun fetchVendorMenu(vendorId: Int, shopId: Int, branchId: Int) {
        viewModelScope.launch {
            Log.d(
                "VendorMenu",
                "Fetching menu for vendor: $vendorId, shop: $shopId, branch: $branchId"
            )

            _menuState.value = MenuState.Loading // Show loading state

            val result = repository.getVendorMenu(vendorId, shopId, branchId)
            result.onSuccess { response ->
                if (response.error != null || response.items.isNullOrEmpty()) {
                    // If API returns an error or empty items, show "No items found"
                    Log.e("VendorMenu", "No items found for this vendor/shop/branch")
                    _menuState.value =
                        MenuState.Success(MenuResponse(items = emptyList())) // ✅ Proper type

                } else {
                    _menuState.value = MenuState.Success(response) // Show menu items
                    Log.d("VendorMenu", "Menu Items: ${response.items}")
                }
            }.onFailure { error ->
                Log.e("VendorMenu", "Error: ${error.localizedMessage}")
                _menuState.value =
                    MenuState.Success(MenuResponse(error = "")) // ✅ If expecting error string
                // Show empty items instead of error
            }
        }
    }


    var updateState by mutableStateOf<Result<GenericResponse>?>(null) // ✅ Use GenericResponse
        private set

    fun updateCustomer(
        customerId: Int,
        name: String?,
        email: String?,
        phoneNo: String?,
        password: String?,
        cnic: String?,
        profilePictureUri: Uri?,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    Log.d("UpdateCustomer", "Customer ID: $customerId")
                    Log.d("UpdateCustomer", "Name: $name, Email: $email, Phone No: $phoneNo")
                    Log.d("UpdateCustomer", "Password: $password, CNIC: $cnic")
                    Log.d("UpdateCustomer", "Profile Picture URI: $profilePictureUri")

                    val profilePicturePart = profilePictureUri?.let { uri ->
                        val file = uriToFiles(uri, context)
                        if (file != null && file.exists()) {
                            Log.d(
                                "UpdateCustomer",
                                "File Size: ${file.length()} bytes, File Name: ${file.name}"
                            )
                            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                            MultipartBody.Part.createFormData(
                                "profile_picture",
                                file.name,
                                requestFile
                            )
                        } else {
                            Log.e("UpdateCustomer", "Failed to convert URI to File")
                            null
                        }
                    }

                    val result = repository.updateCustomer(
                        customerId,
                        name,
                        email,
                        phoneNo,
                        password,
                        cnic,
                        profilePicturePart
                    )
                    updateState = result

                    Log.d("UpdateCustomer", "API Response: $result")

                }
            } catch (e: Exception) {
                Log.e("UpdateCustomer", "Error in API Call: ${e.localizedMessage}")
            } catch (e: CancellationException) {
                Log.e("UpdateCustomer", "Request was cancelled")
            }
        }

    }


    fun uriToFiles(uri: Uri, context: Context): File? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, "profile_pic_${System.currentTimeMillis()}.jpg")
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        } catch (e: Exception) {
            Log.e("UpdateCustomer", "Error converting URI to File: ${e.localizedMessage}")
            null
        }
    }


    private val _cartState = MutableStateFlow<CartState>(CartState.Loading)
    val cartState: StateFlow<CartState> get() = _cartState


    fun fetchCartDetails(customerId: Int) {
        viewModelScope.launch {
            Log.d(
                "CartDebug",
                "Fetching cart details for customerId: $customerId"
            ) // ✅ Log API call

            _cartState.value = CartState.Loading
            val result = repository.getCartDetails(customerId)

            result.onSuccess { cartData ->
                if (cartData == null) {  // ✅ Handle Null Cart Properly
                    Log.e("CartDebug", "ViewModel: Cart is null, showing empty cart message")
                    _cartState.value =
                        CartState.Empty("No items found in the cart. Please add items.")
                } else {
                    _cartState.value = CartState.Success(cartData)
                }
            }.onFailure { error ->
                Log.e("CartDebug", "Error Fetching Cart: ${error.localizedMessage}") // ❌ Log errors
                _cartState.value = CartState.Error(error.localizedMessage ?: "Error fetching cart")
            }
        }
    }


    private val _cartResponse = MutableLiveData<Result<AddCartResponse>>()
    val cartResponse: LiveData<Result<AddCartResponse>> = _cartResponse

    fun addItemToCart(customerId: Int, item: CartMenuItem, quantity: Int) {
        viewModelScope.launch {
            val request = AddToCartRequest(
                customer_id = customerId,
                vendor_id = item.vendor_id,
                shop_id = item.shop_id,
                branch_id = item.branch_id,
                itemdetails_id = item.itemdetails_id,
                quantity = quantity,
                price = item.price
            )
            Log.d("VIEWMODELCART", "$request")
            val result = repository.addItemToCart(request)
            _cartResponse.postValue(result)
        }
    }


    fun placeOrder(
        orderRequest: OrderRequest,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.placeOrder(orderRequest)
                if (response.isSuccessful) {
                    response.body()?.let {
                        onSuccess(it.message)
                    } ?: onError("Unknown error occurred")
                } else {
                    onError("Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("Exception: ${e.message}")
            }
        }
    }


    var addressList by mutableStateOf<List<Address>>(emptyList())
        private set

    var selectedAddress by mutableStateOf<Address?>(null)
        private set

    fun fetchAddresses(customerId: Int) {
        viewModelScope.launch {
            repository.getCustomerAddresses(customerId).onSuccess { addresses ->
                addressList = addresses
                Log.d("AddressViewModel", "Address: ${addresses}")
            }.onFailure { error ->
                Log.e("AddressViewModel", "Error: ${error.message}")
            }
        }
    }

    fun selectAddress(address: Address) {
        selectedAddress = address
        Log.d("Selected Address ID", "ID: ${address.id}")
    }


    private val _clearCartState = MutableStateFlow<Result<ClearCartResponse>?>(null)
    val clearCartState: StateFlow<Result<ClearCartResponse>?> = _clearCartState

    fun clearCart(customerId: Int) {
        viewModelScope.launch {
            _clearCartState.value = repository.clearCart(customerId)
        }
    }

    var orderState by mutableStateOf<OrderUiState>(OrderUiState.Loading)
        private set

    fun fetchCustomerOrders(customerId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getCustomerOrders(customerId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val orders = response.body()?.data ?: emptyList()
                    orderState = OrderUiState.Success(orders)
                } else {
                    orderState = OrderUiState.Error("No orders found or failed to fetch orders")
                }

            } catch (e: Exception) {
                orderState = OrderUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private val _orderDetails = mutableStateOf<OrderDetailsResponse?>(null)
    val orderDetails: State<OrderDetailsResponse?> = _orderDetails

    // Function to fetch order details
    fun fetchOrderDetails(orderId: Int) {
        viewModelScope.launch {
            // Call the repository to get order details
            _orderDetails.value = repository.getOrderDetails(orderId)
        }
    }


    var routeInfo by mutableStateOf<RouteInfoResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun getRouteInfo(suborderId: Int) {
        viewModelScope.launch {
            isLoading = true
            routeInfo = repository.fetchRouteInfo(suborderId)
            isLoading = false
        }
    }


    private val _confirmDeliveryResult = mutableStateOf<Result<String>?>(null)
    val confirmDeliveryResult: State<Result<String>?> = _confirmDeliveryResult

    fun confirmDelivery(suborderId: Int) {
        viewModelScope.launch {
            val result = repository.confirmOrderDelivery(suborderId)
            _confirmDeliveryResult.value = result
        }
    }

    fun clearDeliveryResult() {
        _confirmDeliveryResult.value = null
    }


    private val _paymentStatus = mutableStateOf<PaymentStatusResponse?>(null)
    val paymentStatus: State<PaymentStatusResponse?> = _paymentStatus

    private var pollingJob: Job? = null

    fun startPolling(suborderId: Int) {
        stopPolling() // in case already running
        pollingJob = viewModelScope.launch {
            while (isActive) {
                val result = repository.fetchPaymentStatus(suborderId)
                _paymentStatus.value = result
                delay(5000) // Poll every 5 seconds
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }


    private val _confirmPaymentResult = mutableStateOf<Result<String>?>(null)
    val confirmPaymentResult: State<Result<String>?> = _confirmPaymentResult

    fun confirmPayment(suborderId: Int) {
        viewModelScope.launch {
            val result = repository.confirmPaymentByCustomer(suborderId)
            _confirmPaymentResult.value = result
        }
    }

    fun clearConfirmPaymentResult() {
        _confirmPaymentResult.value = null
    }

    private val _cancelOrderResult = mutableStateOf<Result<String>?>(null)
    val cancelOrderResult: State<Result<String>?> = _cancelOrderResult

    fun cancelOrder(orderId: Int) {
        viewModelScope.launch {
            _cancelOrderResult.value = repository.cancelOrder(orderId)
        }
    }

    fun clearCancelResult() {
        _cancelOrderResult.value = null
    }


    private val _liveTracking = mutableStateOf<LiveLocationData?>(null)
    val liveTracking: State<LiveLocationData?> = _liveTracking


    fun getLatestLocation(suborderId: Int) {
        viewModelScope.launch {
            while (true) {
                val result = repository.getLiveTracking(suborderId)
                _liveTracking.value = result?.data
                delay(5000) // Refresh every 10 seconds
            }
        }
    }


    private val _liveRoute = MutableStateFlow<List<LatLng>>(emptyList())
    val liveRoute: StateFlow<List<LatLng>> = _liveRoute

    fun startLiveRouteTracking(suborderId: Int) {
        viewModelScope.launch {
            while (true) {
                val response = repository.fetchLiveRoute(suborderId)
                val points = response?.data?.map {
                    LatLng(it.latitude, it.longitude)
                } ?: emptyList()

                _liveRoute.value = points
                delay(10000L) // every 10 seconds
            }
        }
    }


    var addAddressResponse by mutableStateOf<String?>(null)
    var errorMessageAddress by mutableStateOf<String?>(null)
    var isLoadingAddress by mutableStateOf(false)

    fun addAddress(customerId: Int, request: AddAddressRequest) {
        viewModelScope.launch {
            isLoadingAddress = true
            try {
                val response = repository.addAddress(customerId, request)
                if (response.isSuccessful) {
                    addAddressResponse = response.body()?.message
                } else {
                    errorMessageAddress = response.errorBody()?.string()
                }
            } catch (e: Exception) {
                errorMessageAddress = e.message
            } finally {
                isLoadingAddress = false
            }
        }
    }

}

sealed class OrderUiState {
    object Loading : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}

sealed class CartState {
    object Loading : CartState()
    data class Success(val cart: CartResponse) : CartState()
    data class Error(val message: String) : CartState()
    data class Empty(val message: String) : CartState()
}





