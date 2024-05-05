package com.shakil.barivara.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class DroidFileManager {
    fun isFileExist(backupDBPath: File): Boolean {
        return backupDBPath.exists()
    }

    fun createFile(environment: File?, path: String): File {
        return if (environment == null) {
            File(path)
        } else {
            File(environment, path)
        }
    }

    //create an InputStream from the URI and then, from this, create an OutputStream by copying the contents of the input stream.
    //http://jtdz-solenoids.com/stackoverflow_/questions/57093479/get-real-path-from-uri-data-is-deprecated-in-android-q
    fun getPath(context: Context, uri: Uri?): String? {
        val contentResolver = context.contentResolver ?: return null
        val filePath = (context.applicationInfo.dataDir + File.separator
                + System.currentTimeMillis())
        val file = File(filePath)
        try {
            val inputStream = contentResolver.openInputStream(uri!!) ?: return null
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (ignore: IOException) {
            return null
        }
        return file.absolutePath
    }

    fun createFolder(path: String): Boolean {
        var isAlreadyCreated = false
        val dir = File(path)
        if (!dir.exists()) {
            if (dir.mkdir()) {
                isAlreadyCreated = true
            }
        }
        return isAlreadyCreated
    }

    fun folderExists(path: String): Boolean {
        var isFolderExists = false
        val dir = File(path)
        isFolderExists = !dir.exists()
        return isFolderExists
    }
}
