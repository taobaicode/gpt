package com.aiafmaster.gpt

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import retrofit2.http.Headers
import java.io.*
import java.util.concurrent.TimeUnit


class ChatGPTManager(private val apiKey: String) {
    companion object {
        private const val HOST = "https://api.openai.com"
    }
    interface ChatGPTAPI {
        @GET("/v1/models")
        fun login() : Call<ChatGPTResponse>

        @Headers("Content-Type: application/json")
        @POST("/v1/chat/completions")
        fun complete(@Body completion : ChatGPTCompletions) : Call<ChatGPTResponse>

        @POST("/v1/audio/transcriptions")
        fun transcript(@Body file : MultipartBody) : Call<ChatGPTTranscription>

        @POST("v1/images/variations")
        fun createImageVariations(@Body request: MultipartBody): Call<ChatGPTImageVariation>
    }

    private val okHttpInterceptor = Interceptor {
        val originalRequest = it.request()
        val newRequest = originalRequest.newBuilder().header("Authorization", "Bearer $apiKey").build()
        it.proceed(newRequest)
    }

    private val logging:HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().apply {
            readTimeout(60, TimeUnit.SECONDS)
            connectTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            callTimeout(60, TimeUnit.SECONDS)
            addInterceptor(okHttpInterceptor)
        }.build()
    }
    fun transcript(file: File) : String? {
        val requestBodyModel = "whisper-1".toRequestBody(null)
        val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val m = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("model", null, requestBodyModel)
                .addFormDataPart("file", file.name, requestFile).build()

        val retrofit = Retrofit.Builder().baseUrl(HOST).addConverterFactory(GsonConverterFactory.create()).client(httpClient).build()
        val transcription = retrofit.create<ChatGPTAPI>().transcript(m)
        val resp = transcription.execute()
        if (resp.body()!= null) {
            return resp.body()!!.transcription
        } else if (resp.errorBody() != null) {
            val gson = Gson()
            val type = object : TypeToken<ChatGPTResponse>(){}.type
            val chatGPTResponse : ChatGPTResponse = gson.fromJson(resp.errorBody()!!.charStream(), type)
            println("Error body ${chatGPTResponse?.gptError?.gptMessage}")
            return chatGPTResponse?.gptError?.gptMessage
        }
        return null
    }

    fun complete(content : String) : String? {
        println("complete")
        val retrofit = Retrofit.Builder().baseUrl(HOST).addConverterFactory(GsonConverterFactory.create()).client(httpClient).build()
        val service = retrofit.create<ChatGPTAPI>()
        val message = ChatGPTMessage("user", content)
        val completion = ChatGPTCompletions("gpt-3.5-turbo", 0.7, listOf<ChatGPTMessage>(message))
        try {
            println("Complete starts")
            val resp = service.complete(completion).execute()
            if (resp.code() == 200) {
                val chatGPTResponse = resp.body()
                println("Response body ${chatGPTResponse?.gptChoices?.get(0)?.gptMessage?.gptContent}")
                return chatGPTResponse?.gptChoices?.get(0)?.gptMessage?.gptContent
            } else if (resp.errorBody() != null) {
                val gson = Gson()
                val type = object : TypeToken<ChatGPTResponse>(){}.type
                // println("error body stream ${resp.errorBody()!!.string()}")
                val chatGPTResponse : ChatGPTResponse = gson.fromJson(resp.errorBody()!!.charStream(), type)
                println("Error body ${chatGPTResponse?.gptError?.gptMessage}")
            }
            println("response code : ${resp.code()}")
            return resp.code().toString()
        } catch (e:Exception) {
            return e.toString()
        }
    }

     fun createImageVariation(n:Int, w:Int, h:Int, file: File): ChatGPTImageVariationResult {
        val retrofit = Retrofit.Builder().baseUrl(HOST).addConverterFactory(GsonConverterFactory.create()).client(httpClient).build()
        val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("image", file.name, file.asRequestBody())
            .addFormDataPart("n", null, n.toString().toRequestBody(null))
            .addFormDataPart("size", null, "${w}x${h}".toRequestBody(null))
            .build()
        val service = retrofit.create<ChatGPTAPI>()
        val resp = service.createImageVariations(multipartBody).execute()
        lateinit var result: ChatGPTImageVariationResult
        var urls: List<String> = mutableListOf()
        if (resp.body()!=null) {
            val images = resp.body()
            images?.let {
                urls=(it.images.map{it.url;})
            }
            result = ChatGPTImageVariationResult(true,"", urls)
        } else if (resp.errorBody() !=null) {
            val gson = Gson()
            val type = object : TypeToken<ChatGPTResponse>(){}.type
            val chatGPTResponse : ChatGPTResponse = gson.fromJson(resp.errorBody()!!.charStream(), type)
            var errorMessage = "No error message"
            chatGPTResponse?.gptError?.gptMessage?.let{errorMessage= it}
            result = ChatGPTImageVariationResult(false, errorMessage, urls)
            println("Error body ${chatGPTResponse?.gptError?.gptMessage}")
        }
        return result
    }

    fun downloadFile(url: String, saveTo: File) {
        val request = Request.Builder().url(url).build()
        val response = OkHttpClient.Builder().build().newCall(request).execute()
        response.body?.let {
            saveTo.createNewFile()
            val outputStream = FileOutputStream(saveTo)
            outputStream.write(it.bytes())
            outputStream.close()
        }
    }

    fun login() : Boolean {
        println("login")
        val retrofit = Retrofit.Builder().baseUrl(HOST).addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create<ChatGPTAPI>()
        service.login().enqueue(object : retrofit2.Callback<ChatGPTResponse> {
            override fun onResponse(call: Call<ChatGPTResponse>, response: Response<ChatGPTResponse>) {
                println(response.body())
            }

            override fun onFailure(call: Call<ChatGPTResponse>, t: Throwable) {
                println("onFailure")
            }
        })
        val resp = service.login().execute()
        val response:ChatGPTResponse ? = resp.body()
        return true;
    }
}