package com.example.service

import android.view.accessibility.AccessibilityNodeInfo
import com.example.model.SupportedApp

data class UiNodeSnapshot(
    val viewId: String,
    val contentDescription: String,
    val text: String,
    val isSelected: Boolean,
    val isVisible: Boolean,
)

data class DetectionResult(
    val app: SupportedApp,
    val featureLabel: String,
    val matchedReason: String,
)

object DistractionDetector {

    private const val MAX_TRAVERSAL_NODES = 160
    private const val MAX_TRAVERSAL_DEPTH = 16

    fun detect(app: SupportedApp, root: AccessibilityNodeInfo?): DetectionResult? {
        if (root == null) return null
        val snapshots = ArrayList<UiNodeSnapshot>(64)
        collectSnapshots(root, depth = 0, out = snapshots)
        return detectFromSnapshots(app, snapshots)
    }

    fun detectFromSnapshots(
        app: SupportedApp,
        nodes: List<UiNodeSnapshot>,
    ): DetectionResult? {
        if (nodes.isEmpty()) return null
        return when (app) {
            SupportedApp.INSTAGRAM -> detectInstagramReels(nodes)
            SupportedApp.YOUTUBE -> detectYouTubeShorts(nodes)
            SupportedApp.FACEBOOK -> detectFacebookReels(nodes)
        }
    }

    private fun detectInstagramReels(nodes: List<UiNodeSnapshot>): DetectionResult? {
        val visibleNodes = nodes.filter { it.isVisible }

        // Never interfere with Stories (Instagram internally prefixes Stories as reel_viewer_*),
        // Direct Messages, or Profile screens.
        val hasSafeExclusion = visibleNodes.any { node ->
            val id = node.viewId.lowercase()
            id.contains("story_viewer") ||
                id.contains("stories_tray") ||
                id.contains("reel_viewer_progress_bar") ||
                id.contains("reel_viewer_image_view") ||
                id.contains("reel_viewer_media_container") ||
                id.contains("direct_inbox") ||
                id.contains("thread_fragment") ||
                id.contains("message_list") ||
                id.contains("profile_header")
        }

        val clipsViewerNode = visibleNodes.firstOrNull { node ->
            val id = node.viewId.lowercase()
            id.contains("clips_viewer_view_pager") ||
                id.contains("clips_viewer_root") ||
                id.contains("clips_video_container") ||
                id.contains("clips_media_component") ||
                id.contains("clips_ufi_component") ||
                id.contains("clips_author_info") ||
                id.contains("reels_viewer_root")
        }

        if (clipsViewerNode != null && !hasSafeExclusion) {
            return DetectionResult(
                app = SupportedApp.INSTAGRAM,
                featureLabel = "Instagram Reels",
                matchedReason = clipsViewerNode.viewId,
            )
        }

        val selectedClipsTab = visibleNodes.firstOrNull { node ->
            val id = node.viewId.lowercase()
            val desc = node.contentDescription.lowercase()
            node.isSelected && (id.contains("clips_tab") || desc == "reels")
        }

        if (selectedClipsTab != null && !hasSafeExclusion) {
            return DetectionResult(
                app = SupportedApp.INSTAGRAM,
                featureLabel = "Instagram Reels",
                matchedReason = "selected_reels_tab",
            )
        }

        return null
    }

