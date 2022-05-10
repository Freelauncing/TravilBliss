package com.assignment.travelbliss.model

import com.google.firebase.database.Exclude

// Model Class  for Location
data class Location(
    var name:String? = null,
    var imageUrl:String? = null,
    var description:String? = null,
    var locationLtLn:String? = null,
    var rating:String? = null,
    @get:Exclude
    @set:Exclude
    var key:String? = null

)