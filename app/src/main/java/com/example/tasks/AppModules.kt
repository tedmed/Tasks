package com.example.tasks

import androidx.room.Room
import com.example.tasks.datastore.DataStoreManager
import com.example.tasks.db.TodoDatabase
import com.example.tasks.repository.SettingsRepository
import com.example.tasks.viewmodel.SettingsViewModel
import com.example.tasks.viewmodel.TodoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val roomDBModule = module {
    single {
        Room.databaseBuilder(get(), TodoDatabase::class.java, TodoDatabase.NAME)
            .build()
    }

    single { get<TodoDatabase>().getTodoDao() }
}

val settingsModule = module {
    single { DataStoreManager(androidContext()) }
    single { SettingsRepository(get()) }
}

val viewModelModule = module {
    viewModel { TodoViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}
