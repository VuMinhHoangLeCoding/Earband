package Android.TestCollection.Earband.fragment

import Android.TestCollection.Earband.CallbackMainShuffle
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class FragmentTaskbarAboveViewPager : Fragment() {

    private lateinit var shuffleButton: ImageButton
    private var callback: CallbackMainShuffle? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = when (Util.theme) {
            "MUEL" -> inflater.inflate(R.layout.muel_fragment_taskbar_above_view_pager, container, false)
            else -> inflater.inflate(R.layout.muel_fragment_taskbar_above_view_pager, container, false)
        }

        initializeView(view)

        shuffleButton.setOnClickListener {
            callback?.triggerShuffle()
        }

        return view
    }

    private fun initializeView(view: View) {
        shuffleButton = view.findViewById(R.id.shuffle_button)
    }


    fun setShuffleButtonCallback(callback: CallbackMainShuffle) {
        this.callback = callback
    }
}