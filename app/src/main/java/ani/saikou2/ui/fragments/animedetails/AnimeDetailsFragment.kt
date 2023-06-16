package ani.saikou2.ui.fragments.animedetails

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ani.saikou2.databinding.FragmentAnimeDetailsBinding
import ani.saikou2.ui.generic.ViewBindingFragment
import ani.saikou2.ui.generic.observeFlow
import ani.saikou2.ui.generic.showToast
import coil.load
import coil.size.Scale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimeDetailsFragment : ViewBindingFragment<FragmentAnimeDetailsBinding>(FragmentAnimeDetailsBinding::inflate) {
    private val animeDetailsViewModel: AnimeDetailsViewModel by viewModels()

    override fun onCreateBindingView() {
        val navHostFragment = childFragmentManager.findFragmentById(binding.mediaDetailsFragmentContainer.id) as NavHostFragment
        binding.bottomNavigationBar.setupWithNavController(navHostFragment.navController)


        animeDetailsViewModel.animeDetails.observeFlow(viewLifecycleOwner){
            it?.let {
                binding.coverImage.load(it.coverImage){
                    scale(Scale.FILL)
                }
                binding.mediaTitle.text = it.title
            }
        }
    }
}