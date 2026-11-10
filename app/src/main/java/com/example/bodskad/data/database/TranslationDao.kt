package com.example.bodskad.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bodskad.data.model.TranslationHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {
  @Query("SELECT * FROM translation_history ORDER BY timestamp DESC LIMIT 50")
  fun getRecentTranslations(): Flow<List<TranslationHistory>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(translation: TranslationHistory): Long

  @Query("DELETE FROM translation_history WHERE id = :id")
  suspend fun delete(id: Long)

  @Query("DELETE FROM translation_history")
  suspend fun clearAll()

  @Query("SELECT COUNT(*) FROM translation_history")
  fun getTranslationCount(): Flow<Int>
}
