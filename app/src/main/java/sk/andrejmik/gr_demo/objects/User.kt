package sk.andrejmik.gr_demo.objects

import com.google.gson.annotations.SerializedName

open class User : BaseObject()
{
    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("email")
    var email: String = ""

    @SerializedName("first_name")
    var firstName: String = ""

    @SerializedName("last_name")
    var lastName: String = ""
}