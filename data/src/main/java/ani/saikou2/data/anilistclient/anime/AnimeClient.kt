package ani.saikou2.data.anilistclient.anime

import ani.saikou2.data.tracker.MediaDetailsFull
import ani.saikou2.data.tracker.MediaCardData

interface AnimeClient {
    suspend fun getTrendingAnime() : List<MediaCardData>

    suspend fun getPopularAnime() : List<MediaCardData>

    suspend fun getRecentlyUpdated() : List<MediaCardData>

    suspend fun getAnimeDetails(id: Int) : MediaDetailsFull
}
