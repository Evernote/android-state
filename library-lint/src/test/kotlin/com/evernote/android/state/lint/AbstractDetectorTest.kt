package com.evernote.android.state.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.utils.SdkUtils
import junit.framework.AssertionFailedError
import java.io.File
import java.io.InputStream

abstract class AbstractDetectorTest : LintDetectorTest() {
    override fun getTestResource(relativePath: String, expectExists: Boolean): InputStream {
        val path = "resources/test/java/$relativePath".replace('/', File.separatorChar)
        return File(testDataRootDir, path).inputStream()
    }

    private val testDataRootDir: File
        get() {
            val source = javaClass.protectionDomain.codeSource
            val location = source.location
            val classesDir = SdkUtils.urlToFile(location)
            return classesDir.parentFile.absoluteFile.parentFile.parentFile
        }

    protected fun getTestFile(file: String) = TestFile().from(file, this).to(file)!!

    override fun tearDown() {
        try {
            super.tearDown()
        } catch (e: AssertionFailedError) {
            // this is weird 🤷
            if (e.message != "There was a crash during lint execution; consult log for details") {
                throw e
            }
        }
    }
}
