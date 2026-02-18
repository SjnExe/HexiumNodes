package com.hexium.nodes.feature.home.server.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.core.model.AllocationData
import com.hexium.nodes.core.model.SubdomainData
import com.hexium.nodes.data.PterodactylRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NetworkUiState(
    val allocations: List<AllocationData> = emptyList(),
    val subdomains: List<SubdomainData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTab: Int = 0 // 0 for Ports, 1 for Subdomains
)

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val repository: PterodactylRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NetworkUiState())
    val uiState: StateFlow<NetworkUiState> = _uiState.asStateFlow()

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    fun loadData(serverId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Try to load allocations
                val allocations = repository.getAllocations(serverId)
                _uiState.value = _uiState.value.copy(allocations = allocations)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to load ports: ${e.message}")
            }

            try {
                // Try to load subdomains independently
                val subdomains = repository.getSubdomains(serverId)
                _uiState.value = _uiState.value.copy(subdomains = subdomains)
            } catch (e: Exception) {
                // Don't overwrite the error if allocations failed too, just append or log?
                // For now, if allocations worked but subdomains failed, maybe just show empty subdomains?
                // Or update error message?
                 val currentError = _uiState.value.error
                 if (currentError == null) {
                    // Only show error if no other error exists, or maybe ignore 404?
                    // _uiState.value = _uiState.value.copy(error = "Failed to load subdomains: ${e.message}")
                 }
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun createAllocation(serverId: String) {
        viewModelScope.launch {
             try {
                repository.createAllocation(serverId)
                loadData(serverId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to create port: ${e.message}")
            }
        }
    }

    fun deleteAllocation(serverId: String, allocationId: Int) {
         viewModelScope.launch {
             try {
                repository.deleteAllocation(serverId, allocationId)
                loadData(serverId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to delete port: ${e.message}")
            }
        }
    }

    fun updateAllocationNote(serverId: String, allocationId: Int, notes: String) {
         viewModelScope.launch {
             try {
                repository.updateAllocationNote(serverId, allocationId, notes)
                loadData(serverId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to update note: ${e.message}")
            }
        }
    }

    fun createSubdomain(serverId: String, domain: String) {
        viewModelScope.launch {
             try {
                repository.createSubdomain(serverId, domain)
                loadData(serverId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to create subdomain: ${e.message}")
            }
        }
    }

    fun deleteSubdomain(serverId: String, subdomainId: Int) {
        viewModelScope.launch {
             try {
                repository.deleteSubdomain(serverId, subdomainId)
                loadData(serverId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to delete subdomain: ${e.message}")
            }
        }
    }
}
