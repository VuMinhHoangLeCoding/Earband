package Android.TestCollection.Earband.adapter

import Android.TestCollection.Earband.fragment.viewPager.ViewPagerAudioHistory
import Android.TestCollection.Earband.fragment.viewPager.ViewPagerPlaylist
import Android.TestCollection.Earband.fragment.viewPager.ViewPagerRecyclerViewAudio
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ViewPagerRecyclerViewAudio()
            1 -> ViewPagerPlaylist()
            2 -> ViewPagerAudioHistory()
            else -> TODO()
        }
    }
}