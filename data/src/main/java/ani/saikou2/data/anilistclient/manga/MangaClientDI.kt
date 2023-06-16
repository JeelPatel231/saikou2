package ani.saikou2.data.anilistclient.manga

import com.apollographql.apollo3.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MangaClientDI {

    @Provides
    @Singleton
    fun providesMangaClient(
        apolloClient: ApolloClient
    ): MangaClient {
        return AnilistMangaClientImpl(apolloClient)
    }

}