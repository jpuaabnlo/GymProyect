package com.example.proyectogym.data.remote

import com.example.proyectogym.data.local.entity.AttendanceEntity
import com.example.proyectogym.data.local.entity.MediaAssetEntity
import com.example.proyectogym.data.local.entity.MembershipEntity
import com.example.proyectogym.data.local.entity.NotificationEntity
import com.example.proyectogym.data.local.entity.ProductEntity
import com.example.proyectogym.data.local.entity.ProgressNoteEntity
import com.example.proyectogym.data.local.entity.RoutineEntity
import com.example.proyectogym.data.local.entity.RoutineExerciseEntity
import com.example.proyectogym.data.local.entity.SaleEntity
import com.example.proyectogym.data.local.entity.ServiceEntity
import com.example.proyectogym.data.local.entity.UserEntity
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("password") val password: String = "",
    @SerializedName("telefono") val telefono: String = "",
    @SerializedName("rol") val rol: String = "CLIENTE",
    @SerializedName("objetivo") val objetivo: String = "",
    @SerializedName("horario_habitual") val horarioHabitual: String = "No definido",
    @SerializedName("fecha_alta") val fechaAlta: String = "",
    @SerializedName("coach_id") val coachId: Int? = null
)

data class ServiceDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("tipo") val tipo: String = "MENSUAL",
    @SerializedName("precio") val precio: Double = 0.0,
    @SerializedName("duracion_dias") val duracionDias: Int = 30,
    @SerializedName("horario") val horario: String = "",
    @SerializedName("activo") val activo: Boolean = true
)

data class MembershipDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user_id") val userId: Int = 0,
    @SerializedName("cliente_nombre") val clienteNombre: String = "",
    @SerializedName("service_id") val serviceId: Int = 0,
    @SerializedName("servicio_nombre") val servicioNombre: String = "",
    @SerializedName("monto_pagado") val montoPagado: Double = 0.0,
    @SerializedName("metodo_pago") val metodoPago: String = "EFECTIVO",
    @SerializedName("fecha_inicio") val fechaInicio: String = "",
    @SerializedName("fecha_vencimiento") val fechaVencimiento: String = "",
    @SerializedName("dias_restantes") val diasRestantes: Int = 0,
    @SerializedName("estado") val estado: String = "ACTIVA"
)

data class AttendanceDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user_id") val userId: Int = 0,
    @SerializedName("usuario_nombre") val usuarioNombre: String = "",
    @SerializedName("servicio_nombre") val servicioNombre: String = "",
    @SerializedName("fecha") val fecha: String = "",
    @SerializedName("hora") val hora: String = "",
    @SerializedName("origen") val origen: String = "MANUAL"
)

data class RoutineDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("coach_id") val coachId: Int = 0,
    @SerializedName("cliente_id") val clienteId: Int? = null,
    @SerializedName("cliente_nombre") val clienteNombre: String = "Sin asignar",
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("objetivo") val objetivo: String = "",
    @SerializedName("dias_semana") val diasSemana: String = ""
)

data class RoutineExerciseDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("routine_id") val routineId: Int = 0,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("grupo_muscular") val grupoMuscular: String = "",
    @SerializedName("series") val series: Int = 0,
    @SerializedName("repeticiones") val repeticiones: Int = 0,
    @SerializedName("peso_sugerido_kg") val pesoSugeridoKg: Double = 0.0,
    @SerializedName("completado") val completado: Boolean = false
)

data class ProgressNoteDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user_id") val userId: Int = 0,
    @SerializedName("titulo") val titulo: String = "",
    @SerializedName("peso_corporal_kg") val pesoCorporalKg: Double = 0.0,
    @SerializedName("peso_levantado_kg") val pesoLevantadoKg: Double = 0.0,
    @SerializedName("repeticiones_logradas") val repeticionesLogradas: Int = 0,
    @SerializedName("comentario") val comentario: String = "",
    @SerializedName("fecha") val fecha: String = ""
)

data class ProductDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("categoria") val categoria: String = "",
    @SerializedName("precio") val precio: Double = 0.0,
    @SerializedName("stock") val stock: Int = 0
)

data class SaleDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("admin_id") val adminId: Int = 0,
    @SerializedName("resumen_productos") val resumenProductos: String = "",
    @SerializedName("total") val total: Double = 0.0,
    @SerializedName("metodo_pago") val metodoPago: String = "EFECTIVO",
    @SerializedName("fecha") val fecha: String = ""
)

