package ani.saikou2.data.anilistclient.manga

import ani.saikou2.data.tracker.MediaCardData
import ani.saikou2.data.tracker.MediaDetailsFull


interface MangaClient {
    suspend fun getTrendingManga(): List<MediaCardData>

    suspend fun getPopularManga(): List<MediaCardData>

    suspend fun getTrendingNovel(): List<MediaCardData>

    suspend fun getMangaDetails(id: Int) : MediaDetailsFull
}