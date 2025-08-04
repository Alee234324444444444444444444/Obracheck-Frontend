package com.example.obracheck_frontend.ui.site

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.obracheck_frontend.model.domain.Site
import com.example.obracheck_frontend.viewmodel.SiteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteListScreen(
    userId: Long,
    siteViewModel: SiteViewModel,
    onNavigateToForm: (Long?) -> Unit,
    onNavigateToWorkers: (Long) -> Unit
) {
    val sites = siteViewModel.sites.collectAsState().value

    // Estados para diálogos
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var selectedSite by remember { mutableStateOf<Site?>(null) }

    LaunchedEffect(Unit) {
        siteViewModel.loadSitesByUser(userId)
    }

    val primaryGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF304FFE), Color(0xFF00BCD4))
    )
    val accentColor = Color(0xFF00BCD4)
    val backgroundColor = Color(0xFFF5F7FA)
    val cardColor = Color.White
    val textPrimary = Color(0xFF2C3E50)
    val textSecondary = Color(0xFF7F8C8D)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Apartment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mis Proyectos",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(primaryGradient)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToForm(null) },
                containerColor = accentColor,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar proyecto")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Header estadísticas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("${sites.size}", "Proyectos", Color(0xFF304FFE))
                    VerticalDivider(color = Color(0xFFE0E6ED), modifier = Modifier.height(40.dp))
                    StatItem("${sites.size}", "Activos", accentColor)
                }
            }

            // Lista de sitios
            if (sites.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Engineering,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color(0xFFBDC3C7)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No hay proyectos", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = textSecondary)
                    Text(
                        "Toca el botón + para crear tu primer proyecto",
                        fontSize = 14.sp,
                        color = textSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(sites) { site ->
                        ProjectCard(
                            site = site,
                            onEdit = {
                                selectedSite = site
                                showUpdateDialog = true
                            },
                            onDelete = {
                                selectedSite = site
                                showDeleteDialog = true
                            },
                            onWorkersClick = {
                                onNavigateToWorkers(site.id)
                            },
                                    cardColor = cardColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            accentColor = accentColor
                        )

                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    // Diálogo de eliminar
    if (showDeleteDialog && selectedSite != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFE53E3E),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "¿Eliminar sitio?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar el sitio '${selectedSite!!.name}'? Esta acción no se puede deshacer.",
                    fontSize = 16.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedSite?.let { site ->
                            siteViewModel.deleteSite(userId, site.id) {
                                showDeleteDialog = false
                                selectedSite = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53E3E),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = textSecondary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = cardColor,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Diálogo de actualizar
    if (showUpdateDialog && selectedSite != null) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            icon = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "¿Actualizar sitio?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            },
            text = {
                Text(
                    text = "¿Deseas actualizar la información del sitio '${selectedSite!!.name}'?",
                    fontSize = 16.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedSite?.let { site ->
                            onNavigateToForm(site.id)
                            showUpdateDialog = false
                            selectedSite = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Actualizar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showUpdateDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = textSecondary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = cardColor,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 12.sp, color = Color(0xFF7F8C8D), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ProjectCard(
    site: Site,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onWorkersClick: () -> Unit,
    cardColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
)
 {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Barra superior de color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Brush.horizontalGradient(colors = listOf(accentColor, Color(0xFF304FFE))))
            )

            Column(modifier = Modifier.padding(20.dp)) {
                // Header con nombre y menú
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = site.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            lineHeight = 24.sp
                        )
                    }

                    // Menú de opciones
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(cardColor, RoundedCornerShape(8.dp))
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = accentColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Actualizar", color = textPrimary)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = Color(0xFFE53E3E),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Eliminar", color = Color(0xFFE53E3E))
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dirección con icono
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Apartment,
                        contentDescription = null,
                        tint = textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = site.address,
                        fontSize = 14.sp,
                        color = textSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Badge de estado
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE8F5E8)
                ) {
                    Text(
                        text = "🟢 Activo",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D2E)
                    )


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        IconButton(
                            onClick = { onWorkersClick() },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Construction,
                                contentDescription = "Trabajadores",
                                tint = Color(0xFF2E7D2E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }




                }
            }
        }
    }
}



