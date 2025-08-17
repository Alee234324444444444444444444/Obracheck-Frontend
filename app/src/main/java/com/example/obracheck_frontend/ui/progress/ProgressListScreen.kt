package com.example.obracheck_frontend.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.obracheck_frontend.model.domain.Progress
import com.example.obracheck_frontend.viewmodel.ProgressViewModel

// Paleta ObraCheck consistente
private val Brand = Color(0xFF1F2A33)   // gris carbón
private val Accent = Color(0xFFF6C445)  // amarillo casco
private val Muted = Color(0xFF7B8AA0)   // gris etiquetas
private val Border = Color(0xFFD1D5DB)  // borde inputs
private val Bg = Color(0xFFF9FAFB)      // fondo
private val Danger = Color(0xFFEF4444)  // rojo para eliminar
private val CardBg = Color.White        // fondo de cards

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressListScreen(
    siteId: Long,
    workerId: Long,
    navController: NavController,
    viewModel: ProgressViewModel
) {
    val progresses by viewModel.progresses.collectAsState()
    var deleteTarget by remember { mutableStateOf<Progress?>(null) }

    LaunchedEffect(siteId, workerId) {
        viewModel.loadProgressesByWorker(siteId, workerId)
    }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Progreso del Trabajador",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Brand)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("progressform/$siteId/$workerId") },
                containerColor = Brand,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Nuevo progreso",
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
                colors = CardDefaults.elevatedCardColors(containerColor = CardBg),
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Resumen de Progreso",
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

                        // Solo mostrar cantidad de progresos
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${progresses.size}",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brand
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Progresos",
                                fontSize = 16.sp,
                                color = Muted,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Separador visual con título para la sección de progresos
            if (progresses.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Historial de Progresos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brand
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Border.copy(alpha = 0.5f))
                    )
                }
            }

            // Lista o mensaje vacío
            if (progresses.isEmpty()) {
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
                        colors = CardDefaults.elevatedCardColors(containerColor = CardBg),
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
                                Icons.Default.Assignment,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Muted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Sin reportes de progreso",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brand
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Toca el botón + para crear el primer progreso",
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
                    items(progresses, key = { it.id }) { progress ->
                        ProgressCard(
                            progress = progress,
                            navController = navController,
                            onEdit = {
                                navController.navigate("progressform/$siteId/$workerId?editId=${progress.id}")
                            },
                            onDelete = { deleteTarget = progress }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    // Diálogo de eliminar con estilo ObraCheck
    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
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
                    text = "¿Eliminar progreso?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Brand
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¿Estás seguro de que deseas eliminar este progreso?",
                        fontSize = 16.sp,
                        color = Brand,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Se perderán todas las evidencias asociadas.",
                        fontSize = 14.sp,
                        color = Muted,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = deleteTarget!!.id
                        viewModel.deleteProgress(id) {
                            viewModel.loadProgressesByWorker(siteId, workerId)
                        }
                        deleteTarget = null
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
                    onClick = { deleteTarget = null },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Brand
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Cancelar", fontWeight = FontWeight.Medium)
                }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun ProgressCard(
    progress: Progress,
    navController: NavController,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
        colors = CardDefaults.elevatedCardColors(containerColor = CardBg),
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
                // Header con descripción y menú
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = progress.description,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brand,
                            lineHeight = 24.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
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
                                .background(CardBg)
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

                // Información del progreso con iconos
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Muted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Fecha: ${progress.date.take(10)}",
                            fontSize = 14.sp,
                            color = Brand.copy(alpha = 0.8f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Muted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Obra: ${progress.site.name}",
                            fontSize = 14.sp,
                            color = Brand.copy(alpha = 0.8f),
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Muted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Encargado: ${progress.worker.name}",
                            fontSize = 14.sp,
                            color = Brand.copy(alpha = 0.8f),
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fila inferior con botón de evidencias - MOVIDO A LA IZQUIERDA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start, // Cambiado de End a Start
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón de evidencias con el color Brand (mismo que el botón de crear progreso)
                    Button(
                        onClick = {
                            // Aquí puedes navegar a las evidencias si tienes esa funcionalidad
                            navController.navigate("evidencelist/${progress.id}")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Brand, // Cambiado de EvidenceGreen a Brand
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Evidencias",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Evidencias",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}