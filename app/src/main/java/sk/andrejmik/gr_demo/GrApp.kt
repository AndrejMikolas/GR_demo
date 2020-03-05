package sk.andrejmik.gr_demo

import android.app.Application
import android.content.Context


open class GrApp : Application()
{
    companion object
    {
        private var appInstance: Application? = null

        fun getContext(): Context?
        {
            return appInstance!!.applicationContext
        }
    }

    override fun onCreate()
    {
        super.onCreate()
        appInstance = this
    }
}