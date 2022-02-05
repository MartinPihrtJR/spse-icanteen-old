package cz.pihrtm.spseicanteen

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews





/**
 * Implementation of App Widget functionality.
 */
class AppWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    override fun onEnabled(context: Context) {
        val food = context.getSharedPreferences("savedFood", Context.MODE_PRIVATE).getString("TodayFood", context.getString(R.string.noFoodData))
        val soup = context.getSharedPreferences("savedFood", Context.MODE_PRIVATE).getString("TodaySoup", context.getString(R.string.noSoupData))
        val popis = context.getSharedPreferences("savedFood", Context.MODE_PRIVATE).getString("TodayPopis", context.getString(R.string.noPopis))
        val lastUpdate = context.getSharedPreferences("widgetLayout", Context.MODE_PRIVATE).getString("lastUpdate", context.getString(R.string.lastUpdatedWTitle))
        val enabledLayout = context.getSharedPreferences("widgetLayout", Context.MODE_PRIVATE).getBoolean("widgetHide", true)
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.app_widget)
        if (enabledLayout){
            views.setViewVisibility(R.id.widgetSoupTitle, View.GONE)
            views.setViewVisibility(R.id.widgetTodayTitle, View.GONE)
            views.setViewVisibility(R.id.widgetErrorTitle, View.VISIBLE)
            views.setViewVisibility(R.id.widgetLastUpdate, View.GONE)
        }
        else{
            views.setViewVisibility(R.id.widgetSoupTitle, View.VISIBLE)
            views.setViewVisibility(R.id.widgetTodayTitle, View.VISIBLE)
            views.setViewVisibility(R.id.widgetErrorTitle, View.GONE)
            views.setViewVisibility(R.id.widgetLastUpdate, View.VISIBLE)
            views.setTextViewText(R.id.widgetTodayTitle, "$popis: $food")
            views.setTextViewText(R.id.widgetSoupTitle, context.getString(R.string.noSoupPrefix)+ " $soup")
            views.setTextViewText(R.id.widgetLastUpdate, lastUpdate)
        }
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.layoutMain, pendingIntent)


    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val food = context.getSharedPreferences("savedFood", Context.MODE_PRIVATE).getString("TodayFood", context.getString(R.string.noFoodData))
    val soup = context.getSharedPreferences("savedFood", Context.MODE_PRIVATE).getString("TodaySoup", context.getString(R.string.noSoupData))
    val popis = context.getSharedPreferences("savedFood", Context.MODE_PRIVATE).getString("TodayPopis", context.getString(R.string.noPopis))
    val lastUpdate = context.getSharedPreferences("widgetLayout", Context.MODE_PRIVATE).getString("lastUpdate", context.getString(R.string.lastUpdatedWTitle))
    val enabledLayout = context.getSharedPreferences("widgetLayout", Context.MODE_PRIVATE).getBoolean("widgetHide", true)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.app_widget)
    if (enabledLayout){
        views.setViewVisibility(R.id.widgetSoupTitle, View.GONE)
        views.setViewVisibility(R.id.widgetTodayTitle, View.GONE)
        views.setViewVisibility(R.id.widgetErrorTitle, View.VISIBLE)
        views.setViewVisibility(R.id.widgetLastUpdate, View.GONE)
    }
    else{
        views.setViewVisibility(R.id.widgetSoupTitle, View.VISIBLE)
        views.setViewVisibility(R.id.widgetTodayTitle, View.VISIBLE)
        views.setViewVisibility(R.id.widgetErrorTitle, View.GONE)
        views.setViewVisibility(R.id.widgetLastUpdate, View.VISIBLE)
        views.setTextViewText(R.id.widgetTodayTitle, "$popis $food")
        views.setTextViewText(R.id.widgetSoupTitle, soup)
        views.setTextViewText(R.id.widgetLastUpdate, lastUpdate)
    }
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    views.setOnClickPendingIntent(R.id.layoutMain, pendingIntent)


    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
