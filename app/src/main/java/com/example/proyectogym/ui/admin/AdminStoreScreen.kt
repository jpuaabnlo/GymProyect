package com.example.proyectogym.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogym.ui.AppViewModelProvider

@Composable
fun AdminStoreScreen(
    adminId: Int,
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val metodosPago = listOf("EFECTIVO", "TARJETA", "TRANSFERENCIA")

    val totalCarrito = uiState.carrito.entries.sumOf { (prodId, cantidad) ->
        val prod = uiState.productos.firstOrNull { it.id == prodId }
        (prod?.precio ?: 0.0) * cantidad
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Tienda (Punto de Venta)",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Venta de suplementos, bebidas y accesorios con control de inventario.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (uiState.mensajeFeedback != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.mensajeFeedback!!,
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        items(uiState.productos, key = { it.id }) { prod ->
            val enCarrito = uiState.carrito[prod.id] ?: 0
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = prod.nombre, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${prod.categoria} · $${prod.precio} · Stock: ${prod.stock}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(onClick = { viewModel.quitarProductoCarrito(prod) }) {
                            Text("-")
                        }
                        Text(text = "$enCarrito", fontWeight = FontWeight.Bold)
                        OutlinedButton(onClick = { viewModel.agregarProductoCarrito(prod) }) {
                            Text("+")
                        }
                    }
                }
            }
        }

        // Carrito y método de pago
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Método de pago:",
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        metodosPago.forEach { metodo ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = uiState.metodoPagoTienda == metodo,
                                    onClick = { viewModel.onMetodoPagoTiendaChange(metodo) }
                                )
                                Text(metodo, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Text(
                        text = "Total carrito: $$totalCarrito",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { viewModel.confirmarVentaTienda(adminId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Vender y descontar inventario")
                    }
                }
            }
        }

        // Historial de ventas
        item {
            Text(
                text = "Ventas recientes (${uiState.ventas.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(uiState.ventas, key = { "venta_${it.id}" }) { venta ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = venta.resumenProductos, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "Total: $${venta.total} (${venta.metodoPago}) · ${venta.fecha}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
