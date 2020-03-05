package sk.andrejmik.gr_demo.repository_fake

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import org.json.JSONObject
import sk.andrejmik.gr_demo.objects.User
import sk.andrejmik.gr_demo.repository_interface.IUserRepository
import java.io.IOException
import java.util.concurrent.TimeUnit


open class UserFakeRepository : IUserRepository
{
    private val resourceUrl: String = Constants.apiUrl.plus("users")

    override fun getAll(page: Int, perPage: Int): Observable<List<User>>
    {
        val requestUrl: String = resourceUrl.plus("?page=$page&per_page=$perPage")
        val client = OkHttpClient()
        val request = Request.Builder().url(requestUrl).get().build()

        return Observable.create<List<User>> { emitter ->
            client.newCall(request).enqueue(object : Callback
            {
                override fun onFailure(call: Call, e: IOException)
                {
                    emitter.onError(e)
                }

                override fun onResponse(call: Call, response: Response)
                {
                    if (response.isSuccessful)
                    {
                        val jsonBody = response.body?.string()
                        val jObject = JSONObject(jsonBody)
                        val jArray = jObject.getJSONArray("data")
                        val usersType = object : TypeToken<List<User?>?>()
                        {}.type
                        val result = Gson().fromJson<List<User>>(jArray.toString(), usersType)
                        emitter.onNext(result)
                        emitter.onComplete()
                    } else
                    {
                        emitter.onError(IllegalStateException("${response.code}"))
                    }
                }
            })
            emitter.setCancellable { }
        }.timeout(10, TimeUnit.SECONDS).subscribeOn(Schedulers.computation()).observeOn(Schedulers.computation())
    }

    override fun get(id: Int): Observable<User>
    {
        val requestUrl: String = resourceUrl.plus("/$id")
        val client = OkHttpClient()
        val request = Request.Builder().url(requestUrl).get().build()

        return Observable.create<User> { emitter ->
            client.newCall(request).enqueue(object : Callback
            {
                override fun onFailure(call: Call, e: IOException)
                {
                    emitter.onError(e)
                }

                override fun onResponse(call: Call, response: Response)
                {
                    if (response.isSuccessful)
                    {
                        val jsonBody = response.body?.string()
                        val jObject = JSONObject(jsonBody).getJSONObject("data")
                        val result = Gson().fromJson<User>(jObject.toString(), User::class.java)
                        emitter.onNext(result)
                        emitter.onComplete()
                    } else
                    {
                        emitter.onError(IllegalStateException("${response.code}"))
                    }
                }
            })
            emitter.setCancellable { }
        }.timeout(10, TimeUnit.SECONDS).subscribeOn(Schedulers.computation()).observeOn(Schedulers.computation())
    }
}