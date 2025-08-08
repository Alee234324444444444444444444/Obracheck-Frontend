package com.example.obracheck_frontend.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.obracheck_frontend.R
import com.example.obracheck_frontend.navigation.NavRoutes

// Paleta del logo
private val Brand = Color(0xFF1F2A33)
private val Accent = Color(0xFFF6C445)
private val Muted = Color(0xFF7B8AA0)
private val Bg = Color(0xFFF9FAFB)

@Composable
fun WelcomeScreen(
    navController: NavController,
    userId: Long,
    userName: String
) {
    Surface(Modifier.fillMaxSize(), color = Bg) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo principal
            Image(
                painter = painterResource(id = R.drawable.logo_obracheck),
                contentDescription = "Logo ObraCheck",
                modifier = Modifier
                    .size(220.dp)
                    .padding(top = 8.dp)
            )

            // Tarjeta central con el saludo y acciones
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp)
                    .border(
                        width = 3.dp, // grosor del marco
                        color = Brand, // color del borde (puede ser Accent para amarillo)
                        shape = RoundedCornerShape(20.dp) // que coincida con el shape de la tarjeta
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
                    // Saludo
                    Text(
                        text = "Hola, $userName",
                        color = Brand,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "¡Bienvenido a ObraCheck!",
                        color = Brand.copy(alpha = 0.92f),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    // Línea acento
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(72.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Accent)
                    )

                    // Mini descripción
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Organiza tus obras, equipos y tareas en un solo lugar.",
                        color = Muted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    // Botón principal
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            navController.navigate("sitelist/$userId") {
                                popUpTo(NavRoutes.WELCOME) { inclusive = true }
                                launchSingleTop = true
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
                        Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }

                    // Sombra/acento bajo el botón (detalle visual)
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

