package Android.TestCollection.Earband.adapter

import Android.TestCollection.Earband.databinding.RecyclerViewPermissionCardSwitchBinding
import Android.TestCollection.Earband.recycler_view_items.PermissionCard
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class PermissionCardAdapter(
    private val items: List<PermissionCard>,
    private val onSwitchToggled: (position: Int, isChecked: Boolean) -> Unit
) : RecyclerView.Adapter<PermissionCardAdapter.PermissionCardViewHolder>() {

    class PermissionCardViewHolder(private val binding: RecyclerViewPermissionCardSwitchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PermissionCard, onSwitchToggled: (position: Int, isChecked: Boolean) -> Unit) {
            binding.textTitle.text = item.permissionTitle
            binding.textviewDescription.text = item.permissionDescription
            binding.switchAllowPermission.isChecked = item.isChecked

            binding.switchAllowPermission.setOnCheckedChangeListener { _, isChecked ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) onSwitchToggled(position, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionCardViewHolder {
        val binding = RecyclerViewPermissionCardSwitchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PermissionCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PermissionCardViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item) { pos, isChecked ->
            onSwitchToggled(pos, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


}