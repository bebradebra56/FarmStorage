package com.farmerinven.apsola.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farmerinven.apsola.data.model.Category
import com.farmerinven.apsola.data.repository.CategoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingCategory: Category? = null
)

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = true, editingCategory = null)
    }

    fun showEditDialog(category: Category) {
        _uiState.value = _uiState.value.copy(showAddDialog = true, editingCategory = category)
    }

    fun hideDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false, editingCategory = null)
    }

    fun saveCategory(name: String, iconName: String, colorHex: String) {
        viewModelScope.launch {
            val editingCategory = _uiState.value.editingCategory
            if (editingCategory != null) {
                val updatedCategory = editingCategory.copy(
                    name = name,
                    iconName = iconName,
                    colorHex = colorHex
                )
                categoryRepository.updateCategory(updatedCategory)
            } else {
                val newCategory = Category(
                    name = name,
                    iconName = iconName,
                    colorHex = colorHex
                )
                categoryRepository.insertCategory(newCategory)
            }
            hideDialog()
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }
}
