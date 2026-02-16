package com.farmerinven.apsola.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farmerinven.apsola.data.model.Category
import com.farmerinven.apsola.data.model.InventoryItem
import com.farmerinven.apsola.data.model.ItemStatus
import com.farmerinven.apsola.data.repository.CategoryRepository
import com.farmerinven.apsola.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class InventoryUiState(
    val items: List<InventoryItem> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: Long? = null,
    val searchQuery: String = "",
    val selectedStatus: ItemStatus? = null,
    val workingCount: Int = 0,
    val needsRepairCount: Int = 0,
    val outOfStockCount: Int = 0
)

class InventoryViewModel(
    private val inventoryRepository: InventoryRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                inventoryRepository.getAllItems(),
                categoryRepository.getAllCategories(),
                inventoryRepository.getItemCountByStatus(ItemStatus.WORKING),
                inventoryRepository.getItemCountByStatus(ItemStatus.NEEDS_REPAIR),
                inventoryRepository.getItemCountByStatus(ItemStatus.OUT_OF_STOCK)
            ) { items, categories, working, needsRepair, outOfStock ->
                _uiState.value.copy(
                    items = filterItems(items),
                    categories = categories,
                    workingCount = working,
                    needsRepairCount = needsRepair,
                    outOfStockCount = outOfStock
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun filterItems(items: List<InventoryItem>): List<InventoryItem> {
        var filtered = items

        _uiState.value.selectedCategory?.let { categoryId ->
            filtered = filtered.filter { it.categoryId == categoryId }
        }

        _uiState.value.selectedStatus?.let { status ->
            filtered = filtered.filter { it.status == status }
        }

        if (_uiState.value.searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.name.contains(_uiState.value.searchQuery, ignoreCase = true)
            }
        }

        return filtered
    }

    fun setSelectedCategory(categoryId: Long?) {
        _uiState.value = _uiState.value.copy(selectedCategory = categoryId)
        viewModelScope.launch {
            inventoryRepository.getAllItems().collect { items ->
                _uiState.value = _uiState.value.copy(items = filterItems(items))
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch {
            inventoryRepository.getAllItems().collect { items ->
                _uiState.value = _uiState.value.copy(items = filterItems(items))
            }
        }
    }

    fun setSelectedStatus(status: ItemStatus?) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
        viewModelScope.launch {
            inventoryRepository.getAllItems().collect { items ->
                _uiState.value = _uiState.value.copy(items = filterItems(items))
            }
        }
    }

    fun deleteItem(item: InventoryItem) {
        viewModelScope.launch {
            inventoryRepository.deleteItem(item)
        }
    }
}
