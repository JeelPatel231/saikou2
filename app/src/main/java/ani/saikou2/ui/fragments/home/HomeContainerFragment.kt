package ani.saikou2.ui.fragments.home

import androidx.fragment.app.Fragment
import ani.saikou2.databinding.FragmentHomeBinding
import ani.saikou2.domain.tracker.StartLoginTrackerUseCase
import ani.saikou2.domain.tracker.TrackerUseCases
import ani.saikou2.ui.generic.ViewBindingFragment
import ani.saikou2.ui.generic.observeFlow
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeContainerFragment :
    ViewBindingFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    @Inject lateinit var startLoginTrackerUseCase: StartLoginTrackerUseCase
    @Inject lateinit var trackerUseCases: TrackerUseCases

    private fun navigateTo(fragmentInstance: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(binding.homeFragmentContainerView.id, fragmentInstance)
            .commit()
    }

    override fun onCreateBindingView() {
        val noLoginFragment = NoLoginFragment()
        val userFragment = UserFragment()

        trackerUseCases.getUserDataUseCase().observeFlow(viewLifecycleOwner) {
            when (it) {
                null -> navigateTo(noLoginFragment)
                else -> navigateTo(userFragment)
            }
        }
    }
}