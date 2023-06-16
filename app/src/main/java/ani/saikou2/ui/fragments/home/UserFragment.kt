package ani.saikou2.ui.fragments.home

import androidx.lifecycle.lifecycleScope
import ani.saikou2.MainActivity
import ani.saikou2.R
import ani.saikou2.databinding.FragmentUserBinding
import ani.saikou2.domain.tracker.TrackerUseCases
import ani.saikou2.ui.fragments.MainFragmentDirections
import ani.saikou2.ui.generic.ViewBindingFragment
import ani.saikou2.ui.generic.getOuterNavController
import ani.saikou2.ui.generic.observeFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment: ViewBindingFragment<FragmentUserBinding>(FragmentUserBinding::inflate) {
    @Inject
    lateinit var trackerUseCases: TrackerUseCases


    override fun onCreateBindingView() {
        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch { trackerUseCases.logoutUserUseCase() }
        }

        binding.pluginsButton.setOnClickListener {
            getOuterNavController().navigate(MainFragmentDirections.toPluginFragment())
        }

        trackerUseCases.getUserDataUseCase().observeFlow(viewLifecycleOwner) {
            it?.let {
                binding.username.text = it.username
            }
        }
    }
}