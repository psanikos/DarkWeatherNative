package npsprojects.darkweather

import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import npsprojects.darkweather.ui.theme.green_300
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import java.security.KeyStore


@Composable
fun BannerAdView() {
    val bannerId = "ca-app-pub-9340838273925003/1697078171"
    val testId = "ca-app-pub-3940256099942544/6300978111"
    AndroidView(factory = { ctx ->

        LinearLayout(ctx).apply {
            val adView = AdView(ctx)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = bannerId
            this.orientation = LinearLayout.VERTICAL
            this.addView(adView)
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)

        }
    }, modifier = Modifier
        .fillMaxWidth()
        .height(50.dp))
}

