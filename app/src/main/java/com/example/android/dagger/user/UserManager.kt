/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.dagger.user

import com.example.android.dagger.di.SESSION_SCOPE_ID
import com.example.android.dagger.storage.Storage
import org.koin.core.KoinComponent
import org.koin.core.qualifier.named

private const val REGISTERED_USER = "registered_user"
private const val PASSWORD_SUFFIX = "password"

/**
 * Handles User lifecycle. Manages registrations, logs in and logs out.
 * Knows when the user is logged in.
 *
 * Marked with @Singleton since we only one an instance of UserManager in the application graph.
 */
class UserManager constructor(
    private val storage: Storage
    // Since UserManager will be in charge of managing the UserComponent lifecycle,
    // it needs to know how to create instances of it
): KoinComponent {

    /**
    *  UserComponent is specific to a logged in user. Holds an instance of UserComponent.
    *  This determines if the user is logged in or not, when the user logs in,
    *  a new Component will be created. When the user logs out, this will be null.
    */

    val username: String
        get() = storage.getString(REGISTERED_USER)

    fun isUserLoggedIn() = getKoin().getScopeOrNull(SESSION_SCOPE_ID) != null

    fun isUserRegistered() = storage.getString(REGISTERED_USER).isNotEmpty()

    fun registerUser(username: String, password: String) {
        storage.setString(REGISTERED_USER, username)
        storage.setString("$username$PASSWORD_SUFFIX", password)
        userJustLoggedIn()
    }

    fun loginUser(username: String, password: String): Boolean {
        val registeredUser = this.username
        if (registeredUser != username) return false

        val registeredPassword = storage.getString("$username$PASSWORD_SUFFIX")
        if (registeredPassword != password) return false

        userJustLoggedIn()
        return true
    }

    fun logout() {
        // When the user logs out, we remove the instance of UserComponent from memory
        getKoin().getScopeOrNull(SESSION_SCOPE_ID)?.close()
    }

    fun unregister() {
        val username = storage.getString(REGISTERED_USER)
        storage.setString(REGISTERED_USER, "")
        storage.setString("$username$PASSWORD_SUFFIX", "")
        logout()
    }

    private fun userJustLoggedIn() {
        // When the user logs in, we create a new instance of UserComponent
        getKoin().getOrCreateScope(SESSION_SCOPE_ID, named("session"))
    }
}
