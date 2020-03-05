package sk.andrejmik.gr_demo.utils

import android.content.Context
import android.net.ConnectivityManager
import sk.andrejmik.gr_demo.GrApp


open class NetworkHelper
{
    companion object
    {
        fun verifyAvailableNetwork(): Boolean
        {
            val connectivityManager = GrApp.getContext()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

}