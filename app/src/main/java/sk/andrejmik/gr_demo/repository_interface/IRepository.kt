package sk.andrejmik.gr_demo.repository_interface

import io.reactivex.Observable
import sk.andrejmik.gr_demo.objects.BaseObject

interface IRepository<T : BaseObject>
{
    fun get(id: Int): Observable<T>
}