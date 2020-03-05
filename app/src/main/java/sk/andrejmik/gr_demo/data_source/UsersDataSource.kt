package sk.andrejmik.gr_demo.data_source

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import sk.andrejmik.gr_demo.objects.User
import sk.andrejmik.gr_demo.repository_interface.Constants
import sk.andrejmik.gr_demo.repository_interface.IUserRepository
import sk.andrejmik.gr_demo.utils.Event
import sk.andrejmik.gr_demo.utils.LoadEvent
import sk.andrejmik.gr_demo.utils.NetworkHelper

class UsersDataSource(
    private val userRepository: IUserRepository, private val compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, User>()
{
    private val TAG = "USERDATASOURCE"
    val initLoadEvent = MutableLiveData<Event<LoadEvent>>()
    val afterLoadEvent = MutableLiveData<Event<LoadEvent>>()

    private var retryCompletable: Completable? = null
    private var lastPageLoaded = false

    fun retry()
    {
        if (retryCompletable != null)
        {
            compositeDisposable.add(
                retryCompletable!!.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ }, { throwable ->
                })
            )
        }
    }

    private fun setRetry(action: Action?)
    {
        if (action == null)
        {
            this.retryCompletable = null
        } else
        {
            this.retryCompletable = Completable.fromAction(action)
        }
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, User>
    )
    {
        if (!NetworkHelper.verifyAvailableNetwork())
        {
            setRetry(Action { loadInitial(params, callback) })
            initLoadEvent.postValue(Event(LoadEvent.NETWORK_ERROR))
            return
        }
        initLoadEvent.postValue(Event(LoadEvent.STARTED))

        compositeDisposable.add(
            userRepository.getAll(1, params.requestedLoadSize).subscribe({ users ->
                setRetry(null)
                if (users.size < Constants.pageSize)
                {
                    lastPageLoaded = true
                    initLoadEvent.postValue(Event(LoadEvent.NO_MORE))
                } else
                {
                    initLoadEvent.postValue(Event(LoadEvent.COMPLETE))
                }
                callback.onResult(users, null, 2)
            }, { throwable ->
                setRetry(Action { loadInitial(params, callback) })
                initLoadEvent.postValue(Event(LoadEvent.UNKNOWN_ERROR))
            })
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>)
    {
        if (lastPageLoaded)
        {
            afterLoadEvent.postValue(Event(LoadEvent.NO_MORE))
            return
        }
        if (!NetworkHelper.verifyAvailableNetwork())
        {
            setRetry(Action { loadAfter(params, callback) })
            afterLoadEvent.postValue(Event(LoadEvent.NETWORK_ERROR))
            return
        }
        afterLoadEvent.postValue(Event(LoadEvent.STARTED))
        compositeDisposable.add(
            userRepository.getAll(
                params.key, params.requestedLoadSize
            ).subscribe({ users ->
                setRetry(null)
                if (users.size < Constants.pageSize)
                {
                    lastPageLoaded = true
                    afterLoadEvent.postValue(Event(LoadEvent.NO_MORE))
                } else
                {
                    afterLoadEvent.postValue(Event(LoadEvent.COMPLETE))
                }
                callback.onResult(users, params.key + 1)
            }, { throwable ->
                setRetry(Action { loadAfter(params, callback) })
                afterLoadEvent.postValue(Event(LoadEvent.UNKNOWN_ERROR))
            })
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, User>)
    {
        /* Not needed */
    }

}