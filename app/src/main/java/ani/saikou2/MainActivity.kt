package ani.saikou2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ani.saikou2.databinding.ActivityMainBinding
import ani.saikou2.domain.tracker.TrackerUseCases
import ani.saikou2.ui.generic.getOuterNavController
import ani.saikou2.ui.generic.observeFlow
import ani.saikou2.ui.generic.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var trackerUseCases: TrackerUseCases
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch(Dispatchers.IO) { handleLogin() }

        // code start
        onBackPressedDispatcher.addCallback(this) {
            // Back is pressed... Finishing the activity
            if (getOuterNavController().popBackStack().not()) {
                finish()
            }
        }
    }

    private suspend fun handleLogin() {
        val data = intent?.data ?: return
        //"saikou://logintracker/anilist/?access_token=...&token_type=...")
        if(data.authority == "logintracker" && data.path == "/anilist") {
            trackerUseCases.loginUserUseCase(data)
            showToast("Logged Into Anilist!", Toast.LENGTH_SHORT)
        } else {
            showToast("Unknown Link, Cannot Handle!", Toast.LENGTH_SHORT)
        }
    }
}
