package com.example.grindlog.presentation.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grindlog.data.local.entity.Reminder
import com.example.grindlog.data.repository.ReminderRepository
import com.example.grindlog.domain.notification.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    val reminders: StateFlow<List<Reminder>> = reminderRepository.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val upcomingReminders: StateFlow<List<Reminder>> = reminderRepository.getUpcomingReminders(Date())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun showAddReminderDialog() {
        _uiState.value = _uiState.value.copy(showAddReminderDialog = true)
    }

    fun hideAddReminderDialog() {
        _uiState.value = _uiState.value.copy(
            showAddReminderDialog = false,
            reminderTitle = "",
            reminderDescription = "",
            selectedDate = null,
            selectedTime = "09:00",
            selectedPlatform = "",
            isContestReminder = false,
            notifyBefore = 60
        )
    }

    fun updateReminderTitle(title: String) {
        _uiState.value = _uiState.value.copy(reminderTitle = title)
    }

    fun updateReminderDescription(description: String) {
        _uiState.value = _uiState.value.copy(reminderDescription = description)
    }

    fun updateSelectedDate(date: Date) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun updateSelectedTime(time: String) {
        _uiState.value = _uiState.value.copy(selectedTime = time)
    }

    fun updateSelectedPlatform(platform: String) {
        _uiState.value = _uiState.value.copy(selectedPlatform = platform)
    }

    fun updateIsContestReminder(isContest: Boolean) {
        _uiState.value = _uiState.value.copy(isContestReminder = isContest)
    }

    fun updateNotifyBefore(minutes: Int) {
        _uiState.value = _uiState.value.copy(notifyBefore = minutes)
    }

    fun saveReminder() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.reminderTitle.isNotBlank() && state.selectedDate != null) {
                val dateTime = combineDateTime(state.selectedDate!!, state.selectedTime)

                val reminder = Reminder(
                    title = state.reminderTitle,
                    description = state.reminderDescription,
                    dateTime = dateTime,
                    isContestReminder = state.isContestReminder,
                    platform = if (state.isContestReminder) state.selectedPlatform else null
                )
                val reminderId = reminderRepository.insertReminder(reminder)

                notificationScheduler.scheduleReminderWithDelay(
                    reminder.copy(id = reminderId),
                    state.notifyBefore
                )

                hideAddReminderDialog()
            }
        }
    }

    private fun combineDateTime(date: Date, time: String): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val timeParts = time.split(":")
        val hour = timeParts[0].toIntOrNull() ?: 9
        val minute = timeParts[1].toIntOrNull() ?: 0

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminder)
            notificationScheduler.cancelReminder(reminder.id)
        }
    }

    fun toggleReminderActive(reminder: Reminder) {
        viewModelScope.launch {
            val updatedReminder = reminder.copy(isActive = !reminder.isActive)
            reminderRepository.updateReminder(updatedReminder)

            if (updatedReminder.isActive) {
                notificationScheduler.scheduleReminderWithDelay(updatedReminder, 60)
            } else {
                notificationScheduler.cancelReminder(updatedReminder.id)
            }
        }
    }
}

data class ReminderUiState(
    val showAddReminderDialog: Boolean = false,
    val reminderTitle: String = "",
    val reminderDescription: String = "",
    val selectedDate: Date? = null,
    val selectedTime: String = "09:00",
    val selectedPlatform: String = "",
    val isContestReminder: Boolean = false,
    val notifyBefore: Int = 60
)
