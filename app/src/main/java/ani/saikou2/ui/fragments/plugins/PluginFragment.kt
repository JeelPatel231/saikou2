package ani.saikou2.ui.fragments.plugins

import android.content.DialogInterface
import android.database.sqlite.SQLiteConstraintException
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ani.saikou2.data.plugins.PluginException
import ani.saikou2.databinding.FragmentPluginBinding
import ani.saikou2.databinding.PluginUrlEntryDialogBinding
import ani.saikou2.databinding.SimpleTextRecyclerLayoutBinding
import ani.saikou2.ui.adapters.PackageRecyclerAdapter
import ani.saikou2.ui.adapters.SimpleTextRecyclerAdapter
import ani.saikou2.ui.generic.ViewBindingFragment
import ani.saikou2.ui.generic.observeFlow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PluginFragment : ViewBindingFragment<FragmentPluginBinding>(FragmentPluginBinding::inflate) {
    private val pluginViewModel: PluginViewModel by activityViewModels()
    private var addPluginDialogBinding: PluginUrlEntryDialogBinding? = null
    private var simpleRecyclerTextViewBinding: SimpleTextRecyclerLayoutBinding? = null

    private fun showToast(message: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    // Add plugin DIALOG
    // BOTH DIALOGS ARE LAZILY made because fragment isn't attached to context until
    // onCreateView is called. But dialogs are only opened after clicking button on populated UI
    private val addPluginDialog by lazy {
        val dialogBuilder = AlertDialog.Builder(requireContext()).apply {
            setView(addPluginDialogBinding!!.root)
                .setMessage("Enter Plugin URL")
                .setNegativeButton("Cancel") { _, _ -> }
                .setPositiveButton("Add", onDialogPositiveHandler)
        }
        dialogBuilder.create()
    }

    private val genericAlertDialog by lazy {
        AlertDialog.Builder(requireContext())
            .setPositiveButton("Okay", null)
            .create()
    }


    private fun showDialogWithData(heading:String, listData:List<String> = emptyList()) = requireActivity().runOnUiThread {
        genericAlertDialog.setMessage(heading)
        simpleRecyclerTextViewBinding!!.textRecycler.adapter = SimpleTextRecyclerAdapter(listData)
        genericAlertDialog.show()
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        // print the stackTrace to logs regardless for debugging
        exception.printStackTrace()
        val localMessage = exception.localizedMessage

        when (exception){
            is SQLiteConstraintException -> when {
                localMessage.isNullOrBlank() -> showToast(exception.message ?: "Empty Message")
                localMessage.contains("UNIQUE") -> showDialogWithData("Package already exists!")
                else -> showToast(localMessage)
            }

            is PluginException.UnmetDependencies -> showDialogWithData(
                "Package ${exception.packageName} has Unmet Dependencies!",
                exception.dependencies
            )

            is PluginException.HasDependees -> showDialogWithData(
                "Packages that depend on ${exception.packageName}!",
                exception.dependees
            )

            // other File or Network I/O exceptions
            else -> showToast(exception.message ?: "Empty Message")
        }
    }


    private val onDialogPositiveHandler = DialogInterface.OnClickListener { _, _ ->
        val inputText = addPluginDialogBinding!!.urlInput.text.toString()

        if(inputText.isEmpty()) return@OnClickListener

        lifecycleScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            pluginViewModel.startDownload(inputText)
        }
    }

    override fun onCreateBindingView() {
        addPluginDialogBinding = PluginUrlEntryDialogBinding.inflate(layoutInflater)
        simpleRecyclerTextViewBinding = SimpleTextRecyclerLayoutBinding.inflate(layoutInflater).apply {
            this.textRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            genericAlertDialog.setView(this.root)
        }

        val packageAdapter = PackageRecyclerAdapter(
            onDeletePackage = {lifecycleScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                pluginViewModel.deletePackage(it)
            }},
            onReinstallPackage = {lifecycleScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                pluginViewModel.reinstallPackage(it)
            }}
        )

        binding.pluginRecyclerView.apply {
            adapter = packageAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        pluginViewModel.packagesInDatabase.observeFlow(viewLifecycleOwner) {
            packageAdapter.setData(it)
        }

        binding.fabAdd.setOnClickListener {
            addPluginDialog.show()
        }
    }
}
