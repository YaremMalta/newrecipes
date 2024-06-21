package com.example.newrecipes.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newrecipes.data.pojo.MealDB

@Database(entities = [MealDB::class], version = 6, exportSchema = false)
abstract class MealsDatabase : RoomDatabase() {

    abstract fun dao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: MealsDatabase? = null

        fun getInstance(context: Context): MealsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealsDatabase::class.java,
                    "meals_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
