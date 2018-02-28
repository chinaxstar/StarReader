package cn.xstar.starreader

import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * @author: xstar
 * @since: 2018-02-27.
 */
open class TextPageAdapter : PagerAdapter {
    constructor() {
        textSize = settings.textSize
        textSpace = settings.textSpace
        lineSpace = settings.lineSpace
        margin = settings.margin
    }

    var pages: List<TextPage>? = null
    var settings: Settings = Settings()
    var textSize: Int = 0
    var textSpace: Int = 0
    var lineSpace: Int = 0
    var margin: Int = 0

    override fun getCount(): Int {
        return pages?.size ?: 0
    }

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object`
    }

    override fun getItemPosition(`object`: Any?): Int {
        return POSITION_NONE
    }

    override fun saveState(): Parcelable {
        return super.saveState()
    }

    val items = SparseArray<View>()
    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        var view: View? = items.get(position, null)
        if (view == null)
            view = LayoutInflater.from(container?.context).inflate(R.layout.texpage_item_layout, null)
        if (view is TextView) {
            view.let {
                it.textSize = textSize.toFloat()
                it.text = pages?.get(position).toString()
                it.setPadding(margin, margin, margin, margin)
            }
        }
        items.put(position, view)
        if (container is ViewPager) {
            container.addView(view)
        }
        return view!!
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        if (container is ViewPager) {
            container.removeView(items.get(position))
        }
    }
}