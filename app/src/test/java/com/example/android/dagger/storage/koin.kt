package com.example.android.dagger.storage

import com.example.android.dagger.user.UserManager
import org.koin.dsl.module

val testModule = module {
    single<UserManager> {
        UserManager(get())
    }

    // Storage is single for test. to verify values
    single<Storage> {
        FakeStorage()
    }
}