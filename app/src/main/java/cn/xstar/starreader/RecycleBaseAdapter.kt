package cn.xstar.starreader

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * @author: xstar
 * @since: 2018-02-28.
 */
abstract class RecycleBaseAdapter<T : BaseHolder, V> : RecyclerView.Adapter<T>() {
    var datas: List<V>? = null
    var layout: Int = 0
    override fun onBindViewHolder(holder: T, position: Int) {
        val data = datas!![position]
        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(holder, position, data) }
        holder.itemView.setOnLongClickListener {
            onItemClickListener?.onItemLongClick(holder, position, data) ?: false
        }
        onBindView(holder, position, data)
    }

    abstract fun onBindView(holder: T, position: Int, data: V)

    var inflate: LayoutInflater? = null
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): T {
        if (inflate == null) inflate = LayoutInflater.from(parent?.context)
        val temp = inflate!!.inflate(layout, parent, false)
        return BaseHolder(temp) as T
    }

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    interface OnItemClickListener<T, V> {
        fun onItemClick(holder: T, position: Int, data: V)
        fun onItemLongClick(holder: T, position: Int, data: V): Boolean
    }

    var onItemClickListener: OnItemClickListener<T, V>? = null

}