package com.farmerinven.apsola.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farmerinven.apsola.data.model.Reminder
import com.farmerinven.apsola.data.model.RepeatInterval
import com.farmerinven.apsola.data.repository.ReminderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ReminderUiState(
    val reminders: List<Reminder> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingReminder: Reminder? = null
)

class ReminderViewModel(
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            reminderRepository.getAllReminders().collect { reminders ->
                _uiState.value = _uiState.value.copy(reminders = reminders)
            }
        }
    }

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = true, editingReminder = null)
    }

    fun showEditDialog(reminder: Reminder) {
        _uiState.value = _uiState.value.copy(showAddDialog = true, editingReminder = reminder)
    }

    fun hideDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false, editingReminder = null)
    }

    fun saveReminder(
        title: String,
        description: String,
        dueDate: Long,
        repeatInterval: RepeatInterval
    ) {
        viewModelScope.launch {
            val editingReminder = _uiState.value.editingReminder
            if (editingReminder != null) {
                val updatedReminder = editingReminder.copy(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    repeatInterval = repeatInterval
                )
                reminderRepository.updateReminder(updatedReminder)
            } else {
                val newReminder = Reminder(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    repeatInterval = repeatInterval
                )
                reminderRepository.insertReminder(newReminder)
            }
            hideDialog()
        }
    }

    fun toggleReminderCompletion(reminder: Reminder) {
        viewModelScope.launch {
            val updatedReminder = reminder.copy(isCompleted = !reminder.isCompleted)
            reminderRepository.updateReminder(updatedReminder)
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminder)
        }
    }
}
