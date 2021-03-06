package com.bangkit.pneumoniadetector.tools

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import com.bangkit.pneumoniadetector.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

// For file and photo purpose
object FilePhotoTools {

    private const val FILENAME_FORMAT = "dd-MMM-yyyy"

    private val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())


    fun createFile(application: Application): File {
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        val outputDirectory = if (
            mediaDir != null && mediaDir.exists()
        ) mediaDir else application.filesDir

        return File(outputDirectory, "$timeStamp.jpg")
    }

    fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()
        return if (isBackCamera) {
            matrix.postRotate(90f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } else {
            matrix.postRotate(-90f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
    }

    // A method for reducing image file size
    // example: maxSizeByte = 1000000 (Byte) -> 1MB max size for file
    // default maxSizeByte = 1000000 (Byte) -> 1MB
    fun reduceFileImage(file: File, maxSizeByte: Int = 1000000): File{

        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int

        if(maxSizeByte > 0){
            do{
                val bmpStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPickByteArray = bmpStream.toByteArray()
                streamLength = bmpPickByteArray.size
                compressQuality -= 5
            } while (streamLength > maxSizeByte)
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        }

        return file
    }

    private fun createTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    // parse uri to file
    fun uriToFile(selectedImg: Uri, context: Context): File{
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while(inputStream.read(buf).also{len = it} > 0)
            outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }
}