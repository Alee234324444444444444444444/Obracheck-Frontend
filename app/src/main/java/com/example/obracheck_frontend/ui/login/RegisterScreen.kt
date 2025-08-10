package com.example.obracheck_frontend.ui.login


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.obracheck_frontend.R
import com.example.obracheck_frontend.model.dto.CreateUserRequestDto
import com.example.obracheck_frontend.viewmodel.UserViewModel

// Colores del brand
private val Brand = Color(0xFF1F2A33)   // gris carbón
private val Accent = Color(0xFFF6C445)  // amarillo casco
private val Muted  = Color(0xFF7B8AA0)  // gris etiquetas
private val Border = Color(0xFFD1D5DB)  // borde inputs
private val Bg     = Color(0xFFF9FAFB)  // fondo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: UserViewModel
) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf(TextFieldValue()) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val tfColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Brand,
        unfocusedTextColor = Brand,
        disabledTextColor = Brand,
        cursorColor = Brand,
        focusedBorderColor = Brand,
        unfocusedBorderColor = Border,
        focusedLabelColor = Brand,
        unfocusedLabelColor = Muted,
        focusedLeadingIconColor = Brand,
        unfocusedLeadingIconColor = Muted,
        focusedPlaceholderColor = Muted,
        unfocusedPlaceholderColor = Muted
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Bg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_obracheck),
                contentDescription = "Logo Obracheck",
                modifier = Modifier
                    .size(220.dp)
                    .padding(top = 8.dp)
            )

            // Tarjeta del formulario
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp)
                    .border(
                        width = 2.dp,
                        color = Brand,
                        shape = RoundedCornerShape(20.dp)
                    ),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Crear cuenta en Obracheck",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brand,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(72.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Accent)
                    )

                    Spacer(Modifier.height(18.dp))

                    // Nombre
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (nameError != null) nameError = null
                        },
                        label = { Text("Nombre") },
                        placeholder = { Text("Tu nombre completo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        isError = nameError != null,
                        supportingText = {
                            if (nameError != null) {
                                Text(nameError!!, color = Color(0xFFB00020))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = tfColors,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) emailError = null
                        },
                        label = { Text("Correo") },
                        placeholder = { Text("tucorreo@dominio.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        isError = emailError != null,
                        supportingText = {
                            if (emailError != null) {
                                Text(emailError!!, color = Color(0xFFB00020))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = tfColors,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(Modifier.height(20.dp))

                    // Botón de registro
                    Button(
                        onClick = {
                            if (isLoading) return@Button

                            val nombre = name.text.trim()
                            val correo = email.text.trim()

                            var hasError = false
                            if (nombre.isBlank()) {
                                nameError = "Por favor ingresa tu nombre"; hasError = true
                            }
                            if (correo.isBlank()) {
                                emailError = "Por favor ingresa tu correo"; hasError = true
                            } else if (!correo.contains("@") || !correo.contains(".")) {
                                emailError = "El correo no es válido"; hasError = true
                            }

                            if (!hasError) {
                                isLoading = true
                                val request = CreateUserRequestDto(nombre, correo)
                                viewModel.createUser(request) { userId ->
                                    isLoading = false
                                    if (userId != null) {
                                        // Usuario creado exitosamente, ir al login
                                        navController.navigate("login") {
                                            popUpTo("register") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    } else {
                                        emailError = "El correo ya está registrado"
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Brand,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 12.dp),
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(
                            if (isLoading) "Creando cuenta..." else "Registrarse",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Enlace para ir al login
                    TextButton(
                        onClick = {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Text(
                            "¿Ya tienes cuenta? Iniciar sesión",
                            color = Brand,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(80.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Accent)
                    )
                }
            }
        }
    }
}