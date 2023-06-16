package ani.saikou2.ui.fragments.mangadetails

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ani.saikou2.databinding.FragmentMangaDetailsBinding
import ani.saikou2.databinding.SampleTextScreenLayoutBinding
import ani.saikou2.ui.generic.ViewBindingFragment
import ani.saikou2.ui.generic.observeFlow
import coil.load
import coil.size.Scale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MangaDetailsFragment :
    ViewBindingFragment<FragmentMangaDetailsBinding>(FragmentMangaDetailsBinding::inflate) {
    private val mangaDetailsViewModel: MangaDetailsViewModel by viewModels()

    override fun onCreateBindingView() {
        val navHostFragment = childFragmentManager.findFragmentById(binding.mediaDetailsFragmentContainer.id) as NavHostFragment
        binding.bottomNavigationBar.setupWithNavController(navHostFragment.navController)


        mangaDetailsViewModel.mangaDetails.observeFlow(viewLifecycleOwner) {
            it?.let {
                binding.coverImage.load(it.coverImage) {
                    scale(Scale.FILL)
                }
                binding.mediaTitle.text = it.title
            }
        }
    }
}