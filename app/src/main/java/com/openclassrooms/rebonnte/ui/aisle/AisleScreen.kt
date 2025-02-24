package com.openclassrooms.rebonnte.ui.aisle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.startDetailActivity

@Composable
fun AisleScreen(viewModel: AisleViewModel) {
    val aisles by viewModel.aisles.collectAsState(initial = emptyList())
    val context = LocalContext.current

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(aisles) { aisle ->
            AisleItem(
                aisle = aisle,
                onClick = { startDetailActivity(context, aisle.name) },
                onDelete = { viewModel.deleteAisle(aisle) }
            )
        }
    }
}

@Composable
fun AisleItem(
    aisle: Aisle,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = aisle.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Row {
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .padding(end = 32.dp)

            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete aisle"
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Arrow"
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Delete Aisle") },
                text = { Text("Are you sure you want to delete ${aisle.name}?") },
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
}