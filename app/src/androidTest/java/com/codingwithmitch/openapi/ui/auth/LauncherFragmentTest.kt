package com.codingwithmitch.openapi.ui.auth

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.runner.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.testing.SingleFragmentActivity
import com.codingwithmitch.openapi.utils.EspressoTestMatchers.Companion.withDrawable
import com.codingwithmitch.openapi.utils.ViewModelUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class LauncherFragmentTest{

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(
        SingleFragmentActivity::class.java,
        true,
        true
    )


    private lateinit var viewModel: AuthViewModel
    private lateinit var launcherFragment: TestLauncherFragment


    @Before
    fun init() {
        launcherFragment = TestLauncherFragment()
        viewModel = mock(AuthViewModel::class.java)
        launcherFragment.providerFactory = ViewModelUtil.createFor(viewModel)
        activityRule.activity.setFragment(launcherFragment)
//        EspressoTestUtil.disableProgressBarAnimations(activityRule)
    }

    @Test
    fun verifyUI() {

        onView(withId(R.id.app_logo))
            .check(matches(withDrawable(R.drawable.codingwithmitch_logo)))
//
//        onView(withId(R.id.login))
//            .check(matches(withText(R.string.text_login)))
//
//        onView(withId(R.id.register))
//            .check(matches(withText(R.string.text_register)))
//
//        onView(withId(R.id.forgot_password))
//            .check(matches(withText(R.string.text_forgot_password)))
    }

    class TestLauncherFragment: LauncherFragment()
}























