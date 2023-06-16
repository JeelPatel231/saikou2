package ani.saikou2.di

import android.app.Application
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import ani.saikou2.data.database.AppDatabase
import ani.saikou2.data.plugins.loadPluginSafe
import ani.saikou2.reference.AnimeParser
import ani.saikou2.reference.VideoExtractor
import ani.saikou2.reference.associateNotNull
import ani.saikou2.usecases.CreateMediaSourceFromUri
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Injection {

    @Provides
    @Singleton
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun providesCreateMediaSourceFromUri(
        okHttpClient: OkHttpClient,
        simpleVideoCache: SimpleCache
    ):CreateMediaSourceFromUri {
        return CreateMediaSourceFromUri(okHttpClient, simpleVideoCache)
    }

    @Provides
    @Singleton
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun provideSimpleVideoCache(application: Application): SimpleCache {
        val databaseProvider = StandaloneDatabaseProvider(application)
        return SimpleCache(
            File(application.cacheDir, "exoplayer").also { it.deleteOnExit() }, // Ensures always fresh file
            LeastRecentlyUsedCacheEvictor(300L * 1024L * 1024L),
            databaseProvider
        )
    }

    @Provides
    @Singleton
    fun providesExoplayer(
        application: Application
    ) : ExoPlayer {
        return ExoPlayer.Builder(application).build()
    }


    class ParserProxy(val data: Map<String, AnimeParser>)
    @Provides
    @Singleton
    fun provideParsers(database: AppDatabase, ctx: Application) : ParserProxy = runBlocking {
        return@runBlocking ParserProxy(database.parserDao().listAll().associateNotNull {
            it.className to loadPluginSafe<AnimeParser>(ctx, "${it.className}.jar", it.className)
        })
    }

    class ExtractorProxy(val data: Map<String, VideoExtractor>)
    @Provides
    @Singleton
    fun provideExtractors(database: AppDatabase, ctx: Application) : ExtractorProxy = runBlocking {
        return@runBlocking ExtractorProxy(database.extractorDao().listAll().associateNotNull {
            it.className to loadPluginSafe<VideoExtractor>(ctx, "${it.className}.jar", it.className)
        })
    }


}