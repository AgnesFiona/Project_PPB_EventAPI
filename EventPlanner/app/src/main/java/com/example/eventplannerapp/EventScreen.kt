package com.example.eventplannerapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventplannerapp.models.Event
import java.time.Instant
import java.time.ZoneId
import androidx.compose.ui.Alignment
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(viewModel: EventViewModel) {
    // State untuk tanggal yang dipilih user
    var selectedDate by remember { mutableStateOf(LocalDate.now().toString()) }

    val events by viewModel.events.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Event?>(null) }
    var showEditDialog by remember { mutableStateOf<Event?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    // setiap kali selectedDate berubah, load event baru
    LaunchedEffect(selectedDate) {
        viewModel.loadEventsByDate(selectedDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LastMinute.ly") },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Event")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            items(events) { event ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(event.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(event.description, color = Color.Gray)
                        Text("Tanggal: ${event.date}", fontSize = 12.sp, color = Color.DarkGray)

                        Row {
                            TextButton(onClick = { showEditDialog = event }) { Text("Edit") }
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = { showDeleteDialog = event }) { Text("Delete", color = Color.Red) }
                        }
                    }
                }
            }
        }
    }

    // Dialog Tambah Event → pakai selectedDate yang bisa berubah
    if (showAddDialog) {
        AddEventDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { t, d, date -> viewModel.addEvent(t, d, date) },
            selectedDate = selectedDate
        )
    }

    // Konfirmasi Hapus
    showDeleteDialog?.let { event ->
        DeleteConfirmDialog(onDismiss = { showDeleteDialog = null }, onConfirm = {
            viewModel.deleteEvent(event)
        })
    }

    // Edit Event
    showEditDialog?.let { event ->
        EditEventDialog(event, onDismiss = { showEditDialog = null }, onUpdate = {
            viewModel.updateEvent(it)
        })
    }

    // DatePicker → update selectedDate
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                selectedDate = date   // update state
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showAddDialog) {
        AddEventDialog(onDismiss = { showAddDialog = false }, onAdd = { t, d, date ->
            viewModel.addEvent(t, d, date)
        }, selectedDate = selectedDate)
    }

    showDeleteDialog?.let { event ->
        DeleteConfirmDialog(onDismiss = { showDeleteDialog = null }, onConfirm = {
            viewModel.deleteEvent(event)
        })
    }

    showEditDialog?.let { event ->
        EditEventDialog(event, onDismiss = { showEditDialog = null }, onUpdate = {
            viewModel.updateEvent(it)
        })
    }

    if (showDatePicker) {
        DatePickerDialog(onDateSelected = { date ->
            viewModel.loadEventsByDate(date)
        }, onDismiss = { showDatePicker = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit,
    selectedDate: String
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(selectedDate) }

    val showDatePicker = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Event") },
        text = {
            Column {
                TextField(value = title, onValueChange = { title = it }, label = { Text("Judul") })
                TextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") })

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tanggal: $date", fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { showDatePicker.value = true }) {
                        Text("Pilih Tanggal")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank()) {
                    onAdd(title, desc, date)
                    onDismiss()
                }
            }) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )

    // DatePicker dialog muncul jika user klik "Pilih Tanggal"
    if (showDatePicker.value) {
        DatePickerDialog(
            onDateSelected = { selected ->
                date = selected
            },
            onDismiss = { showDatePicker.value = false }
        )
    }
}

@Composable
fun DeleteConfirmDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Hapus") },
        text = { Text("Apakah kamu yakin ingin menghapus event ini?") },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) { Text("Ya") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Tidak") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventDialog(event: Event, onDismiss: () -> Unit, onUpdate: (Event) -> Unit) {
    var title by remember { mutableStateOf(event.title) }
    var desc by remember { mutableStateOf(event.description) }
    var date by remember { mutableStateOf(event.date) }

    val showDatePicker = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Event") },
        text = {
            Column {
                TextField(value = title, onValueChange = { title = it }, label = { Text("Judul") })
                TextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") })

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tanggal: $date", fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { showDatePicker.value = true }) {
                        Text("Pilih Tanggal")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onUpdate(event.copy(title = title, description = desc, date = date))
                onDismiss()
            }) { Text("Update") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )

    // DatePicker dialog muncul jika user klik "Pilih Tanggal"
    if (showDatePicker.value) {
        DatePickerDialog(
            onDateSelected = { selected ->
                date = selected
            },
            onDismiss = { showDatePicker.value = false }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(onDateSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Tanggal") },
        text = {
            DatePicker(state = datePickerState)
        },
        confirmButton = {
            TextButton(onClick = {
                val millis = datePickerState.selectedDateMillis
                millis?.let {
                    val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    onDateSelected(date.toString()) // format YYYY-MM-DD
                }
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}