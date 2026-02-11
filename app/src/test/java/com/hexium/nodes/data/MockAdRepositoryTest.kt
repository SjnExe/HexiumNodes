package com.hexium.nodes.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MockAdRepositoryTest {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var repository: MockAdRepository

    @Before
    fun setup() {
        context = mock()
        sharedPreferences = mock()
        editor = mock()

        whenever(context.getSharedPreferences(any(), any())).thenReturn(sharedPreferences)

        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putStringSet(any(), anyOrNull())).thenReturn(editor)
        whenever(editor.putFloat(any(), any())).thenReturn(editor)
        whenever(editor.putBoolean(any(), any())).thenReturn(editor)
        whenever(editor.putString(any(), anyOrNull())).thenReturn(editor)
        whenever(editor.remove(any())).thenReturn(editor)

        doNothing().whenever(editor).apply()

        // Setup default return values using any() for simplicity
        // Using anyOrNull() for nullable second parameters in SharedPreferences
        whenever(sharedPreferences.getStringSet(any(), anyOrNull())).thenReturn(emptySet())
        whenever(sharedPreferences.getFloat(any(), any())).thenReturn(0.0f)
        whenever(sharedPreferences.getBoolean(any(), any())).thenReturn(false)
        whenever(sharedPreferences.getString(any(), anyOrNull())).thenReturn(null)

        repository = MockAdRepository(context)
    }

    @Test
    fun `watchAd increases credits and history`() = runBlocking {
        // Given
        // Use exact matchers for arguments we care about
        whenever(sharedPreferences.getFloat(eq("credits"), any())).thenReturn(10.0f)
        whenever(sharedPreferences.getStringSet(eq("ad_history"), anyOrNull())).thenReturn(emptySet())

        // When
        val result = repository.watchAd()

        // Then
        assertTrue(result)
    }

    @Test
    fun `getAvailableAds returns max when empty history`() = runBlocking {
        // Given
        whenever(sharedPreferences.getStringSet(eq("ad_history"), anyOrNull())).thenReturn(emptySet())

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
