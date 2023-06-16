package ani.saikou2.ui.fragments

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ani.saikou2.databinding.FragmentMainBinding
import ani.saikou2.domain.tracker.StartLoginTrackerUseCase
import ani.saikou2.domain.tracker.TrackerUseCases
import ani.saikou2.ui.fragments.home.NoLoginFragment
import ani.saikou2.ui.fragments.home.UserFragment
import ani.saikou2.ui.generic.ViewBindingFragment
import ani.saikou2.ui.generic.observeFlow
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment: ViewBindingFragment<FragmentMainBinding>(
    FragmentMainBinding::inflate
) {
    override fun onCreateBindingView() {
        val navHostFragment = childFragmentManager.findFragmentById(binding.mainFragmentContainerView.id) as NavHostFragment
        binding.mainBottomNav.setupWithNavController(navHostFragment.navController)
    }
}