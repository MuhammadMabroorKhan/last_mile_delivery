package com.example.lastmiledelivery.ui.customer


import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.lastmiledelivery.data.models.customer.DeliveryBoyInfo
import com.example.lastmiledelivery.data.models.customer.DeliveryBoyRatingRequest
import com.example.lastmiledelivery.data.models.customer.ItemRatingRequest
import com.example.lastmiledelivery.data.models.customer.RatingData
import com.example.lastmiledelivery.data.models.customer.RatingItem
import com.example.lastmiledelivery.data.models.customer.RatingOrderResponse
import com.example.lastmiledelivery.data.models.customer.RatingSubOrder
import com.example.lastmiledelivery.viewmodels.customer.RatingUiState
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderRatingScreen(
    suborderId: Int,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    val orderDetails by viewModel.orderDetailsRating.collectAsState()
    val error by viewModel.error.collectAsState()
    val ratingState = viewModel.ratingState
    val context = LocalContext.current

    val itemRatings = remember { mutableStateMapOf<Int, Int>() }
    val itemReviews = remember { mutableStateMapOf<Int, String>() }
    val itemImages = remember { mutableStateMapOf<Int, List<Uri>>() }

    val deliveryBoyRating = remember { mutableStateOf(0) }
    val deliveryBoyReview = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchOrderDetailsRating(suborderId)
        viewModel.loadRatings(suborderId)
    }

    LaunchedEffect(ratingState) {
        Log.d("RatingCheck", "Rating State: $ratingState")
    }


    when {
        error != null -> {
            Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
        }

        orderDetails == null || ratingState is RatingUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        else -> {
            val boy = orderDetails!!.deliveryBoyInfo
            val ratingData = (ratingState as? RatingUiState.Success)?.data

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Delivery Boy Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = boy.profilePicture,
                        contentDescription = "Profile Pic",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Name: ${boy.name}", style = MaterialTheme.typography.titleMedium)
                        Text("Email: ${boy.email}", style = MaterialTheme.typography.bodySmall)
                        Text("CNIC: ${boy.licenseNo}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Rate Delivery Boy", style = MaterialTheme.typography.titleLarge)

                val deliveryRating = ratingData?.delivery_boy_rating
                if (deliveryRating != null && deliveryRating.has_rated) {
                    RatingBar(
                        value = (deliveryRating.rating_stars ?: 0).toFloat(),
                        config = RatingBarConfig().size(24.dp),
                        onValueChange = {},
                        onRatingChanged = {},
                    )
                    Text(
                        text = deliveryRating.comments ?: "No comment",
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    RatingBar(
                        value = deliveryBoyRating.value.toFloat(),
                        config = RatingBarConfig()
                            .activeColor(Color(0xFFFFC107))
                            .size(24.dp),
                        onValueChange = { deliveryBoyRating.value = it.toInt() },
                        onRatingChanged = { deliveryBoyRating.value = it.toInt() }
                    )
                    OutlinedTextField(
                        value = deliveryBoyReview.value,
                        onValueChange = { deliveryBoyReview.value = it },
                        label = { Text("Write review for delivery boy") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Rate Items:", style = MaterialTheme.typography.titleLarge)

                orderDetails!!.items.forEach { item ->
                    val itemId = item.id
                    val isInvalidItemId = item.itemDetailId == null && item.itemDetailsId == 0
                    val nameColor = if (isInvalidItemId) Color.Red else Color.Unspecified

                    val existingItemRating = ratingData?.item_ratings?.ratings?.find {
                        val responseId = it.itemdetails_ID?.toString()?.trim()
                        val inputId = item.itemDetailId?.toString()?.trim() ?: item.itemDetailsId.toString()
                        responseId == inputId
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = item.itemName ?: "Unnamed Item",
                            color = nameColor,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        if (existingItemRating != null && ratingData.item_ratings.has_rated) {
                            RatingBar(
                                value = existingItemRating.rating_stars.toFloatOrNull() ?: 0f,
                                config = RatingBarConfig().size(24.dp),
                                onValueChange = {},
                                onRatingChanged = {},
                            )
                            Text(
                                text = existingItemRating.comments,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            if (existingItemRating.images.isNotEmpty()) {
                                LazyRow {
                                    items(existingItemRating.images) { imageUrl ->
                                        AsyncImage(
                                            model = imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(80.dp)
                                                .padding(end = 8.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }
                                }
                            }
                        } else {
                            RatingBar(
                                value = itemRatings[itemId]?.toFloat() ?: 0f,
                                config = RatingBarConfig()
                                    .activeColor(Color(0xFFFFC107))
                                    .size(24.dp),
                                onValueChange = { itemRatings[itemId] = it.toInt() },
                                onRatingChanged = { itemRatings[itemId] = it.toInt() }
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedTextField(
                                value = itemReviews[itemId] ?: "",
                                onValueChange = { itemReviews[itemId] = it },
                                label = { Text("Write review for item") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            val imageUris = itemImages[itemId] ?: emptyList()
                            MultiImagePicker(
                                selectedImages = imageUris,
                                onImagesSelected = { itemImages[itemId] = it }
                            )

                            if (imageUris.isNotEmpty()) {
                                LazyRow {
                                    items(imageUris) { uri ->
                                        Image(
                                            painter = rememberAsyncImagePainter(uri),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(80.dp)
                                                .padding(end = 8.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Show button only if not rated already
                if (deliveryRating?.has_rated == false || ratingData?.item_ratings?.has_rated == false) {

                    Button(onClick = {
                        val unratedItems = orderDetails!!.items.filter { item ->
                            val itemId = item.id
                            val existingRating = ratingData?.item_ratings?.ratings?.any {
                                val responseId = it.itemdetails_ID?.toString()?.trim()
                                val inputId = item.itemDetailId?.toString()?.trim() ?: item.itemDetailsId.toString()
                                responseId == inputId
                            } ?: false
                            !existingRating && itemRatings[itemId] == null
                        }

                        val isDeliveryBoyAlreadyRated = ratingData?.delivery_boy_rating?.has_rated == true
                        val deliveryRatingMissing = !isDeliveryBoyAlreadyRated && deliveryBoyRating.value == 0

                        when {
                            deliveryRatingMissing -> {
                                Toast.makeText(context, "Please rate the delivery boy", Toast.LENGTH_SHORT).show()
                            }
                            unratedItems.isNotEmpty() -> {
                                Toast.makeText(context, "Please rate all items", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                // Submit delivery boy rating if not already rated
                                if (!isDeliveryBoyAlreadyRated) {
                                    viewModel.rateDeliveryBoy(
                                        DeliveryBoyRatingRequest(
                                            suborderId = suborderId,
                                            ratingStars = deliveryBoyRating.value,
                                            comments = deliveryBoyReview.value
                                        )
                                    )
                                }

                                // Submit item ratings as grouped requests
                                val groupedItemRatings = orderDetails!!.items.mapNotNull { item ->
                                    val itemId = item.id
                                    val alreadyRated = ratingData?.item_ratings?.ratings?.any {
                                        val responseId = it.itemdetails_ID?.toString()?.trim()
                                        val inputId = item.itemDetailId?.toString()?.trim() ?: item.itemDetailsId.toString()
                                        responseId == inputId
                                    } ?: false

                                    if (!alreadyRated) {
                                        val rating = itemRatings[itemId] ?: return@mapNotNull null
                                        val comment = itemReviews[itemId] ?: ""
                                        val uris = itemImages[itemId] ?: emptyList()

                                        ItemRatingRequest(
                                            itemdetails_ID = item.itemDetailId ?: item.itemDetailsId,
                                            suborders_ID = 0, // will be replaced in ViewModel
                                            rating = rating,
                                            comment = comment,
                                            images = uris
                                        )
                                    } else null
                                }

                                viewModel.submitRatingsForItems(
                                    context = context,
                                    suborderId = suborderId,
                                    groupedItems = groupedItemRatings
                                )

                                Toast.makeText(context, "Submitted successfully!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Text("Submit Ratings")
                    }

                }
            }
        }
    }
}


@Composable
fun MultiImagePicker(
    selectedImages: List<Uri>,
    onImagesSelected: (List<Uri>) -> Unit
) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.all { it.value }
        if (granted) {
            showDialog.value = true
        } else {
            Toast.makeText(context, "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri.value != null) {
                val updated = selectedImages.toMutableList()
                updated.add(cameraImageUri.value!!)
                onImagesSelected(updated)
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                val updated = selectedImages.toMutableList()
                updated.addAll(uris)
                onImagesSelected(updated)
            }
        }

    // Launch camera intent
    fun launchCamera() {
        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
        )
        cameraImageUri.value = uri
        if (uri != null) {
            cameraLauncher.launch(uri)
        }
    }

    // Request necessary permissions based on API level
    fun requestPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            permissions.add(android.Manifest.permission.READ_MEDIA_IMAGES)
        }
        permissions.add(android.Manifest.permission.CAMERA)

        permissionLauncher.launch(permissions.toTypedArray())
    }

    // Main button
    Button(onClick = { requestPermissions() }) {
        Text("Add Images")
    }

    // Dialog
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Choose Option") },
            text = {
                Column {
                    Text(
                        "Take Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDialog.value = false
                                launchCamera()
                            }
                            .padding(12.dp)
                    )
                    Text(
                        "Pick from Gallery",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDialog.value = false
                                galleryLauncher.launch("image/*")
                            }
                            .padding(12.dp)
                    )
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

