package ani.saikou2.ui.generic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class ViewBindingFragment<TBindingType: ViewBinding>(
    val inflaterCallback: (LayoutInflater, ViewGroup?, Boolean) -> TBindingType
) : Fragment(){

    private var _binding: TBindingType? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflaterCallback(inflater, container, false)
        onCreateBindingView()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestroyBindingView()
        _binding = null
    }

    abstract fun onCreateBindingView()
    open fun onDestroyBindingView(){}
}