package ani.saikou2.domain.mangaclient

import ani.saikou2.data.anilistclient.anime.AnimeClient
import ani.saikou2.data.anilistclient.manga.MangaClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MangaUseCaseDI {

    @Provides
    @Singleton
    fun providesGetDetailsUseCase(
        mangaClient: MangaClient
    ) : GetMangaDetailsUseCase {
        return GetMangaDetailsUseCase(mangaClient)
    }

    @Provides
    @Singleton
    fun providesMangaClientUseCases(
        mangaClient: MangaClient
    ) : MangaClientUseCases {
        return MangaClientUseCases(
            getPopularManga = GetPopularMangaUseCase(mangaClient),
            getTrendingNovel = GetTrendingNovelUseCase(mangaClient),
            getTrendingManga = GetTrendingMangaUseCase(mangaClient)
        )
    }

}