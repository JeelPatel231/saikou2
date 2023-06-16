package ani.saikou2.data.tracker

import ani.saikou2.data.database.AppDatabase
import com.apollographql.apollo3.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.brahmkshatriya.nicehttp.ignoreAllSSLErrors
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrackerDI {

    @Provides
    @Singleton
    fun providesNoLoginApolloAnilistClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://graphql.anilist.co/")
            .build()
    }

    @Provides
    @Singleton
    fun providesHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .ignoreAllSSLErrors()
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    @Provides
    @Singleton
    fun providesAnilistData(): AnilistData {
        return AnilistData(
            "6604",
            "15UaWKmaJtkxoUGloCFq4zEwRaM9AHHtQ2nQXiJ1",
            "saikou://logintracker/anilist"
        )
    }

    @Provides
    @Singleton
    fun providesTracker(
        appDatabase: AppDatabase,
        httpClient: OkHttpClient,
        anilistData: AnilistData
    ): Tracker {
        return AnilistTrackerImpl(
            trackerDao = appDatabase.trackerDao(),
            httpClient = httpClient,
            anilistData = anilistData
        ).apply {
            initialize()
        }
    }
}