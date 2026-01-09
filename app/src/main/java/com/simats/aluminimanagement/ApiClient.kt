package com.simats.aluminimanagement

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "https://t34ffxx1-80.inc1.devtunnels.ms/alumnidatamanagement/"

    @Volatile
    private var _instance: ApiService? = null

    val instance: ApiService
        get() = _instance ?: throw IllegalStateException("ApiClient has not been initialized. Call init() in your Application class.")

    fun init(context: Context) {
        synchronized(this) {
            if (_instance == null) {
                val sessionManager = SessionManager.getInstance(context)

                // Use BASIC logging (faster than BODY, but helps with debugging)
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC)

                val okHttpClient = OkHttpClient.Builder()
                    // Reasonable timeouts
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    // Connection pooling for faster subsequent requests
                    .connectionPool(ConnectionPool(5, 30, TimeUnit.SECONDS))
                    // Retry on failure
                    .retryOnConnectionFailure(true)
                    .addInterceptor(logging)
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                        sessionManager.getUser()?.email?.let {
                            request.addHeader("User-Email", it)
                        }
                        chain.proceed(request.build())
                    }
                    .build()

                val gson = GsonBuilder()
                    .setLenient()
                    .create()

                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

                _instance = retrofit.create(ApiService::class.java)
            }
        }
    }
}