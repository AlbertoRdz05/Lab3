package com.example.lab3

import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdivinaNumeroApp()
        }
    }
}

@Composable
fun AdivinaNumeroApp() {
    var numeroSecreto by remember { mutableStateOf(Random.nextInt(0, 101)) }
    var intento by remember { mutableStateOf(1) }
    var mensaje by remember { mutableStateOf("Presiona 'Iniciar' para comenzar el juego.") }
    var input by remember { mutableStateOf("") }
    var juegoFinalizado by remember { mutableStateOf(false) }
    var juegoIniciado by remember { mutableStateOf(false) }
    var tiempoRestante by remember { mutableStateOf(60) }

    // Temporizador que inicia cuando el juego comienza
    LaunchedEffect(key1 = juegoIniciado, key2 = juegoFinalizado) {
        if (juegoIniciado && !juegoFinalizado) {
            tiempoRestante = 60
            while (tiempoRestante > 0 && !juegoFinalizado) {
                delay(1000)
                tiempoRestante--
            }
            if (!juegoFinalizado) {
                mensaje = "⏰ ¡Se acabó el tiempo! El número era $numeroSecreto."
                juegoFinalizado = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Tiempo restante: ${tiempoRestante}s")
            Text(text = mensaje)

            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Tu intento") },
                enabled = juegoIniciado && !juegoFinalizado,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Button(
                onClick = {
                    val numeroJugador = input.toIntOrNull()
                    if (numeroJugador == null || numeroJugador !in 0..100) {
                        mensaje = "Por favor ingresa un número válido entre 0 y 100."
                        return@Button
                    }

                    if (numeroJugador == numeroSecreto) {
                        mensaje = "🎉 ¡Felicidades! Adivinaste el número en $intento intento(s)."
                        juegoFinalizado = true
                    } else if (intento >= 3) {
                        mensaje = "😢 ¡Perdiste! El número era $numeroSecreto."
                        juegoFinalizado = true
                    } else {
                        val pista = if (numeroJugador < numeroSecreto) "El número es mayor." else "El número es menor."
                        val minRango = (numeroSecreto - 5).coerceAtLeast(0)
                        val maxRango = (numeroSecreto + 5).coerceAtMost(100)
                        mensaje = "$pista Está entre $minRango y $maxRango. Intento $intento de 3."
                        intento++
                    }

                    input = ""
                },
                enabled = juegoIniciado && !juegoFinalizado,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Intentar")
            }

            if (!juegoIniciado) {
                Button(
                    onClick = {
                        juegoIniciado = true
                        mensaje = "¡Adivina un número entre 0 y 100!"
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Iniciar")
                }
            }

            if (juegoFinalizado) {
                Button(
                    onClick = {
                        numeroSecreto = Random.nextInt(0, 101)
                        intento = 1
                        mensaje = "Presiona 'Iniciar' para comenzar un nuevo juego."
                        input = ""
                        juegoFinalizado = false
                        juegoIniciado = false
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Reiniciar")
                }
            }
        }
    }
}
