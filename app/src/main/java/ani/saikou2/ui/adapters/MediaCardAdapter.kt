package ani.saikou2.ui.adapters

import ani.saikou2.R
import ani.saikou2.data.tracker.AppMediaType
import ani.saikou2.data.tracker.MediaCardData
import ani.saikou2.databinding.MediaSmallLayoutBinding
import ani.saikou2.ui.generic.GenericRecyclerAdapter
import coil.load
import coil.size.Scale

class MediaCardAdapter(
    private val onItemClick : (Int, AppMediaType) -> Unit
): GenericRecyclerAdapter<MediaCardData, MediaSmallLayoutBinding>(MediaSmallLayoutBinding::inflate) {
    override fun onBind(binding: MediaSmallLayoutBinding, entry: MediaCardData, position: Int) {
        binding.root.setOnClickListener {
            onItemClick(entry.id, entry.type)
        }
        binding.coverImage.load(entry.coverImage){
            scale(Scale.FILL)
        }
        binding.score.text = entry.meanScore.toString()
        binding.title.text = entry.title
        binding.totalCount.text =
            when(entry.type){

                AppMediaType.ANIME -> binding.root.context.getString(
                    R.string.media_release_data,
                    "~",
                    (entry.nextAiringEpisode ?: entry.episodes ?: "~").toString() ,
                    (entry.episodes ?: "~").toString()
                )

                AppMediaType.MANGA -> binding.root.context.getString(
                    R.string.media_release_data,
                    "~",
                    (entry.chapters ?: "~").toString(),
                    ""
                )

                else -> "??"
            }
    }
}