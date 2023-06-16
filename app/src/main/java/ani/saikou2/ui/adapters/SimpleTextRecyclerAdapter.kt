package ani.saikou2.ui.adapters

import ani.saikou2.databinding.SimpleTextViewBinding
import ani.saikou2.ui.generic.GenericRecyclerAdapter

class SimpleTextRecyclerAdapter(
    listData: List<String>
): GenericRecyclerAdapter<String, SimpleTextViewBinding>(
    SimpleTextViewBinding::inflate, listData
){
    override fun onBind(binding: SimpleTextViewBinding, entry: String, position: Int) {
        binding.simpleTextView.text = entry
    }
}