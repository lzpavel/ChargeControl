package com.lzpavel.chargecontrol

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideChargeSettings(): ChargeSettings {
        return ChargeSettings.get()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(dataStore: DataStore<Preferences>, chargeSettings: ChargeSettings): DataStoreManager {
        return DataStoreManager(dataStore, chargeSettings)
    }



//    @Provides
//    @Singleton
//    fun providePreferencesDataStore(@ApplicationContext context: Context): PreferencesDataStore {
//        return PreferencesDataStore(context)
//    }

//    @Provides
//    @Singleton
//    fun dataStore(@ApplicationContext appContext: Context): DataStore<Preferences> =
//        appContext.createDataStore("settings")

//    @Singleton
//    @Provides
//    fun providePerson(): Person {
//        return Person("Tom", 32)
//    }

}