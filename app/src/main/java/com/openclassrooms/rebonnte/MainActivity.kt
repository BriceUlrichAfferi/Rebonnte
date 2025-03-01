package com.openclassrooms.rebonnte

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.openclassrooms.rebonnte.bottom_navigation.BottomNavBar
import com.openclassrooms.rebonnte.route.appNavigation
import com.openclassrooms.rebonnte.sign_in.EmailAuthClient
import com.openclassrooms.rebonnte.sign_in.GoogleAuthUiClient
import com.openclassrooms.rebonnte.ui.aisle.AisleDetailScreen
import com.openclassrooms.rebonnte.ui.aisle.AisleScreen
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.medicine.AddMedicineScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineDetailScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private val emailAuthClient by lazy { EmailAuthClient() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp(
                googleAuthUiClient = googleAuthUiClient,
                emailAuthClient = emailAuthClient,
                lifecycleScope = lifecycleScope
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(
    googleAuthUiClient: GoogleAuthUiClient,
    emailAuthClient: EmailAuthClient,
    lifecycleScope: LifecycleCoroutineScope
) {
    val navController = rememberNavController()
    val medicineViewModel: MedicineViewModel = koinViewModel()
    val aisleViewModel: AisleViewModel = koinViewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    RebonnteTheme {
        Scaffold(
            topBar = {
                if (currentRoute in listOf("aisle", "medicine")) {
                    var isSearchActive by rememberSaveable { mutableStateOf(false) }
                    var searchQuery by remember { mutableStateOf("") }

                    Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                        TopAppBar(
                            title = { if (currentRoute == "aisle") Text("Aisle") else Text("Medicines") },
                            actions = {
                                var expanded by remember { mutableStateOf(false) }
                                if (currentRoute == "medicine") {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(end = 8.dp)
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Box {
                                            IconButton(onClick = { expanded = true }) {
                                                Icon(Icons.Default.MoreVert, contentDescription = null)
                                            }
                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false },
                                                offset = DpOffset(x = 0.dp, y = 0.dp)
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text("Sort by None") },
                                                    onClick = {
                                                        medicineViewModel.sortByNone()
                                                        expanded = false
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Sort by Name") },
                                                    onClick = {
                                                        medicineViewModel.sortByName()
                                                        expanded = false
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Sort by Stock") },
                                                    onClick = {
                                                        medicineViewModel.sortByStock()
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        )
                        if (currentRoute == "medicine") {
                            EmbeddedSearchBar(
                                query = searchQuery,
                                onQueryChange = { newQuery ->
                                    medicineViewModel.filterByName(newQuery)
                                    searchQuery = newQuery
                                },
                                isSearchActive = isSearchActive,
                                onActiveChanged = { isSearchActive = it }
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (currentRoute in listOf("aisle", "medicine")) {
                    BottomNavBar(
                        navController = navController,
                        currentRoute = currentRoute,
                        googleAuthUiClient = googleAuthUiClient,
                        emailAuthClient = emailAuthClient,
                        aisleViewModel = aisleViewModel,
                        onSignOut = { /* ... */ }
                    )
                }
            },
            floatingActionButton = {
                if (currentRoute in listOf("aisle", "medicine")) {
                    FloatingActionButton(onClick = {
                        if (currentRoute == "medicine") {
                            navController.navigate("add_medicine")
                        } else if (currentRoute == "aisle") {
                            aisleViewModel.addRandomAisle()
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                startDestination = "sign_in"
            ) {
                appNavigation(navController, googleAuthUiClient, emailAuthClient, lifecycleScope)
                composable("aisle") { AisleScreen(navController, aisleViewModel) }
                composable("medicine") { MedicineScreen(navController, medicineViewModel) }
                composable("aisle_detail/{aisleName}") { backStackEntry ->
                    val aisleName = backStackEntry.arguments?.getString("aisleName") ?: "Unknown"
                    AisleDetailScreen(
                        name = aisleName,
                        viewModel = medicineViewModel,
                        navController = navController
                    )
                }
                composable("medicine_detail/{medicineName}") { backStackEntry ->
                    val medicineName = backStackEntry.arguments?.getString("medicineName") ?: "Unknown"
                    MedicineDetailScreen(
                        name = medicineName,
                        viewModel = medicineViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("add_medicine") {
                    AddMedicineScreen(
                        navController = navController,
                        medicineViewModel = medicineViewModel,
                        aisleViewModel = aisleViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun EmbeddedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf(query) }
    val activeChanged: (Boolean) -> Unit = { active ->
        searchQuery = ""
        onQueryChange("")
        onActiveChanged(active)
    }
    val shape: Shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier.fillMaxWidth().height(48.dp).padding(horizontal = 16.dp)
            .clip(shape).background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSearchActive) {
            IconButton(onClick = { activeChanged(false) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        BasicTextField(
            value = searchQuery,
            onValueChange = { newQuery ->
                searchQuery = newQuery
                onQueryChange(newQuery)
            },
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                innerTextField()
            }
        )
        if (isSearchActive && searchQuery.isNotEmpty()) {
            IconButton(onClick = {
                searchQuery = ""
                onQueryChange("")
            }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}