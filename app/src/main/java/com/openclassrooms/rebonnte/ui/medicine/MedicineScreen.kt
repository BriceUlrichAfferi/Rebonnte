package com.openclassrooms.rebonnte.ui.medicine

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun MedicineScreen(
    navController: NavController, // Add NavController parameter
    medicineViewModel: MedicineViewModel = koinViewModel(),
    aisleViewModel: AisleViewModel = koinViewModel()
) {
    val medicines by medicineViewModel.medicines.collectAsState(initial = emptyList())

    // Handle back press to navigate to "aisle"
    BackHandler(enabled = true) {
        navController.navigate("aisle") {
            // Clear the back stack up to "aisle" to avoid stacking multiple instances
            popUpTo(navController.graph.startDestinationId) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(medicines) { medicine ->
            MedicineItem(
                medicine = medicine,
                onClick = { navController.navigate("medicine_detail/${medicine.name}") },
                onDelete = { medicineViewModel.deleteMedicine(medicine.id) }
            )
        }
    }
}

@Composable
fun MedicineItem(
    medicine: Medicine,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = medicine.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Stock: ${medicine.stock}", color = Color.Gray, fontSize = 14.sp)
            Text(text = "Aisle: ${medicine.nameAisle}", color = Color.Gray, fontSize = 14.sp)
        }
        IconButton(onClick = { showDialog = true }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Medicine", tint = Color.Red)
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Medicine") },
            text = { Text("Are you sure you want to delete ${medicine.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}