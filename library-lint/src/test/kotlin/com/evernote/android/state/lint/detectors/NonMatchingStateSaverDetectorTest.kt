package com.evernote.android.state.lint.detectors

import com.evernote.android.state.lint.AbstractDetectorTest
import com.evernote.android.state.lint.detector.AndroidStateDetector
import org.assertj.core.api.Assertions.assertThat

/**
 * Created by junchengc on 5/11/17.
 */

private const val NO_WARNINGS = "No warnings."

class NonMatchingStateSaverDetectorTest : AbstractDetectorTest() {

    private val stateSaverJava = getTestFile("stub/StateSaver.java")

    override fun getDetector() = AndroidStateDetector()
    override fun getIssues() = listOf(AndroidStateDetector.ISSUE)

    fun testEmpty() {
        assertThat(lintFiles(getTestFile("Empty.java"))).isEqualTo(NO_WARNINGS)
    }

    fun testValidActivity() {
        assertThat(lintFiles(stateSaverJava, getTestFile("ValidActivity.java"))).isEqualTo(NO_WARNINGS)
    }

    fun testValidActivityKotlin() {
        assertThat(lintFiles(stateSaverJava, getTestFile("ValidActivityKt.kt"))).isEqualTo(NO_WARNINGS)
    }

    fun testValidActivityDirectImport() {
        assertThat(lintFiles(stateSaverJava, getTestFile("ValidActivityDirectImport.java"))).isEqualTo(NO_WARNINGS)
    }

    fun testValidActivityDirectImportKotlin() {
        assertThat(lintFiles(stateSaverJava, getTestFile("ValidActivityDirectImportKt.kt"))).isEqualTo(NO_WARNINGS)
    }

    fun testValidActivityOtherMethod() {
        assertThat(lintFiles(stateSaverJava, getTestFile("ValidActivityOtherMethod.java"))).isEqualTo(NO_WARNINGS)
    }

    fun testValidActivityOtherMethodKotlin() {
        assertThat(lintFiles(stateSaverJava, getTestFile("ValidActivityOtherMethodKt.kt"))).isEqualTo(NO_WARNINGS)
    }

    fun testInvalidActivityNoSave() {
        assertThat(lintFiles(stateSaverJava, getTestFile("InvalidActivityNoSave.java"))).isEqualTo(
            """
            InvalidActivityNoSave.java:24: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
                    StateSaver.restoreInstanceState(this, savedInstanceState);
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            0 errors, 1 warnings

            """.trimIndent()
        )
    }

    fun testInvalidActivityNoSaveKotlin() {
        assertThat(lintFiles(stateSaverJava, getTestFile("InvalidActivityNoSaveKt.kt"))).isEqualTo(
            """
            InvalidActivityNoSaveKt.kt:23: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
                    StateSaver.restoreInstanceState(this, savedInstanceState)
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            0 errors, 1 warnings

            """.trimIndent()
        )
    }

    fun testInvalidActivityNoRestore() {
        assertThat(lintFiles(stateSaverJava, getTestFile("InvalidActivityNoRestore.java"))).isEqualTo(
            """
            InvalidActivityNoRestore.java:29: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
                    StateSaver.saveInstanceState(this, outState);
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            0 errors, 1 warnings

            """.trimIndent()
        )
    }

    fun testInvalidActivityNoRestoreKotlin() {
        assertThat(lintFiles(stateSaverJava, getTestFile("InvalidActivityNoRestoreKt.kt"))).isEqualTo(
            """
            InvalidActivityNoRestoreKt.kt:27: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
                    StateSaver.saveInstanceState(this, outState)
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            0 errors, 1 warnings

            """.trimIndent()
        )
    }

    fun testInvalidActivityNoRestoreDirectImport() {
        assertThat(lintFiles(stateSaverJava, getTestFile("InvalidActivityNoRestoreDirectImport.java"))).isEqualTo(
            """
            InvalidActivityNoRestoreDirectImport.java:30: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
                    saveInstanceState(this, outState);
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            0 errors, 1 warnings

            """.trimIndent()
        )
    }

    fun testInvalidActivityNoRestoreDirectImportKotlin() {
        assertThat(lintFiles(stateSaverJava, getTestFile("InvalidActivityNoRestoreDirectImportKt.kt"))).isEqualTo(
            """
            InvalidActivityNoRestoreDirectImportKt.kt:27: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
                    saveInstanceState(this, outState)
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            0 errors, 1 warnings

            """.trimIndent()
        )
    }

    fun testInvalidActivityOtherMethod() {
        assertThat(lintFiles(stateSaverJava, getTestFile("InvalidActivityOtherMethod.java"))).isEqualTo(
            """
            InvalidActivityOtherMethod.java:30: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
                    StateSaver.saveInstanceState(this, outState);
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            0 errors, 1 warnings

            """.trimIndent()
        )
    }

    fun testInvalidActivityOtherMethodKotlin() {
        assertThat(lintFiles(stateSaverJava, getTestFile("InvalidActivityOtherMethodKt.kt"))).isEqualTo(
            """
            InvalidActivityOtherMethodKt.kt:28: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
                    StateSaver.saveInstanceState(this, outState)
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            0 errors, 1 warnings

            """.trimIndent()
        )
    }

    fun testMultipleFiles() {
        assertThat(lintFiles(stateSaverJava, getTestFile("InvalidActivityNoRestoreKt.kt"), getTestFile("InvalidActivityNoSave.java"))).isEqualTo(
            """
            InvalidActivityNoRestoreKt.kt:27: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
                    StateSaver.saveInstanceState(this, outState)
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            InvalidActivityNoSave.java:24: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
                    StateSaver.restoreInstanceState(this, savedInstanceState);
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            0 errors, 2 warnings

            """.trimIndent()
        )
    }
}
