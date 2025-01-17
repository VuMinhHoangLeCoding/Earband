package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.CallbackMainShuffle
import Android.TestCollection.Earband.databinding.FragmentTaskbarAboveViewPagerBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentTaskbarAboveViewPager : Fragment() {

    private var _binding: FragmentTaskbarAboveViewPagerBinding? = null
    private val binding get() = _binding!!

    private var callback: CallbackMainShuffle? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTaskbarAboveViewPagerBinding.inflate(inflater, container, false)

        binding.shuffleButton.setOnClickListener {
            callback?.triggerShuffle()
        }
        return binding.root
    }

    fun setShuffleButtonCallback(callback: CallbackMainShuffle) {
        this.callback = callback
    }
}