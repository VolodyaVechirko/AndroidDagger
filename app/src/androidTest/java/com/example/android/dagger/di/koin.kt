package com.example.android.dagger.di

import com.example.android.dagger.storage.FakeStorage
import com.example.android.dagger.storage.Storage
import org.koin.dsl.module

val testModule = module {
    factory<Storage>(override = true) {
        FakeStorage()
    }
}