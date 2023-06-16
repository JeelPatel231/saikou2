package ani.saikou2.ui.adapters

import androidx.annotation.DrawableRes
import ani.saikou2.R
import ani.saikou2.data.tracker.AppMediaType
import ani.saikou2.data.tracker.MediaCardData
import ani.saikou2.data.tracker.MediaRelationCardData
import ani.saikou2.data.tracker.MediaRelationType
import ani.saikou2.databinding.MediaRelationSmallLayoutBinding
import ani.saikou2.databinding.MediaSmallLayoutBinding
import ani.saikou2.ui.generic.GenericRecyclerAdapter
import coil.load
import coil.size.Scale

class RelationsAdapter(
    private val onItemClick : (Int, AppMediaType) -> Unit
): GenericRecyclerAdapter<MediaRelationCardData, MediaRelationSmallLayoutBinding>(MediaRelationSmallLayoutBinding::inflate) {

    private fun getRelationDrawable(relation: MediaRelationType): Int {
        return when(relation){
            // TODO: switch to enum and add more cases
            MediaRelationType.SOURCE -> R.drawable.round_menu_book_24
            else -> R.drawable.round_star_24
        }
    }

    override fun onBind(binding: MediaRelationSmallLayoutBinding, entry: MediaRelationCardData, position: Int) {
        binding.root.setOnClickListener {
            onItemClick(entry.id, entry.type)
        }
        binding.coverImage.load(entry.coverImage){
            scale(Scale.FILL)
        }

        binding.relationIcon.load(getRelationDrawable(entry.relation))
        binding.relationHolder.text = entry.relation.value

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