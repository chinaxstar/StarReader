package cn.xstar.starreader

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


inline fun <T : TextView> RecyclerView.ViewHolder.find(id: Int): T {
    return itemView.findViewById<T>(id) as T
}

inline fun Date.format(pattern: String): String {
    val f = AppUtil.singleDateFormat(pattern)
    return f.format(this)
}

/**
 * @author: xstar
 * @since: 2018-02-28.
 */
object AppUtil {
    var dateFormat: SimpleDateFormat? = null
    fun singleDateFormat(pattern: String): SimpleDateFormat {
        if (dateFormat == null) dateFormat = SimpleDateFormat(pattern)
        return dateFormat!!
    }

    fun <T : View> find(view: View, id: Int): T {
        return view.findViewById<T>(id) as T
    }

    fun <T : View> find(act: Activity, id: Int): T {
        return act.findViewById<T>(id) as T
    }

    /**
     * 短长toast
     */
    fun sShow(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    /**
     * 长toast
     */
    fun lShow(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = 0
        val res = context.resources
        val resourceId = res.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

    fun setStatusBarColor(act: Activity, color: Int, whiteStatusIcon: Boolean) {
        val window = act.window
        if (Build.VERSION.SDK_INT >= 23) {
            val decor = window.decorView
            var ui = decor.systemUiVisibility
            if (whiteStatusIcon) {
                ui = ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                ui = ui and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            decor.systemUiVisibility = ui
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //设置状态栏颜色
            window.statusBarColor = color
        } else if (Build.VERSION.SDK_INT >= 19) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    fun uri2RealPath(context: Context, uri: Uri): String? {
        var scheme: String? = uri.scheme ?: return uri.path
        var data: String? = null
        when (scheme) {
            ContentResolver.SCHEME_FILE -> uri.path
            ContentResolver.SCHEME_CONTENT -> {
                val cursor = context.contentResolver.query(uri, Array(1, { MediaStore.Images.ImageColumns.DATA }), null, null, null)
                cursor.moveToFirst()
                data = cursor?.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                cursor?.close()
            }
        }
        return data
    }

    fun getExsitFile(path: String): File? {
        val file = File(path)
        if (file.exists()) return file
        return null
    }


}

/**
 * 设置
 * 字大小
 * 字间距
 * 行间距
 * 行字数
 * 页面行数
 */
data class Settings(val textSize: Int = 23, val textSpace: Int = 10, val lineSpace: Int = 20,
                    val lineChars: Int = 15, val pageLines: Int = 22, val textColor: Int = 0xffffff,
                    val margin: Int = 10, val charset: String = "GBK")

data class TextPage(val lines: List<String>) {
    override fun toString(): String {
        return lines.joinToString(separator = "\r\n", prefix = "", postfix = "")
    }
}

data class BookInfo(val path: String, val title: String, val lastOpenDate: Date)

data class MainInfo(val sets: Settings? = null, val books: List<BookInfo>) {
    fun containBook(book: BookInfo): Int {
        if (books != null) {
            for (i in books.indices) {
                if (books[i].path == book.path) {
                    return i
                }
            }
        }
        return -1
    }
}


object PrefsUtil {
    var prefs: SharedPreferences? = null
    var gson: Gson? = null
    open fun init(context: Context) {
        prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        gson = Gson()
    }

    open fun <T> getObj(key: String, tClass: Class<T>): T {
        val json = prefs!!.getString(key, "{}")
        return gson!!.fromJson<T>(json, tClass)
    }

    open fun <T> getObj(tClass: Class<T>): T {
        return getObj(tClass.name, tClass)
    }

    open fun saveObj(key: String, obj: Any): Boolean {
        val json = gson!!.toJson(obj)
        return prefs!!.edit().putString(key, json).commit()
    }

    open fun saveObj(obj: Any): Boolean {
        return saveObj(obj.javaClass.name, obj)
    }
}

class BaseHolder(item: View) : RecyclerView.ViewHolder(item)

class SimpleItemDecoration : RecyclerView.ItemDecoration() {

}