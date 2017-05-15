package com.evernote.android.state.lint.registry

import com.evernote.android.state.lint.detector.EvernoteJavaDetector
import org.junit.Before
import org.junit.Test


/**
 * Test the [IssueRegistry].
 */
class IssueRegistryTest {

    private var mIssueRegistry: StateIssueRegistry? = null

    /**
     * Setup for the other test methods.
     */
    @Before
    fun setUp() {
        mIssueRegistry = StateIssueRegistry()
    }

    /**
     * Test that the issue registry contains the correct number of issues.
     */
    @Test
    fun testNumberOfIssues() {
        val size = mIssueRegistry!!.issues.size
        assert(size == 1)
    }

    /**
     * Test that the issue registry contains the correct issues.
     */
    @Test
    fun testGetIssues() {
        val actual = mIssueRegistry!!.issues
        assert(actual.contains(EvernoteJavaDetector.NonMatchingStateSaverCalls.ISSUE))
    }

}
