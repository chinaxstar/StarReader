package cn.xstar.starreader

import android.app.backup.SharedPreferencesBackupHelper
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_read_text.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

/**
 * 阅读文本
 */
class ReadTextActivity : AppCompatActivity() {
    companion object {
        const val READ_FILE_PATH = "READ_FILE_PATH"
    }

    var set = Settings()
    var pageDatas = ArrayList<String>()
    var textPages = ArrayList<TextPage>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_text)
        AppUtil.setStatusBarColor(this, Color.WHITE, true)
        val filePath = intent.getStringExtra(READ_FILE_PATH)
        val file = AppUtil.getExsitFile(filePath)
        if (file == null) {
            AppUtil.sShow(this, "文件不存在！")
            finish()
            return
        }
        var mainInfo = PrefsUtil.getObj(MainInfo::class.java)
        val bookInfo = BookInfo(filePath, file.name, Date())
        val index = mainInfo.containBook(bookInfo)
        val books = ArrayList<BookInfo>()
        if (mainInfo.books == null) {
            books.add(bookInfo)
            mainInfo = mainInfo.copy(books = books)
        } else
            if (index == -1) {
                books.add(bookInfo)
                books.addAll(mainInfo.books)
                mainInfo = mainInfo.copy(books = books)
            } else {
                books.addAll(mainInfo.books)
                books[index] = bookInfo
                mainInfo = mainInfo.copy(books = books)
            }
        PrefsUtil.saveObj(mainInfo)
        initData(set, file)
    }

    private fun initData(set: Settings, file: File) {
        val fr = InputStreamReader(FileInputStream(file), set.charset)
        val lines = fr.readLines()
        fr.close()
        var temp = ""
        for (i in lines.indices) {
            temp = lines[i].trim()
            if (temp.isEmpty()) continue
            pageDatas.addAll(lines(set, temp))
        }
        for (i in 0..pageDatas.size step set.pageLines) {
            if (i != 0) {
                textPages.add(TextPage(pageDatas.subList(i - set.pageLines, i)))
            }
        }
        val page = TextPageAdapter()
        page.pages = textPages
        contentPages.adapter = page
    }

    fun lines(set: Settings, line: String): List<String> {
        val lines = ArrayList<String>()
        var ls = line
        while (ls.isNotEmpty()) {
            if (ls.length < set.lineChars) {
                lines.add(ls)
                ls = ""
            } else {
                lines.add(ls.substring(0, set.lineChars))
                ls = ls.substring(set.lineChars)
            }
        }
        return lines
    }
}
