package com.aiafmaster.gpt.ui.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.*
import com.aiafmaster.gpt.ChatGPTManager
import kotlinx.coroutines.*
import okhttp3.internal.wait
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.OutputStream


class ImageViewModelFactory(private val apiKey: String): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImageViewModel(apiKey) as T
    }
}

class ImageViewModel(private val apiKey: String) : ViewModel() {
    private val _imageFiles = MutableLiveData<List<Bitmap>>()
    val imageFile : LiveData<List<Bitmap>> = _imageFiles
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchImageVariations(imageURI: Uri, context: Context) {
        println("api: ${apiKey}")
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageURI);
                    val min = minOf(bitmap.height, bitmap.width)
                    val cropped = Bitmap.createBitmap(bitmap, 0,0, min, min)
                    val file = context.getFileStreamPath("imagevar.png")
                    val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
                    cropped.compress(Bitmap.CompressFormat.PNG, 100, os)
                    os.close()
//                    val options = BitmapFactory.Options()
//                    options.inJustDecodeBounds = true;
//                    BitmapFactory.decodeFile(file.absolutePath, options)
//                    println("Image size is ${bitmap.height}x${bitmap.width}")
//                    val images = listOf("1", "2", "1", "2", "1", "2", "1", "2")
                    val result = ChatGPTManager(apiKey).createImageVariation(
                        8,
                        1024,
                        1024,
                        file
                    )
                    if (result.success) {
                        asyncDownloadImages(result.urls, context)
                    } else {
                        _errorMessage.postValue(result.errorMessage)
                    }
                }
            } catch(t: Throwable) {
                Log.e(ImageViewModel::class.java.simpleName, "fetchImageVariations", t)
                _errorMessage.postValue(t.message)
            }
        }
    }

    private fun downloadImages(imageUri: List<String>, context: Context) {
        val gpt = ChatGPTManager(apiKey)
        for ((index, value) in imageUri.withIndex()) {
            println("Download $index")
            val file = context.getFileStreamPath("$index.png")
            gpt.downloadFile(value, file)
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val images = ArrayList<Bitmap>()
            _imageFiles.value?.forEach {
                images.add(it)
            }
            images.add(bitmap)
            _imageFiles.postValue(images)
        }
    }

    private fun asyncDownloadImages(imageUri: List<String>, context: Context) {
        val gpt = ChatGPTManager(apiKey)
        val item = imageUri.iterator()
        var index : Int = 0;
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var deferred = mutableListOf<Deferred<Bitmap>>()
                while (item.hasNext()) {
                    deferred.clear()
                    for (i in 1..3) {
                        if (item.hasNext()) {
                            val imageURL = item.next()
                            val taskIndex = index++
                            val defer: Deferred<Bitmap> = async {
                                downloadImage(gpt, imageURL, taskIndex, context)
                            }
                            deferred.add(defer)
                        }
                    }
                    var bitmaps = mutableListOf<Bitmap>()
                    for(def in deferred) {
                        bitmaps.add(def.await())
                    }
                    val images = ArrayList<Bitmap>()
                    _imageFiles.value?.forEach {
                        images.add(it)
                    }
                    images.addAll(bitmaps)
                    _imageFiles.postValue(images)
                }
            }
        }
    }

    private fun downloadImage(gpt: ChatGPTManager, imageUri: String, index: Int, context: Context): Bitmap {
        println("Download $index")
        val file = context.getFileStreamPath("$index.png")
        gpt.downloadFile(imageUri, file)
        return BitmapFactory.decodeFile(file.absolutePath)
    }
}