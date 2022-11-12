package io.github.elaydis.calllogmonitor

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import io.github.elaydis.calllogmonitor.presentation.MainActivity
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var permissionsRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.READ_CALL_LOG,
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.READ_PHONE_STATE
    )

    @Test
    fun testStartStopServerFlow() {
        checkServerNotRunningState()
        clickStartStopButton()
        checkServerRunningState()
        clickStartStopButton()
        checkServerNotRunningState()
    }

    @Test
    fun testCallLog() {
        checkServerNotRunningState()
        clickStartStopButton()
        makePhoneCall()
        goBackToApp()
        checkServerRunningState()
        checkCallLogEntry()
    }

    // currently only works if phone app is on the home screen
    private fun makePhoneCall() {
        device.pressHome()
        device.findObject(UiSelector().description("Phone")).clickAndWaitForNewWindow()
        device.findObject(UiSelector().description("key pad")).clickAndWaitForNewWindow()
        device.findObject(UiSelector().text("1")).click()
        device.findObject(UiSelector().text("2")).click()
        device.findObject(UiSelector().text("3")).click()
        device.findObject(UiSelector().description("dial")).clickAndWaitForNewWindow()
        device.findObject(UiSelector().description("End call")).clickAndWaitForNewWindow()
    }

    private fun goBackToApp() {
        val packageName = "io.github.elaydis.calllogmonitor"
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), 5000L)
    }

    private fun clickStartStopButton() {
        onView(withId(R.id.startStopButton)).perform(click())
    }

    private fun checkServerNotRunningState() {
        onView(withId(R.id.startStopButton)).check(matches(withText("Start Server")))
        onView(withId(R.id.serverStatusTextView)).check(matches(withText("Server not running.")))
    }

    private fun checkServerRunningState() {
        onView(withId(R.id.serverStatusTextView)).check(matches(withText(containsString("Listening at"))))
        onView(withId(R.id.startStopButton)).check(matches(withText("Stop Server")))
    }

    private fun checkCallLogEntry() {
        onView(withId(R.id.nameTextView)).check(matches(withText("123")))
        onView(withId(R.id.durationTextView)).check(matches(isDisplayed()))
    }
}