package com.stepan.neoroblox

import com.stepan.neoroblox.data.Pkce
import com.stepan.neoroblox.utils.formatCount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BasicTests {
    @Test
    fun pkceChallengeMatchesRfc7636Example() {
        val verifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"
        assertEquals("E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM", Pkce.challenge(verifier))
    }

    @Test
    fun pkceVerifierHasValidLength() {
        val v = Pkce.newVerifier()
        assertTrue(v.length in 43..128)
    }

    @Test
    fun formatCountAbbreviates() {
        assertEquals("999", formatCount(999))
        assertEquals("1.5K", formatCount(1500))
        assertEquals("2M", formatCount(2_000_000))
    }
}
