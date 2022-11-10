package io.github.elaydis.calllogmonitor

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import io.github.elaydis.calllogmonitor.presentation.MainActivity
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var phoneLogPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.READ_CALL_LOG)

    @get:Rule
    var contactsPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.READ_CONTACTS)

    @get:Rule
    var phoneStatePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.READ_PHONE_STATE)

    @Test
    fun testStartStopServerFlow() {
        checkServerNotRunningState()
        clickStartStopButton()
        checkServerRunningState()
        clickStartStopButton()
        checkServerNotRunningState()
    }

    private fun checkServerNotRunningState() {
        onView(withId(R.id.startStopButton)).check(matches(withText("Start Server")))
        onView(withId(R.id.serverStatusTextView)).check(matches(withText("Server not running.")))
    }

    private fun clickStartStopButton() {
        onView(withId(R.id.startStopButton)).perform(click())
    }

    private fun checkServerRunningState() {
        onView(withId(R.id.serverStatusTextView)).check(matches(withText(containsString("Listening at"))))
        onView(withId(R.id.startStopButton)).check(matches(withText("Stop Server")))
    }
}