package com.example.eventplannerapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(viewModel: EventViewModel, selectedDate: String) {
    val events by viewModel.events.collectAsState()

    LaunchedEffect(selectedDate) {
        viewModel.loadEventsByDate(selectedDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Event Planner") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Tambah event baru (dummy)
                viewModel.addEvent("Meeting", "Discuss project", selectedDate)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(events) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(event.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(event.description, color = Color.Gray)
                        Text("Tanggal: ${event.date}", fontSize = 12.sp, color = Color.DarkGray)

                        Row {
                            TextButton(onClick = {
                                viewModel.updateEvent(event.copy(title = event.title + " (Updated)"))
                            }) {
                                Text("Edit")
                            }
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = { viewModel.deleteEvent(event) }) {
                                Text("Delete", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}