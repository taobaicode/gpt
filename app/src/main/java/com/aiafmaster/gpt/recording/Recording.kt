package com.aiafmaster.gpt.recording

import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import java.io.File

object Recording {
    private var recorder: MediaRecorder? = null
    private var file: File? = null
    private const val FILE_NAME = "record.mp4"

    fun start(context: Activity) {
        if (ActivityCompat.checkSelfPermission(context, "android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, arrayOf<String>("android.permission.RECORD_AUDIO"), 1);
        } else {
            file = context.getFileStreamPath(FILE_NAME)
            recorder = MediaRecorder();
            with(recorder!!) {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFile(file!!.absolutePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            }
            recorder!!.prepare()
            recorder!!.start()
        }
    }

    fun stop() : File? {
        try {
            recorder?.stop()
        } catch (e: Exception) {
            e.fillInStackTrace().printStackTrace()
        }
        return file
    }
}