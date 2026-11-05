package com.example.proyectogym.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogym.data.local.entity.ProductEntity
import com.example.proyectogym.data.local.entity.ServiceEntity
import com.example.proyectogym.data.local.entity.UserEntity
import com.example.proyectogym.ui.AppViewModelProvider

@Composable
fun AdminManagementScreen(
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var pestanaActual by remember { mutableIntStateOf(0) }
    val titulos = listOf("Usuarios", "Servicios", "Productos")

    var mostrarDialogoUsuario by remember { mutableStateOf(false) }
    var usuarioEdit by remember { mutableStateOf<UserEntity?>(null) }
    var uNombre by remember { mutableStateOf("") }
    var uEmail by remember { mutableStateOf("") }
    var uPass by remember { mutableStateOf("") }
    var uTel by remember { mutableStateOf("") }
    var uRol by remember { mutableStateOf("CLIENTE") }

    var mostrarDialogoServicio by remember { mutableStateOf(false) }
    var servicioEdit by remember { mutableStateOf<ServiceEntity?>(null) }
    var sNombre by remember { mutableStateOf("") }
    var sTipo by remember { mutableStateOf("MENSUAL") }
    var sPrecio by remember { mutableStateOf("") }
    var sDias by remember { mutableStateOf("30") }
    var sHorario by remember { mutableStateOf("") }

    var mostrarDialogoProducto by remember { mutableStateOf(false) }
    var productoEdit by remember { mutableStateOf<ProductEntity?>(null) }
    var pNombre by remember { mutableStateOf("") }
    var pCategoria by remember { mutableStateOf("Suplementos") }
    var pPrecio by remember { mutableStateOf("") }
    var pStock by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pestanaActual) {
            titulos.forEachIndexed { index, titulo ->
                Tab(
                    selected = pestanaActual == index,
                    onClick = { pestanaActual = index },
                    text = { Text(titulo) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (pestanaActual) {
                0 -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Usuarios (${uiState.usuarios.size})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Button(onClick = {
                                usuarioEdit = null
                                uNombre = ""
                                uEmail = ""
                                uPass = ""
                                uTel = ""
                                uRol = "CLIENTE"
                                mostrarDialogoUsuario = true
                            }) {
                                Text("+ Nuevo")
                            }
                        }
                    }
                    items(uiState.usuarios, key = { "u_${it.id}" }) { user ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "${user.nombre} (${user.rol})", fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${user.email} · Tel: ${user.telefono}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = {
                                        usuarioEdit = user
                                        uNombre = user.nombre
                                        uEmail = user.email
                                        uPass = user.password
                                        uTel = user.telefono
                                        uRol = user.rol
                                        mostrarDialogoUsuario = true
                                    }) {
                                        Text("Editar")
                                    }
                                    TextButton(onClick = { viewModel.eliminarUsuario(user) }) {
                                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Servicios (${uiState.servicios.size})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Button(onClick = {
                                servicioEdit = null
                                sNombre = ""
                                sTipo = "MENSUAL"
                                sPrecio = ""
                                sDias = "30"
                                sHorario = ""
                                mostrarDialogoServicio = true
                            }) {
                                Text("+ Nuevo")
                            }
                        }
                    }
                    items(uiState.servicios, key = { "s_${it.id}" }) { serv ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = serv.nombre, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${serv.tipo} · $${serv.precio} · ${serv.duracionDias} días · ${serv.horario}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = {
                                        servicioEdit = serv
                                        sNombre = serv.nombre
                                        sTipo = serv.tipo
                                        sPrecio = serv.precio.toString()
                                        sDias = serv.duracionDias.toString()
                                        sHorario = serv.horario
                                        mostrarDialogoServicio = true
                                    }) {
                                        Text("Editar")
                                    }
                                    TextButton(onClick = { viewModel.eliminarServicio(serv) }) {
                                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Inventario (${uiState.productos.size})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Button(onClick = {
                                productoEdit = null
                                pNombre = ""
                                pCategoria = "Suplementos"
                                pPrecio = ""
                                pStock = ""
                                mostrarDialogoProducto = true
                            }) {
                                Text("+ Nuevo")
                            }
                        }
                    }
                    items(uiState.productos, key = { "p_${it.id}" }) { prod ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = prod.nombre, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${prod.categoria} · $${prod.precio} · Existencia: ${prod.stock}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = {
                                        productoEdit = prod
                                        pNombre = prod.nombre
                                        pCategoria = prod.categoria
                                        pPrecio = prod.precio.toString()
                                        pStock = prod.stock.toString()
                                        mostrarDialogoProducto = true
                                    }) {
                                        Text("Editar")
                                    }
                                    TextButton(onClick = { viewModel.eliminarProducto(prod) }) {
                                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoUsuario) {
        val roles = listOf("CLIENTE", "ENTRENADOR", "ADMIN")
        AlertDialog(
            onDismissRequest = { mostrarDialogoUsuario = false },
            title = { Text(if (usuarioEdit == null) "Nuevo usuario" else "Editar usuario") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uNombre,
                        onValueChange = { uNombre = it },
                        label = { Text("Nombre") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uEmail,
                        onValueChange = { uEmail = it },
                        label = { Text("Correo") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uPass,
                        onValueChange = { uPass = it },
                        label = { Text("Contraseña") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uTel,
                        onValueChange = { uTel = it },
                        label = { Text("Teléfono") },
                        singleLine = true
                    )
                    Text("Rol del usuario:")
                    roles.forEach { rol ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = uRol == rol, onClick = { uRol = rol })
                            Text(rol)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoUsuario = false
                    viewModel.guardarUsuario(
                        idExistente = usuarioEdit?.id,
                        nombre = uNombre,
                        email = uEmail,
                        password = uPass,
                        telefono = uTel,
                        rol = uRol
                    )
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoUsuario = false }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarDialogoServicio) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoServicio = false },
            title = { Text(if (servicioEdit == null) "Nuevo servicio" else "Editar servicio") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sNombre,
                        onValueChange = { sNombre = it },
                        label = { Text("Nombre del servicio") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sTipo,
                        onValueChange = { sTipo = it },
                        label = { Text("Tipo (MENSUAL, CLASE, VISITA)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sPrecio,
                        onValueChange = { sPrecio = it },
                        label = { Text("Precio ($)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sDias,
                        onValueChange = { sDias = it },
                        label = { Text("Duración (días)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sHorario,
                        onValueChange = { sHorario = it },
                        label = { Text("Horario") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoServicio = false
                    viewModel.guardarServicio(
                        idExistente = servicioEdit?.id,
                        nombre = sNombre,
                        tipo = sTipo,
                        precio = sPrecio,
                        duracionDias = sDias,
                        horario = sHorario
                    )
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoServicio = false }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarDialogoProducto) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoProducto = false },
            title = { Text(if (productoEdit == null) "Nuevo producto" else "Editar producto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pNombre,
                        onValueChange = { pNombre = it },
                        label = { Text("Nombre del producto") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pCategoria,
                        onValueChange = { pCategoria = it },
                        label = { Text("Categoría") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pPrecio,
                        onValueChange = { pPrecio = it },
                        label = { Text("Precio ($)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pStock,
                        onValueChange = { pStock = it },
                        label = { Text("Existencia (stock)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoProducto = false
                    viewModel.guardarProducto(
                        idExistente = productoEdit?.id,
                        nombre = pNombre,
                        categoria = pCategoria,
                        precio = pPrecio,
                        stock = pStock
                    )
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoProducto = false }) { Text("Cancelar") }
            }
        )
    }
}
