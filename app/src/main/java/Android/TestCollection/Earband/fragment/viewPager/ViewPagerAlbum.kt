package Android.TestCollection.Earband.fragment.viewPager

import Android.TestCollection.Earband.databinding.ViewPagerAlbumBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ViewPagerAlbum : Fragment() {

    private var _binding: ViewPagerAlbumBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ViewPagerAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

}