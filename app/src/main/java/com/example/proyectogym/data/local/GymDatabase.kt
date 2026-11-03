package com.example.proyectogym.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectogym.data.local.dao.MediaAssetDao
import com.example.proyectogym.data.local.dao.MembershipDao
import com.example.proyectogym.data.local.dao.NotificationDao
import com.example.proyectogym.data.local.dao.RoutineDao
import com.example.proyectogym.data.local.dao.StoreDao
import com.example.proyectogym.data.local.dao.UserDao
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

@Database(
    entities = [
        UserEntity::class,
        ServiceEntity::class,
        MembershipEntity::class,
        AttendanceEntity::class,
        RoutineEntity::class,
        RoutineExerciseEntity::class,
        ProgressNoteEntity::class,
        ProductEntity::class,
        SaleEntity::class,
        NotificationEntity::class,
        MediaAssetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GymDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun membershipDao(): MembershipDao
    abstract fun routineDao(): RoutineDao
    abstract fun storeDao(): StoreDao
    abstract fun notificationDao(): NotificationDao
    abstract fun mediaAssetDao(): MediaAssetDao

    companion object {
        @Volatile
        private var Instance: GymDatabase? = null

        fun getDatabase(context: Context): GymDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    GymDatabase::class.java,
                    "gym_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
