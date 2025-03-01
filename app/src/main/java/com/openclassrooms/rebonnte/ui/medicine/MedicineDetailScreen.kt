package com.openclassrooms.rebonnte.ui.medicine

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.ui.history.History
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    name: String,
    viewModel: MedicineViewModel,
    onBack: () -> Unit
) {
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
    val medicine = medicines.find { it.name == name }
    val context = LocalContext.current

    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserEmail = currentUser?.email ?: "Unknown"
    val currentUserId = currentUser?.uid ?: "Unknown"

    LaunchedEffect(currentUser) {
        println("MedicineDetailScreen - UID: $currentUserId, Email: $currentUserEmail")
    }

    LaunchedEffect(medicines) {
        val updatedMedicine = medicines.find { it.name == name }
        println("MedicineDetailScreen updated medicine: $updatedMedicine")
    }

    if (name == "Unknown") {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine: $name") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (medicine == null) {
            Text(
                text = "Loading medicine details...",
                modifier = Modifier.padding(paddingValues).padding(16.dp)
            )
        } else {
            Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                TextField(
                    value = medicine.name,
                    onValueChange = {},
                    label = { Text("Name") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = medicine.nameAisle,
                    onValueChange = {},
                    label = { Text("Aisle") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = {
                        if (medicine.stock > 0) {
                            val newStock = medicine.stock - 1
                            val updatedHistories = medicine.histories.toMutableList().apply {
                                add(History(medicine.name, currentUserEmail, Date().toString(), "Decreased stock"))
                            }
                            viewModel.updateMedicine(medicine.copy(stock = newStock, histories = updatedHistories))
                            Toast.makeText(context, "Stock decreased", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Stock cannot go below 0", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Minus One")
                    }
                    TextField(
                        value = medicine.stock.toString(),
                        onValueChange = { newValue ->
                            val newStock = newValue.toIntOrNull() ?: medicine.stock
                            viewModel.updateMedicine(medicine.copy(stock = newStock))
                        },
                        label = { Text("Stock") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        val newStock = medicine.stock + 1
                        val updatedHistories = medicine.histories.toMutableList().apply {
                            add(History(medicine.name, currentUserEmail, Date().toString(), "Increased stock"))
                        }
                        viewModel.updateMedicine(medicine.copy(stock = newStock, histories = updatedHistories))
                        Toast.makeText(context, "Stock increased", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Plus One")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "History", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(medicine.histories) { history ->
                        HistoryItem(history = history)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(history: History) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = history.medicineName, fontWeight = FontWeight.Bold)
            Text(text = "User: ${history.userId}")
            Text(text = "Date: ${history.date}")
            Text(text = "Details: ${history.details}")
        }
    }
}