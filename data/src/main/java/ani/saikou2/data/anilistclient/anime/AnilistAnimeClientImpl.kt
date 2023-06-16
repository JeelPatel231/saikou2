package ani.saikou2.data.anilistclient.anime

import ani.saikou2.AnimeRecentlyUpdatedQuery
import ani.saikou2.data.anilistclient.BaseAnilistClient
import ani.saikou2.data.tracker.MediaDetailsFull
import ani.saikou2.data.tracker.MediaCardData
import ani.saikou2.data.tracker.toApp
import ani.saikou2.type.MediaSort
import ani.saikou2.type.MediaType
import com.apollographql.apollo3.ApolloClient

class AnimeClientAnilistImpl(
    override val anilistClient: ApolloClient
) : BaseAnilistClient(), AnimeClient {

    override suspend fun getTrendingAnime(): List<MediaCardData> {
        return executeBaselineMediaQuery(
                page = 1,
                perPage = 30,
                sort = listOf(MediaSort.TRENDING_DESC),
                type = MediaType.ANIME
        )
    }

    override suspend fun getPopularAnime(): List<MediaCardData> {
        return executeBaselineMediaQuery(
                page = 1,
                perPage = 30,
                sort = listOf(MediaSort.POPULARITY_DESC),
                type = MediaType.ANIME
            )
    }

    override suspend fun getRecentlyUpdated(): List<MediaCardData> {
        return anilistClient.query(
            AnimeRecentlyUpdatedQuery(lesser = (System.currentTimeMillis() / 1000).toInt() - 10000)
        ).execute().data?.Page?.airingSchedules?.mapNotNull { it?.media }?.map {
            MediaCardData(
                id = it.id,
                title = it.title?.english ?: it.title?.romaji ?: it.title?.userPreferred!!,
                status = it.status!!.toApp(),
                type = it.type!!.toApp(),
                isAdult = it.isAdult ?: false,
                meanScore = (it.meanScore ?: 0) / 10f,
                coverImage = it.coverImage?.large!!,
                nextAiringEpisode = it.nextAiringEpisode?.episode,
                episodes = it.episodes,
                chapters = it.chapters,
            )
        } ?: emptyList()
    }

    override suspend fun getAnimeDetails(id: Int): MediaDetailsFull  = getMediaDetails(id)
}
