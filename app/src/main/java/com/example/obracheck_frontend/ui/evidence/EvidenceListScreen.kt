package com.example.obracheck_frontend.ui.evidence

import androidx.compose.animation.core.*
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
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import com.example.obracheck_frontend.model.domain.Evidence
import com.example.obracheck_frontend.viewmodel.EvidenceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Paleta ObraCheck consistente
private val Brand = Color(0xFF1F2A33)   // gris carbón
private val Accent = Color(0xFFF6C445)  // amarillo casco
private val Muted = Color(0xFF7B8AA0)   // gris etiquetas
private val Border = Color(0xFFD1D5DB)  // borde inputs
private val Bg = Color(0xFFF9FAFB)      // fondo
private val Success = Color(0xFF10B981) // verde para estado activo
private val Danger = Color(0xFFEF4444)  // rojo para eliminar
private val CardBg = Color.White        // fondo de cards
private val Purple = Color(0xFF8B5CF6)  // violeta para evidencias
private val Orange = Color(0xFFF97316)  // naranja para archivos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenceListScreen(
    progressId: Long,
    navController: NavHostController,
    viewModel: EvidenceViewModel
) {
    val evidences by viewModel.evidences.collectAsState()
    // Por ahora usamos un nombre fijo, luego puedes implementar la carga real
    val progressName = "Progreso #$progressId"
    var deleteTarget by remember { mutableStateOf<Evidence?>(null) }
    var showMenu by remember { mutableStateOf<Long?>(null) }
    var selectedImageId by remember { mutableStateOf<Long?>(null) }

    // Animación para el contador de evidencias
    val animatedCount by animateIntAsState(
        targetValue = evidences.size,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "count_animation"
    )

    LaunchedEffect(progressId) {
        viewModel.loadEvidencesByProgress(progressId)
        // Si tienes el método loadProgressName, descomenta esta línea:
        // viewModel.loadProgressName(progressId)
    }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Evidencias del Progreso",
                            color = Color.White,
                            fontSize = 18.sp,
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
                onClick = { navController.navigate("evidenceform/$progressId?editId=-1") },
                containerColor = Purple,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Nueva evidencia",
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
            // Header estadísticas súper bonito con animación
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
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
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
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = Purple,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Galería de Evidencias",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brand
                            )
                        }

                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(70.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Accent)
                        )

                        Spacer(Modifier.height(20.dp))

                        // Estadísticas mejoradas con iconos
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            EvidenceStatCard(
                                count = animatedCount,
                                label = "Total",
                                icon = Icons.Default.Collections,
                                color = Brand
                            )

                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Border.copy(alpha = 0.3f))
                            )

                            EvidenceStatCard(
                                count = evidences.count { it.contentType?.startsWith("image/") == true },
                                label = "Fotos",
                                icon = Icons.Default.PhotoCamera,
                                color = Success
                            )

                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Border.copy(alpha = 0.3f))
                            )

                            EvidenceStatCard(
                                count = evidences.count { it.contentType?.startsWith("video/") == true },
                                label = "Videos",
                                icon = Icons.Default.Videocam,
                                color = Orange
                            )
                        }
                    }
                }
            }

            // Separador visual con título para la sección de evidencias
            if (evidences.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Folder,
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Archivos de Evidencia",
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
            if (evidences.isEmpty()) {
                // Estado vacío súper bonito
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
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Icono animado
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Purple.copy(alpha = 0.1f))
                                    .border(
                                        2.dp,
                                        Purple.copy(alpha = 0.2f),
                                        RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = Purple
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                "Sin evidencias registradas",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brand,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "Toca el botón + para subir la primera evidencia de este progreso",
                                fontSize = 14.sp,
                                color = Muted,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )

                            Spacer(Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .height(3.dp)
                                    .width(60.dp)
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
                    items(evidences, key = { it.id }) { evidence ->
                        EvidenceCard(
                            evidence = evidence,
                            progressName = progressName,
                            onEdit = { navController.navigate("evidenceform/$progressId?editId=${evidence.id}") },
                            onDelete = { deleteTarget = evidence },
                            onViewImage = { id -> selectedImageId = id },
                            onMenuToggle = { evidenceId ->
                                showMenu = if (showMenu == evidenceId) null else evidenceId
                            },
                            isMenuOpen = showMenu == evidence.id
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    // Diálogo para mostrar imagen completa
    selectedImageId?.let { imageId ->
        Dialog(onDismissRequest = { selectedImageId = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Vista de Imagen",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brand
                        )
                        IconButton(
                            onClick = { selectedImageId = null },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Muted
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Estado local de carga/bitmap
                    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
                    var isLoading by remember { mutableStateOf(true) }
                    var failed by remember { mutableStateOf(false) }

                    // Limpia el bitmap al cerrar/cambiar de imagen
                    DisposableEffect(imageId) {
                        onDispose {
                            bitmap?.recycle()
                            bitmap = null
                        }
                    }

                    LaunchedEffect(imageId) {
                        isLoading = true
                        failed = false
                        try {
                            val bytes = viewModel.getImageBytes(imageId)

                            // Decodifica FUERA del Main (CPU-bound) y con downsampling
                            bitmap = withContext(Dispatchers.Default) {
                                bytes?.let { data ->
                                    // Opcional: calcula un inSampleSize simple para no cargar fotos gigantes
                                    val optsBounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                                    BitmapFactory.decodeByteArray(data, 0, data.size, optsBounds)

                                    val maxW = 1080  // ajusta al ancho que quieres mostrar
                                    val maxH = 1080  // ajusta a tu alto máximo de preview
                                    var sample = 1
                                    val outW = optsBounds.outWidth
                                    val outH = optsBounds.outHeight
                                    while (outW / sample > maxW || outH / sample > maxH) sample *= 2

                                    val optsDecode = BitmapFactory.Options().apply {
                                        inSampleSize = sample
                                        inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888
                                    }
                                    BitmapFactory.decodeByteArray(data, 0, data.size, optsDecode)
                                }
                            }

                            failed = (bitmap == null)
                        } catch (t: Throwable) {
                            failed = true
                            android.util.Log.e("EvidenceDialog", "Decode error", t)
                        } finally {
                            isLoading = false
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Border.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isLoading -> {
                                CircularProgressIndicator(color = Brand, strokeWidth = 3.dp)
                            }
                            failed || bitmap == null -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.BrokenImage,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = Danger
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text("No se pudo cargar la imagen", color = Muted, fontSize = 14.sp)
                                }
                            }
                            else -> {
                                bitmap?.let { bmp ->
                                    Image(
                                        bitmap = bmp.asImageBitmap(),
                                        contentDescription = "Evidencia",
                                        modifier = Modifier.fillMaxWidth(),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { selectedImageId = null },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Brand,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cerrar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Diálogo de eliminar súper bonito
    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Danger.copy(alpha = 0.1f))
                        .border(2.dp, Danger.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = Danger,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "¿Eliminar evidencia?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Brand,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¿Estás seguro de que deseas eliminar esta evidencia?",
                        fontSize = 16.sp,
                        color = Brand,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "El archivo '${target.originalFileName.let {
                            if (it.contains(".")) it.substringBeforeLast(".") else it
                        }}' se perderá permanentemente.",
                        fontSize = 14.sp,
                        color = Muted,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(40.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Danger.copy(alpha = 0.3f))
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = target.id
                        viewModel.deleteEvidence(id) { /* recarga automática */ }
                        deleteTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Danger,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Eliminar", fontWeight = FontWeight.Bold)
                    }
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
private fun EvidenceStatCard(
    count: Int,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.1f))
                .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$count",
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
fun EvidenceCard(
    evidence: Evidence,
    progressName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewImage: (Long) -> Unit,
    onMenuToggle: (Long) -> Unit,
    isMenuOpen: Boolean
) {
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
            // Barra superior con color según tipo de archivo
            val topBarColor = when {
                evidence.contentType?.startsWith("image/") == true -> Success
                evidence.contentType?.startsWith("video/") == true -> Orange
                evidence.contentType?.startsWith("application/pdf") == true -> Danger
                else -> Purple
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(topBarColor)
            )

            Column(modifier = Modifier.padding(20.dp)) {
                // Header con nombre y menú
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Icono del tipo de archivo
                            val fileIcon = when {
                                evidence.contentType?.startsWith("image/") == true -> Icons.Default.Image
                                evidence.contentType?.startsWith("video/") == true -> Icons.Default.VideoFile
                                evidence.contentType?.startsWith("application/pdf") == true -> Icons.Default.PictureAsPdf
                                else -> Icons.Default.InsertDriveFile
                            }

                            Icon(
                                fileIcon,
                                contentDescription = null,
                                tint = topBarColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            // Solo mostrar el nombre sin extensión
                            val fileName = if (evidence.originalFileName.contains(".")) {
                                evidence.originalFileName.substringBeforeLast(".")
                            } else {
                                evidence.originalFileName
                            }
                            Text(
                                text = fileName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brand,
                                lineHeight = 22.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Nombre del progreso
                        Text(
                            text = progressName,
                            fontSize = 12.sp,
                            color = Muted,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Línea de acento
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(40.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(topBarColor.copy(alpha = 0.3f))
                        )
                    }

                    // Menú de opciones mejorado
                    Box {
                        IconButton(
                            onClick = { onMenuToggle(evidence.id) },
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
                            expanded = isMenuOpen,
                            onDismissRequest = { onMenuToggle(evidence.id) },
                            offset = DpOffset(0.dp, 8.dp),
                            modifier = Modifier
                                .shadow(12.dp, RoundedCornerShape(16.dp), clip = false)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, Border.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                .background(CardBg)
                                .padding(vertical = 4.dp)
                        ) {
                            // Opción Ver imagen (solo para imágenes)
                            if (evidence.contentType?.startsWith("image/") == true) {
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = Success,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    text = {
                                        Text(
                                            "Ver Imagen",
                                            color = Success,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    onClick = {
                                        onMenuToggle(evidence.id)
                                        // Pasar el ID de la evidencia
                                        onViewImage(evidence.id)
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
                            }

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
                                    onMenuToggle(evidence.id)
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
                                    onMenuToggle(evidence.id)
                                    onDelete()
                                },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}