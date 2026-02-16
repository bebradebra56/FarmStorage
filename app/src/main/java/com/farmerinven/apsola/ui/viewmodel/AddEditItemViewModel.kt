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

data class AddEditItemUiState(
    val name: String = "",
    val categoryId: Long = 0,
    val quantity: Int = 1,
    val status: ItemStatus = ItemStatus.WORKING,
    val lastUsedDate: Long = System.currentTimeMillis(),
    val notes: String = "",
    val categories: List<Category> = emptyList(),
    val isEditMode: Boolean = false,
    val itemId: Long? = null
)

class AddEditItemViewModel(
    private val inventoryRepository: InventoryRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditItemUiState())
    val uiState: StateFlow<AddEditItemUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    categoryId = categories.firstOrNull()?.id ?: 0
                )
            }
        }
    }

    fun loadItem(itemId: Long) {
        viewModelScope.launch {
            val item = inventoryRepository.getItemById(itemId)
            if (item != null) {
                _uiState.value = _uiState.value.copy(
                    name = item.name,
                    categoryId = item.categoryId,
                    quantity = item.quantity,
                    status = item.status,
                    lastUsedDate = item.lastUsedDate,
                    notes = item.notes,
                    isEditMode = true,
                    itemId = item.id
                )
            }
        }
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateCategoryId(categoryId: Long) {
        _uiState.value = _uiState.value.copy(categoryId = categoryId)
    }

    fun updateQuantity(quantity: Int) {
        _uiState.value = _uiState.value.copy(quantity = quantity.coerceAtLeast(0))
    }

    fun updateStatus(status: ItemStatus) {
        _uiState.value = _uiState.value.copy(status = status)
    }

    fun updateLastUsedDate(date: Long) {
        _uiState.value = _uiState.value.copy(lastUsedDate = date)
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun saveItem(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank()) return

        viewModelScope.launch {
            if (state.isEditMode && state.itemId != null) {
                val item = InventoryItem(
                    id = state.itemId,
                    name = state.name,
                    categoryId = state.categoryId,
                    quantity = state.quantity,
                    status = state.status,
                    lastUsedDate = state.lastUsedDate,
                    notes = state.notes
                )
                inventoryRepository.updateItem(item)
            } else {
                val item = InventoryItem(
                    name = state.name,
                    categoryId = state.categoryId,
                    quantity = state.quantity,
                    status = state.status,
                    lastUsedDate = state.lastUsedDate,
                    notes = state.notes
                )
                inventoryRepository.insertItem(item)
            }
            onSuccess()
        }
    }
}
