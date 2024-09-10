package com.lzpavel.chargecontrol

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

//    @Singleton
//    @Provides
//    fun providePerson(): Person {
//        return Person("Tom", 32)
//    }

}