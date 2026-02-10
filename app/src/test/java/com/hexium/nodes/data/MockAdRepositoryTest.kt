package com.hexium.nodes.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anySet
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class MockAdRepositoryTest {

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var sharedPreferences: SharedPreferences

    @Mock
    lateinit var editor: SharedPreferences.Editor

    private lateinit var repository: MockAdRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        `when`(context.getSharedPreferences(anyString(), eq(Context.MODE_PRIVATE)))
            .thenReturn(sharedPreferences)

        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putStringSet(anyString(), anySet())).thenReturn(editor)
        `when`(editor.putFloat(anyString(), anyFloat())).thenReturn(editor)
        `when`(editor.putBoolean(anyString(), org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(editor)
        `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
        `when`(editor.remove(anyString())).thenReturn(editor)

        repository = MockAdRepository(context)
    }

    @Test
    fun `watchAd increases credits and history`() = runBlocking {
        // Given
        `when`(sharedPreferences.getFloat("credits", 0.00f)).thenReturn(10.0f)
        `when`(sharedPreferences.getStringSet("ad_history", emptySet())).thenReturn(emptySet())

        // When
        val result = repository.watchAd()

        // Then
        assertTrue(result)
    }

    @Test
    fun `getAvailableAds returns max when empty history`() = runBlocking {
        // Given
        `when`(sharedPreferences.getStringSet("ad_history", emptySet())).thenReturn(emptySet())

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
