package com.example.socialvocal.sessionManagement

object SessionManager {
    private var currentUser: String? = null
    private var listUser = listOf("user1", "user2", "user3")

    fun setCurrentUser(username: String) {
        currentUser = username
    }

    fun getCurrentUser(): String? {
        return currentUser
    }

    fun getListUser(): List<String> {
        return listUser
    }

    fun clearSession() {
        currentUser = null
    }
}
