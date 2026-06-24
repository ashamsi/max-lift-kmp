package com.ashamsi.maxlift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        MobileAds.initialize(this)

        setContent {
            App(factory = SecureStorageFactory(this), context = this)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(context = androidx.compose.ui.platform.LocalContext.current)
}
