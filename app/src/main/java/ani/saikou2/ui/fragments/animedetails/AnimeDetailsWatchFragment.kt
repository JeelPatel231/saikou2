package ani.saikou2.ui.fragments.animedetails

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ani.saikou2.databinding.SampleTextScreenLayoutBinding
import ani.saikou2.ui.generic.ViewBindingFragment
import ani.saikou2.ui.generic.getNavParentFragment

class AnimeDetailsWatchFragment : ViewBindingFragment<SampleTextScreenLayoutBinding>(SampleTextScreenLayoutBinding::inflate) {
    private val animeDetailsViewModel: AnimeDetailsViewModel by viewModels({ getNavParentFragment() })

    override fun onCreateBindingView() {
        binding.textView.text = "Anime Watch ${animeDetailsViewModel.navArgs.id}"
    }
}