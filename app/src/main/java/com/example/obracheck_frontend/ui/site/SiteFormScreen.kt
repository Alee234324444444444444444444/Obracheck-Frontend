package com.example.obracheck_frontend.ui.site

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.obracheck_frontend.model.dto.CreateSiteRequestDto
import com.example.obracheck_frontend.viewmodel.SiteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteFormScreen(
    siteViewModel: SiteViewModel,
    siteId: Long?,
    onSubmitComplete: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") } // lo capturaremos como texto

    LaunchedEffect(siteId) {
        if (siteId != null) {
            siteViewModel.getSite(siteId) { site ->
                name = site.name
                address = site.address
                userId = site.user.id.toString()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (siteId == null) "Crear sitio" else "Editar sitio"
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del sitio") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección del sitio") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = userId,
                onValueChange = { userId = it },
                label = { Text("ID del Usuario responsable") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val request = CreateSiteRequestDto(
                        name = name,
                        address = address,
                        user_id = userId.toLongOrNull() ?: 0L
                    )
                    if (siteId == null) {
                        siteViewModel.createSite(request) {
                            onSubmitComplete()
                        }
                    } else {
                        siteViewModel.updateSite(siteId, request) {
                            onSubmitComplete()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}
