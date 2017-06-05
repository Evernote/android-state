package com.evernote.android.state.lint.registry

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue
import com.evernote.android.state.lint.detector.StateJavaDetector

class StateIssueRegistry : IssueRegistry() {
    override fun getIssues(): List<Issue> = listOf(StateJavaDetector.NonMatchingStateSaverCalls.ISSUE)
}
