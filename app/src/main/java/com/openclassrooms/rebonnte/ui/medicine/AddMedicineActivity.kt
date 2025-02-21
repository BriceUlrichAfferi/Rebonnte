package com.openclassrooms.rebonnte.ui.medicine

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import com.openclassrooms.rebonnte.utils.findActivity
import org.koin.androidx.compose.koinViewModel

class AddMedicineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RebonnteTheme {
                val medicineViewModel: MedicineViewModel = koinViewModel()
                val aisleViewModel: AisleViewModel = koinViewModel()
                AddMedicineScreen(
                    medicineViewModel = medicineViewModel,
                    aisleViewModel = aisleViewModel
                ) {
                    Toast.makeText(this, "Medicine added", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    medicineViewModel: MedicineViewModel,
    aisleViewModel: AisleViewModel,
    onMedicineAdded: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var selectedAisle by remember { mutableStateOf("") }
    val aisles by aisleViewModel.aisles.collectAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Medicine") },
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Medicine Name
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Medicine Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Stock
            TextField(
                value = stock,
                onValueChange = { if (it.all { char -> char.isDigit() }) stock = it },
                label = { Text("Stock") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Aisle Picker
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedAisle,
                    onValueChange = { /* Read-only; selection handled by dropdown */ },
                    label = { Text("Aisle Name") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor() // Anchor for dropdown positioning
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (aisles.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No aisles available") },
                            onClick = { /* Do nothing */ }
                        )
                    } else {
                        aisles.forEach { aisle ->
                            DropdownMenuItem(
                                text = { Text(aisle.name) },
                                onClick = {
                                    selectedAisle = aisle.name
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Add Button
            Button(
                onClick = {
                    if (name.isBlank() || stock.isBlank() || selectedAisle.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    } else {
                        val newMedicine = Medicine(
                            name = name,
                            stock = stock.toInt(),
                            nameAisle = selectedAisle
                        )
                        medicineViewModel.addMedicine(newMedicine)
                        onMedicineAdded()
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add Medicine")
            }
        }
    }
}