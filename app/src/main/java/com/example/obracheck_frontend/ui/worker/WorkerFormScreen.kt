package com.example.obracheck_frontend.ui.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.obracheck_frontend.model.dto.CreateWorkerRequestDto
import com.example.obracheck_frontend.viewmodel.WorkerViewModel


private val Brand = Color(0xFF1F2A33)   // gris carbón
private val Accent = Color(0xFFF6C445)  // amarillo casco
private val Muted = Color(0xFF7B8AA0)   // gris etiquetas
private val Border = Color(0xFFD1D5DB)  // borde inputs
private val Bg = Color(0xFFF9FAFB)      // fondo
private val Success = Color(0xFF10B981) // verde para estado activo
private val Danger = Color(0xFFEF4444)  // rojo para errores
private val CardBg = Color.White        // fondo de cards

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerFormScreen(
    siteId: Long,
    editId: Long? = null,
    viewModel: WorkerViewModel = WorkerViewModel(),
    onSubmitComplete: () -> Unit,
    onCancel: () -> Unit = {}
) {
    // Estado UI
    var name by remember { mutableStateOf(TextFieldValue()) }
    var role by remember { mutableStateOf(TextFieldValue()) }
    var ci by remember { mutableStateOf(TextFieldValue()) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var roleError by remember { mutableStateOf<String?>(null) }
    var ciError by remember { mutableStateOf<String?>(null) }
    var ciExistsError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val isEditing = editId != null

    // Precarga en modo edición
    LaunchedEffect(editId) {
        if (editId != null) {
            viewModel.getWorker(editId) { worker ->
                name = TextFieldValue(worker.name)
                role = TextFieldValue(worker.role)
                ci = TextFieldValue(worker.ci)
            }
        }
    }

    // Efecto para navegar después del éxito
    LaunchedEffect(showSuccessAnimation) {
        if (showSuccessAnimation) {
            kotlinx.coroutines.delay(1500)
            onSubmitComplete()
        }
    }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isEditing) Icons.Default.Edit else Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEditing) "Editar Trabajador" else "Nuevo Trabajador",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isLoading) {
                                // Si está cargando, detener el proceso
                                isLoading = false
                                showSuccessAnimation = false
                            }
                            // Siempre navegar de vuelta
                            onSubmitComplete()
                        },
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
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header con icono y descripción
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Construction,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Brand
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isEditing) "Actualizar Información" else "Registrar Nuevo Miembro",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brand,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(50.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Accent)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isEditing) "Modifica los datos del trabajador"
                            else "Completa la información para agregar un nuevo miembro al equipo",
                            fontSize = 14.sp,
                            color = Muted,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Formulario principal
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = Border,
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.elevatedCardColors(containerColor = CardBg),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Título de la sección
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Información Personal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brand
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Border.copy(alpha = 0.5f))
                        )
                    }

                    // Campo Nombre
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Nombre Completo *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Brand
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                if (nameError != null) nameError = null
                            },
                            placeholder = {
                                Text(
                                    "Ej. Juan Carlos Pérez",
                                    color = Muted.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (nameError != null) Danger else Brand,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            isError = nameError != null,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Brand,
                                unfocusedBorderColor = Border,
                                errorBorderColor = Danger,
                                focusedLabelColor = Brand,
                                unfocusedLabelColor = Muted,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        if (nameError != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Danger)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = nameError!!,
                                    color = Danger,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Campo Rol
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Rol en la Obra *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Brand
                        )

                        OutlinedTextField(
                            value = role,
                            onValueChange = {
                                role = it
                                if (roleError != null) roleError = null
                            },
                            placeholder = {
                                Text(
                                    "Ej. Maestro de Obra, Albañil, Electricista",
                                    color = Muted.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Work,
                                    contentDescription = null,
                                    tint = if (roleError != null) Danger else Brand,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            isError = roleError != null,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Brand,
                                unfocusedBorderColor = Border,
                                errorBorderColor = Danger,
                                focusedLabelColor = Brand,
                                unfocusedLabelColor = Muted,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        if (roleError != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Danger)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = roleError!!,
                                    color = Danger,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Campo CI
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Cédula de Identidad *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Brand
                        )

                        OutlinedTextField(
                            value = ci,
                            onValueChange = {
                                ci = it
                                if (ciError != null) ciError = null
                                ciExistsError = null
                            },
                            placeholder = {
                                Text(
                                    "Ej. 1234567890",
                                    color = Muted.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = if (ciError != null || ciExistsError != null) Danger else Brand,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            isError = ciError != null || ciExistsError != null,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Brand,
                                unfocusedBorderColor = Border,
                                errorBorderColor = Danger,
                                focusedLabelColor = Brand,
                                unfocusedLabelColor = Muted,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            )
                        )

                        if (ciError != null || ciExistsError != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Danger)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = ciError ?: ciExistsError ?: "",
                                    color = Danger,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Nota informativa
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
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Accent)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Los campos marcados con * son obligatorios para completar el registro del trabajador.",
                                fontSize = 12.sp,
                                color = Brand.copy(alpha = 0.8f),
                                lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Botones de acción (Cancelar y Guardar/Crear)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Cancelar
                OutlinedButton(
                    onClick = {
                        if (isLoading) {
                            // Si está cargando, detener el proceso y volver
                            isLoading = false
                            showSuccessAnimation = false
                        }
                        // Siempre navegar de vuelta a la lista
                        onSubmitComplete()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isLoading) Danger else Brand,
                        containerColor = Color.Transparent
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 2.dp,
                        color = if (isLoading) Danger else Brand
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            if (isLoading) Icons.Default.Stop else Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLoading) "Detener" else "Cancelar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botón Guardar/Crear
                Button(
                    onClick = {
                        // Validación
                        nameError = if (name.text.isBlank()) "El nombre es obligatorio" else null
                        roleError = if (role.text.isBlank()) "El rol es obligatorio" else null
                        ciError = if (ci.text.isBlank()) "La cédula es obligatoria" else null
                        ciExistsError = null

                        if (nameError == null && roleError == null && ciError == null && !isLoading) {
                            isLoading = true
                            val req = CreateWorkerRequestDto(
                                name = name.text.trim(),
                                role = role.text.trim(),
                                ci = ci.text.trim(),
                                site_id = siteId
                            )

                            val onOk: () -> Unit = {
                                isLoading = false
                                showSuccessAnimation = true
                            }
                            val onErr: (String) -> Unit = { msg ->
                                isLoading = false
                                ciExistsError = msg.ifBlank { "No se pudo guardar. Intenta otra vez." }
                            }

                            if (editId != null) {
                                viewModel.updateWorker(editId, req, onOk, onErr)
                            } else {
                                viewModel.createWorker(req, onOk, onErr)
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .shadow(
                            elevation = if (isLoading) 2.dp else 6.dp,
                            shape = RoundedCornerShape(16.dp),
                            clip = false
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showSuccessAnimation) Success else Brand,
                        contentColor = Color.White
                    ),
                    enabled = !isLoading
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        when {
                            isLoading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Procesando...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            showSuccessAnimation -> {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Completo",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            else -> {
                                Icon(
                                    if (isEditing) Icons.Default.SaveAs else Icons.Default.GroupAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEditing) "Modificar" else "Registrar",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Espaciado final
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}