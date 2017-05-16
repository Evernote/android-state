package com.evernote.android.state.lint.detector

import com.android.tools.lint.detector.api.*
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.util.PsiTreeUtil
import java.util.EnumSet
import kotlin.collections.HashMap
import kotlin.collections.HashSet

/**
 * This detector crawls through the Java files and looks for non-matching instances of
 * saveInstanceState and restoreInstanceState for the StateSaver library.
 *
 * Created by junchengc on 5/15/17.
 */
class StateJavaDetector : Detector(), Detector.JavaPsiScanner {
    object NonMatchingStateSaverCalls {
        var saveInstanceStateCalled = HashMap<PsiClass, PsiMethodCallExpression>()
        var restoreInstanceStateCalled = HashMap<PsiClass, PsiMethodCallExpression>()
        var allClasses = HashSet<PsiClass>()

        val SAVE_INSTANCE_STATE_METHOD = "saveInstanceState"
        val RESTORE_INSTANCE_STATE_METHOD = "restoreInstanceState"

        // Non Matching State Saver Calls
        val ID = "NonMatchingStateSaverCalls"
        val ADVICE = "StateSaver calls should always occur in pairs. StateSaver.saveInstanceState() should always have a matching call to StateSaver.restoreInstanceState()."

        val ISSUE: Issue = Issue.create(
                ID,
                ADVICE,
                "$ADVICE Failing to do so could result in weird behaviors after activity recreation where certain states are not persisted.",
                Category.CORRECTNESS,
                6,
                Severity.WARNING,
                Implementation(StateJavaDetector::class.java, EnumSet.of(Scope.JAVA_FILE)))

        val APPLICABLE_METHODS = listOf(SAVE_INSTANCE_STATE_METHOD, RESTORE_INSTANCE_STATE_METHOD)

        val APPLICABLE_SUPER_CLASSES = listOf("com.evernote.android.state.StateSaver")
    }

    private class MethodInvocationVisitor(private val mContext: JavaContext) : JavaElementVisitor() {
        override fun visitMethodCallExpression(node: PsiMethodCallExpression) {
            val expression = node.methodExpression
            val methodName = expression.referenceName ?: return

            if (NonMatchingStateSaverCalls.APPLICABLE_METHODS.contains(methodName)) {
                val method = node.resolveMethod()
                val containingClass = method?.containingClass

                if (NonMatchingStateSaverCalls.APPLICABLE_SUPER_CLASSES.contains(containingClass?.qualifiedName)) {
                    val cls = PsiTreeUtil.getParentOfType(node, PsiClass::class.java, false)!!
                    when (methodName) {
                        NonMatchingStateSaverCalls.SAVE_INSTANCE_STATE_METHOD -> NonMatchingStateSaverCalls.saveInstanceStateCalled.put(cls, node)
                        NonMatchingStateSaverCalls.RESTORE_INSTANCE_STATE_METHOD -> NonMatchingStateSaverCalls.restoreInstanceStateCalled.put(cls, node)
                    }
                    NonMatchingStateSaverCalls.allClasses.add(cls)
                }
            }
        }
    }

    override fun afterCheckFile(context: Context?) {
        super.afterCheckFile(context)
        if (context is JavaContext) {
            NonMatchingStateSaverCalls
                    .allClasses
                    .filter { cls -> NonMatchingStateSaverCalls.saveInstanceStateCalled.containsKey(cls) xor NonMatchingStateSaverCalls.restoreInstanceStateCalled.containsKey(cls) }
                    .forEach({ cls ->
                        val node = NonMatchingStateSaverCalls.saveInstanceStateCalled[cls] ?: NonMatchingStateSaverCalls.restoreInstanceStateCalled[cls]
                        if (node != null) {
                            context.report(NonMatchingStateSaverCalls.ISSUE, node, context.getLocation(node), NonMatchingStateSaverCalls.ADVICE)
                        }
                    })
        }
        NonMatchingStateSaverCalls.saveInstanceStateCalled.clear()
        NonMatchingStateSaverCalls.restoreInstanceStateCalled.clear()
    }

    override fun createPsiVisitor(context: JavaContext): JavaElementVisitor {
        return MethodInvocationVisitor(context)
    }

    override fun getApplicablePsiTypes(): List<Class<out PsiElement>> {
        return arrayListOf(PsiMethodCallExpression::class.java)
    }

    override fun getApplicableFiles(): EnumSet<Scope> = Scope.JAVA_FILE_SCOPE

    override fun getApplicableMethodNames(): List<String> = NonMatchingStateSaverCalls.APPLICABLE_METHODS
}
