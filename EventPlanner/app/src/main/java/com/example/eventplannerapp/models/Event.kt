package com.example.eventplannerapp.models

data class Event (
    val id:Int,
    val title:String,
    val description:String,
    val date:String,
    val time:String,
    val location:String
)