package com.evernote.android.state.lint.detectors

import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.evernote.android.state.lint.AbstractDetectorTest
import com.evernote.android.state.lint.detector.StateJavaDetector

/**
 * Created by junchengc on 5/11/17.
 */

class NonMatchingStateSaverDetectorTest : AbstractDetectorTest() {

    override fun getDetector(): Detector {
        return StateJavaDetector()
    }

    override fun getIssues(): List<Issue> {
        return listOf(
                StateJavaDetector.NonMatchingStateSaverCalls.ISSUE
        )
    }

    override val testResourceDirectory: String = "java"

    private val stateSaverFile = getTestFile("stub/StateSaver.java")
    private val emptyFile = getTestFile("Empty.java")
    private val validActivityFile = getTestFile("ValidActivity.java")
    private val invalidActivityNoSaveFile = getTestFile("InvalidActivityNoSave.java")
    private val invalidActivityNoRestoreFile = getTestFile("InvalidActivityNoRestore.java")
    private val invalidChildActivityFile = getTestFile("InvalidChildActivity.java")

    /**
     * Test that an empty java file has no warnings.
     */
    fun testEmpty() {
        assertEquals(
                NO_WARNINGS,
                lintFiles(emptyFile)
        )
    }

    /**
     * Test that a valid activity will show no warnings
     */
    fun testValidActivity() {
        assertEquals(
                NO_WARNINGS,
                lintFiles(stateSaverFile, validActivityFile)
        )
    }

    /**
     * Test that an invalid activity with only a call to restoreInstanceState emits an error
     */
    fun testInvalidActivityNoSave() {
        assertEquals(
                """
                InvalidActivityNoSave.java:24: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [com.evernote.NonMatchingStateSaverCalls]
                        StateSaver.restoreInstanceState(this, savedInstanceState);
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                0 errors, 1 warnings

                """.trimIndent(),
                lintFiles(stateSaverFile, invalidActivityNoSaveFile)
        )
    }

    /**
     * Test that an invalid activity with only a call to saveInstanceState emits an error
     */
    fun testInvalidActivityNoRestore() {
        assertEquals(
                """
                InvalidActivityNoRestore.java:29: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [com.evernote.NonMatchingStateSaverCalls]
                        StateSaver.saveInstanceState(this, outState);
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                0 errors, 1 warnings

                """.trimIndent(),
                lintFiles(stateSaverFile, invalidActivityNoRestoreFile)
        )
    }

    /**
     * Test multiple files at once and ensure no more than 1 error is emitted for each mistake
     */
    fun testMultipleFiles() {
        assertEquals(
                """
                InvalidActivityNoRestore.java:29: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [com.evernote.NonMatchingStateSaverCalls]
                        StateSaver.saveInstanceState(this, outState);
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                InvalidActivityNoSave.java:24: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [com.evernote.NonMatchingStateSaverCalls]
                        StateSaver.restoreInstanceState(this, savedInstanceState);
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                0 errors, 2 warnings

                """.trimIndent(),
                lintFiles(stateSaverFile, validActivityFile, invalidActivityNoSaveFile, invalidActivityNoRestoreFile)
        )
    }

    /**
     * TODO: NOT IMPLEMENTED
     * Ensure that, if a parent class is calling StateSaver, the child activity should also be properly
     * inheriting methods calling on StateSaver
     */
//    fun testInvalidChildActivity() {
//        assertEquals(
//                """
//                InvalidActivityNoRestore.java:29: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [com.evernote.NonMatchingStateSaverCalls]
//                        StateSaver.saveInstanceState(this, outState);
//                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//                InvalidActivityNoSave.java:24: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [com.evernote.NonMatchingStateSaverCalls]
//                        StateSaver.restoreInstanceState(this, savedInstanceState);
//                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//                0 errors, 2 warnings
//
//                """.trimIndent(),
//                lintFiles(stateSaverFile, validActivityFile, invalidChildActivityFile)
//        )
//    }
}
