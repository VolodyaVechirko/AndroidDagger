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

package com.example.android.dagger.storage

import com.example.android.dagger.di.sessionModule
import com.example.android.dagger.user.UserManager
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

class UserManagerTest : KoinTest {

    @Before
    fun setup() {
        startKoin {
            modules(testModule, sessionModule)
        }
    }

    @Test
    fun `Username returns what is in the storage`() {
        val userManager = getKoin().get<UserManager>()
        assertEquals("", userManager.username)

        userManager.registerUser("username", "password")

        assertEquals("username", userManager.username)
    }

    @Test
    fun `IsUserRegistered behaves as expected`() {
        val userManager = getKoin().get<UserManager>()
        assertFalse(userManager.isUserRegistered())

        userManager.registerUser("username", "password")

        assertTrue(userManager.isUserRegistered())
    }

    @Test
    fun `Register user adds username and password to the storage`() {
        val userManager = getKoin().get<UserManager>()
        val storage = getKoin().get<Storage>()

        assertFalse(userManager.isUserRegistered())
        assertFalse(userManager.isUserLoggedIn())

        userManager.registerUser("username", "password")

        assertTrue(userManager.isUserRegistered())
        assertTrue(userManager.isUserLoggedIn())
        assertEquals("username", storage.getString("registered_user"))
        assertEquals("password", storage.getString("usernamepassword"))
    }

    @Test
    fun `Login succeeds when username is registered and password is correct`() {
        val userManager = getKoin().get<UserManager>()
        userManager.registerUser("username", "password")
        userManager.logout()

        assertTrue(userManager.loginUser("username", "password"))
        assertTrue(userManager.isUserLoggedIn())
    }

    @Test
    fun `Login fails when username is not registered`() {
        val userManager = getKoin().get<UserManager>()
        userManager.registerUser("username", "password")
        userManager.logout()

        assertFalse(userManager.loginUser("username2", "password"))
        assertFalse(userManager.isUserLoggedIn())
    }

    @Test
    fun `Login fails when username is registered but password is incorrect`() {
        val userManager = getKoin().get<UserManager>()
        userManager.registerUser("username", "password")
        userManager.logout()

        assertFalse(userManager.loginUser("username", "password2"))
        assertFalse(userManager.isUserLoggedIn())
    }

    @Test
    fun `Unregister behaves as expected`() {
        val userManager = getKoin().get<UserManager>()
        userManager.registerUser("username", "password")
        assertTrue(userManager.isUserLoggedIn())

        userManager.unregister()
        assertFalse(userManager.isUserLoggedIn())
        assertFalse(userManager.isUserRegistered())
    }

    @After
    fun autoClose() {
        stopKoin()
    }
}
