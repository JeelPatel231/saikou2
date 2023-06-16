package ani.saikou2.data.tracker

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class Tracker {
    protected abstract suspend fun isLoggedIn(): Boolean

    // login the user in the service, side effects with database
    protected abstract suspend fun doLogin(callbackUri: Uri)

    // logout the user from the service, side effects with database
    protected abstract suspend fun doLogout()

    abstract suspend fun getUser(): User?

    // update a given media item, eg: its score, watching status
    abstract suspend fun postMedia(id: Int, score: Int, mediaListStatus: AppMediaListStatus)

    abstract suspend fun getCurrentAnime(): List<MediaCardData>

    abstract suspend fun getCurrentManga(): List<MediaCardData>


    // DO NOT OVERRIDE
    // live updates of logged in user, null if not logged in
    private val _loggedInUser: MutableStateFlow<User?> = MutableStateFlow(null)
    val loggedInUser = _loggedInUser.asStateFlow()

    private suspend fun updateUser(){
        _loggedInUser.value = getUser()
    }

    suspend fun login(uri: Uri) = withContext(Dispatchers.IO){
            doLogin(uri)
            updateUser()
    }

    suspend fun logout() = withContext(Dispatchers.IO){
        doLogout()
        updateUser()
    }

    fun initialize(){
        CoroutineScope(Dispatchers.IO).launch {
            if (isLoggedIn()) updateUser()
        }
    }
}