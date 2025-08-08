package com.example.obracheck_frontend.ui.site

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
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
import com.example.obracheck_frontend.model.dto.CreateSiteRequestDto
import com.example.obracheck_frontend.viewmodel.SiteViewModel

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
fun SiteFormScreen(
    siteViewModel: SiteViewModel,
    siteId: Long?,
    userId: Long,
    onSubmitComplete: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val isEditing = siteId != null

    // Cargar datos del sitio si es edición
    LaunchedEffect(siteId) {
        if (siteId != null) {
            siteViewModel.getSite(siteId) { site ->
                name = site.name
                address = site.address
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
                            if (isEditing) Icons.Default.Edit else Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEditing) "Editar Proyecto" else "Nuevo Proyecto",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onSubmitComplete,
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
                            Icons.Default.Engineering,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Brand
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isEditing) "Actualizar Información" else "Crear Nuevo Proyecto",
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
                            text = if (isEditing) "Modifica los datos del proyecto existente"
                            else "Completa la información para crear un nuevo proyecto",
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
                        Text(
                            text = "Información del Proyecto",
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
                            text = "Nombre del Proyecto *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Brand
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = null
                            },
                            placeholder = {
                                Text(
                                    "Ej. Construcción Torre Residencial",
                                    color = Muted.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.LocationCity,
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
                                // Texto negro cuando el usuario escribe
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

                    // Campo Dirección
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Dirección del Proyecto *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Brand
                        )

                        OutlinedTextField(
                            value = address,
                            onValueChange = {
                                address = it
                                addressError = null
                            },
                            placeholder = {
                                Text(
                                    "Ej. Av. Principal 123, Sector Norte",
                                    color = Muted.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Place,
                                    contentDescription = null,
                                    tint = if (addressError != null) Danger else Brand,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            isError = addressError != null,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Brand,
                                unfocusedBorderColor = Border,
                                errorBorderColor = Danger,
                                focusedLabelColor = Brand,
                                unfocusedLabelColor = Muted,
                                // Texto negro cuando el usuario escribe
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        if (addressError != null) {
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
                                    text = addressError!!,
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
                                text = "Los campos marcados con * son obligatorios para completar el registro del proyecto.",
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
                    onClick = onSubmitComplete,
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

                // Botón Guardar/Crear
                Button(
                    onClick = {
                        var hasError = false
                        if (name.isBlank()) {
                            nameError = "El nombre del proyecto es obligatorio"
                            hasError = true
                        }
                        if (address.isBlank()) {
                            addressError = "La dirección del proyecto es obligatoria"
                            hasError = true
                        }
                        if (hasError) return@Button

                        isLoading = true
                        val request = CreateSiteRequestDto(
                            name = name,
                            address = address,
                            user_id = userId
                        )

                        if (siteId == null) {
                            siteViewModel.createSite(userId, request) {
                                isLoading = false
                                showSuccessAnimation = true
                            }
                        } else {
                            siteViewModel.updateSite(userId, siteId, request) {
                                isLoading = false
                                showSuccessAnimation = true
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
                                    if (isEditing) Icons.Default.Edit else Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEditing) "Guardar" else "Crear",
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