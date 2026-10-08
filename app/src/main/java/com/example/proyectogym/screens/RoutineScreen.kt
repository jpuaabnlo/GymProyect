package com.example.proyectogym.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Ejercicio(
    val id: Int,
    val nombre: String,
    val series: Int,
    val repeticiones: Int,
    val completado: Boolean = false
)

@Composable
fun RoutineScreen(){
    val listaEjercicios = remember{
        mutableStateListOf(
            Ejercicio(1, "Press de Banca plana", 4, 10),
            Ejercicio(2, "Sentadilla libre", 4, 12),
            Ejercicio(3, "Desplantes con mancuerna", 3, 12),
            Ejercicio(4, "Curl de bíceps", 3, 15)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        item {
            Text(
                text = "Rutina de hoy",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        items(listaEjercicios, key ={ it.id }) { ejercicio ->
            EjercicioItem(
                ejercicio = ejercicio,
                onCheckedChange = { estaMarcado ->
                    val index = listaEjercicios.indexOf(ejercicio)
                    if(index != 1){
                        listaEjercicios[index] = ejercicio.copy(completado = estaMarcado)
                    }
                }
            )
        }
    }
}

@Composable
fun EjercicioItem(ejercicio: Ejercicio, onCheckedChange: (Boolean) -> Unit){
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = if(ejercicio.completado) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column {
                Text(text = ejercicio.nombre, fontWeight = FontWeight.Bold)
                Text(
                    text = "${ejercicio.series} series x ${ejercicio.repeticiones} repeticiones",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Checkbox(
                checked = ejercicio.completado,
                onCheckedChange = onCheckedChange
            )
        }
    }
}