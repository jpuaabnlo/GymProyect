package com.example.proyectogym.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface GymApiService {

    @GET("users.json")
    suspend fun getUsers(): Map<String, UserDto>?

    @PUT("users/user_{id}.json")
    suspend fun saveUser(@Path("id") id: Int, @Body user: UserDto): UserDto

    @DELETE("users/user_{id}.json")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    @GET("services.json")
    suspend fun getServices(): Map<String, ServiceDto>?

    @PUT("services/service_{id}.json")
    suspend fun saveService(@Path("id") id: Int, @Body service: ServiceDto): ServiceDto

    @DELETE("services/service_{id}.json")
    suspend fun deleteService(@Path("id") id: Int): Response<Unit>

    @GET("memberships.json")
    suspend fun getMemberships(): Map<String, MembershipDto>?

    @PUT("memberships/membership_{id}.json")
    suspend fun saveMembership(@Path("id") id: Int, @Body membership: MembershipDto): MembershipDto

    @DELETE("memberships/membership_{id}.json")
    suspend fun deleteMembership(@Path("id") id: Int): Response<Unit>

    @GET("attendances.json")
    suspend fun getAttendances(): Map<String, AttendanceDto>?

    @PUT("attendances/attendance_{id}.json")
    suspend fun saveAttendance(@Path("id") id: Int, @Body attendance: AttendanceDto): AttendanceDto

    @GET("routines.json")
    suspend fun getRoutines(): Map<String, RoutineDto>?

    @PUT("routines/routine_{id}.json")
    suspend fun saveRoutine(@Path("id") id: Int, @Body routine: RoutineDto): RoutineDto

    @DELETE("routines/routine_{id}.json")
    suspend fun deleteRoutine(@Path("id") id: Int): Response<Unit>

    @GET("routine_exercises.json")
    suspend fun getRoutineExercises(): Map<String, RoutineExerciseDto>?

    @PUT("routine_exercises/exercise_{id}.json")
    suspend fun saveRoutineExercise(@Path("id") id: Int, @Body exercise: RoutineExerciseDto): RoutineExerciseDto

    @DELETE("routine_exercises/exercise_{id}.json")
    suspend fun deleteRoutineExercise(@Path("id") id: Int): Response<Unit>

    @GET("progress_notes.json")
    suspend fun getProgressNotes(): Map<String, ProgressNoteDto>?

    @PUT("progress_notes/note_{id}.json")
    suspend fun saveProgressNote(@Path("id") id: Int, @Body note: ProgressNoteDto): ProgressNoteDto

    @DELETE("progress_notes/note_{id}.json")
    suspend fun deleteProgressNote(@Path("id") id: Int): Response<Unit>

    @GET("products.json")
    suspend fun getProducts(): Map<String, ProductDto>?

    @PUT("products/product_{id}.json")
    suspend fun saveProduct(@Path("id") id: Int, @Body product: ProductDto): ProductDto

    @DELETE("products/product_{id}.json")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>

    @GET("sales.json")
    suspend fun getSales(): Map<String, SaleDto>?

    @PUT("sales/sale_{id}.json")
    suspend fun saveSale(@Path("id") id: Int, @Body sale: SaleDto): SaleDto

    @GET("notifications.json")
    suspend fun getNotifications(): Map<String, NotificationDto>?

    @PUT("notifications/notification_{id}.json")
    suspend fun saveNotification(@Path("id") id: Int, @Body notification: NotificationDto): NotificationDto

    @DELETE("notifications/notification_{id}.json")
    suspend fun deleteNotification(@Path("id") id: Int): Response<Unit>

    @GET("media_assets.json")
    suspend fun getMediaAssets(): Map<String, MediaAssetDto>?

    @PUT("media_assets/media_{id}.json")
    suspend fun saveMediaAsset(@Path("id") id: Int, @Body mediaAsset: MediaAssetDto): MediaAssetDto

    @DELETE("media_assets/media_{id}.json")
    suspend fun deleteMediaAsset(@Path("id") id: Int): Response<Unit>
}
