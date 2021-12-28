package com.example.membot

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.fragment.app.createViewModelLazy
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.viewModels
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.membot.ui.pi.PiViewModel

import junit.framework.TestCase
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before
import java.lang.Exception
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.hamcrest.Matcher


@RunWith(AndroidJUnit4::class)
class PiFragmentTest : TestCase() {

    @Test
    fun displayGameOverTest() {
        launchFragmentInContainer<PiFragment>(themeResId = R.style.Theme_MaterialComponents)
        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.submitButton)).perform(click())
        assertThat(onView(withId(R.id.retry_button)).check(matches(isDisplayed())))
    }

    @Test
    fun calculateUserScore(){
        var scenario = launchFragmentInContainer<PiFragment>(themeResId = R.style.Theme_MaterialComponents)
        onView(withId(R.id.textInputEditText)).perform(replaceText("1415"))
        onView(withId(R.id.submitButton)).perform(click())
        val scoreVI: ViewInteraction = onView(withId(R.id.userScore))
        var score = getText(scoreVI)
        assertThat(score).isEqualTo("1")
    }

    @Test
    fun calculateUserStrikes(){
        var scenario = launchFragmentInContainer<PiFragment>(themeResId = R.style.Theme_MaterialComponents)
        onView(withId(R.id.textInputEditText)).perform(replaceText("123"))
        onView(withId(R.id.submitButton)).perform(click())
        val strikesVI: ViewInteraction = onView(withId(R.id.userStrikes))
        var strikes = getText(strikesVI)
        assertThat(strikes).isEqualTo("1")
    }

    @Test
    fun calculateFailState(){
        launchFragmentInContainer<PiFragment>(themeResId = R.style.Theme_MaterialComponents)
        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.submitButton)).perform(click())

        val strikesVI: ViewInteraction = onView(withId(R.id.userStrikes))
        var strikes = getText(strikesVI)
        assertThat(strikes).isEqualTo("3")
        assertThat(onView(withId(R.id.retry_button)).check(matches(isDisplayed())))
        assertThat(onView(withId(R.id.quit_button)).check(matches(isDisplayed())))
    }

    @Test
    fun compareUserInputToCurrentDecimals(){
        var scenario = launchFragmentInContainer<PiFragment>(themeResId = R.style.Theme_MaterialComponents)
        onView(withId(R.id.textInputEditText)).perform(replaceText("1415"))
        onView(withId(R.id.submitButton)).perform(click())
        assertThat(onView(withId(R.id.userScore)).check(matches((withText("1")))))
    }

    @Test
    fun calculateListOfPiDecimals(){
        var scenario = launchFragmentInContainer<PiFragment>(themeResId = R.style.Theme_MaterialComponents)
        val piDigitsVI: ViewInteraction = onView(withId(R.id.pi_digits_textView))
        var piDigits = getText(piDigitsVI)
        assertThat(piDigits).isEqualTo("[1, 4, 1, 5]")
    }

    @Test
    fun testScrollingDecimals(){
        var scenario = launchFragmentInContainer<PiFragment>(themeResId = R.style.Theme_MaterialComponents)
        assertThat(onView(withId(R.id.pi_digits_textView)).check(matches(isDisplayed())))
    }

    @Test
    fun displayScoreAndStrikes(){
        var scenario = launchFragmentInContainer<PiFragment>(themeResId = R.style.Theme_MaterialComponents)
        onView(withId(R.id.textInputEditText)).perform(replaceText("1415"))
        onView(withId(R.id.submitButton)).perform(click())
        assertThat(onView(withId(R.id.userScore)).check(matches((withText("1")))))
        onView(withId(R.id.submitButton)).perform(click())
        assertThat(onView(withId(R.id.userStrikes)).check(matches((withText("1")))))
    }


    private fun getText(matcher: ViewInteraction): String {
        var text = String()
        matcher.perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "Text of the view"
            }

            override fun perform(uiController: UiController, view: View) {
                val tv = view as TextView
                text = tv.text.toString()
            }
        })
        return text
    }
}

// How to get text from a textview
//val piDigitsVI: ViewInteraction = onView(withId(R.id.pi_digits_textView))
//var piDigits = getText(piDigitsVI) // getting the name