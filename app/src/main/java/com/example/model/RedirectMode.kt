package com.example.model

enum class RedirectMode(val key: String) {
    NAVIGATE_BACK("navigate_back"),
    GO_HOME("go_home");

    companion object {
        fun fromKey(key: String?): RedirectMode {
            return entries.firstOrNull { it.key == key } ?: NAVIGATE_BACK
        }
    }
}
