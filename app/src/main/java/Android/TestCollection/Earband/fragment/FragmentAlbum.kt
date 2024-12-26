package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.databinding.FragmentAlbumBinding
import Android.TestCollection.Earband.databinding.FragmentMainBodyBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentAlbum : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

}