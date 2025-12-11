package com.example.eventplannerapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.eventplannerapp.models.Event
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EventViewModel(private val ops: EventOperations) : ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events = _events.asStateFlow()

    fun loadEventsByDate(date: String) {
        viewModelScope.launch {
            _events.value = ops.getEventsByDate(date)
        }
    }

    fun addEvent(title: String, desc: String, date: String) {
        viewModelScope.launch {
            ops.addEvent(title, desc, date)
            loadEventsByDate(date)

        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            ops.updateEvent(event)
            loadEventsByDate(event.date)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            ops.deleteEvent(event.id)
            loadEventsByDate(event.date)
            // HAPUS: loadAllEventDates()
        }
    }

}