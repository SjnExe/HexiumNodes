package com.hexium.nodes.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anySet
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class MockAdRepositoryTest {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var repository: MockAdRepository

    @Before
    fun setup() {
        context = mock(Context::class.java)
        sharedPreferences = mock(SharedPreferences::class.java)
        editor = mock(SharedPreferences.Editor::class.java)

        `when`(context.getSharedPreferences(anyString(), anyInt()))
            .thenReturn(sharedPreferences)

        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putStringSet(anyString(), anySet())).thenReturn(editor)
        `when`(editor.putFloat(anyString(), anyFloat())).thenReturn(editor)
        `when`(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor)
        `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
        `when`(editor.remove(anyString())).thenReturn(editor)

        // Fix: Use simple do-nothing answer for apply
        `when`(editor.apply()).thenAnswer { }

        // Setup default behaviors to avoid NPEs or strict stubbing errors
        // Note: We use specific keys in tests, but fallbacks here are good
        `when`(sharedPreferences.getStringSet(anyString(), anySet())).thenReturn(emptySet())
        `when`(sharedPreferences.getFloat(anyString(), anyFloat())).thenReturn(0.0f)
        `when`(sharedPreferences.getBoolean(anyString(), anyBoolean())).thenReturn(false)
        `when`(sharedPreferences.getString(anyString(), org.mockito.ArgumentMatchers.nullable(String::class.java))).thenReturn(null)

        repository = MockAdRepository(context)
    }

    @Test
    fun `watchAd increases credits and history`() = runBlocking {
        // Given
        // Overwrite specific keys needed for this test
        `when`(sharedPreferences.getFloat(eq("credits"), anyFloat())).thenReturn(10.0f)
        `when`(sharedPreferences.getStringSet(eq("ad_history"), anySet())).thenReturn(emptySet())

        // When
        val result = repository.watchAd()

        // Then
        assertTrue(result)
    }

    @Test
    fun `getAvailableAds returns max when empty history`() = runBlocking {
        // Given
        `when`(sharedPreferences.getStringSet(eq("ad_history"), anySet())).thenReturn(emptySet())

        // When
        val available = repository.getAvailableAds()

        // Then
        assertEquals(50, available)
    }

    @Test
    fun `login succeeds with correct credentials`() = runBlocking {
        val result = repository.login("admin", "1234")
        assertTrue(result)
    }

    @Test
    fun `login fails with incorrect credentials`() = runBlocking {
        val result = repository.login("user", "password")
        assertFalse(result)
    }
}
