package com.openclassrooms.rebonnte.ui.medicine

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import com.openclassrooms.rebonnte.utils.findActivity // Import the shared function
import org.koin.androidx.compose.koinViewModel
import java.util.Date

class MedicineDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("nameMedicine") ?: "Unknown"
        setContent {
            RebonnteTheme {
                val viewModel: MedicineViewModel = koinViewModel()
                MedicineDetailScreen(name, viewModel) {
                    Toast.makeText(this, "Invalid medicine name", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(name: String, viewModel: MedicineViewModel, onError: () -> Unit) {
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
    val context = LocalContext.current

    // Find medicine or wait for it to appear in real-time
    val medicine = medicines.find { it.name == name }
    var stock by remember(medicine) { mutableStateOf(medicine?.stock ?: 0) }
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown"
    // Only trigger error if name is "Unknown"
    if (name == "Unknown") {
        LaunchedEffect(Unit) { onError() }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine: $name") },
                navigationIcon = {
                    IconButton(onClick = { context.findActivity().finish() }) {
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
            // Show loading or placeholder while waiting for real-time update
            Text(
                text = "Loading medicine details...",
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = {
                        if (stock > 0) {
                            stock--
                            val updatedHistories = medicine.histories.toMutableList().apply {
                                add(History(medicine.name, medicine.addedByEmail, Date().toString(), "Decreased stock"))
                            }
                            viewModel.updateMedicine(medicine.copy(stock = stock, histories = updatedHistories))
                            Toast.makeText(context, "Stock decreased", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Stock cannot go below 0", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Minus One")
                    }
                    TextField(
                        value = stock.toString(),
                        onValueChange = { newValue -> stock = newValue.toIntOrNull() ?: stock },
                        label = { Text("Stock") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        stock++
                        val updatedHistories = medicine.histories.toMutableList().apply {
                            add(History(medicine.name, medicine.addedByEmail, Date().toString(), "Increased stock"))
                        }
                        viewModel.updateMedicine(medicine.copy(stock = stock, histories = updatedHistories))
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