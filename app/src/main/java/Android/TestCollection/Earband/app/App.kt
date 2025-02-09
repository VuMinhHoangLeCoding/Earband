package Android.TestCollection.Earband.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        private var instance:App?= null

        fun getContext(): App {
            return instance!!
        }
    }

}