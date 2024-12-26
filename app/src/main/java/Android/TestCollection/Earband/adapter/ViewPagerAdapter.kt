package Android.TestCollection.Earband.adapter

import Android.TestCollection.Earband.fragment.FragmentAlbum
import Android.TestCollection.Earband.fragment.FragmentMainBody
import Android.TestCollection.Earband.fragment.FragmentRecyclerViewAudio
import androidx.collection.emptyLongSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentRecyclerViewAudio()
            1 -> FragmentAlbum()
            else -> TODO()
        }
    }
}