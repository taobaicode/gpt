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
    private val HOST = "https://api.openai.com"
    // private val apiKey ="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"

    interface ChatGPTAPI {
        // @Headers("Authorization: Bearer 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")
        @GET("/v1/models")
        fun login() : Call<ChatGPTResponse>

        @Headers(//"Authorization: Bearer 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",
        "Content-Type: application/json")
        @POST("/v1/chat/completions")
        fun complete(@Body completion : ChatGPTCompletions) : Call<ChatGPTResponse>
        // fun login() : Call<ResponseBody>

        // @Headers("Authorization: Bearer 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")
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

    suspend fun transcript(file: File) : String? {
//        val requestBodyModel = stripPartContentLengthHeaders(RequestBody.create(null, "whisper-1"))
        //val requestBodyModel = RequestBody.create(null, "whisper-1")
        val requestBodyModel = "whisper-1".toRequestBody(null)
        //val requestFile = RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file)
        val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
//        val requestFile = stripPartContentLengthHeaders(RequestBody.create(MediaType.parse("application/octet-stream"), file))
//        val b= MultipartBody.Part.createFormData("", file.name, requestFile)
//        val b1= MultipartBody.Part.
        val m = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //.addPart(stripPartContentLengthHeaders(MultipartBody.Part.createFormData("model", "whisper-1")))
                .addFormDataPart("model", null, requestBodyModel)
                .addFormDataPart("file", file.name, requestFile).build()

//        val buf = Buffer()
//        m?.writeTo(buf);
//        log.createNewFile();
//        val outputStream = FileOutputStream(log)
//        buf.copyTo(outputStream);
//        outputStream.close()

        val logging = HttpLoggingInterceptor()
// set your desired log level
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
// add your other interceptors …
// add logging as last interceptor
// add your other interceptors …
// add logging as last interceptor
        httpClient.addInterceptor(okHttpInterceptor)
        // httpClient.addInterceptor(logging) // <-- this is the important line!
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)

        val retrofit = Retrofit.Builder().baseUrl(HOST).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build()
        val transcription = retrofit.create<ChatGPTAPI>().transcript(m)
        val resp = transcription.execute()
        if (resp.body()!= null) {
            return resp.body()!!.transcription
        } else if (resp.errorBody() != null) {
            val gson = Gson()
            val type = object : TypeToken<ChatGPTResponse>(){}.type
            // println("error body stream ${resp.errorBody()!!.string()}")
            val chatGPTResponse : ChatGPTResponse = gson.fromJson(resp.errorBody()!!.charStream(), type)
            println("Error body ${chatGPTResponse?.gptError?.gptMessage}")
            return chatGPTResponse?.gptError?.gptMessage
        }
        return null
    }

    suspend fun complete(content : String) : String? {
        println("complete")
        val okHttpClient = OkHttpClient.Builder()
        with (okHttpClient) {
            readTimeout(60, TimeUnit.SECONDS)
            connectTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            callTimeout(60, TimeUnit.SECONDS)
            addInterceptor(okHttpInterceptor)
        }
        val retrofit = Retrofit.Builder().baseUrl(HOST).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient.build()).build()
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
//        val response:ResponseBody ? = resp.body()
    }

    suspend fun createImageVariation(n:Int, w:Int, h:Int, file: File): ChatGPTImageVariationResult {
        val okHttpClient = OkHttpClient.Builder()
        with (okHttpClient) {
            readTimeout(60, TimeUnit.SECONDS)
            connectTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            callTimeout(60, TimeUnit.SECONDS)
            addInterceptor(okHttpInterceptor)
        }
        val retrofit = Retrofit.Builder().baseUrl(HOST).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient.build()).build()
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
            // println("error body stream ${resp.errorBody()!!.string()}")
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

    suspend fun login() : Boolean {
        println("login")
        val retrofit = Retrofit.Builder().baseUrl(HOST).addConverterFactory(GsonConverterFactory.create()).build()
        // val retrofit = Retrofit.Builder().baseUrl(HOST).build()
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
//        println("error ${response?.gptError?.gptMessage}")
//        println(response?.gptObject)
//        println(response?.gptModel?.forEach(){
//            println(it.gptId)
//        })
        return true;
    }

//    private val CONTENT_LENGTH_PATTERN: Pattern = Pattern.compile(
//        "\r?\ncontent-length:\\s*[0-9]+", Pattern.CASE_INSENSITIVE or Pattern.MULTILINE
//    )
//
//    private fun stripPartContentLengthHeaders(delegate: RequestBody): RequestBody? {
//        return object : RequestBody() {
//
//            override fun contentType(): MediaType? {
//                return delegate.contentType()
//            }
//
//            @Throws(IOException::class)
//            override fun writeTo(sink: BufferedSink) {
//                val buffer = Buffer()
//                delegate.writeTo(buffer)
//                val output = ByteArrayOutputStream()
//                buffer.writeTo(output)
//                val contentType = delegate.contentType()
//                val charset: Charset? = if (contentType != null) delegate.contentType()!!
//                    .charset(StandardCharsets.ISO_8859_1) else StandardCharsets.ISO_8859_1
//                val bodyWithoutContentLengthHeaders: String =
//                    CONTENT_LENGTH_PATTERN.matcher(output.toString()).replaceAll("")
//                sink.writeString(bodyWithoutContentLengthHeaders, charset)
//            }
//        }
//    }
}