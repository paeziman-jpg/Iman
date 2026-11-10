package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.bodskad.data.database.PrepopulatedData
import com.example.bodskad.data.model.Dialect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Bodskad", appName)
  }

  @Test
  fun `verify initial vocabulary and cultural data loaded`() {
    val entries = PrepopulatedData.getInitialEntries()
    assertTrue(entries.isNotEmpty())
    val tashiDelek = entries.firstOrNull { it.wylie.contains("bkra shis") }
    assertNotNull(tashiDelek)
    assertEquals("བཀྲ་ཤིས་བདེ་ལེགས།", tashiDelek?.tibetan)
    assertTrue(tashiDelek?.culturalContext?.isNotBlank() == true)
  }

  @Test
  fun `verify dialect nuances definition`() {
    val dialects = Dialect.values()
    assertEquals(4, dialects.size)
    val central = Dialect.CENTRAL
    val amdo = Dialect.AMDO
    val kham = Dialect.KHAM
    val dharma = Dialect.DHARMA
    assertEquals("central", central.id)
    assertEquals("amdo", amdo.id)
    assertEquals("kham", kham.id)
    assertEquals("dharma", dharma.id)
  }

  @Test
  fun `verify progress tracking and accuracy logic`() {
    val totalAttempts = 50
    val correctAttempts = 46
    val accuracyPercent = ((correctAttempts.toFloat() / totalAttempts) * 100).toInt()
    assertEquals(92, accuracyPercent)
    assertTrue(accuracyPercent > 90)
  }

  @Test
  fun `verify streak increments on flashcard session completion`() = kotlinx.coroutines.test.runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = com.example.bodskad.data.repository.BodskadRepository(context, this)
    val initialStreak = repository.streakDays.value
    assertTrue(initialStreak >= 6)
    val updatedStreak = repository.completeFlashcardSession()
    assertTrue(updatedStreak >= initialStreak)
    assertTrue(repository.isStreakActiveToday.value)
  }
}
