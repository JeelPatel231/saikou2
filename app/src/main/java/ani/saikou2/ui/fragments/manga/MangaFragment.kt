package ani.saikou2.ui.fragments.manga

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ani.saikou2.R
import ani.saikou2.data.tracker.AppMediaType
import ani.saikou2.databinding.MediaHomePageLayoutBinding
import ani.saikou2.databinding.MediaInfoLayoutBinding
import ani.saikou2.databinding.SampleTextScreenLayoutBinding
import ani.saikou2.ui.adapters.MediaCardAdapter
import ani.saikou2.ui.fragments.MainFragmentDirections
import ani.saikou2.ui.fragments.anime.AnimeFragmentViewModel
import ani.saikou2.ui.generic.ViewBindingFragment
import ani.saikou2.ui.generic.getOuterNavController
import ani.saikou2.ui.generic.observeFlow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MangaFragment : ViewBindingFragment<MediaHomePageLayoutBinding>(MediaHomePageLayoutBinding::inflate){
    private val mangaFragmentViewModel : MangaFragmentViewModel by viewModels()

    private fun navigateToDetails(id: Int, type: AppMediaType){
        val destination = when(type) {
            AppMediaType.ANIME -> MainFragmentDirections.toAnimeDetailsFragment(id)
            AppMediaType.MANGA -> MainFragmentDirections.toMangaDetailsFragment(id)
            else -> throw IllegalStateException("Unknown Media Type")
        }
        getOuterNavController().navigate(destination)
    }

    override fun onCreateBindingView() {
        binding.firstRowText.text = getString(R.string.trending_manga)
        binding.secondRowText.text = getString(R.string.popular_manga)
        binding.thirdRowText.text = getString(R.string.trending_novel)

        with(MediaCardAdapter(::navigateToDetails)){
            binding.firstRowRecyclerView.adapter = this
            binding.firstRowRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            mangaFragmentViewModel.trendingManga.observeFlow(viewLifecycleOwner){
                it?.let { this.setData(it) }
            }
        }

        with(MediaCardAdapter(::navigateToDetails)){
            binding.secondRowRecyclerView.adapter = this
            binding.secondRowRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            mangaFragmentViewModel.popularManga.observeFlow(viewLifecycleOwner){
                it?.let { this.setData(it) }
            }
        }

        with(MediaCardAdapter(::navigateToDetails)){
            binding.thirdRowRecyclerView.adapter = this
            binding.thirdRowRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            mangaFragmentViewModel.trendingNovel.observeFlow(viewLifecycleOwner){
                it?.let { this.setData(it) }
            }
        }
    }
}