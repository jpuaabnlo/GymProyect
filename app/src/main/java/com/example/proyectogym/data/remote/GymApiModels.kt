package com.example.proyectogym.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Modelos DTO para serialización / deserialización JSON con Gson y Retrofit.
 * Dejan lista la capa de comunicación con la futura Web API REST del gimnasio.
 */
data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class PasswordRecoveryDto(
    @SerializedName("email") val email: String
)

data class ApiMessageDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class UserDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("objetivo") val objetivo: String = "",
    @SerializedName("horario_habitual") val horarioHabitual: String = "No definido"
)

data class ServiceDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("precio") val precio: Double,
    @SerializedName("duracion_dias") val duracionDias: Int,
    @SerializedName("horario") val horario: String
)

data class RoutineDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("coach_id") val coachId: Int,
    @SerializedName("cliente_id") val clienteId: Int? = null,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("objetivo") val objetivo: String,
    @SerializedName("dias_semana") val diasSemana: String
)

data class ProductDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("precio") val precio: Double,
    @SerializedName("stock") val stock: Int
)

data class ProgressNoteDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("peso_corporal_kg") val pesoCorporalKg: Double,
    @SerializedName("peso_levantado_kg") val pesoLevantadoKg: Double,
    @SerializedName("repeticiones_logradas") val repeticionesLogradas: Int,
    @SerializedName("comentario") val comentario: String,
    @SerializedName("fecha") val fecha: String
)
