//package com.evernote.android.state.lint.detectors
//
//import com.android.tools.lint.detector.api.Detector
//import com.android.tools.lint.detector.api.Issue
//import com.evernote.android.state.lint.AbstractDetectorTest
//import com.evernote.android.state.lint.detector.StateJavaDetector
//
///**
// * Created by junchengc on 5/11/17.
// */
//
//class NonMatchingStateSaverDetectorTest : AbstractDetectorTest() {
//
//    override fun getDetector(): Detector {
//        return StateJavaDetector()
//    }
//
//    override fun getIssues(): List<Issue> {
//        return listOf(
//                StateJavaDetector.NonMatchingStateSaverCalls.ISSUE
//        )
//    }
//
//    override val testResourceDirectory: String = "java"
//
//    private val stateSaverFile = getTestFile("stub/StateSaver.java")
//    private val emptyFile = getTestFile("Empty.java")
//    private val validActivityFile = getTestFile("ValidActivity.java")
//    private val validActivityFileKt = getTestFile("ValidActivityKt.kt")
//    private val invalidActivityNoSaveFile = getTestFile("InvalidActivityNoSave.java")
//    private val invalidActivityNoSaveFileKt = getTestFile("InvalidActivityNoSaveKt.kt")
//    private val invalidActivityNoRestoreFile = getTestFile("InvalidActivityNoRestore.java")
//    private val invalidActivityNoRestoreFileKt = getTestFile("InvalidActivityNoRestoreKt.kt")
//
//    /**
//     * Test that an empty java file has no warnings.
//     */
//    fun testEmpty() {
//        assertEquals(
//                NO_WARNINGS,
//                lintFiles(emptyFile)
//        )
//    }
//
//    /**
//     * Test that a valid activity will show no warnings
//     */
//    fun testValidActivity() {
//        assertEquals(
//                NO_WARNINGS,
//                lintFiles(stateSaverFile, validActivityFile)
//        )
//    }
//
//    /**
//     * Test that a valid activity written in Kotlin will show no warnings
//     */
//    fun testValidActivityKt() {
//        assertEquals(
//                NO_WARNINGS,
//                lintFiles(stateSaverFile, validActivityFileKt)
//        )
//    }
//
//
//    /**
//     * Test that an invalid activity with only a call to restoreInstanceState emits an error
//     */
//    fun testInvalidActivityNoSave() {
//        assertEquals(
//                """
//                InvalidActivityNoSave.java:24: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
//                        StateSaver.restoreInstanceState(this, savedInstanceState);
//                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//                0 errors, 1 warnings
//
//                """.trimIndent(),
//                lintFiles(stateSaverFile, invalidActivityNoSaveFile)
//        )
//    }
//
//    /**
//     * Test that an invalid activity written in Kotlin with only a call to restoreInstanceState emits an error
//     */
//    fun ignoreTestInvalidActivityNoSaveKt() {
//        assertEquals(
//                """
//                InvalidActivityNoSaveKt.kt:24: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
//                        StateSaver.restoreInstanceState(this, savedInstanceState);
//                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//                0 errors, 1 warnings
//
//                """.trimIndent(),
//                lintFiles(stateSaverFile, invalidActivityNoSaveFileKt)
//        )
//    }
//
//
//    /**
//     * Test that an invalid activity with only a call to saveInstanceState emits an error
//     */
//    fun testInvalidActivityNoRestore() {
//        assertEquals(
//                """
//                InvalidActivityNoRestore.java:29: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
//                        StateSaver.saveInstanceState(this, outState);
//                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//                0 errors, 1 warnings
//
//                """.trimIndent(),
//                lintFiles(stateSaverFile, invalidActivityNoRestoreFile)
//        )
//    }
//
//    /**
//     * Test that an invalid activity written in Kotlin with only a call to saveInstanceState emits an error
//     */
//    fun ignoreTestInvalidActivityNoRestoreKt() {
//        assertEquals(
//                """
//                InvalidActivityNoRestoreKt.kt:29: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
//                        StateSaver.saveInstanceState(this, outState);
//                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//                0 errors, 1 warnings
//
//                """.trimIndent(),
//                lintFiles(stateSaverFile, invalidActivityNoRestoreFileKt)
//        )
//    }
//
//    /**
//     * Test multiple files at once and ensure no more than 1 error is emitted for each mistake
//     */
//    fun testMultipleFiles() {
//        assertEquals(
//                """
//                InvalidActivityNoRestore.java:29: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
//                        StateSaver.saveInstanceState(this, outState);
//                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//                InvalidActivityNoSave.java:24: Warning: StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState(). [NonMatchingStateSaverCalls]
//                        StateSaver.restoreInstanceState(this, savedInstanceState);
//                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//                0 errors, 2 warnings
//
//                """.trimIndent(),
//                lintFiles(stateSaverFile, validActivityFile, invalidActivityNoSaveFile, invalidActivityNoRestoreFile)
//        )
//    }
//}
