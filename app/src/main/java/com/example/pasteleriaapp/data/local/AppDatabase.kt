package com.example.pasteleriaapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pasteleriaapp.data.local.dao.CategoriaDao
import com.example.pasteleriaapp.data.local.dao.ProductoDao
import com.example.pasteleriaapp.data.local.entity.CategoriaEntity
import com.example.pasteleriaapp.data.local.entity.ProductoEntity

@Database(
    entities = [CategoriaEntity::class, ProductoEntity::class],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriaDao(): CategoriaDao
    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pasteleriaApp_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

