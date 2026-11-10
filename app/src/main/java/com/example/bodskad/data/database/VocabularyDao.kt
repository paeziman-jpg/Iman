package com.example.bodskad.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bodskad.data.model.TibetanEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {
  @Query("SELECT * FROM tibetan_entries ORDER BY id ASC")
  fun getAllEntries(): Flow<List<TibetanEntry>>

  @Query("""
    SELECT * FROM tibetan_entries 
    WHERE (tibetan LIKE '%' || :query || '%' 
       OR wylie LIKE '%' || :query || '%' 
       OR phonetic LIKE '%' || :query || '%' 
       OR english LIKE '%' || :query || '%')
    ORDER BY id ASC
  """)
  fun searchEntries(query: String): Flow<List<TibetanEntry>>

  @Query("SELECT * FROM tibetan_entries WHERE category = :category ORDER BY id ASC")
  fun getEntriesByCategory(category: String): Flow<List<TibetanEntry>>

  @Query("SELECT * FROM tibetan_entries WHERE difficulty = :difficulty ORDER BY id ASC")
  fun getEntriesByDifficulty(difficulty: String): Flow<List<TibetanEntry>>

  @Query("SELECT * FROM tibetan_entries WHERE isFavorite = 1 ORDER BY id DESC")
  fun getFavorites(): Flow<List<TibetanEntry>>

  @Query("SELECT * FROM tibetan_entries WHERE isFlashcard = 1 ORDER BY srsNextReview ASC")
  fun getFlashcards(): Flow<List<TibetanEntry>>

  @Query("SELECT * FROM tibetan_entries WHERE id = :id LIMIT 1")
  suspend fun getEntryById(id: Long): TibetanEntry?

  @Query("""
    SELECT * FROM tibetan_entries 
    WHERE LOWER(english) LIKE '%' || LOWER(:term) || '%' 
       OR tibetan LIKE '%' || :term || '%' 
       OR LOWER(wylie) LIKE '%' || LOWER(:term) || '%'
    LIMIT 10
  """)
  suspend fun lookupInstant(term: String): List<TibetanEntry>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(entries: List<TibetanEntry>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(entry: TibetanEntry): Long

  @Update
  suspend fun update(entry: TibetanEntry)

  @Query("UPDATE tibetan_entries SET isFavorite = :isFav WHERE id = :id")
  suspend fun updateFavorite(id: Long, isFav: Boolean)

  @Query("UPDATE tibetan_entries SET isFlashcard = :isCard WHERE id = :id")
  suspend fun updateFlashcardStatus(id: Long, isCard: Boolean)

  @Query("SELECT COUNT(*) FROM tibetan_entries")
  suspend fun getCount(): Int

  @Query("SELECT COUNT(*) FROM tibetan_entries WHERE isFlashcard = 1 AND srsMasteryLevel >= 3")
  fun getMasteredCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM tibetan_entries WHERE isFlashcard = 1")
  fun getActiveFlashcardCount(): Flow<Int>
}
