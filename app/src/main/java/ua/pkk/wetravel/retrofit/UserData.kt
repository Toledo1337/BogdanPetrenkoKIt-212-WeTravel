package ua.pkk.wetravel.retrofit

import com.squareup.moshi.Json

data class UserData(
        @Json(name = "user_name") val userName:String,
        @Json(name = "user_info") val userInfo:String,
        @Json(name = "status") val status:String
)

