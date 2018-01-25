package com.evernote.android.state.lint.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UDeclaration
import org.jetbrains.uast.UElement
import org.jetbrains.uast.getContainingUClass
import org.jetbrains.uast.getContainingUFile
import java.util.EnumSet

/**
 * This detector crawls through the Java and Kotlin files and looks for non-matching instances of
 * saveInstanceState and restoreInstanceState for the StateSaver library.
 *
 * Created by junchengc on 5/15/17.
 */
class AndroidStateDetector : Detector(), Detector.UastScanner {
    companion object Issues {
        private val IMPLEMENTATION = Implementation(AndroidStateDetector::class.java, Scope.JAVA_FILE_SCOPE)
        private const val ADVICE = "StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState()."

        @JvmField
        val ISSUE = Issue.create(
            "NonMatchingStateSaverCalls",
            ADVICE,
            "$ADVICE Failing to do so could result in weird behaviors after activity recreation where certain states are not persisted.",
            Category.CORRECTNESS,
            6,
            Severity.WARNING,
            IMPLEMENTATION
        )

        private const val SAVE_INSTANCE_STATE_METHOD = "saveInstanceState"
        private const val RESTORE_INSTANCE_STATE_METHOD = "restoreInstanceState"
        private val APPLICABLE_METHODS = listOf(SAVE_INSTANCE_STATE_METHOD, RESTORE_INSTANCE_STATE_METHOD)
        private const val STATE_SAVER_CLASS_SIMPLE = "StateSaver"
        private const val STATE_SAVER_CLASS = "com.evernote.android.state.$STATE_SAVER_CLASS_SIMPLE"
    }

    private val saveCalls = mutableMapOf<UDeclaration, UCallExpression>()
    private val restoreCalls = mutableMapOf<UDeclaration, UCallExpression>()

    override fun afterCheckFile(context: Context) {
        try {
            if (context !is JavaContext) return

            val allClasses = mutableSetOf<UDeclaration>().apply {
                addAll(saveCalls.keys)
                addAll(restoreCalls.keys)
            }

            allClasses
                .filter { saveCalls.containsKey(it) xor restoreCalls.containsKey(it) }
                .map { (saveCalls[it] ?: restoreCalls[it])!! }
                .forEach {
                    context.report(ISSUE, it, context.getLocation(it), ADVICE)
                }
        } finally {
            saveCalls.clear()
            restoreCalls.clear()
        }
    }

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitCallExpression(node: UCallExpression) {
            val methodName = node.methodIdentifier?.name ?: return
            val uastParent = node.uastParent ?: return

            if (!APPLICABLE_METHODS.contains(methodName)) return

            val uFile = node.getContainingUFile() ?: return
            val imports = uFile.imports.mapNotNull { it.importReference?.asSourceString() }
            val parent = uastParent.psi?.text ?: return // StateSaver.restoreInstanceState or surrounding method with direct import

            if (!isStateSaverMethod(methodName, imports, parent)) return

            val cls = node.getContainingUClass()!!
            when (methodName) {
                SAVE_INSTANCE_STATE_METHOD -> saveCalls[cls] = node
                RESTORE_INSTANCE_STATE_METHOD -> restoreCalls[cls] = node
            }
        }

        private fun isStateSaverMethod(methodName: String, imports: List<String>, parent: String) =
            if (imports.contains("$STATE_SAVER_CLASS.$methodName")) {
                true // direct import
            } else {
                // StateSaver.methodName check
                imports.contains(STATE_SAVER_CLASS) && parent.startsWith("$STATE_SAVER_CLASS_SIMPLE.$methodName")
            }
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UCallExpression::class.java)
    override fun getApplicableFiles(): EnumSet<Scope> = Scope.JAVA_FILE_SCOPE
    override fun getApplicableMethodNames(): List<String> = APPLICABLE_METHODS
}
