package Android.TestCollection.Earband.ui.main

import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class ViewPagerPlaylist : Fragment() {

    private lateinit var recyclerview: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = when (Util.theme) {
            "MUEL" -> inflater.inflate(R.layout.muel_viewpager_playlist, container, false)
            "MUD" -> inflater.inflate(R.layout.mud_viewpager_playlist, container, false)
            else -> inflater.inflate(R.layout.muel_viewpager_playlist, container, false)
        }

        recyclerview = view.findViewById(R.id.recycler_view)

        return view
    }

}