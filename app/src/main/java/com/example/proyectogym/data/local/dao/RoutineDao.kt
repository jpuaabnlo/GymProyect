package com.example.proyectogym.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectogym.data.local.entity.ProgressNoteEntity
import com.example.proyectogym.data.local.entity.RoutineEntity
import com.example.proyectogym.data.local.entity.RoutineExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines ORDER BY id DESC")
    fun observeAllRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE coachId = :coachId ORDER BY id DESC")
    fun observeRoutinesByCoach(coachId: Int): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE clienteId = :clienteId ORDER BY id DESC LIMIT 1")
    fun observeActiveRoutineForClient(clienteId: Int): Flow<RoutineEntity?>

    @Query("SELECT * FROM routines WHERE id = :routineId")
    suspend fun getRoutineById(routineId: Int): RoutineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity): Long

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY id ASC")
    fun observeExercisesForRoutine(routineId: Int): Flow<List<RoutineExerciseEntity>>

    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY id ASC")
    suspend fun getExercisesForRoutineOnce(routineId: Int): List<RoutineExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: RoutineExerciseEntity): Long

    @Update
    suspend fun updateExercise(exercise: RoutineExerciseEntity)

    @Delete
    suspend fun deleteExercise(exercise: RoutineExerciseEntity)

    @Query("DELETE FROM routine_exercises WHERE routineId = :routineId")
    suspend fun deleteExercisesByRoutineId(routineId: Int)

    @Query("SELECT * FROM progress_notes WHERE userId = :userId ORDER BY id DESC")
    fun observeProgressNotesByUser(userId: Int): Flow<List<ProgressNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressNote(note: ProgressNoteEntity): Long

    @Update
    suspend fun updateProgressNote(note: ProgressNoteEntity)

    @Delete
    suspend fun deleteProgressNote(note: ProgressNoteEntity)
}
