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
    var haveHeader = false
    var haveFooter = false
    var headerLayout: Int = 0
    var footerLayout: Int = 0
    override fun onBindViewHolder(holder: T, position: Int) {
        if (haveHeader.and(position == 0)) {
            onBindHeader(holder, position)
            return
        }
        if (haveFooter.and(position == itemCount - 1)) {
            onBindFooter(holder, position)
            return
        }
        val data = datas!![position]
        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(holder, position, data) }
        holder.itemView.setOnLongClickListener {
            onItemClickListener?.onItemLongClick(holder, position, data) ?: false
        }
        onBindView(holder, position, data)
    }

    abstract fun onBindView(holder: T, position: Int, data: V)
    open fun onBindHeader(holder: T, position: Int) {}
    open fun onBindFooter(holder: T, position: Int) {}
    var inflate: LayoutInflater? = null
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): T {
        if (inflate == null) inflate = LayoutInflater.from(parent?.context)
        val temp = inflate!!.inflate(viewType, parent, false)
        return BaseHolder(temp) as T
    }

    override fun getItemCount(): Int {
        var count = datas?.size ?: 0
        if (haveHeader) count++
        if (haveFooter) count++
        return count
    }

    override fun getItemViewType(position: Int): Int {
        var _layout: Int
        if (haveHeader.and(position == 0)) {
            _layout = headerLayout
        } else if (haveFooter.and(position == itemCount - 1)) {
            _layout = footerLayout
        } else {
            _layout = layout
        }
        return _layout
    }

    interface OnItemClickListener<T, V> {
        fun onItemClick(holder: T, position: Int, data: V)
        fun onItemLongClick(holder: T, position: Int, data: V): Boolean
    }

    var onItemClickListener: OnItemClickListener<T, V>? = null

}