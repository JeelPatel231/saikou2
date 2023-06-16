package ani.saikou2.ui.fragments.anime

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ani.saikou2.R
import ani.saikou2.data.tracker.AppMediaType
import ani.saikou2.databinding.MediaHomePageLayoutBinding
import ani.saikou2.ui.adapters.MediaCardAdapter
import ani.saikou2.ui.fragments.MainFragmentDirections
import ani.saikou2.ui.generic.ViewBindingFragment
import ani.saikou2.ui.generic.getOuterNavController
import ani.saikou2.ui.generic.observeFlow
import ani.saikou2.ui.generic.showToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AnimeFragment: ViewBindingFragment<MediaHomePageLayoutBinding>(MediaHomePageLayoutBinding::inflate){
    private val animeHomeViewModel : AnimeFragmentViewModel by viewModels()

    private fun navigateToAnimeDetails(id: Int, type: AppMediaType){
        val destination = when(type) {
            AppMediaType.ANIME -> MainFragmentDirections.toAnimeDetailsFragment(id)
            AppMediaType.MANGA -> MainFragmentDirections.toMangaDetailsFragment(id)
            else -> throw IllegalStateException("Unknown Media Type")
        }
        getOuterNavController().navigate(destination)
    }

    override fun onCreateBindingView() {
        binding.firstRowText.text = getString(R.string.trending_anime)
        binding.secondRowText.text = getString(R.string.recently_updated)
        binding.thirdRowText.text = getString(R.string.popular_anime)

        with(MediaCardAdapter(::navigateToAnimeDetails)){
            binding.firstRowRecyclerView.adapter = this
            binding.firstRowRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            animeHomeViewModel.trendingAnime.observeFlow(viewLifecycleOwner){
                it?.let { this.setData(it) }
            }
        }

        with(MediaCardAdapter(::navigateToAnimeDetails)){
            binding.secondRowRecyclerView.adapter = this
            binding.secondRowRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            animeHomeViewModel.recentlyUpdated.observeFlow(viewLifecycleOwner){
                it?.let { this.setData(it) }
            }
        }

        with(MediaCardAdapter(::navigateToAnimeDetails)){
            binding.thirdRowRecyclerView.adapter = this
            binding.thirdRowRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            animeHomeViewModel.popularAnime.observeFlow(viewLifecycleOwner){
                it?.let { this.setData(it) }
            }
        }
    }
}