package com.example.socialvocal.sessionManagement

object SessionManager {
    private var currentUser: String? = null

    fun setCurrentUser(username: String) {
        currentUser = username
    }

    fun getCurrentUser(): String? {
        return currentUser
    }

    fun clearSession() {
        currentUser = null
    }
}
