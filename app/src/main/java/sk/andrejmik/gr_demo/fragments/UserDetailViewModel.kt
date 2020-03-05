package sk.andrejmik.gr_demo.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import sk.andrejmik.gr_demo.objects.User
import sk.andrejmik.gr_demo.repository_fake.UserFakeRepository
import sk.andrejmik.gr_demo.utils.Event
import sk.andrejmik.gr_demo.utils.LoadEvent
import sk.andrejmik.gr_demo.utils.NetworkHelper

class UserDetailViewModel(userId: Int) : ViewModel()
{
    private var userFakeRepository: UserFakeRepository = UserFakeRepository()
    private val user: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }
    val onEvent = MutableLiveData<Event<LoadEvent>>()

    init
    {
        loadUser(userId)
    }

    fun getUser(): LiveData<User>
    {
        return user
    }

    fun loadUser(id: Int?)
    {
        onEvent.postValue(Event(LoadEvent.STARTED))
        if (!NetworkHelper.verifyAvailableNetwork())
        {
            onEvent.postValue(Event(LoadEvent.NETWORK_ERROR))
            return
        }
        if (id == null)
        {
            onEvent.postValue(Event(LoadEvent.UNKNOWN_ERROR))
            return
        }
        userFakeRepository.get(id).subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(this::next, this::error, this::complete)
    }

    private fun next(loadedUser: User)
    {
        user.postValue(loadedUser)
    }

    private fun complete()
    {
        onEvent.postValue(Event(LoadEvent.COMPLETE))
    }

    private fun error(t: Throwable)
    {
        onEvent.postValue(Event(LoadEvent.UNKNOWN_ERROR))
    }
}
