package cn.xstar.starreader

import android.app.Application

/**
 * @author: xstar
 * @since: 2018-02-28.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        PrefsUtil.init(this)
    }
}