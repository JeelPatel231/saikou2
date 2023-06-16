package ani.saikou2.data.anilistclient.manga

import ani.saikou2.AnimeRecentlyUpdatedQuery
import ani.saikou2.data.anilistclient.BaseAnilistClient
import ani.saikou2.data.anilistclient.anime.AnimeClient
import ani.saikou2.data.tracker.MediaCardData
import ani.saikou2.data.tracker.MediaDetailsFull
import ani.saikou2.data.tracker.toApp
import ani.saikou2.type.MediaFormat
import ani.saikou2.type.MediaSort
import ani.saikou2.type.MediaType
import com.apollographql.apollo3.ApolloClient

class AnilistMangaClientImpl(override val anilistClient: ApolloClient) : BaseAnilistClient(), MangaClient {

    override suspend fun getTrendingManga(): List<MediaCardData> =
        executeBaselineMediaQuery(
            page = 1,
            perPage = 30,
            sort = listOf(MediaSort.TRENDING_DESC),
            type = MediaType.MANGA
        )

    override suspend fun getPopularManga(): List<MediaCardData> =
        executeBaselineMediaQuery(
            page = 1,
            perPage = 30,
            sort = listOf(MediaSort.POPULARITY_DESC),
            type = MediaType.MANGA
        )


    override suspend fun getTrendingNovel(): List<MediaCardData> =
        executeBaselineMediaQuery(
            page = 1,
            perPage = 30,
            sort = listOf(MediaSort.TRENDING_DESC),
            type = MediaType.MANGA,
            format = MediaFormat.NOVEL
        )

    override suspend fun getMangaDetails(id: Int): MediaDetailsFull = getMediaDetails(id)
}