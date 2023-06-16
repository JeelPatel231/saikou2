package ani.saikou2.ui.fragments.animedetails

import android.media.session.PlaybackState
import android.net.Uri
import android.text.TextUtils
import android.text.TextUtils.EllipsizeCallback
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import ani.saikou2.R
import ani.saikou2.data.tracker.AppMediaType
import ani.saikou2.databinding.MediaInfoLayoutBinding
import ani.saikou2.databinding.SampleTextScreenLayoutBinding
import ani.saikou2.reference.VideoType
import ani.saikou2.ui.adapters.CharacterCardAdapter
import ani.saikou2.ui.adapters.MediaCardAdapter
import ani.saikou2.ui.adapters.RelationsAdapter
import ani.saikou2.ui.fragments.MainFragmentDirections
import ani.saikou2.ui.generic.ViewBindingFragment
import ani.saikou2.ui.generic.getNavParentFragment
import ani.saikou2.ui.generic.getOuterNavController
import ani.saikou2.ui.generic.observeFlow
import ani.saikou2.ui.generic.showToast
import ani.saikou2.ui.generic.visibilityGone
import coil.Coil
import coil.request.ImageRequest
import com.google.android.material.chip.Chip
import java.lang.IllegalStateException

class AnimeDetailsInfoFragment :
    ViewBindingFragment<MediaInfoLayoutBinding>(MediaInfoLayoutBinding::inflate) {
    private val animeDetailsViewModel: AnimeDetailsViewModel by viewModels(ownerProducer = { getNavParentFragment() })

    private val exoplayer: ExoPlayer
        get() = animeDetailsViewModel.exoPlayer

    private var exoplayerPlaybackPosition: Long = 0

    override fun onPause() {
        super.onPause()
        exoplayerPlaybackPosition = exoplayer.contentPosition
        exoplayer.pause()
    }

    override fun onResume() {
        super.onResume()
        exoplayer.seekTo(exoplayerPlaybackPosition)
    }

    override fun onDestroyBindingView() {
        super.onDestroyBindingView()
        exoplayer.stop()
    }

    private fun navigateToAnimeDetails(id: Int, type: AppMediaType){
        val destination = when(type){
            AppMediaType.ANIME -> AnimeDetailsFragmentDirections.toSelf(id)
            AppMediaType.MANGA -> AnimeDetailsFragmentDirections.toMangaDetailsFragment(id)
            else -> throw IllegalStateException("Unknown Media Type")
        }
        getOuterNavController().navigate(destination)
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onCreateBindingView() {
        binding.totalMediaItemText.text = getString(R.string.total_media, "Episodes")
        val maxLines = binding.synopsisTextHolder.maxLines


        val relationsAdapter = RelationsAdapter(::navigateToAnimeDetails)
        binding.relationsRecyclerView.apply {
            adapter = relationsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        val recommendationAdapter = MediaCardAdapter(::navigateToAnimeDetails)
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
                totalMediaItemHolder.text = it.episodes.toString()
                averageDurationHolder.text = it.duration.toString()
                formatHolder.text = it.format
                sourceHolder.text = it.source
                studioHolder.text = it.studios.joinToString(" | ")
                seasonHolder.text = it.season
                startDateHolder.text = it.startDate.toString()
                endDateHolder.text = it.endDate.toString()
                synopsisTextHolder.text = it.description

                if(it.trailer == null ){
                    videoView.visibilityGone()
                } else {
                    exoplayer.apply {
                        val url = "https://invidious.slipfox.xyz/latest_version?id=${it.trailer!!.id}&local=true"
                        println("TRAILER LINK : $url")
                        val mediaSource = animeDetailsViewModel.createMediaSourceFromUri(url, VideoType.CONTAINER)
                        videoView.player = this
                        setMediaSource(mediaSource)
                    }
                }

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

                relationsAdapter.setData(it.relations)
                recommendationAdapter.setData(it.recommendation)
                characterAdapter.setData(it.characters)
            }
        }
    }
}