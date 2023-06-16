package ani.saikou2.domain.animeclient

import ani.saikou2.data.anilistclient.anime.AnimeClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnimeUseCaseDI {

    @Provides
    @Singleton
    fun providesGetDetailsUseCase(
        animeClient: AnimeClient
    ) : GetAnimeDetailsUseCase {
        return GetAnimeDetailsUseCase(animeClient)
    }

    @Provides
    @Singleton
    fun providesAnimeClientUseCases(
        animeClient: AnimeClient
    ) : AnimeClientUseCases {
        return AnimeClientUseCases(
            getPopularAnime = GetPopularAnimeUseCase(animeClient),
            getRecentlyUpdatedAnime = GetRecentlyUpdatedAnimeUseCase(animeClient),
            getTrendingAnime = GetTrendingAnimeUseCase(animeClient)
        )
    }

}