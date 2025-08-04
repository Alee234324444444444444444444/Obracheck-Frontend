package com.example.obracheck_frontend.ui.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.obracheck_frontend.model.dto.CreateWorkerRequestDto
import com.example.obracheck_frontend.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerFormScreen(
    siteId: Long,
    editId: Long? = null,
    viewModel: WorkerViewModel = WorkerViewModel(),
    onSubmitComplete: () -> Unit
) {
    val accentColor = Color(0xFF00BCD4)
    val backgroundColor = Color(0xFFF5F7FA)
    val headerGradient = Brush.horizontalGradient(listOf(Color(0xFF304FFE), accentColor))

    var name by remember { mutableStateOf(TextFieldValue()) }
    var role by remember { mutableStateOf(TextFieldValue()) }
    var ci by remember { mutableStateOf(TextFieldValue()) }

    var showError by remember { mutableStateOf(false) }
    var ciExistsError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }


    LaunchedEffect(siteId) {
        viewModel.loadWorkersBySite(siteId)
    }

    LaunchedEffect(editId) {
        if (editId != null) {
            viewModel.getWorker(editId) { worker ->
                name = TextFieldValue(worker.name)
                role = TextFieldValue(worker.role)
                ci = TextFieldValue(worker.ci)
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (editId != null) "Editar Trabajador" else "Nuevo Trabajador",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(headerGradient)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showError = name.text.isBlank() || role.text.isBlank() || ci.text.isBlank()
                    ciExistsError = false
                    errorText = ""

                    if (!showError) {
                        val request = CreateWorkerRequestDto(
                            name = name.text,
                            role = role.text,
                            ci = ci.text,
                            site_id = siteId
                        )

                        if (editId != null) {
                            viewModel.updateWorker(
                                id = editId,
                                request = request,
                                onComplete = { onSubmitComplete() },
                                onError = { error ->
                                    ciExistsError = true
                                    errorText = error
                                }
                            )
                        } else {
                            viewModel.createWorker(
                                request = request,
                                onComplete = { onSubmitComplete() },
                                onError = { error ->
                                    ciExistsError = true
                                    errorText = error
                                }
                            )
                        }
                    }
                },
                containerColor = accentColor,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = if (editId != null) Icons.Default.Save else Icons.Default.PersonAdd,
                    contentDescription = if (editId != null) "Guardar cambios" else "Crear"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (showError) {
                Text(
                    text = "Todos los campos son obligatorios.",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = role,
                onValueChange = { role = it },
                label = { Text("Rol") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (ciExistsError && errorText.isNotBlank()) {
                Text(
                    text = errorText,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            OutlinedTextField(
                value = ci,
                onValueChange = {
                    ci = it
                    ciExistsError = false
                    errorText = ""
                },
                label = { Text("CI") },
                isError = ciExistsError,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}
