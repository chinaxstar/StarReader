package cn.xstar.starreader

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
        more.setOnClickListener { openFileChosser() }
        if (Build.VERSION.SDK_INT > 23)
            requestPermissions(ps, REQ_PERMISSION)

        read_logs.layoutManager = LinearLayoutManager(this)
        read_logs.addItemDecoration(SimpleItemDecoration())
        adapter.layout = R.layout.logs_item_layout
        adapter.datas = PrefsUtil.getObj(MainInfo::class.java).books
        adapter.onItemClickListener = object : RecycleBaseAdapter.OnItemClickListener<BaseHolder, BookInfo> {
            override fun onItemClick(holder: BaseHolder, position: Int, data: BookInfo) {
                turnToReadPage(data.path)
            }

            override fun onItemLongClick(holder: BaseHolder, position: Int, data: BookInfo): Boolean {
                AppUtil.sShow(applicationContext, data.path)
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
            var fp: List<String>?
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
    }
}
