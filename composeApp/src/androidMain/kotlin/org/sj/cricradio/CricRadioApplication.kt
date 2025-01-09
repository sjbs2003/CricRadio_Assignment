package org.sj.cricradio

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.sj.cricradio.Network.networkModule
import org.sj.cricradio.data.repository.repositoryModule
import org.sj.cricradio.presentation.viewmodelModule

class CricRadioApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@CricRadioApplication)
            modules(
                listOf(
                    networkModule,
                    repositoryModule,
                    viewmodelModule
                )
            )
        }
    }
}