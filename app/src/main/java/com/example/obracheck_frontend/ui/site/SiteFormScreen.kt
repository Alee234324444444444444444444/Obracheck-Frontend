package com.example.obracheck_frontend.ui.site

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.obracheck_frontend.model.dto.CreateSiteRequestDto
import com.example.obracheck_frontend.viewmodel.SiteViewModel

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

    val primaryGradient = Brush.horizontalGradient(
        listOf(Color(0xFF304FFE), Color(0xFF00BCD4))
    )
    val backgroundColor = Color(0xFFF5F7FA)
    val cardColor = Color.White
    val accentColor = Color(0xFF00BCD4)
    val textPrimary = Color(0xFF2C3E50)
    val errorColor = Color(0xFFD32F2F)

    LaunchedEffect(siteId) {
        if (siteId != null) {
            siteViewModel.getSite(siteId) { site ->
                name = site.name
                address = site.address
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (siteId == null) "Crear Proyecto" else "Editar Proyecto",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onSubmitComplete) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(primaryGradient)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Datos del proyecto",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (nameError != null) {
                        Text(
                            text = nameError ?: "",
                            color = errorColor,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        )
                    }
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = null
                        },
                        label = { Text("Nombre del sitio") },
                        leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                        isError = nameError != null,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (addressError != null) {
                        Text(
                            text = addressError ?: "",
                            color = errorColor,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        )
                    }
                    OutlinedTextField(
                        value = address,
                        onValueChange = {
                            address = it
                            addressError = null
                        },
                        label = { Text("Dirección del sitio") },
                        leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                        isError = addressError != null,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            var hasError = false
                            if (name.isBlank()) {
                                nameError = "El nombre es obligatorio"
                                hasError = true
                            }
                            if (address.isBlank()) {
                                addressError = "La dirección es obligatoria"
                                hasError = true
                            }
                            if (hasError) return@Button

                            val request = CreateSiteRequestDto(
                                name = name,
                                address = address,
                                user_id = userId
                            )

                            if (siteId == null) {
                                siteViewModel.createSite(userId, request) {
                                    onSubmitComplete()
                                }
                            } else {
                                siteViewModel.updateSite(userId, siteId, request) {
                                    onSubmitComplete()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text(
                            text = if (siteId == null) "Crear proyecto" else "Guardar cambios",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
