package com.evernote.android.state.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.utils.SdkUtils
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.MalformedURLException

abstract class AbstractDetectorTest : LintDetectorTest() {
    companion object {
        protected val PATH_TEST_RESOURCES = "/src/test/resources/"
        val NO_WARNINGS = "No warnings."
    }

    protected abstract val testResourceDirectory: String

    override fun getTestResource(relativePath: String, expectExists: Boolean): InputStream? {
        val path = (PATH_TEST_RESOURCES + testResourceDirectory + File.separatorChar + relativePath).replace('/', File.separatorChar)
        val file = File(testDataRootDir, path)
        if (file.exists()) {
            try {
                return BufferedInputStream(FileInputStream(file))
            } catch (e: FileNotFoundException) {
                if (expectExists) {
                    fail("Could not find file " + relativePath)
                }
            }

        }
        return null
    }

    private val testDataRootDir: File?
        get() {
            val source = javaClass.protectionDomain.codeSource
            if (source != null) {
                val location = source.location
                try {
                    val classesDir = SdkUtils.urlToFile(location)
                    return classesDir.parentFile.absoluteFile.parentFile.parentFile
                } catch (e: MalformedURLException) {
                    fail(e.localizedMessage)
                }

            }
            return null
        }

    protected fun getTestFile(file: String) = TestFile().from(file, this).to(file)
}
