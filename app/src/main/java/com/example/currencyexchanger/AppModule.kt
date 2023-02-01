package com.example.currencyexchanger

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object AppModule {
    @Provides
    fun provideTestString() = "This is a string to inject"
}