package com.example.lastmiledelivery.viewmodels.admin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.admin.PendingBranch
import com.example.lastmiledelivery.data.models.admin.RejectionReason
import com.example.lastmiledelivery.data.models.admin.VendorApproval
import com.example.lastmiledelivery.data.repository.admin.VendorApprovalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VendorApprovalViewModel @Inject constructor(private val repository: VendorApprovalRepository) : ViewModel() {

    private val _vendors = MutableStateFlow<List<VendorApproval>>(emptyList())
    val vendors: StateFlow<List<VendorApproval>> = _vendors.asStateFlow()

    private val _selectedVendor = MutableStateFlow<VendorApproval?>(null)
    val selectedVendor: StateFlow<VendorApproval?> = _selectedVendor.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        fetchVendors()
    }

    private fun fetchVendors() {
        viewModelScope.launch {
            val fetchedVendors = repository.getVendors()
            _vendors.value = fetchedVendors
        }
    }

    fun selectVendor(vendor: VendorApproval) {
        _selectedVendor.value = vendor
    }


fun approveVendor(vendorId: Int) {
    viewModelScope.launch {
        val result = repository.approveVendor(vendorId)
        result.onSuccess { msg ->
            _message.value = msg
            Log.d("VendorApproval", "Success: $msg") // ✅ Log success message
            // ✅ Update the selected vendor's approval status
            _selectedVendor.value = _selectedVendor.value?.copy(approvalStatus = "Approved")

            fetchVendors() // Refresh list after approval
        }.onFailure {
            Log.e("VendorApproval", "Error: ${it.message}") // ✅ Log error
            _message.value = "Error: ${it.message}"
        }
    }
}

    private val _rejectResult = MutableLiveData<Result<String>>()
    val rejectResult: LiveData<Result<String>> = _rejectResult

    fun rejectVendor(vendorId: Int, selectedReasons: List<String>) {
        viewModelScope.launch {
            if (selectedReasons.isEmpty()) {
                _rejectResult.value = Result.failure(Exception("At least one reason must be selected"))
                return@launch
            }

            val result = repository.rejectVendor(vendorId, selectedReasons)
            result.onSuccess { msg ->
                _rejectResult.value = Result.success(msg)

                Log.d("VendorRejection", "Success: $msg") // ✅ Log success message

                // ✅ Update the selected vendor's approval status to "Rejected"
                _selectedVendor.value = _selectedVendor.value?.copy(approvalStatus = "Rejected")

                fetchVendors() // Refresh list after rejection
                fetchRejectionReasons(vendorId)
            }.onFailure {
                Log.e("VendorRejection", "Error: ${it.message}") // ✅ Log error
                _rejectResult.value = Result.failure(it)
            }
        }
    }




    private val _rejectionReasons = MutableStateFlow<List<RejectionReason>>(emptyList())
    val rejectionReasons: StateFlow<List<RejectionReason>> = _rejectionReasons

    private val _correctionStatus = MutableStateFlow<String?>(null)
    val correctionStatus: StateFlow<String?> = _correctionStatus

    fun fetchRejectionReasons(vendorId: Int) {
        viewModelScope.launch {
            repository.getRejectionReasons(vendorId)
                .onSuccess { reasons -> _rejectionReasons.value = reasons }
                .onFailure { _rejectionReasons.value = emptyList() }
        }
    }




    fun correctRejectionReason(vendorId: Int, reasonId: Int) {
        viewModelScope.launch {
            repository.correctRejectionReason(vendorId, reasonId)
                .onSuccess { message -> _correctionStatus.value = message
                    fetchRejectionReasons(vendorId) // ✅ Refresh list after correction
                }
                .onFailure { _correctionStatus.value = "Failed to correct reason" }
        }
    }

    fun clearCorrectionStatus() {
        _correctionStatus.value = null // Reset the status message
    }









    private val _pendingBranches = MutableStateFlow<List<PendingBranch>>(emptyList())
    val pendingBranches: StateFlow<List<PendingBranch>> = _pendingBranches

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchPendingBranches()
    }

    fun fetchPendingBranches() {
        viewModelScope.launch {
            repository.getPendingBranches()
                .onSuccess { _pendingBranches.value = it }
                .onFailure { _errorMessage.value = it.localizedMessage }
        }
    }


    private val _approvalState = MutableStateFlow<Result<String>?>(null)
    val approvalState: StateFlow<Result<String>?> = _approvalState
    private val _selectedBranch = MutableStateFlow<PendingBranch?>(null)
    val selectedBranch: StateFlow<PendingBranch?> = _selectedBranch.asStateFlow()


    private val _rejectionState = MutableStateFlow<Result<String>?>(null)
    val rejectionState: StateFlow<Result<String>?> = _rejectionState

     fun approveBranch(branchId: Int) {
        viewModelScope.launch {
            repository.approveBranch(branchId)
                .onSuccess { _approvalState.value = Result.success(it)
                    fetchPendingBranches()
                    _selectedBranch.value = _selectedBranch.value?.copy(branchApprovalStatus = "Approved")
                }
                .onFailure { _approvalState.value = Result.failure(it) }
        }
    }

     fun rejectBranch(branchId: Int, rejectionReasons: List<String>) {
        viewModelScope.launch {
            repository.rejectBranch(branchId, rejectionReasons)
                .onSuccess { _rejectionState.value = Result.success(it)
                    fetchPendingBranches()
                    _selectedBranch.value = _selectedBranch.value?.copy(branchApprovalStatus = "Rejected")
                }
                .onFailure { _rejectionState.value = Result.failure(it) }
        }
    }


    private val _branchRejectionReasons = MutableStateFlow<List<RejectionReason>>(emptyList())
    val branchRejectionReasons: StateFlow<List<RejectionReason>> = _branchRejectionReasons

    private val _branchCorrectionStatus = MutableStateFlow<String?>(null)
    val branchCorrectionStatus: StateFlow<String?> = _branchCorrectionStatus

    fun getBranchRejectionReasons(branchId: Int) {
        viewModelScope.launch {
            repository.getBranchRejectionReasons(branchId)
                .onSuccess { reasons -> _branchRejectionReasons.value = reasons }
                .onFailure { reasons -> _branchRejectionReasons.value = emptyList() }
        }
    }


    fun correctBranchRejectionReason(branchId: Int, reasonId: Int) {
        viewModelScope.launch {
            val result = repository.correctBranchRejectionReason(branchId, reasonId)
            if (result.isSuccess) {
                _branchCorrectionStatus.value = result.getOrNull()
                getBranchRejectionReasons(branchId)
            } else {
                _branchCorrectionStatus.value = "Failed to correct reason"
            }
        }

    }

    fun clearBranchCorrectionStatus() {
        _branchCorrectionStatus.value = null
    }
    fun selectBranch(branch: PendingBranch) {
        _selectedBranch.value = branch
    }

}