data class NotificationDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user_id") val userId: Int = 0,
    @SerializedName("titulo") val titulo: String = "",
    @SerializedName("mensaje") val mensaje: String = "",
    @SerializedName("hora_programada") val horaProgramada: String = "",
    @SerializedName("tipo") val tipo: String = "",
    @SerializedName("activa") val activa: Boolean = true,
    @SerializedName("leida") val leida: Boolean = false,
    @SerializedName("fecha_creacion") val fechaCreacion: String = ""
)

data class MediaAssetDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("owner_id") val ownerId: Int = 0,
    @SerializedName("owner_type") val ownerType: String = "",
    @SerializedName("uri_local") val uriLocal: String = "",
    @SerializedName("url_remota") val urlRemota: String = "",
    @SerializedName("mime_type") val mimeType: String = "",
    @SerializedName("descripcion") val descripcion: String = "",
    @SerializedName("fecha_registro") val fechaRegistro: String = ""
)

fun UserEntity.toDto() = UserDto(id, nombre, email, password, telefono, rol, objetivo, horarioHabitual, fechaAlta, coachId)
fun UserDto.toEntity() = UserEntity(id, nombre, email, password, telefono, rol, objetivo, horarioHabitual, fechaAlta, coachId)

fun ServiceEntity.toDto() = ServiceDto(id, nombre, tipo, precio, duracionDias, horario, activo)
fun ServiceDto.toEntity() = ServiceEntity(id, nombre, tipo, precio, duracionDias, horario, activo)

fun MembershipEntity.toDto() = MembershipDto(id, userId, clienteNombre, serviceId, servicioNombre, montoPagado, metodoPago, fechaInicio, fechaVencimiento, diasRestantes, estado)
fun MembershipDto.toEntity() = MembershipEntity(id, userId, clienteNombre, serviceId, servicioNombre, montoPagado, metodoPago, fechaInicio, fechaVencimiento, diasRestantes, estado)

fun AttendanceEntity.toDto() = AttendanceDto(id, userId, usuarioNombre, servicioNombre, fecha, hora, origen)
fun AttendanceDto.toEntity() = AttendanceEntity(id, userId, usuarioNombre, servicioNombre, fecha, hora, origen)

fun RoutineEntity.toDto() = RoutineDto(id, coachId, clienteId, clienteNombre, nombre, objetivo, diasSemana)
fun RoutineDto.toEntity() = RoutineEntity(id, coachId, clienteId, clienteNombre, nombre, objetivo, diasSemana)

fun RoutineExerciseEntity.toDto() = RoutineExerciseDto(id, routineId, nombre, grupoMuscular, series, repeticiones, pesoSugeridoKg, completado)
fun RoutineExerciseDto.toEntity() = RoutineExerciseEntity(id, routineId, nombre, grupoMuscular, series, repeticiones, pesoSugeridoKg, completado)

fun ProgressNoteEntity.toDto() = ProgressNoteDto(id, userId, titulo, pesoCorporalKg, pesoLevantadoKg, repeticionesLogradas, comentario, fecha)
fun ProgressNoteDto.toEntity() = ProgressNoteEntity(id, userId, titulo, pesoCorporalKg, pesoLevantadoKg, repeticionesLogradas, comentario, fecha)

fun ProductEntity.toDto() = ProductDto(id, nombre, categoria, precio, stock)
fun ProductDto.toEntity() = ProductEntity(id, nombre, categoria, precio, stock)

fun SaleEntity.toDto() = SaleDto(id, adminId, resumenProductos, total, metodoPago, fecha)
fun SaleDto.toEntity() = SaleEntity(id, adminId, resumenProductos, total, metodoPago, fecha)

fun NotificationEntity.toDto() = NotificationDto(id, userId, titulo, mensaje, horaProgramada, tipo, activa, leida, fechaCreacion)
fun NotificationDto.toEntity() = NotificationEntity(id, userId, titulo, mensaje, horaProgramada, tipo, activa, leida, fechaCreacion)

fun MediaAssetEntity.toDto() = MediaAssetDto(id, ownerId, ownerType, uriLocal, urlRemota, mimeType, descripcion, fechaRegistro)
fun MediaAssetDto.toEntity() = MediaAssetEntity(id, ownerId, ownerType, uriLocal, urlRemota, mimeType, descripcion, fechaRegistro)
