package ani.saikou2.ui.adapters

import ani.saikou2.R
import ani.saikou2.data.tracker.AppMediaType
import ani.saikou2.data.tracker.CharacterCardData
import ani.saikou2.data.tracker.MediaCardData
import ani.saikou2.databinding.CharacterSmallLayoutBinding
import ani.saikou2.databinding.MediaSmallLayoutBinding
import ani.saikou2.ui.generic.GenericRecyclerAdapter
import coil.load
import coil.size.Scale

class CharacterCardAdapter(
    private val onItemClick : (Int) -> Unit
): GenericRecyclerAdapter<CharacterCardData, CharacterSmallLayoutBinding>(CharacterSmallLayoutBinding::inflate) {
    override fun onBind(binding: CharacterSmallLayoutBinding, entry: CharacterCardData, position: Int) {
        binding.root.setOnClickListener {
            onItemClick(entry.id)
        }
        binding.avatar.load(entry.avatar){
            scale(Scale.FILL)
        }

        binding.name.text = entry.name
        binding.role.text = entry.role
    }
}