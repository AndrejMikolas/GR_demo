package sk.andrejmik.gr_demo.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sk.andrejmik.gr_demo.fragments.UserDetailViewModel


class UserDetailViewModelFactory(
    private val mParam: Int
) : ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        return UserDetailViewModel(mParam) as T
    }

}