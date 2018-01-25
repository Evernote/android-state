package com.evernote.android.state.lint.registry

import com.evernote.android.state.lint.detector.AndroidStateDetector
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


/**
 * Test the [StateIssueRegistry].
 */
class StateIssueRegistryTest {

    /**
     * Test that the issue registry contains the correct number of issues.
     */
    @Test
    fun testNumberOfIssues() {
        assertThat(StateIssueRegistry().issues).hasSize(1)
    }

    /**
     * Test that the issue registry contains the correct issues.
     */
    @Test
    fun testGetIssues() {
        assertThat(StateIssueRegistry().issues).contains(AndroidStateDetector.ISSUE)
    }
}
