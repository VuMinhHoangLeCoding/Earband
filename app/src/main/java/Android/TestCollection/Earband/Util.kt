package Android.TestCollection.Earband

import android.content.Context
import android.widget.Toast

object Util {
    fun triggerToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}