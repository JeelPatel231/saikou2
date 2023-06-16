package ani.saikou2.domain.animeclient

import ani.saikou2.data.anilistclient.anime.AnimeClient

data class AnimeClientUseCases(
    val getTrendingAnime: GetTrendingAnimeUseCase,
    val getPopularAnime: GetPopularAnimeUseCase,
    val getRecentlyUpdatedAnime: GetRecentlyUpdatedAnimeUseCase
)

class GetTrendingAnimeUseCase (
    private val animeClient: AnimeClient
) {
    suspend operator fun invoke() =
        animeClient.getTrendingAnime()
}

class GetPopularAnimeUseCase (
    private val animeClient: AnimeClient
) {
    suspend operator fun invoke() =
        animeClient.getPopularAnime()
}

class GetRecentlyUpdatedAnimeUseCase(
    private val animeClient: AnimeClient
) {
    suspend operator fun invoke() =
        animeClient.getRecentlyUpdated()
}

class GetAnimeDetailsUseCase(
    private val animeClient: AnimeClient
) {
    suspend operator fun invoke(id: Int) =
        animeClient.getAnimeDetails(id)
}