package sk.andrejmik.gr_demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.users_fragment.*
import sk.andrejmik.gr_demo.R
import sk.andrejmik.gr_demo.list_adapters.UserAdapter
import sk.andrejmik.gr_demo.utils.Event
import sk.andrejmik.gr_demo.utils.LoadEvent
import sk.andrejmik.gr_demo.utils.RecyclerTouchListener


class UsersFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener
{
    private val fragment: Fragment = this
    private lateinit var viewModel: UsersViewModel
    private lateinit var userAdapter: UserAdapter

    private lateinit var snackNetworkError: Snackbar
    private lateinit var snackUnknownError: Snackbar

    //region Observers
    private val initLoadObserver = Observer<Event<LoadEvent>> { event ->
        event?.getContentIfNotHandled()?.let {
            when (event.peekContent())
            {
                LoadEvent.UNKNOWN_ERROR ->
                {
                    controlSnackUnknownError(true)
                    controlSnackNetworkError(false)
                    swipe_container.isEnabled = true
                    swipe_container.isRefreshing = false
                }
                LoadEvent.NETWORK_ERROR ->
                {
                    controlSnackUnknownError(false)
                    controlSnackNetworkError(true)
                    swipe_container.isEnabled = true
                    swipe_container.isRefreshing = false
                }
                LoadEvent.COMPLETE ->
                {
                    dismissSnackBars()
                    swipe_container.isEnabled = true
                    swipe_container.isRefreshing = false
                }
                LoadEvent.STARTED ->
                {
                    dismissSnackBars()
                    swipe_container.isRefreshing = true
                }
                LoadEvent.NO_MORE ->
                {
                    dismissSnackBars()
                }
            }
        }
    }

    private val afterLoadObserver = Observer<Event<LoadEvent>> { event ->
        event?.getContentIfNotHandled()?.let {
            when (event.peekContent())
            {
                LoadEvent.UNKNOWN_ERROR ->
                {
                    progressBar_users_loading_more.visibility = View.GONE
                    controlSnackUnknownError(true)
                    controlSnackNetworkError(false)
                }
                LoadEvent.NETWORK_ERROR ->
                {
                    progressBar_users_loading_more.visibility = View.GONE
                    controlSnackUnknownError(false)
                    controlSnackNetworkError(true)
                }
                LoadEvent.COMPLETE ->
                {
                    progressBar_users_loading_more.visibility = View.GONE
                    dismissSnackBars()
                }
                LoadEvent.STARTED ->
                {
                    progressBar_users_loading_more.visibility = View.VISIBLE
                    dismissSnackBars()
                }
                LoadEvent.NO_MORE ->
                {
                    progressBar_users_loading_more.visibility = View.GONE
                    dismissSnackBars()
                    Toast.makeText(context, resources.getString(R.string.done), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
//endregion

    private var recyclerTouchListener = RecyclerTouchListener(context, recycler_view_users, object : RecyclerTouchListener.ClickListener
    {
        override fun onClick(view: View?, position: Int)
        {
            val user = (recycler_view_users.adapter as UserAdapter).currentList?.get(position)
            val bundle = Bundle()
            bundle.putInt("userId", user?.id!!)
            NavHostFragment.findNavController(fragment).navigate(
                R.id.action_usersFragment_to_userDetailFragment, bundle
            )
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View?
    {
        return inflater.inflate(R.layout.users_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UsersViewModel::class.java)
        initAdapter()
        initSnacks()
        initSwipeToRefresh()
    }

    private fun controlSnackNetworkError(visibleState: Boolean)
    {
        if (visibleState)
        {
            snackNetworkError = Snackbar.make(swipe_container, resources.getString(R.string.network_error), Snackbar.LENGTH_INDEFINITE)
            snackNetworkError.setAction(resources.getString(R.string.retry)) { viewModel.retry() }
            snackNetworkError.show()
        } else
        {
            if (snackNetworkError != null)
            {
                snackNetworkError.dismiss()
            }
        }
    }

    private fun controlSnackUnknownError(visibleState: Boolean)
    {
        if (visibleState)
        {
            snackUnknownError = Snackbar.make(swipe_container, resources.getString(R.string.unknown_error), Snackbar.LENGTH_INDEFINITE)
            snackUnknownError.setAction(resources.getString(R.string.retry)) { viewModel.retry() }
            snackUnknownError.show()
        } else
        {
            if (snackNetworkError != null)
            {
                snackUnknownError.dismiss()
            }
        }
    }

    private fun dismissSnackBars()
    {
        controlSnackNetworkError(false)
        controlSnackUnknownError(false)
    }

    private fun initAdapter()
    {
        val linearLayoutManager = LinearLayoutManager(fragment.context, LinearLayoutManager.VERTICAL, false)
        userAdapter = UserAdapter()
        recycler_view_users.layoutManager = linearLayoutManager
        recycler_view_users.adapter = userAdapter
        recycler_view_users.addOnItemTouchListener(recyclerTouchListener)
        viewModel.usersList.observe(viewLifecycleOwner, Observer { userAdapter.submitList(it) })
        viewModel.getInitLoadEvent().observe(viewLifecycleOwner, initLoadObserver)
        viewModel.getAfterLoadEvent().observe(viewLifecycleOwner, afterLoadObserver)
    }

    private fun initSnacks()
    {
        snackNetworkError = Snackbar.make(swipe_container, resources.getString(R.string.network_error), Snackbar.LENGTH_INDEFINITE)
        snackNetworkError.setAction(resources.getString(R.string.retry)) { viewModel.retry() }
        snackUnknownError = Snackbar.make(swipe_container, resources.getString(R.string.unknown_error), Snackbar.LENGTH_INDEFINITE)
        snackUnknownError.setAction(resources.getString(R.string.retry)) { viewModel.retry() }
    }

    private fun initSwipeToRefresh()
    {
        swipe_container.setOnRefreshListener(this)
    }

    override fun onRefresh()
    {
        viewModel.refresh()
    }
}
