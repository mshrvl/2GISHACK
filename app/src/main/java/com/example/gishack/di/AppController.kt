package com.example.gishack.di

import android.app.Application
import com.example.auth.di.authModule
import com.example.data.dataModule
import com.example.map.di.mapModule
import com.example.profile.profilemodule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import ru.dgis.sdk.DGis

class AppController : Application() {
    override fun onCreate() {
        DGis.initialize(this@AppController)
        super.onCreate()
        startKoin {
            androidContext(this@AppController)
            modules(applicationModule)
        }
    }
}

val applicationModule = module {
    includes(authModule, mapModule, profilemodule, dataModule)
}