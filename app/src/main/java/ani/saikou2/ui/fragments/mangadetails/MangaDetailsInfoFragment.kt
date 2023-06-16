package ani.saikou2.ui.fragments.mangadetails

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import ani.saikou2.R
import ani.saikou2.data.tracker.AppMediaType
import ani.saikou2.databinding.MediaInfoLayoutBinding
import ani.saikou2.reference.VideoType
import ani.saikou2.ui.adapters.CharacterCardAdapter
import ani.saikou2.ui.adapters.MediaCardAdapter
import ani.saikou2.ui.adapters.RelationsAdapter
import ani.saikou2.ui.fragments.animedetails.AnimeDetailsFragmentDirections
import ani.saikou2.ui.fragments.animedetails.AnimeDetailsViewModel
import ani.saikou2.ui.generic.ViewBindingFragment
import ani.saikou2.ui.generic.getNavParentFragment
import ani.saikou2.ui.generic.getOuterNavController
import ani.saikou2.ui.generic.observeFlow
import ani.saikou2.ui.generic.showToast
import ani.saikou2.ui.generic.visibilityGone
import com.google.android.material.chip.Chip


class MangaDetailsInfoFragment :
    ViewBindingFragment<MediaInfoLayoutBinding>(MediaInfoLayoutBinding::inflate) {
    private val animeDetailsViewModel: AnimeDetailsViewModel by viewModels(ownerProducer = { getNavParentFragment() })

    private fun navigateToDetails(id: Int, type: AppMediaType){
        val destination = when(type) {
            AppMediaType.ANIME -> MangaDetailsFragmentDirections.toAnimeDetailsFragment(id)
            AppMediaType.MANGA -> MangaDetailsFragmentDirections.toSelf(id)
            else -> throw IllegalStateException("Unknown Media Type")
        }
        getOuterNavController().navigate(destination)
    }

    override fun onCreateBindingView() {
        binding.totalMediaItemText.text = getString(R.string.total_media, "Chapters")
        binding.averageDurationHolder.visibilityGone()
        binding.averageDurationText.visibilityGone()
        val maxLines = binding.synopsisTextHolder.maxLines

        // dynamic contents
        val relationsAdapter = RelationsAdapter(::navigateToDetails)
        binding.relationsRecyclerView.apply {
            adapter = relationsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        val recommendationAdapter = MediaCardAdapter(::navigateToDetails)
        binding.recommendationRecyclerView.apply {
            adapter = recommendationAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        val characterAdapter = CharacterCardAdapter {
            showToast(
                "CLICKED CHARACTER $it",
                Toast.LENGTH_SHORT
            )
        }
        binding.charactersRecyclerView.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        animeDetailsViewModel.animeDetails.observeFlow(viewLifecycleOwner) {
            it ?: return@observeFlow

            val ctx = requireContext()
            with(binding) {
                meanScoreHolder.text = it.meanScore.toString()
                statusHolder.text = it.status.name
                totalMediaItemHolder.text = it.chapters.toString()
                averageDurationHolder.text = it.duration.toString()
                formatHolder.text = it.format
                sourceHolder.text = it.source
                studioHolder.text = it.studios.joinToString(" | ")
                seasonHolder.text = it.season
                startDateHolder.text = it.startDate.toString()
                endDateHolder.text = it.endDate.toString()
                synopsisTextHolder.text = it.description

                synopsisTextHolder.setOnClickListener {
                    if (synopsisTextHolder.maxLines == Int.MAX_VALUE) {
                        synopsisTextHolder.maxLines = maxLines
                    } else {
                        synopsisTextHolder.maxLines = Int.MAX_VALUE
                    }
                }

                for(i in it.synonyms) {
                    synomynsChipGroup.addView( Chip(ctx).apply { text = i } )
                }

                for(i in it.genres) {
                    genresChipGroup.addView( Chip(ctx).apply { text = i } )
                }

                for(i in it.tags) {
                    tagsChipGroup.addView( Chip(ctx).apply { text = i } )
                }

                it.relations.let { relations ->
                    if (relations.isEmpty()) relationsText.visibilityGone()
                    else relationsAdapter.setData(it.relations)
                }

                it.recommendation.let { recommendations ->
                    if (recommendations.isEmpty()) recommendationText.visibilityGone()
                    recommendationAdapter.setData(it.recommendation)
                }

                it.characters.let { characters ->
                    if (characters.isEmpty()) charactersText.visibilityGone()
                    else characterAdapter.setData(characters)
                }
            }
        }
    }
}