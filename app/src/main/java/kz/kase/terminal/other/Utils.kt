package kz.kase.terminal.other

import android.content.Context
import android.util.Log
import kz.kase.iris.model.IrisApiBase
import java.text.SimpleDateFormat
import java.util.*

class Utils{
    companion object {
        var rowWidth = 80;
        fun dp(context: Context, size: Double): Int {
            val scale = context.getResources().getDisplayMetrics().density
            return (size.toFloat() * scale + 0.5f).toInt()
        }
        private val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("ru"))

        fun debug(debugMessage : String) {
            println("[DEBUG] $debugMessage")
        }
        fun dateFrom(dateTime: IrisApiBase.DateTime): Date {
            val time = String.format("%02d.%02d.%d %02d:%02d:%02d", dateTime.date.day, dateTime.date.month, dateTime.date.year, dateTime.time.hour, dateTime.time.hour, dateTime.time.sec)
            val date = Utils.df.parse(time);
            return date
        }
        fun duration(tag: String, action: Runnable){
            Log.d(tag, "Start test: ");
            var startnow: Long = 0
            var endnow: Long = 0
            startnow = android.os.SystemClock.uptimeMillis();
            action.run()
            endnow = android.os.SystemClock.uptimeMillis();
            Log.d(tag, "Execution time: " + (endnow - startnow) + " ms");
        }
    }

}