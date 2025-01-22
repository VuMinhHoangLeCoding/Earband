package Android.TestCollection.Earband.fragment.viewPager

import Android.TestCollection.Earband.databinding.MuelViewpagerPlaylistBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ViewPagerPlaylist : Fragment() {

    private var _binding: MuelViewpagerPlaylistBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = MuelViewpagerPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

}