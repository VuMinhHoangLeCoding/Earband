package Android.TestCollection.Earband.ui.main

import Android.TestCollection.Earband.CallbackDropdownMenu
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
    private lateinit var buttonDropdownMenu: ImageButton
    private var callbackShuffle: CallbackMainShuffle? = null
    private var callbackDropdownMenu: CallbackDropdownMenu? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = when (Util.theme) {
            "MUEL" -> inflater.inflate(R.layout.muel_fragment_taskbar_above_view_pager, container, false)
            "MUD" -> inflater.inflate(R.layout.mud_fragment_taskbar_above_view_pager, container, false)
            else -> inflater.inflate(R.layout.muel_fragment_taskbar_above_view_pager, container, false)
        }

        initializeView(view)

        shuffleButton.setOnClickListener {
            callbackShuffle?.triggerShuffle()
        }

        buttonDropdownMenu.setOnClickListener {
            callbackDropdownMenu?.triggerOpenDropdownMenu()
        }

        return view
    }

    private fun initializeView(view: View) {
        shuffleButton = view.findViewById(R.id.shuffle_button)
        buttonDropdownMenu = view.findViewById(R.id.button_dropdown_menu)
    }


    fun setShuffleButtonCallback(callback: CallbackMainShuffle) {
        this.callbackShuffle = callback
    }

    fun setDropdownMenuButtonCallback(callback: CallbackDropdownMenu){
        this.callbackDropdownMenu = callback
    }
}