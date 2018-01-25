package com.evernote.android.state.lint.registry

import com.android.tools.lint.client.api.IssueRegistry
import com.evernote.android.state.lint.detector.AndroidStateDetector

class StateIssueRegistry : IssueRegistry() {
    override val issues = listOf(AndroidStateDetector.ISSUE)
}
