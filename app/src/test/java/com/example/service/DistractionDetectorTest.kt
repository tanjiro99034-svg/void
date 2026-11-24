package com.example.service

import com.example.model.SupportedApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class DistractionDetectorTest {

    @Test
    fun `instagram reels viewer is detected`() {
        val nodes = listOf(
            UiNodeSnapshot(
                viewId = "com.instagram.android:id/clips_viewer_view_pager",
                contentDescription = "",
                text = "",
                isSelected = false,
                isVisible = true,
            ),
        )
        val result = DistractionDetector.detectFromSnapshots(SupportedApp.INSTAGRAM, nodes)
        assertNotNull(result)
        assertEquals(SupportedApp.INSTAGRAM, result?.app)
    }

    @Test
    fun `instagram stories and direct messages are never blocked`() {
        val storyNodes = listOf(
            UiNodeSnapshot(
                viewId = "com.instagram.android:id/reel_viewer_progress_bar",
                contentDescription = "",
                text = "",
                isSelected = false,
                isVisible = true,
            ),
            UiNodeSnapshot(
                viewId = "com.instagram.android:id/story_viewer",
                contentDescription = "",
                text = "",
                isSelected = false,
                isVisible = true,
            ),
        )
        assertNull(DistractionDetector.detectFromSnapshots(SupportedApp.INSTAGRAM, storyNodes))

        val dmNodes = listOf(
            UiNodeSnapshot(
                viewId = "com.instagram.android:id/direct_inbox",
                contentDescription = "",
                text = "",
                isSelected = false,
                isVisible = true,
            ),
        )
        assertNull(DistractionDetector.detectFromSnapshots(SupportedApp.INSTAGRAM, dmNodes))
    }

    @Test
    fun `youtube shorts player is detected while regular video and search are allowed`() {
        val shortsNodes = listOf(
            UiNodeSnapshot(
                viewId = "com.google.android.youtube:id/reel_watch_player",
                contentDescription = "",
                text = "",
                isSelected = false,
                isVisible = true,
            ),
        )
        val result = DistractionDetector.detectFromSnapshots(SupportedApp.YOUTUBE, shortsNodes)
        assertNotNull(result)
        assertEquals(SupportedApp.YOUTUBE, result?.app)

        val regularVideoNodes = listOf(
            UiNodeSnapshot(
                viewId = "com.google.android.youtube:id/watch_player",
                contentDescription = "",
                text = "",
                isSelected = false,
                isVisible = true,
            ),
            UiNodeSnapshot(
                viewId = "com.google.android.youtube:id/pivot_bar_tab",
                contentDescription = "Subscriptions",
                text = "Subscriptions",
                isSelected = true,
                isVisible = true,
            ),
        )
        assertNull(DistractionDetector.detectFromSnapshots(SupportedApp.YOUTUBE, regularVideoNodes))

        val searchNodes = listOf(
            UiNodeSnapshot(
                viewId = "com.google.android.youtube:id/search_edit_text",
                contentDescription = "",
                text = "kotlin",
                isSelected = false,
                isVisible = true,
            ),
        )
        assertNull(DistractionDetector.detectFromSnapshots(SupportedApp.YOUTUBE, searchNodes))
    }

    @Test
    fun `facebook reels viewer is detected while normal feed and messenger are allowed`() {
        val reelsNodes = listOf(
            UiNodeSnapshot(
                viewId = "com.facebook.katana:id/fb_reels_viewer",
                contentDescription = "",
                text = "",
                isSelected = false,
                isVisible = true,
            ),
        )
        val result = DistractionDetector.detectFromSnapshots(SupportedApp.FACEBOOK, reelsNodes)
        assertNotNull(result)
        assertEquals(SupportedApp.FACEBOOK, result?.app)

        val feedNodes = listOf(
            UiNodeSnapshot(
                viewId = "com.facebook.katana:id/news_feed",
                contentDescription = "",
                text = "",
                isSelected = false,
                isVisible = true,
            ),
        )
        assertNull(DistractionDetector.detectFromSnapshots(SupportedApp.FACEBOOK, feedNodes))
    }
}
