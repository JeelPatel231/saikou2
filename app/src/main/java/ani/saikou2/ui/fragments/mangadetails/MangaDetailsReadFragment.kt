package ani.saikou2.ui.fragments.mangadetails

import androidx.fragment.app.viewModels
import ani.saikou2.databinding.SampleTextScreenLayoutBinding
import ani.saikou2.ui.generic.ViewBindingFragment
import ani.saikou2.ui.generic.getNavParentFragment

class MangaDetailsReadFragment : ViewBindingFragment<SampleTextScreenLayoutBinding>(SampleTextScreenLayoutBinding::inflate) {
    private val mangaDetailsViewModel: MangaDetailsViewModel by viewModels({ getNavParentFragment() })

    override fun onCreateBindingView() {
        binding.textView.text = "Manga Read ${mangaDetailsViewModel.navArgs.id}"
    }
}