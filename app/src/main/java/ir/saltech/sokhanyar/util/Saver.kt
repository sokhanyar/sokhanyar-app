package ir.saltech.sokhanyar.util

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val APPLICATION_DATASTORE = "app_data"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APPLICATION_DATASTORE)

operator fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): T? {
    var dataFlow: T? = null
    runBlocking {
        launch {
            dataFlow = data.map { preferences ->
                preferences[key]
            }.first()
            Log.w("TAG", "Loading app settings...")
        }
    }
    return dataFlow
}

operator fun <T> DataStore<Preferences>.set(key: Preferences.Key<T>, value: T) {
    runBlocking {
        launch {
            edit { preferences ->
                preferences[key] = value
            }
            Log.w("TAG", "Setting app settings...")
        }
    }
}
