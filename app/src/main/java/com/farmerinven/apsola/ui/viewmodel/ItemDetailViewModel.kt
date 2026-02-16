package com.farmerinven.apsola.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farmerinven.apsola.data.model.Category
import com.farmerinven.apsola.data.model.InventoryItem
import com.farmerinven.apsola.data.model.ItemHistory
import com.farmerinven.apsola.data.repository.CategoryRepository
import com.farmerinven.apsola.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ItemDetailUiState(
    val item: InventoryItem? = null,
    val category: Category? = null,
    val history: List<ItemHistory> = emptyList(),
    val isLoading: Boolean = false
)

class ItemDetailViewModel(
    private val inventoryRepository: InventoryRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState: StateFlow<ItemDetailUiState> = _uiState.asStateFlow()

    fun loadItem(itemId: Long) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val item = inventoryRepository.getItemById(itemId)
            if (item != null) {
                val category = categoryRepository.getCategoryById(item.categoryId)
                inventoryRepository.getHistoryForItem(itemId).collect { history ->
                    _uiState.value = _uiState.value.copy(
                        item = item,
                        category = category,
                        history = history,
                        isLoading = false
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            _uiState.value.item?.let { item ->
                inventoryRepository.deleteItem(item)
            }
        }
    }
}
