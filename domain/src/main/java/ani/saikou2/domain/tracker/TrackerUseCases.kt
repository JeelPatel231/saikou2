package ani.saikou2.domain.tracker

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import ani.saikou2.data.tracker.AnilistData
import ani.saikou2.data.tracker.Tracker
import ani.saikou2.data.tracker.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TrackerUseCases(
    val loginUserUseCase: LoginUserUseCase,
    val logoutUserUseCase: LogoutUserUseCase,
    val getUserDataUseCase: GetUserDataUseCase,
)

class StartLoginTrackerUseCase(
    private val anilistData: AnilistData
) {
    operator fun invoke(ctx: Context){
        val data = Uri.parse("https://anilist.co/api/v2/oauth/authorize?client_id=${anilistData.id}&redirect_uri=${anilistData.redirectUri}&response_type=code")
        val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        defaultBrowser.data = data
        startActivity(ctx, defaultBrowser, null)
    }
}

class LoginUserUseCase(
    private val tracker: Tracker,
){
    suspend operator fun invoke(uri: Uri) =
        tracker.login(uri)
}

class LogoutUserUseCase(
    private val tracker: Tracker,
){
    suspend operator fun invoke() = withContext(Dispatchers.IO){
        tracker.logout()
    }
}

class GetUserDataUseCase(
    private val tracker: Tracker,
) {
    operator fun invoke(): StateFlow<User?> {
        return tracker.loggedInUser
    }
}