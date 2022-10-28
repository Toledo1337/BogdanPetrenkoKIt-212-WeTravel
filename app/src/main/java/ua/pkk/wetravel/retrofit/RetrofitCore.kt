package ua.pkk.wetravel.retrofit

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import ua.pkk.wetravel.utils.Video

private const val BASE_URL = "https://wetravel-1591a.firebaseio.com/"

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

interface UserApiService {
    //user_data
    @GET("user_data.json")
    fun getAllUserData(): Call<Map<String, UserData>>

    @GET("user_data/{id}.json")
    fun getUserData(@Path("id") id: String): Call<UserData>

    @PUT("user_data/{id}.json")
    fun createNewUserData(@Path("id") id: String, @Body data: UserData): Call<UserData>

    //users
    @GET("users.json")
    fun getAllUsers(): Call<Map<String, UserProperty>>

    @GET("users/{id}.json")
    fun getUser(@Path("id") id: String): Call<UserProperty>

    @PUT("users/{id}.json")
    fun createNewUser(@Path("id") id: String, @Body user: UserProperty): Call<UserProperty>

    //comments
    @GET("comments/{id}/{video}.json")
    fun getAllVideoComments(@Path("id") id: String, @Path("video") video:String):Call<Map<String,Comment>>

    @PUT("comments/{id}/{video}/{commentID}.json")
    fun createComment(@Path("id") id: String, @Path("video") video:String, @Path("commentID") commentID:String, @Body comment:Comment):Call<Comment>

    @DELETE("comments/{id}/{video}.json")
    fun deleteAllVideoComments(@Path("id") id: String,@Path("video") video:String):Call<String>

    //video
    @GET("video_data/{id}/{video}.json")
    fun getVideoData(@Path("id") id: String, @Path("video") video:String):Call<Video>

    @PUT("video_data/{id}/{video}.json")
    fun uploadVideoData(@Path("id") id: String, @Path("video") video:String, @Body video_data: Video):Call<Video>
}

object UserAPI {
    val RETROFIT_SERVICE: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
}