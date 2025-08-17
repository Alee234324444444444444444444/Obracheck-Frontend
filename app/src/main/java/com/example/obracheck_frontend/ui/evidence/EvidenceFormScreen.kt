package com.example.obracheck_frontend.ui.evidence

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.obracheck_frontend.viewmodel.EvidenceViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Paleta ObraCheck consistente
private val Brand = Color(0xFF1F2A33)   // gris carbón
private val Accent = Color(0xFFF6C445)  // amarillo casco
private val Muted = Color(0xFF7B8AA0)   // gris etiquetas
private val Border = Color(0xFFD1D5DB)  // borde inputs
private val Bg = Color(0xFFF9FAFB)      // fondo
private val Success = Color(0xFF10B981) // verde para estado activo
private val Danger = Color(0xFFEF4444)  // rojo para errores

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenceFormScreen(
    progressId: Long,
    editId: Long?,
    navController: NavHostController,
    viewModel: EvidenceViewModel
) {
    val context = LocalContext.current
    var pickedName by remember { mutableStateOf<String?>(null) }
    var pickedFile by remember { mutableStateOf<File?>(null) }
    var customName by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val isEditing = editId != null && editId >= 0

    // Generar nombre automático cuando se selecciona archivo
    LaunchedEffect(pickedName) {
        if (pickedName != null && customName.isBlank()) {
            val timestamp = SimpleDateFormat("dd-MM-yyyy_HH-mm", Locale.getDefault()).format(Date())
            customName = "Evidencia_$timestamp"
        }
    }

    // Efecto para navegar después del éxito
    LaunchedEffect(showSuccessAnimation) {
        if (showSuccessAnimation) {
            kotlinx.coroutines.delay(1500)
            navController.popBackStack()
        }
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val (name, file) = uriToTempFile(context, uri)
            if (file != null) {
                pickedName = name
                pickedFile = file
                error = null
            } else {
                error = "No se pudo leer el archivo seleccionado"
            }
        }
    }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isEditing) Icons.Default.Edit else Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEditing) "Actualizar Evidencia" else "Nueva Evidencia",
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
            // Header con información del progreso
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Brand
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isEditing) "Reemplazar Evidencia" else "Agregar Nueva Evidencia",
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

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Brand.copy(alpha = 0.1f),
                            modifier = Modifier.border(
                                1.dp,
                                Brand.copy(alpha = 0.3f),
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
                                        .background(Brand)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Progreso #$progressId",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Brand
                                )
                            }
                        }
                    }
                }
            }

            // Sección de selección de archivo
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = if (pickedFile != null) Success else Border,
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
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
                        Icon(
                            Icons.Default.Folder,
                            contentDescription = null,
                            tint = Brand,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Selección de Archivo",
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

                    // Botón de selección de archivo
                    Button(
                        onClick = { picker.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(16.dp),
                                clip = false
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (pickedFile != null) Success else Brand,
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                if (pickedFile != null) Icons.Default.CheckCircle else Icons.Default.Upload,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (pickedFile != null) "Archivo Seleccionado" else "Seleccionar Archivo",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Estado del archivo seleccionado
                    if (pickedFile != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Success.copy(alpha = 0.1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    Success.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Image,
                                    contentDescription = null,
                                    tint = Success,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = pickedName ?: "Archivo seleccionado",
                                    fontSize = 12.sp,
                                    color = Success,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    } else if (!isEditing) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Muted.copy(alpha = 0.1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    Muted.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Muted)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ningún archivo seleccionado",
                                    fontSize = 12.sp,
                                    color = Muted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Mensaje para edición
                    if (isEditing) {
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
                                    text = "Selecciona un nuevo archivo para reemplazar la evidencia existente.",
                                    fontSize = 12.sp,
                                    color = Brand.copy(alpha = 0.8f),
                                    lineHeight = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Sección de nombre personalizado
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = Border,
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
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
                        Icon(
                            Icons.Default.TextFields,
                            contentDescription = null,
                            tint = Brand,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nombre de la Evidencia",
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

                    // Campo de nombre personalizado
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Nombre personalizado *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Brand
                        )

                        OutlinedTextField(
                            value = customName,
                            onValueChange = {
                                customName = it
                                nameError = null
                            },
                            placeholder = {
                                Text(
                                    "Ej. Tuberia_edificio",
                                    color = Muted.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Edit,
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
                            )
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

                    // Nota informativa sobre el nombre
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
                                text = "Este nombre te ayudará a identificar fácilmente la evidencia en la lista. Usa nombres descriptivos y únicos.",
                                fontSize = 12.sp,
                                color = Brand.copy(alpha = 0.8f),
                                lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Mostrar errores
            if (error != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Danger.copy(alpha = 0.1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Danger.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Danger)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error!!,
                            fontSize = 12.sp,
                            color = Danger,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Cancelar
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Brand,
                        containerColor = Color.Transparent
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 2.dp,
                        color = Brand
                    ),
                    enabled = !isLoading
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cancelar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botón Guardar/Actualizar
                Button(
                    onClick = {
                        var hasError = false
                        if (customName.isBlank()) {
                            nameError = "El nombre de la evidencia es obligatorio"
                            hasError = true
                        }
                        if (pickedFile == null) {
                            error = "Debes seleccionar un archivo"
                            hasError = true
                        }
                        if (hasError) return@Button

                        isLoading = true
                        val file = pickedFile!!

                        // Crear archivo con nombre personalizado
                        val customFile = File(file.parent, "$customName.${file.extension}")
                        file.copyTo(customFile, overwrite = true)

                        if (!isEditing) {
                            viewModel.uploadEvidence(
                                file = customFile,
                                progressId = progressId,
                                onComplete = {
                                    isLoading = false
                                    showSuccessAnimation = true
                                    Toast.makeText(context, "Evidencia subida exitosamente", Toast.LENGTH_SHORT).show()
                                },
                                onError = { msg ->
                                    isLoading = false
                                    error = msg
                                }
                            )
                        } else {
                            viewModel.updateEvidence(
                                id = editId,
                                file = customFile,
                                onComplete = {
                                    isLoading = false
                                    showSuccessAnimation = true
                                    Toast.makeText(context, "Evidencia actualizada exitosamente", Toast.LENGTH_SHORT).show()
                                },
                                onError = { msg ->
                                    isLoading = false
                                    error = msg
                                }
                            )
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
                                    if (isEditing) Icons.Default.Edit else Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEditing) "Actualiza" else "Subir",
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

/** Convierte el Uri en un File temporal con extensión coherente (por defecto .jpg). */
private fun uriToTempFile(context: Context, uri: Uri): Pair<String?, File?> {
    return try {
        val cr = context.contentResolver
        val name = queryDisplayName(cr, uri) ?: "evidence.jpg"
        val mime = cr.getType(uri) ?: guessMimeByName(name) ?: "image/jpeg"
        val fixedName = ensureExt(name, mime)
        val temp = File(context.cacheDir, fixedName)
        cr.openInputStream(uri)?.use { input ->
            temp.outputStream().use { output -> input.copyTo(output) }
        } ?: return name to null
        name to temp
    } catch (_: Exception) {
        null to null
    }
}

private fun queryDisplayName(cr: ContentResolver, uri: Uri): String? {
    val cursor = cr.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null) ?: return null
    cursor.use {
        if (!it.moveToFirst()) return null
        val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        return if (idx >= 0) it.getString(idx) else null
    }
}

private fun guessMimeByName(name: String): String? {
    val ext = name.substringAfterLast('.', "").lowercase(Locale.ROOT)
    if (ext.isEmpty()) return null
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        ?: when (ext) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            else -> null
        }
}

private fun ensureExt(name: String, mime: String): String {
    val want = when (mime) {
        "image/jpeg" -> "jpg"
        "image/png"  -> "png"
        "image/webp" -> "webp"
        "image/gif"  -> "gif"
        else -> name.substringAfterLast('.', "")
    }
    if (want.isBlank()) return name
    val base = name.substringBeforeLast('.', name)
    val ext  = name.substringAfterLast('.', "")
    return if (ext.equals(want, true)) name else "$base.$want"
}