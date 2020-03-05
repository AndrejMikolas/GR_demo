package sk.andrejmik.gr_demo.data_source

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.reactivex.disposables.CompositeDisposable
import sk.andrejmik.gr_demo.objects.User
import sk.andrejmik.gr_demo.repository_interface.IUserRepository

open class UsersDataSourceFactory(
    private val compositeDisposable: CompositeDisposable, private val userRepository: IUserRepository
) : DataSource.Factory<Int, User>()
{
    val usersDataSourceLiveData = MutableLiveData<UsersDataSource>()

    override fun create(): DataSource<Int, User>
    {
        val usersDataSource = UsersDataSource(userRepository, compositeDisposable)
        usersDataSourceLiveData.postValue(usersDataSource)
        return usersDataSource
    }
}