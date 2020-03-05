package sk.andrejmik.gr_demo.objects

import com.google.gson.annotations.SerializedName

open class BaseObject
{
    @SerializedName("id")
    var id: Int = 0
}