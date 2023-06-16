package ani.saikou2.ui.generic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class GenericRecyclerAdapter<TData, TBindingType: ViewBinding>(
    private val inflateCallback: (LayoutInflater, ViewGroup?, Boolean) -> TBindingType,
    initialData: Collection<TData> = emptyList(),
) : RecyclerView.Adapter<GenericRecyclerAdapter.ViewHolder<TData, TBindingType>>() {

    private var _data: MutableList<TData> = initialData.toMutableList()

    val data: List<TData>
        get() = _data

    fun fillData(newData: Collection<TData>) {
        val oldDataSize = data.size
        _data.addAll(newData)
        notifyItemRangeInserted(oldDataSize, newData.size)
    }

    fun setData(newData: Collection<TData>) {
        _data = newData.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size


    class ViewHolder<TData, TBindingType: ViewBinding>(
        private val binding: TBindingType,
        private val callback: (TBindingType, TData, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(entry: TData, position: Int){
            callback(binding, entry, position)
        }
    }

    abstract fun onBind(binding: TBindingType, entry: TData, position: Int)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder<TData, TBindingType> {
        val binding = inflateCallback(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding, this::onBind)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder<TData, TBindingType>, position: Int) {
        viewHolder.bind(data[position], position)
    }
}
