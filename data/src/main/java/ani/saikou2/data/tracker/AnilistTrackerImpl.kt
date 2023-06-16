package ani.saikou2.data.tracker

import android.net.Uri
import ani.saikou2.CurrentUserMediaQuery
import ani.saikou2.GetViewerDataQuery
import ani.saikou2.type.MediaListStatus
import ani.saikou2.type.MediaStatus
import ani.saikou2.type.MediaType
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


data class AnilistData(
    val id: String,
    val secret: String,
    val redirectUri: String,
)


class AuthorizationInterceptor(
    private val trackerData: Flow<TrackerData?>,
    val refreshCallback: () -> Unit,
) : HttpInterceptor {
    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain
    ): HttpResponse {
        val token = trackerData.first()?.accessToken
            ?: throw IllegalStateException("User Not Logged In")

        val response =
            chain.proceed(request.newBuilder().addHeader("Authorization", "Bearer $token").build())

        return if (response.statusCode == 401) {
            refreshCallback()
            chain.proceed(request.newBuilder().addHeader("Authorization", "Bearer $token").build())
        } else {
            response
        }
    }
}

class AnilistTrackerImpl(
    private val trackerDao: TrackerDao,
    private val httpClient: OkHttpClient,
    private val anilistData: AnilistData
) : Tracker() {
    private val _loggedInUser: MutableStateFlow<User?> = MutableStateFlow(null)
    private val trackerData = trackerDao.getTrackerData()

    override suspend fun isLoggedIn(): Boolean {
        return trackerData.first() != null
    }

    private fun refreshToken(){
        TODO("Anilist Token Expired :scull:")
    }

    private val apolloClient = ApolloClient.Builder()
        .serverUrl("https://graphql.anilist.co")
        .addHttpInterceptor(AuthorizationInterceptor(trackerData, ::refreshToken))
        .build()

    override suspend fun doLogin(callbackUri: Uri) {
        val code = callbackUri.getQueryParameter("code")
            ?: throw IllegalStateException("Code not found!")


        val request = Request.Builder()
            .url("https://anilist.co/api/v2/oauth/token")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .post(
                Json.encodeToString(
                    AnilistRequestBody(
                        grant_type = "authorization_code",
                        client_id = anilistData.id,
                        client_secret = anilistData.secret,
                        redirect_uri = anilistData.redirectUri,
                        code = code
                    )
                ).toRequestBody()
            ).build()

        val response = httpClient.newCall(request).execute()
        val decoded = Json.decodeFromString<AnilistResponseBody>(response.body.string())

        trackerDao.upsertTracker(
            TrackerData(
                accessToken = decoded.access_token,
                refreshToken = decoded.refresh_token,
                expiration = decoded.expires_in
            )
        )
    }

    override suspend fun doLogout() {
        trackerDao.deleteTracker(trackerDao.getTrackerData().first()!!)
        _loggedInUser.value = null
    }


    override suspend fun postMedia(id: Int, score: Int, mediaListStatus: AppMediaListStatus) {
        TODO("Not yet implemented")
    }

    private suspend fun getUserMedia(
        userId: Int,
        type: MediaType? = null,
        status: MediaListStatus
    ): List<CurrentUserMediaQuery.Media> {
        return apolloClient.query(
            CurrentUserMediaQuery(
                userId = userId,
                type = Optional.present(type),
                status = Optional.present(status)
            )
        ).execute()
            .data?.MediaListCollection?.lists?.flatMap { it?.entries ?: emptyList() }
            ?.mapNotNull { it?.media } ?: emptyList()
    }

    override suspend fun getUser(): User? {
        return try {
                apolloClient.query(GetViewerDataQuery()).execute()
                    .data?.Viewer?.let {
                        User(
                            userId = it.id,
                            username = it.name,
                            bannerImage = it.bannerImage,
                            chapterCount = it.statistics?.manga?.chaptersRead ?: 0,
                            episodeCount = it.statistics?.anime?.episodesWatched ?: 0,
                            profileImage = it.avatar?.medium!!
                        )
                    }
            } catch (e: Throwable) {
                null
            }
    }

    override suspend fun getCurrentAnime(): List<MediaCardData> {
        val userId = loggedInUser.value?.userId
            ?: throw IllegalStateException("No Logged In user Found")

        return getUserMedia(userId, MediaType.ANIME, MediaListStatus.CURRENT).map {
            MediaCardData(
                id = it.id,
                title = it.title?.english ?: it.title?.romaji ?: it.title?.userPreferred!!,
                status = it.status!!.toApp(),
                type = it.type!!.toApp(),
                isAdult = it.isAdult ?: false,
                meanScore = (it.meanScore ?: 0)/10f,
                coverImage = it.coverImage?.large!!,
                nextAiringEpisode = it.nextAiringEpisode?.episode,
                episodes = it.episodes,
                chapters = it.chapters,
            )
        }
    }

    override suspend fun getCurrentManga(): List<MediaCardData> {
        val userId = loggedInUser.value?.userId
            ?: throw IllegalStateException("No Logged In user Found")

        return getUserMedia(userId, MediaType.MANGA, MediaListStatus.CURRENT).map {
            MediaCardData(
                id = it.id,
                title = it.title?.english ?: it.title?.romaji ?: it.title?.userPreferred!!,
                status = it.status!!.toApp(),
                type = it.type!!.toApp(),
                isAdult = it.isAdult ?: false,
                meanScore = (it.meanScore ?: 0)/10f,
                coverImage = it.coverImage?.large!!,
                nextAiringEpisode = it.nextAiringEpisode?.episode,
                episodes = it.episodes,
                chapters = it.chapters,
            )
        }
    }
}