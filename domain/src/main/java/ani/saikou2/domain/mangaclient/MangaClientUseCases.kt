package ani.saikou2.domain.mangaclient

import ani.saikou2.data.anilistclient.manga.MangaClient

data class MangaClientUseCases(
    val getTrendingManga: GetTrendingMangaUseCase,
    val getPopularManga: GetPopularMangaUseCase,
    val getTrendingNovel: GetTrendingNovelUseCase
)

class GetTrendingMangaUseCase (
    private val mangaClient: MangaClient
) {
    suspend operator fun invoke() =
        mangaClient.getTrendingManga()
}

class GetPopularMangaUseCase (
    private val mangaClient: MangaClient
) {
    suspend operator fun invoke() =
        mangaClient.getPopularManga()
}

class GetTrendingNovelUseCase(
    private val mangaClient: MangaClient
) {
    suspend operator fun invoke() =
        mangaClient.getTrendingNovel()
}

class GetMangaDetailsUseCase(
    private val mangaClient: MangaClient
) {
    suspend operator fun invoke(id: Int) =
        mangaClient.getMangaDetails(id)
}