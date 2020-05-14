package com.example.android.dagger.di

import com.example.android.dagger.login.LoginActivity
import com.example.android.dagger.login.LoginViewModel
import com.example.android.dagger.main.MainActivity
import com.example.android.dagger.main.MainViewModel
import com.example.android.dagger.registration.RegistrationActivity
import com.example.android.dagger.registration.RegistrationViewModel
import com.example.android.dagger.registration.enterdetails.EnterDetailsViewModel
import com.example.android.dagger.settings.SettingsActivity
import com.example.android.dagger.settings.SettingsViewModel
import com.example.android.dagger.storage.SharedPreferencesStorage
import com.example.android.dagger.storage.Storage
import com.example.android.dagger.user.UserDataRepository
import com.example.android.dagger.user.UserManager
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

// I intentionally do not use viewModel dsl for ViewModels
// to achieve component scoping with pure Koin using scope

// Application scope
val appModule = module {
    single<UserManager> {
        UserManager(get())
    }

    factory<Storage> {
        SharedPreferencesStorage(get())
    }
}

const val SESSION_SCOPE_ID = "sessionID"

// MainActivity, SettingsActivity scope
val sessionModule = module {
    // bind UserDataRepository to custom scope
    scope(named("session")) {
        scoped { UserDataRepository(get()) }
    }

    scope<SettingsActivity> {
        scoped { SettingsViewModel(getSession(), get()) }
    }

    scope<MainActivity> {
        scoped { MainViewModel(getSession()) }
    }
}

// LoginActivity scope
val loginModule = module {
    // LoginViewModel bound to LoginActivity scope
    scope<LoginActivity> {
        scoped {
            LoginViewModel(get())
        }
    }
}

// RegistrationActivity scope
val registrationModule = module {
    // RegistrationViewModel bound to RegistrationActivity scope
    scope<RegistrationActivity> {
        scoped {
            RegistrationViewModel(get())
        }
    }

    factory { EnterDetailsViewModel() }
}

/**
 * Get instance bound to "session" scope
 * It use existing "session" scope or create it if need
 *
 * @return instance of type T
 */
inline fun <reified T> Scope.getSession(): T {
    val scope = getKoin().getOrCreateScope(SESSION_SCOPE_ID, named("session"))
    return scope.getScope(SESSION_SCOPE_ID).get<T>()
}