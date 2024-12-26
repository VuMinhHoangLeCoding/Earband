package Android.TestCollection.Earband.adapter

import Android.TestCollection.Earband.databinding.RecyclerViewSwitchCardBinding
import Android.TestCollection.Earband.recycler_view_items.PermissionCard
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class PermissionCardAdapter(
    private val items: List<PermissionCard>,
    private val onSwitchToggled: (position: Int, isChecked: Boolean) -> Unit
) : RecyclerView.Adapter<PermissionCardAdapter.PermissionCardViewHolder>() {

    class PermissionCardViewHolder(private val binding:     RecyclerViewSwitchCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PermissionCard, onSwitchToggled: (position: Int, isChecked: Boolean) -> Unit) {
            binding.textviewTitle.text = item.permissionTitle
            binding.textviewDescription.text = item.permissionDescription
            binding.switchAllowPermission.isChecked = item.isChecked

            binding.switchAllowPermission.setOnCheckedChangeListener {
                _, isChecked ->
                    onSwitchToggled(adapterPosition, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionCardViewHolder {
        val binding = RecyclerViewSwitchCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PermissionCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PermissionCardViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item) {
            pos, ischecked ->
                onSwitchToggled(pos, ischecked)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


}