package cn.xstar.starreader

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

/**
 * 首页
 */
class MainActivity : AppCompatActivity() {

    val REQ_FILECHOSER = 0x1086
    val REQ_PERMISSION = 0x1087
    val ps = Array(1, { "android.permission.READ_EXTERNAL_STORAGE" })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppUtil.setStatusBarColor(this, Color.WHITE, true)
        if (Build.VERSION.SDK_INT > 23)
            requestPermissions(ps, REQ_PERMISSION)

        read_logs.layoutManager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
//        decoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_line))
        read_logs.addItemDecoration(decoration)
        adapter.layout = R.layout.logs_item_layout
        adapter.footerLayout = R.layout.logs_footer_layout
        adapter.haveFooter = true
        adapter.datas = PrefsUtil.getObj(MainInfo::class.java).books
        adapter.onItemClickListener = object : RecycleBaseAdapter.OnItemClickListener<BaseHolder, BookInfo> {
            override fun onItemClick(holder: BaseHolder, position: Int, data: BookInfo) {
                turnToReadPage(data.path)
            }

            override fun onItemLongClick(holder: BaseHolder, position: Int, data: BookInfo): Boolean {
                showLongClickDialog(data)
                return true
            }

        }
        read_logs.adapter = adapter
    }

    fun openFileChosser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/plain"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, REQ_FILECHOSER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_FILECHOSER) {
            if (data == null) {
                AppUtil.sShow(this, "路径不存在！")
                return
            }
            val filepath = AppUtil.uri2RealPath(this, data.data)
            var choiceFile = File(filepath)
            if (choiceFile.exists()) {
                turnToReadPage(filepath)
            } else AppUtil.sShow(this, "路径不存在！")
        }
    }

    private fun turnToReadPage(filepath: String?) {
        val intent = Intent(applicationContext, ReadTextActivity::class.java)
        intent.putExtra(ReadTextActivity.READ_FILE_PATH, filepath)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (p in permissions.indices) {
            if ((permissions[p] == ps[0]).and(grantResults[p] == PackageManager.PERMISSION_DENIED)) {
                AppUtil.sShow(this, "没有读取文件权限！")
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.datas = PrefsUtil.getObj(MainInfo::class.java).books
        adapter.notifyDataSetChanged()
    }

    val adapter = object : RecycleBaseAdapter<BaseHolder, BookInfo>() {
        override fun onBindView(holder: BaseHolder, position: Int, data: BookInfo) {
            holder.find<TextView>(R.id.book_title).text = data.title
            holder.find<TextView>(R.id.open_date).text = data.lastOpenDate.format("yyyy/M/d-H-m")
            holder.find<TextView>(R.id.book_path).text = data.path
        }

        override fun onBindFooter(holder: BaseHolder, position: Int) {
            holder.itemView.setOnClickListener { openFileChosser() }
        }
    }

    fun showLongClickDialog(book: BookInfo) {
        val array = Array(1, {
            when (it) {
                0 -> "删除"
//                1 -> "详情"
                else -> "未知"
            }
        })
        val alert = AlertDialog.Builder(this).setAdapter(ArrayAdapter<String>(applicationContext,
                android.R.layout.simple_list_item_activated_1, array)) { dialog, which ->
            when (which) {
                0 -> {
                    val mainInfo = PrefsUtil.getObj(MainInfo::class.java)
                    val index = mainInfo.containBook(book)
                    if (index == -1) {
                        AppUtil.sShow(applicationContext, "文档不存在！")
                        dialog.dismiss()
                    } else {
                        if (mainInfo.books is ArrayList) {
                            mainInfo.books.remove(book)
                            PrefsUtil.saveObj(mainInfo)
                            adapter.datas = PrefsUtil.getObj(MainInfo::class.java).books
                            adapter.notifyItemRemoved(index)
                        }
                        dialog.dismiss()
                    }
                }
            }
        }.create()
        alert.show()
    }
}
