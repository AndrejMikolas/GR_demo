package sk.andrejmik.gr_demo.list_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import sk.andrejmik.gr_demo.R
import sk.andrejmik.gr_demo.databinding.RowUsersListBinding
import sk.andrejmik.gr_demo.objects.User

class UserAdapter : PagedListAdapter<User, RecyclerView.ViewHolder>(UserDiffCallback)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        val binding: RowUsersListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.row_users_list, parent, false
        )
        return UserViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        (holder as UserViewHolder).bind(getItem(position))
    }

    open class UserViewHolder(
        private val binding: RowUsersListBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root)
    {

        fun bind(user: User?)
        {
            if (user == null)
            {
                return
            }
            binding.user = user
            Picasso.with(context).load(user.avatar).into(binding.imageViewAvatar)
            binding.invalidateAll()
            binding.executePendingBindings()
        }
    }

    companion object
    {
        val UserDiffCallback = object : DiffUtil.ItemCallback<User>()
        {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean
            {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean
            {
                return oldItem.email == newItem.email && oldItem.avatar == newItem.avatar
            }
        }
    }
}