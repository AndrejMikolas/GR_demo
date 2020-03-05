package sk.andrejmik.gr_demo.repository_interface

import io.reactivex.Observable
import sk.andrejmik.gr_demo.objects.User

interface IUserRepository : IRepository<User>
{
    fun getAll(page: Int, perPage: Int): Observable<List<User>>
}