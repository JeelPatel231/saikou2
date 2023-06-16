package ani.saikou2.ui.fragments.home

import ani.saikou2.R
import ani.saikou2.databinding.NoLoginLayoutBinding
import ani.saikou2.domain.tracker.LoginUserUseCase
import ani.saikou2.domain.tracker.StartLoginTrackerUseCase
import ani.saikou2.ui.fragments.MainFragmentDirections
import ani.saikou2.ui.generic.ViewBindingFragment
import ani.saikou2.ui.generic.getOuterNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoLoginFragment: ViewBindingFragment<NoLoginLayoutBinding>(NoLoginLayoutBinding::inflate) {
    @Inject lateinit var startLoginTrackerUseCase: StartLoginTrackerUseCase

    override fun onCreateBindingView() {
        binding.loginButton.setOnClickListener {
            startLoginTrackerUseCase(requireActivity())
        }

        binding.pluginsButton.setOnClickListener {
            getOuterNavController().navigate(MainFragmentDirections.toPluginFragment())
        }
    }
}