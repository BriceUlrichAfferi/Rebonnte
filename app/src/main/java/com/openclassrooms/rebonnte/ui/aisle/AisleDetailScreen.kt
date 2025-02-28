package com.openclassrooms.rebonnte.ui.aisle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.openclassrooms.rebonnte.ui.medicine.Medicine
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleDetailScreen(
    name: String,
    viewModel: MedicineViewModel,
    navController: NavController
) {
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
    val filteredMedicines = medicines.filter { it.nameAisle == name }

    if (name == "Unknown") {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aisle: $name") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (filteredMedicines.isEmpty()) {
            Text(
                text = "No medicines in this aisle",
                modifier = Modifier.padding(paddingValues).padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues
            ) {
                items(filteredMedicines) { medicine ->
                    MedicineItem(medicine = medicine) { medicineName ->
                        navController.navigate("medicine_detail/$medicineName")
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineItem(
    medicine: Medicine,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(medicine.name) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = medicine.name, fontWeight = FontWeight.Bold)
            Text(text = "Stock: ${medicine.stock}", color = Color.Gray)
        }
        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Details")
    }
}