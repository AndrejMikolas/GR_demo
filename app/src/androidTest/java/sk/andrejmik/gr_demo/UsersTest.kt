package sk.andrejmik.gr_demo

import android.Manifest
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.squareup.spoon.Spoon
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@SmallTest
@RunWith(AndroidJUnit4::class)
class UsersTest
{
    @get:Rule
    var mActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE
    )

    /**
     * Check opening correct user detail and check information
     */
    @Test
    fun testOpenUserDetailFromList()
    {
        UITestUtils.sleep(3000)
        Espresso.onView(ViewMatchers.withText("George Bluth")).perform(ViewActions.click())
        UITestUtils.sleep(2000)
        Espresso.onView(ViewMatchers.withId(R.id.textView_user_detail_fullname)).check(ViewAssertions.matches(ViewMatchers.withText("George Bluth")))
        Espresso.onView(ViewMatchers.withId(R.id.textView_userMail)).check(ViewAssertions.matches(ViewMatchers.withText("george.bluth@reqres.in")))
    }
}