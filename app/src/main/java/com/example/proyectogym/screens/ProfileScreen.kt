package com.example.proyectogym.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fitInside
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    var nombre by remember { mutableStateOf("") }
    var meta by remember { mutableStateOf("") }

    var mostrarTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = 17,
        initialMinute = 30,
        is24Hour = false
    )

    var horaSeleccionada by remember {mutableStateOf("No definida")}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mi perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = {nombre = it},
            label = { Text("Nombre completo")},
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = meta,
            onValueChange = { meta = it },
            label = { Text("Objetivo") },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = "configurar recordatorio",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "hora elegida: $horaSeleccionada"
            )
            Button(onClick = {mostrarTimePicker = true}) {
                Text("Elegir la hora")
            }
        }
        if(mostrarTimePicker){
            AlertDialog(
                onDismissRequest = {mostrarTimePicker = false},
                confirmButton = {
                    TextButton(onClick = {
                        mostrarTimePicker = false
                        val minutoStr =timePickerState.minute.toString().padStart(2,'0')
                        horaSeleccionada = "${timePickerState.hour}:$minutoStr"
                    }
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { mostrarTimePicker = false}
                    ) {
                        Text("Cancelar")
                    }
                },
                text = {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()){
                        TimePicker( state = timePickerState)
                    }
                }
            )
        }
    }
}