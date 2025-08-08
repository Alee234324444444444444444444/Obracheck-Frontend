package com.example.obracheck_frontend.ui.site

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.obracheck_frontend.model.domain.Site
import com.example.obracheck_frontend.viewmodel.SiteViewModel

// Paleta ObraCheck consistente
private val Brand = Color(0xFF1F2A33)   // gris carbón
private val Accent = Color(0xFFF6C445)  // amarillo casco
private val Muted = Color(0xFF7B8AA0)   // gris etiquetas
private val Border = Color(0xFFD1D5DB)  // borde inputs
private val Bg = Color(0xFFF9FAFB)      // fondo
private val Success = Color(0xFF10B981) // verde para estado activo
private val Danger = Color(0xFFEF4444)  // rojo para eliminar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteListScreen(
    userId: Long,
    siteViewModel: SiteViewModel,
    onNavigateToForm: (Long?) -> Unit,
    onNavigateToWorkers: (Long) -> Unit
) {
    val sites = siteViewModel.sites.collectAsState().value

    // Estado solo para diálogo de eliminar
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedSite by remember { mutableStateOf<Site?>(null) }

    LaunchedEffect(Unit) {
        siteViewModel.loadSitesByUser(userId)
    }

    Scaffold(
        containerColor = Bg,
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
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Brand
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToForm(null) },
                containerColor = Brand,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar proyecto",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Header estadísticas con estilo ObraCheck
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .border(
                        width = 2.dp,
                        color = Brand,
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Resumen de Proyectos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brand
                    )

                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .width(60.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Accent)
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("${sites.size}", "Total", Brand)
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(Border)
                        )
                        StatItem("${sites.size}", "Activos", Success)
                    }
                }
            }

            // Lista de sitios
            if (sites.isEmpty()) {
                // Estado vacío con estilo ObraCheck
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = Border,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Engineering,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Muted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No hay proyectos",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brand
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Toca el botón + para crear tu primer proyecto",
                                fontSize = 14.sp,
                                color = Muted,
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .height(3.dp)
                                    .width(50.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Accent)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(sites) { site ->
                        ProjectCard(
                            site = site,
                            onEdit = {
                                // Va directo al formulario para editar
                                onNavigateToForm(site.id)
                            },
                            onDelete = {
                                selectedSite = site
                                showDeleteDialog = true
                            },
                            onWorkersClick = {
                                onNavigateToWorkers(site.id)
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    // Solo diálogo de eliminar con estilo ObraCheck
    if (showDeleteDialog && selectedSite != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = Danger,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "¿Eliminar proyecto?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Brand
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¿Estás seguro de que deseas eliminar el proyecto '${selectedSite!!.name}'?",
                        fontSize = 16.sp,
                        color = Brand,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Esta acción no se puede deshacer.",
                        fontSize = 14.sp,
                        color = Muted,
                        textAlign = TextAlign.Center
                    )
                }
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
                        containerColor = Danger,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Brand
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Cancelar", fontWeight = FontWeight.Medium)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Muted,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ProjectCard(
    site: Site,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onWorkersClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Brand,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Barra superior amarilla característica
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Accent)
            )

            Column(modifier = Modifier.padding(20.dp)) {
                // Header con nombre y menú
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = site.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brand,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Línea de acento pequeña
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(30.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Accent)
                        )
                    }

                    // Menú de opciones con estilo mejorado
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = Muted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            offset = DpOffset(0.dp, 8.dp),
                            modifier = Modifier
                                .shadow(10.dp, RoundedCornerShape(16.dp), clip = false)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, Border.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(vertical = 4.dp)
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Brand,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                text = {
                                    Text(
                                        "Actualizar",
                                        color = Brand,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )

                            HorizontalDivider(
                                color = Border.copy(alpha = 0.6f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Danger,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                text = {
                                    Text(
                                        "Eliminar",
                                        color = Danger,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dirección con icono
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Muted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = site.address,
                        fontSize = 14.sp,
                        color = Brand.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fila inferior con badge y botón
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge de estado con estilo ObraCheck
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Success.copy(alpha = 0.1f),
                        modifier = Modifier.border(
                            1.dp,
                            Success.copy(alpha = 0.3f),
                            RoundedCornerShape(20.dp)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Success)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Activo",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Success
                            )
                        }
                    }

                    // Botón de trabajadores con estilo ObraCheck
                    Button(
                        onClick = { onWorkersClick() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Brand,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Construction,
                            contentDescription = "Trabajadores",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Equipo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}