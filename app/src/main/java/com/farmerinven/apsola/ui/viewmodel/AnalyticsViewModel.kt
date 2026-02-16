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

data class CategoryStats(
    val category: Category,
    val itemCount: Int
)

data class AnalyticsUiState(
    val totalItems: Int = 0,
    val workingCount: Int = 0,
    val needsRepairCount: Int = 0,
    val outOfStockCount: Int = 0,
    val categoryStats: List<CategoryStats> = emptyList()
)

class AnalyticsViewModel(
    private val inventoryRepository: InventoryRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            combine(
                inventoryRepository.getAllItems(),
                categoryRepository.getAllCategories(),
                inventoryRepository.getItemCountByStatus(ItemStatus.WORKING),
                inventoryRepository.getItemCountByStatus(ItemStatus.NEEDS_REPAIR),
                inventoryRepository.getItemCountByStatus(ItemStatus.OUT_OF_STOCK)
            ) { items, categories, working, needsRepair, outOfStock ->
                val categoryStats = categories.map { category ->
                    CategoryStats(
                        category = category,
                        itemCount = items.count { it.categoryId == category.id }
                    )
                }.filter { it.itemCount > 0 }

                AnalyticsUiState(
                    totalItems = items.size,
                    workingCount = working,
                    needsRepairCount = needsRepair,
                    outOfStockCount = outOfStock,
                    categoryStats = categoryStats
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
