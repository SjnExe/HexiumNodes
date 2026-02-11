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
import org.mockito.kotlin.doNothing
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

        // Mock Context to return SharedPreferences
        // Using any() here is likely safe as it's the entry point
        whenever(context.getSharedPreferences(any(), any())).thenReturn(sharedPreferences)

        // Mock Editor methods to return editor (chaining)
        // Using any() matchers here is safe for void/chainable setters
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putStringSet(any(), any())).thenReturn(editor)
        whenever(editor.putFloat(any(), any())).thenReturn(editor)
        whenever(editor.putBoolean(any(), any())).thenReturn(editor)
        whenever(editor.putString(any(), any())).thenReturn(editor)
        whenever(editor.remove(any())).thenReturn(editor)

        // Void method stubbing
        doNothing().whenever(editor).apply()

        // Default Stubs for SharedPreferences readers
        // Use general matchers in setup to catch "other" calls
        // But BEWARE: Overriding these in tests with Mixed Matchers/Raw values causes issues
        whenever(sharedPreferences.getStringSet(any(), any())).thenReturn(emptySet())
        whenever(sharedPreferences.getFloat(any(), any())).thenReturn(0.0f)
        whenever(sharedPreferences.getBoolean(any(), any())).thenReturn(false)
        whenever(sharedPreferences.getString(any(), any())).thenReturn(null)

        repository = MockAdRepository(context)
    }

    @Test
    fun `watchAd increases credits and history`() = runBlocking {
        // Given
        // Force specific return values for the keys used in this test
        // NOTE: We must use matchers (eq) because setup() used matchers (any)
        // Mixing raw values and matchers usually works if *all* are raw, but if a method is called
        // with specific args, Mockito matches the most specific stub.
        // The issue might be nullable types in 'any()'.
        // Let's use 'org.mockito.kotlin.eq' explicitly to be safe.

        whenever(sharedPreferences.getFloat(org.mockito.kotlin.eq("credits"), any())).thenReturn(10.0f)
        whenever(sharedPreferences.getStringSet(org.mockito.kotlin.eq("ad_history"), any())).thenReturn(emptySet())

        // When
        val result = repository.watchAd()

        // Then
        assertTrue(result)
    }

    @Test
    fun `getAvailableAds returns max when empty history`() = runBlocking {
        // Given
        whenever(sharedPreferences.getStringSet(org.mockito.kotlin.eq("ad_history"), any())).thenReturn(emptySet())

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
