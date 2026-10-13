package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.printToString
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("IkanKu", appName)
  }

  @Test
  fun testAppNavigationCrash() {
    composeTestRule.onNodeWithTag("btn_login_google").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    try {
        composeTestRule.onNodeWithTag("nav_dashboard").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("nav_chats").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("nav_beranda").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("nav_profile").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("nav_live").performClick()
        composeTestRule.waitForIdle()
    } catch(e: Exception) {
        println("UI Tree:")
        println(composeTestRule.onRoot().printToString())
        throw e
    }
  }

  @Test
  fun testJastiperNavigationCrash() {
    val dir = java.io.File("src/main/res/drawable/logo_ikan.png")
    val options = android.graphics.BitmapFactory.Options()
    options.inJustDecodeBounds = true
    android.graphics.BitmapFactory.decodeFile(dir.absolutePath, options)
    println("logo_ikan.png dimensions: ${options.outWidth} x ${options.outHeight}")

    // Switch to Jastiper
    composeTestRule.onNodeWithText("Jastiper").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("btn_login_google").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    try {
        composeTestRule.onNodeWithTag("nav_home").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("nav_pesanan").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("nav_live").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("nav_chat").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("nav_profile").performClick()
        composeTestRule.waitForIdle()
    } catch(e: Exception) {
        println("UI Tree:")
        println(composeTestRule.onRoot().printToString())
        throw e
    }
  }
}
