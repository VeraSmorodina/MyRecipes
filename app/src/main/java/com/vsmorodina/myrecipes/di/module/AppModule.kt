package com.vsmorodina.myrecipes.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val app: Application) {

    @Provides
    fun provideApplicationContext(): Context {
        return app
    }

    @Provides
    fun provideApplication(): Application {
        return app
    }
}