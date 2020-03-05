package sk.andrejmik.gr_demo.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.reactivex.disposables.CompositeDisposable
import sk.andrejmik.gr_demo.data_source.UsersDataSourceFactory
import sk.andrejmik.gr_demo.objects.User
import sk.andrejmik.gr_demo.repository_interface.Constants
import sk.andrejmik.gr_demo.repository_interface.RepositoryFactory
import sk.andrejmik.gr_demo.utils.Event
import sk.andrejmik.gr_demo.utils.LoadEvent

class UsersViewModel : ViewModel()
{
    var usersList: LiveData<PagedList<User>>
    private val compositeDisposable = CompositeDisposable()
    private val pageSize = Constants.pageSize
    private var sourceFactory: UsersDataSourceFactory

    init
    {
        sourceFactory = UsersDataSourceFactory(compositeDisposable, RepositoryFactory.getUserRepository())
        val config = PagedList.Config.Builder().setPageSize(pageSize).setInitialLoadSizeHint(pageSize).setEnablePlaceholders(false).build()
        usersList = LivePagedListBuilder<Int, User>(sourceFactory, config).build()
    }

    override fun onCleared()
    {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun retry()
    {
        sourceFactory.usersDataSourceLiveData.value!!.retry()
    }

    fun refresh()
    {
        sourceFactory.usersDataSourceLiveData.value!!.invalidate()
    }

    fun getInitLoadEvent(): LiveData<Event<LoadEvent>> = Transformations.switchMap(
        sourceFactory.usersDataSourceLiveData
    ) { it.initLoadEvent }

    fun getAfterLoadEvent(): LiveData<Event<LoadEvent>> = Transformations.switchMap(
        sourceFactory.usersDataSourceLiveData
    ) { it.afterLoadEvent }
}
