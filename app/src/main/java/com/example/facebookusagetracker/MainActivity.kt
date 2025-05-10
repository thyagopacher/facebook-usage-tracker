package com.example.facebookusagetracker

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!hasUsageStatsPermission(this)) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 60 * 60 * 24 // últimas 24h

        val usageStatsList: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, endTime
        )

        var totalTime = 0L
        for (usageStats in usageStatsList) {
            if (usageStats.packageName == "com.facebook.katana") {
                totalTime += usageStats.totalTimeInForeground
            }
        }

        val seconds = (totalTime / 1000) % 60
        val minutes = (totalTime / (1000 * 60)) % 60
        val hours = (totalTime / (1000 * 60 * 60))

        val textView: TextView = findViewById(R.id.usage_text)
        textView.text = "Tempo no Facebook nas últimas 24h: %02d:%02d:%02d".format(hours, minutes, seconds)
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}