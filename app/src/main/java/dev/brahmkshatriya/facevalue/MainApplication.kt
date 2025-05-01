package dev.brahmkshatriya.facevalue

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import androidx.work.WorkManager
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.allowHardware
import coil3.request.crossfade
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration
import java.util.concurrent.Executors

@OptIn(KoinExperimentalAPI::class)
class MainApplication : Application(), KoinStartup, SingletonImageLoader.Factory {

    override fun onKoinStartup() = koinConfiguration {
        androidContext(this@MainApplication)
        modules(DI.appModule)

        val factory = DelegatingWorkerFactory().apply {
            addFactory(KoinWorkerFactory())
        }
        val conf = Configuration.Builder()
            .setWorkerFactory(factory)
            .setExecutor(Executors.newFixedThreadPool(MAX_WORKERS))
            .build()
        WorkManager.initialize(koin.get(), conf)
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(1024 * 1024 * 100) // 100MB
                    .build()
            }
            .allowHardware(false)
            .crossfade(100)
            .build()
    }

    companion object {
        private const val MAX_WORKERS = 2
    }
}