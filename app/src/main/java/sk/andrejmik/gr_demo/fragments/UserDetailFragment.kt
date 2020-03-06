package sk.andrejmik.gr_demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_detail_fragment.*
import kotlinx.android.synthetic.main.users_fragment.swipe_container
import sk.andrejmik.gr_demo.R
import sk.andrejmik.gr_demo.databinding.UserDetailFragmentBinding
import sk.andrejmik.gr_demo.objects.User
import sk.andrejmik.gr_demo.utils.Event
import sk.andrejmik.gr_demo.utils.LoadEvent
import sk.andrejmik.gr_demo.utils.UserDetailViewModelFactory

class UserDetailFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener
{
    private lateinit var binding: UserDetailFragmentBinding
    private var userId: Int? = null
    private lateinit var viewModel: UserDetailViewModel

    private lateinit var snackNetworkError: Snackbar
    private lateinit var snackUnknownError: Snackbar

    private val getUserObserver = Observer<User> { user ->
        user?.let {
            binding.user = user
            Picasso.with(context).load(user.avatar).into(imageView_userAvatar)
        }
    }
    private val eventObserver = Observer<Event<LoadEvent>> { event ->
        event?.getContentIfNotHandled()?.let {
            when (event.peekContent())
            {
                LoadEvent.UNKNOWN_ERROR ->
                {
                    //snackNetworkError.dismiss()
                    controlSnackNetworkError(false)
                    controlSnackUnknownError(true)
                    swipe_container.isEnabled = true
                    swipe_container.isRefreshing = false
                }
                LoadEvent.NETWORK_ERROR ->
                {
                    controlSnackNetworkError(true)
                    controlSnackUnknownError(false)
                    snackUnknownError.dismiss()
                    swipe_container.isEnabled = true
                    swipe_container.isRefreshing = false
                }
                LoadEvent.COMPLETE ->
                {
                    dismissSnackBars()
                    swipe_container.isEnabled = true
                    swipe_container.isRefreshing = false
                    Toast.makeText(context, resources.getString(R.string.done), Toast.LENGTH_SHORT).show()
                }
                LoadEvent.STARTED ->
                {
                    dismissSnackBars()
                    swipe_container.isRefreshing = true
                }
                LoadEvent.NO_MORE ->
                {
                }
            }
        }
    }
    private var clickRetryLoadListener = View.OnClickListener {
        loadUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View?
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.user_detail_fragment, container, false)
        val bundle = arguments
        userId = bundle?.getInt("userId")
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, UserDetailViewModelFactory(userId!!))[UserDetailViewModel::class.java]
        swipe_container.setOnRefreshListener(this)
        initSnacks()
        prepareObservers()
    }

    private fun initSnacks()
    {
        snackNetworkError = Snackbar.make(swipe_container, resources.getString(R.string.network_error), Snackbar.LENGTH_INDEFINITE)
        snackNetworkError.setAction(resources.getString(R.string.retry), clickRetryLoadListener)
        snackUnknownError = Snackbar.make(swipe_container, resources.getString(R.string.unknown_error), Snackbar.LENGTH_INDEFINITE)
        snackUnknownError.setAction(resources.getString(R.string.retry), clickRetryLoadListener)
    }

    private fun prepareObservers()
    {
        viewModel.getUser().observe(viewLifecycleOwner, getUserObserver)
        viewModel.onEvent.observe(viewLifecycleOwner, eventObserver)
    }

    private fun controlSnackNetworkError(visibleState: Boolean)
    {
        if (visibleState)
        {
            snackNetworkError = Snackbar.make(swipe_container, resources.getString(R.string.network_error), Snackbar.LENGTH_INDEFINITE)
            snackNetworkError.setAction(resources.getString(R.string.retry), clickRetryLoadListener)
            snackNetworkError.show()
        } else
        {
            snackNetworkError.dismiss()
        }
    }
    private fun controlSnackUnknownError(visibleState: Boolean)
    {
        if (visibleState)
        {
            snackUnknownError = Snackbar.make(swipe_container, resources.getString(R.string.unknown_error), Snackbar.LENGTH_INDEFINITE)
            snackUnknownError.setAction(resources.getString(R.string.retry), clickRetryLoadListener)
            snackUnknownError.show()
        } else
        {
            snackUnknownError.dismiss()
        }
    }

    private fun dismissSnackBars()
    {
        controlSnackNetworkError(false)
        controlSnackUnknownError(false)
    }

    override fun onRefresh()
    {
        viewModel.loadUser(userId!!)
    }

    private fun loadUser()
    {
        viewModel.loadUser(userId!!)
    }

}