    private fun detectYouTubeShorts(nodes: List<UiNodeSnapshot>): DetectionResult? {
        val visibleNodes = nodes.filter { it.isVisible }

        // Never interfere with Search input or regular video watch player when Shorts isn't active.
        val isSearching = visibleNodes.any { node ->
            node.viewId.lowercase().contains("search_edit_text")
        }
        if (isSearching) return null

        val shortsPlayerNode = visibleNodes.firstOrNull { node ->
            val id = node.viewId.lowercase()
            id.contains("reel_watch_player") ||
                id.contains("reel_player_page_container") ||
                id.contains("reel_player_overlay") ||
                id.contains("shorts_player") ||
                id.contains("shorts_video_player") ||
                id.contains("reel_recycler")
        }

        if (shortsPlayerNode != null) {
            return DetectionResult(
                app = SupportedApp.YOUTUBE,
                featureLabel = "YouTube Shorts",
                matchedReason = shortsPlayerNode.viewId,
            )
        }

        val hasRegularWatchPlayer = visibleNodes.any { node ->
            val id = node.viewId.lowercase()
            id.contains("watch_player") || id.contains("next_gen_watch_layout")
        }
        if (hasRegularWatchPlayer) return null

        val selectedShortsTab = visibleNodes.firstOrNull { node ->
            val desc = node.contentDescription.trim().lowercase()
            val txt = node.text.trim().lowercase()
            node.isSelected && (desc == "shorts" || txt == "shorts")
        }

        if (selectedShortsTab != null) {
            return DetectionResult(
                app = SupportedApp.YOUTUBE,
                featureLabel = "YouTube Shorts",
                matchedReason = "selected_shorts_tab",
            )
        }

        return null
    }

    private fun detectFacebookReels(nodes: List<UiNodeSnapshot>): DetectionResult? {
        val visibleNodes = nodes.filter { it.isVisible }

        // Never interfere with normal News Feed, Messages, or Profiles.
        val hasSafeExclusion = visibleNodes.any { node ->
            val id = node.viewId.lowercase()
            id.contains("feed_composer") ||
                id.contains("news_feed") ||
                id.contains("profile_header") ||
                id.contains("orca") ||
                id.contains("messenger")
        }

        val reelsPlayerNode = visibleNodes.firstOrNull { node ->
            val id = node.viewId.lowercase()
            id.contains("reels_viewer") ||
                id.contains("fb_reels_viewer") ||
                id.contains("reels_player") ||
                id.contains("reels_fragment_container") ||
                id.contains("short_form_video_player") ||
                id.contains("fb_shorts_viewer")
        }

        if (reelsPlayerNode != null && !hasSafeExclusion) {
            return DetectionResult(
                app = SupportedApp.FACEBOOK,
                featureLabel = "Facebook Reels",
                matchedReason = reelsPlayerNode.viewId,
            )
        }

        val selectedReelsTabWithShortFormContainer = visibleNodes.any { node ->
            val id = node.viewId.lowercase()
            id.contains("fb_shorts") || id.contains("reels_surface")
        } && visibleNodes.any { node ->
            val desc = node.contentDescription.trim().lowercase()
            val txt = node.text.trim().lowercase()
            node.isSelected && (desc == "reels" || txt == "reels")
        }

        if (selectedReelsTabWithShortFormContainer && !hasSafeExclusion) {
            return DetectionResult(
                app = SupportedApp.FACEBOOK,
                featureLabel = "Facebook Reels",
                matchedReason = "selected_reels_surface",
            )
        }

        return null
    }

    private fun collectSnapshots(
        node: AccessibilityNodeInfo,
        depth: Int,
        out: MutableList<UiNodeSnapshot>,
    ) {
        if (depth > MAX_TRAVERSAL_DEPTH || out.size >= MAX_TRAVERSAL_NODES) return

        out.add(
            UiNodeSnapshot(
                viewId = node.viewIdResourceName.orEmpty(),
                contentDescription = node.contentDescription?.toString().orEmpty(),
                text = node.text?.toString().orEmpty(),
                isSelected = node.isSelected,
                isVisible = node.isVisibleToUser,
            )
        )

        val childCount = node.childCount
        for (i in 0 until childCount) {
            if (out.size >= MAX_TRAVERSAL_NODES) break
            val child = node.getChild(i) ?: continue
            try {
                collectSnapshots(child, depth + 1, out)
            } finally {
                @Suppress("DEPRECATION")
                child.recycle()
            }
        }
    }
}
