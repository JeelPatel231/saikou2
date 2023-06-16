package ani.saikou2.data.anilistclient.anime

import com.apollographql.apollo3.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnimeClientDI {

    @Provides
    @Singleton
    fun providesAnimeClient(
        apolloClient: ApolloClient
    ): AnimeClient {
        return AnimeClientAnilistImpl(apolloClient)
    }

}