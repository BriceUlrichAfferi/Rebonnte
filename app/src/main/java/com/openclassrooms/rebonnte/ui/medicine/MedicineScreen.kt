package com.openclassrooms.rebonnte.ui.medicine


import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material.rememberDismissState
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material3.MaterialTheme
import com.openclassrooms.rebonnte.ui.aisle.Aisle
import java.util.UUID

@Composable
fun MedicineScreen(
    navController: NavController,
    medicineViewModel: MedicineViewModel = koinViewModel()
) {

        val medicines by medicineViewModel.medicines.collectAsState(initial = emptyList())
        LaunchedEffect(medicines) {
            println("Medicines changed, forcing recomposition: $medicines")
        }

    BackHandler(enabled = true) {
        navController.navigate("aisle") {
            popUpTo(navController.graph.startDestinationId) { inclusive = false }
            launchSingleTop = true
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(medicines, key = {
            val medicineId = it.id ?: UUID.randomUUID().toString()
            medicineId
        }) { medicine ->
            SwipeToDeleteItem(
                onDelete = { medicineViewModel.deleteMedicine(medicine.id) }
            ) {
                val aisle = medicineViewModel.getAisleForMedicine(medicine.id)

                MedicineItem(
                    medicine = medicine,
                    aisle = aisle,
                    onClick = { navController.navigate("medicine_detail/${medicine.name}") }
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDeleteItem(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberDismissState()
    var showDialog by remember { mutableStateOf(false) }
    var resetSwipe by remember { mutableStateOf(false) }

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.isDismissed(DismissDirection.StartToEnd) ||
            dismissState.isDismissed(DismissDirection.EndToStart)
        ) {
            showDialog = true
        }
    }

    // Reset the swipe
    LaunchedEffect(resetSwipe) {
        if (resetSwipe) {
            dismissState.reset()
            resetSwipe = false
        }
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                resetSwipe = true
            },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this item?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDelete()
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    resetSwipe = true
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            val color = if (dismissState.targetValue == DismissValue.DismissedToStart ||
                dismissState.targetValue == DismissValue.DismissedToEnd
            ) Color.Red else MaterialTheme.colorScheme.surface

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        dismissContent = { content() }
    )
}



@Composable
fun MedicineItem(
    medicine: Medicine,
    aisle: Aisle,
    onClick: () -> Unit
) {
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
            Text(text = "Aisle: ${aisle.name}", color = Color.Gray, fontSize = 14.sp)
        }
    }
}
