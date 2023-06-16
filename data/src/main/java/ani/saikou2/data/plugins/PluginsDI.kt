package ani.saikou2.data.plugins

import android.app.Application
import ani.saikou2.data.database.AppDatabase
import ani.saikou2.reference.AnimeParser
import ani.saikou2.reference.VideoExtractor
import ani.saikou2.reference.associateNotNull
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PluginsDI {

    @Provides
    @Singleton
    fun providesPluginManager(
        appDatabase: AppDatabase,
        httpClient: OkHttpClient,
        appContext: Application
    ): PluginManager {
        return PluginManager(appDatabase, httpClient, appContext)
    }
}