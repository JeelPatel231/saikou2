package ani.saikou2.ui.adapters

import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ani.saikou2.R
import ani.saikou2.data.plugins.Extractor
import ani.saikou2.data.plugins.PackageWithPlugins
import ani.saikou2.data.plugins.Parser
import ani.saikou2.databinding.PluginCardViewBinding
import ani.saikou2.databinding.SinglePluginBinding
import ani.saikou2.reference.ExtractorMap
import ani.saikou2.reference.ParserMap
import ani.saikou2.ui.generic.GenericRecyclerAdapter

class ParserRecyclerAdapter: GenericRecyclerAdapter<Parser, SinglePluginBinding>(
    SinglePluginBinding::inflate
) {
    override fun onBind(binding: SinglePluginBinding, entry: Parser, position: Int) {
        binding.pluginTextView.text = entry.className
        if (ParserMap[entry.className] == null)
            binding.loadState.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.round_do_not_disturb_alt_24
                )
            )
    }
}

class ExtractorRecyclerAdapter: GenericRecyclerAdapter<Extractor, SinglePluginBinding>(
    SinglePluginBinding::inflate
){
    override fun onBind(binding: SinglePluginBinding, entry: Extractor, position: Int) {
        binding.pluginTextView.text = entry.className
        if (ExtractorMap[entry.className] == null)
            binding.loadState.setImageDrawable(ContextCompat.getDrawable(binding.root.context, R.drawable.round_do_not_disturb_alt_24))
    }
}

class PackageRecyclerAdapter(
    private val onDeletePackage: (PackageWithPlugins) -> Unit,
    private val onReinstallPackage: (PackageWithPlugins) -> Unit,
): GenericRecyclerAdapter<PackageWithPlugins, PluginCardViewBinding>(
    PluginCardViewBinding::inflate
) {
    override fun onBind(binding: PluginCardViewBinding, entry: PackageWithPlugins, position: Int) {
        binding.packageNameHolder.text = entry.packageItem.packageName

        val parserAdapter = ParserRecyclerAdapter()
        binding.parserHolderRecyclerView.apply {
            adapter = parserAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        parserAdapter.fillData(entry.parsers)

        val extractorAdapter = ExtractorRecyclerAdapter()
        binding.extractorHolderRecyclerView.apply {
            adapter = extractorAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        extractorAdapter.fillData(entry.extractors)

        val managePluginDialog = AlertDialog.Builder(binding.root.context)
            .setMessage("Delete Package?")
            .setNegativeButton("Reinstall") { _, _ ->
                onReinstallPackage(entry)
            }
            .setPositiveButton("Delete") { _, _ ->
                onDeletePackage(entry)
            }
            .create()


        binding.root.setOnLongClickListener {
            managePluginDialog.show()
            true
        }
    }
}
