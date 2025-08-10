package com.example.obracheck_frontend.ui.attendance

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.obracheck_frontend.model.dto.AttendanceStatus
import com.example.obracheck_frontend.viewmodel.AttendanceViewModel

// Paleta ObraCheck consistente
private val Brand = Color(0xFF1F2A33)   // gris carbón
private val Accent = Color(0xFFF6C445)  // amarillo casco
private val Muted = Color(0xFF7B8AA0)   // gris etiquetas
private val Border = Color(0xFFD1D5DB)  // borde inputs
private val Bg = Color(0xFFF9FAFB)      // fondo
private val Success = Color(0xFF10B981) // verde para presente
private val Danger = Color(0xFFEF4444)  // rojo para ausente
private val Warning = Color(0xFFF59E0B) // naranja para tardanza
private val CardBg = Color.White        // fondo de cards

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    siteId: Long,
    dateIso: String,                             // yyyy-MM-dd
    viewModel: AttendanceViewModel,              // pásalo desde tu NavHost
    onBack: () -> Unit = {},
    onNavigateToWorkerList: () -> Unit = {}      // función para navegar a WorkerList
) {
    val list by viewModel.attendances.collectAsStateWithLifecycle()

    // Estados para el botón de guardado
    val scope = rememberCoroutineScope()
    var hasChanges by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var showSaveSuccess by remember { mutableStateOf(false) }

    // Calcular estadísticas
    val totalWorkers = list.size
    val presentCount = list.count { it.state == AttendanceStatus.PRESENT }
    val absentCount = list.count { it.state == AttendanceStatus.ABSENT }
    val lateCount = list.count { it.state == AttendanceStatus.LATE }
    val naCount = list.count { it.state == AttendanceStatus.NA }

    LaunchedEffect(siteId, dateIso) {
        viewModel.loadAttendances(siteId, dateIso)
        hasChanges = false
    }

    // Efecto para navegar después de guardar
    LaunchedEffect(showSaveSuccess) {
        if (showSaveSuccess) {
            kotlinx.coroutines.delay(1500)
            showSaveSuccess = false
            onNavigateToWorkerList()
        }
    }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.EventAvailable,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Control de Asistencia",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Header con fecha y estadísticas con estilo ObraCheck
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
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Brand
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Registro del $dateIso",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brand
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(60.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Accent)
                        )

                        if (totalWorkers > 0) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Estadísticas en fila
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("$presentCount", "Presente", Success)

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(40.dp)
                                        .background(Border)
                                )

                                if (lateCount > 0) {
                                    StatItem("$lateCount", "Tardanza", Warning)
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(40.dp)
                                            .background(Border)
                                    )
                                }

                                StatItem("$absentCount", "Ausente", Danger)

                                if (naCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(40.dp)
                                            .background(Border)
                                    )
                                    StatItem("$naCount", "Sin registro", Muted)
                                }
                            }
                        }
                    }
                }
            }

            if (list.isEmpty()) {
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
                                Icons.Default.EventBusy,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Muted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Sin registros de asistencia",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brand
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No hay trabajadores registrados para esta fecha en el proyecto",
                                fontSize = 14.sp,
                                color = Muted,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))
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
                // Lista de asistencia
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // Header informativo
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Accent.copy(alpha = 0.1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    Accent.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Brand,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Toca el estado deseado para cada trabajador.",
                                    fontSize = 13.sp,
                                    color = Brand.copy(alpha = 0.8f),
                                    lineHeight = 18.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(list, key = { it.workerId }) { row ->
                        ImprovedAttendanceRowCard(
                            name = row.workerName,
                            ci = row.ci,
                            state = row.state,
                            onChange = { newState ->
                                hasChanges = true
                                // Guardado inmediato en backend
                                viewModel.updateAttendanceState(
                                    siteId = siteId,
                                    date = dateIso,
                                    workerId = row.workerId,
                                    newStatus = newState
                                )
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                // 🆕 BOTÓN FIJO INFERIOR con estilo ObraCheck
                if (hasChanges) {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(300)
                        ) + fadeIn(),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(300)
                        ) + fadeOut()
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            shape = RoundedCornerShape(20.dp),
                            shadowElevation = 8.dp
                        ) {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 3.dp,
                                        color = Brand,
                                        shape = RoundedCornerShape(20.dp)
                                    ),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = when {
                                        showSaveSuccess -> Success.copy(alpha = 0.1f)
                                        isSaving -> Brand.copy(alpha = 0.05f)
                                        else -> CardBg
                                    }
                                ),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Barra superior amarilla característica
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .background(
                                                when {
                                                    showSaveSuccess -> Success
                                                    else -> Accent
                                                }
                                            )
                                    )

                                    // Contenido del botón
                                    Button(
                                        onClick = {
                                            if (!isSaving) {
                                                isSaving = true
                                                scope.launch {
                                                    // Aquí guardarías en el backend
                                                    // viewModel.saveAllChanges(siteId, dateIso)
                                                    kotlinx.coroutines.delay(1000)
                                                    isSaving = false
                                                    hasChanges = false
                                                    showSaveSuccess = true
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp)
                                            .height(64.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = when {
                                                showSaveSuccess -> Success
                                                isSaving -> Brand.copy(alpha = 0.8f)
                                                else -> Brand
                                            },
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 4.dp,
                                            pressedElevation = 8.dp
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            when {
                                                isSaving -> {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(24.dp),
                                                        color = Color.White,
                                                        strokeWidth = 3.dp
                                                    )
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Text(
                                                        text = "Guardando Lista...",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                showSaveSuccess -> {
                                                    Icon(
                                                        Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(24.dp),
                                                        tint = Color.White
                                                    )
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Text(
                                                        text = "¡Lista Guardada Exitosamente!",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                else -> {
                                                    // 📁 ÍCONO DE ARCHIVO como solicitaste
                                                    Icon(
                                                        Icons.Default.Description, // Ícono de archivo/documento
                                                        contentDescription = null,
                                                        modifier = Modifier.size(24.dp),
                                                        tint = Color.White
                                                    )
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Text(
                                                        text = "Guardar Asistencia",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImprovedAttendanceRowCard(
    name: String,
    ci: String,
    state: AttendanceStatus,
    onChange: (AttendanceStatus) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Brand,
                shape = RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.elevatedCardColors(containerColor = CardBg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Barra superior amarilla característica MÁS GRUESA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Accent)
            )

            Column(modifier = Modifier.padding(20.dp)) {
                // Información del trabajador MEJORADA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar circular con icono
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Brand.copy(alpha = 0.1f),
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Brand,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brand,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Línea de acento amarilla
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(30.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Accent)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Badge,
                                contentDescription = null,
                                tint = Muted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "CI: $ci",
                                fontSize = 13.sp,
                                color = Muted,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Separador visual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Border.copy(alpha = 0.5f))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Título de la sección de estado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Estado de Asistencia",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brand
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Border.copy(alpha = 0.3f))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Selector de estado MEJORADO con disposición vertical
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Primera fila: Presente y Ausente
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ImprovedStateButton(
                            status = AttendanceStatus.PRESENT,
                            currentState = state,
                            onClick = onChange,
                            modifier = Modifier.weight(1f)
                        )
                        ImprovedStateButton(
                            status = AttendanceStatus.ABSENT,
                            currentState = state,
                            onClick = onChange,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Segunda fila: Tardanza y Sin registro
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ImprovedStateButton(
                            status = AttendanceStatus.LATE,
                            currentState = state,
                            onClick = onChange,
                            modifier = Modifier.weight(1f)
                        )
                        ImprovedStateButton(
                            status = AttendanceStatus.NA,
                            currentState = state,
                            onClick = onChange,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImprovedStateButton(
    status: AttendanceStatus,
    currentState: AttendanceStatus,
    onClick: (AttendanceStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = currentState == status
    val (color, icon, label) = when (status) {
        AttendanceStatus.NA -> Triple(Muted, Icons.Default.HelpOutline, "Sin registro")
        AttendanceStatus.PRESENT -> Triple(Success, Icons.Default.CheckCircle, "Presente")
        AttendanceStatus.ABSENT -> Triple(Danger, Icons.Default.Cancel, "Ausente")
        AttendanceStatus.LATE -> Triple(Warning, Icons.Default.AccessTime, "Tardanza")
    }

    Button(
        onClick = { onClick(status) },
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = if (isSelected) 4.dp else 1.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else CardBg,
            contentColor = if (isSelected) Color.White else color
        ),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = color.copy(alpha = 0.6f)
        ) else null,
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        ),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Muted,
            fontWeight = FontWeight.Medium
        )
    }
}