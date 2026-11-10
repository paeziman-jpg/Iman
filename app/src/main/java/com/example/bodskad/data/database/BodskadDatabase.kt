package com.example.bodskad.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bodskad.data.model.TibetanEntry
import com.example.bodskad.data.model.TranslationHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [TibetanEntry::class, TranslationHistory::class],
  version = 1,
  exportSchema = false
)
abstract class BodskadDatabase : RoomDatabase() {
  abstract fun vocabularyDao(): VocabularyDao
  abstract fun translationDao(): TranslationDao

  companion object {
    @Volatile
    private var INSTANCE: BodskadDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): BodskadDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          BodskadDatabase::class.java,
          "bodskad_tibetan_database"
        )
        .addCallback(BodskadDatabaseCallback(scope))
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }

  private class BodskadDatabaseCallback(
    private val scope: CoroutineScope
  ) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      INSTANCE?.let { database ->
        scope.launch(Dispatchers.IO) {
          populateInitialDatabase(database.vocabularyDao())
        }
      }
    }

    suspend fun populateInitialDatabase(dao: VocabularyDao) {
      if (dao.getCount() == 0) {
        dao.insertAll(PrepopulatedData.getInitialEntries())
      }
    }
  }
}
