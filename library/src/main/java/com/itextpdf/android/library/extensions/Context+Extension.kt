package com.itextpdf.android.library.extensions

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.itextpdf.android.library.Constants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import java.io.File
import java.io.FileNotFoundException

/**
 * Returns the fileName of the file at the given uri.
 *
 * @param uri   the uri of the file we want a fileName for
 * @return  the fileName if successful, null if not
 */
fun Context.getFileName(uri: Uri): String? {
    val uriString: String = uri.toString()
    val pdfFile = File(uriString)
    var fileName: String? = null

    // get filename
    if (uriString.startsWith(Constants.CONTENT_PREFIX)) {
        var cursor: Cursor? = null
        try {
            cursor =
                contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                fileName =
                    cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        } finally {
            cursor?.close()
        }
    } else if (uriString.startsWith(Constants.FILE_PREFIX)) {
        fileName = pdfFile.name
    }
    return fileName
}

/**
 * Returns a pdfDocument object with the given filename in writing mode. The document has no pages when initialized.
 *
 * @param fileName  the filename of the pdf that should be written
 * @param mode      the file creation mode that defines who can access the file. Default is MODE_PRIVATE
 *                  which only allows the calling application to access the file.
 * @return  the pdf document in writing mode
 */
fun Context.pdfDocumentWriter(fileName: String, mode: Int = Context.MODE_PRIVATE): PdfDocument? {
    return try {
        val output = openFileOutput(fileName, mode)
        PdfDocument(PdfWriter(output))
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    }
}

/**
 * Returns a pdfDocument object with the given filename in reading mode.
 *
 * @param fileName  the filename of the pdf that should be read
 * @return  the pdf document in reading mode
 */
fun Context.pdfDocumentReader(fileName: String): PdfDocument? {
    return try {
        val file = getFileStreamPath(fileName).absoluteFile
        PdfDocument(PdfReader(file))
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    }
}

/**
 * Returns a pdfDocument object with the given uri in reading mode.
 *
 * @param uri  the uri of the pdf that should be read
 * @return  the pdf document in reading mode
 */
fun Context.pdfDocumentReader(uri: Uri): PdfDocument? {
    val inputStream = contentResolver.openInputStream(uri)
    return try {
        PdfDocument(PdfReader(inputStream))
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    } finally {
        inputStream?.close()
    }
}

/**
 * Returns a pdfDocument object with the given filename in stamping mode.
 *
 * @param fileName  the filename of the pdf that should be written
 * @param mode      the file creation mode that defines who can access the file. Default is MODE_PRIVATE
 *                  which only allows the calling application to access the file.
 * @return  the pdf document in stamping mode
 */
fun Context.pdfDocumentStamping(fileName: String, mode: Int = Context.MODE_PRIVATE): PdfDocument? {
    return try {
        val readFile = getFileStreamPath(fileName).absoluteFile
        val output = openFileOutput(fileName, mode)
        PdfDocument(PdfReader(readFile), PdfWriter(output))
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    }
}