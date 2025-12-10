package com.example.eventplannerapp

import android.content.Context
import android.content.ContentValues
import com.example.eventplannerapp.models.Event

class EventOperations(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addEvent(title: String, desc: String, date: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TITLE, title)
            put(DatabaseHelper.COLUMN_DESC, desc)
            put(DatabaseHelper.COLUMN_DATE, date)
        }
        db.insert(DatabaseHelper.TABLE_EVENTS, null, values)
    }

    fun getEventsByDate(date: String): List<Event> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_EVENTS} WHERE ${DatabaseHelper.COLUMN_DATE} = ?",
            arrayOf(date)
        )
        val events = mutableListOf<Event>()
        if (cursor.moveToFirst()) {
            do {
                events.add(
                    Event(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                        description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESC)),
                        date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return events
    }

    fun updateEvent(event: Event) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TITLE, event.title)
            put(DatabaseHelper.COLUMN_DESC, event.description)
            put(DatabaseHelper.COLUMN_DATE, event.date)
        }
        db.update(DatabaseHelper.TABLE_EVENTS, values, "${DatabaseHelper.COLUMN_ID}=?", arrayOf(event.id.toString()))
    }

    fun deleteEvent(id: Int) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_EVENTS, "${DatabaseHelper.COLUMN_ID}=?", arrayOf(id.toString()))
    }
}