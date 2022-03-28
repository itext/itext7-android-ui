package com.itextpdf.android.library.util

import android.content.Context
import android.net.Uri
import com.itextpdf.kernel.colors.Color
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.annot.PdfAnnotation
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject
import com.itextpdf.kernel.utils.PageRange
import java.io.File

interface PdfManipulator {

    val workingCopy: File

    /**
     * Splits the pdf file at the given uri and creates a new document with the selected page indices and another one for the unselected indices.
     * If selected page indices are empty or contains all the pages, there will only be one document with all pages.
     *
     * @param fileName  the name of the file that will be split. Only relevant for naming the new split documents.
     * @param selectedPageIndices   the list of selected page indices that will be used to create a document with selected and another document
     *  with not selected pages.
     * @param storageFolderPath    the path where the newly created pdf files will be stored
     * @return  the list of uris of the newly created split documents
     */
    fun splitPdfWithSelection(fileName: String, selectedPageIndices: List<Int>, storageFolderPath: String): List<Uri>

    /**
     * Creates the name for the new document created during the split based on initial name and partNumber
     *
     * @param initialFileName   the name of the original document
     * @param partNumber    the part number of the document for which a name should be created. 1 is the first document, 2 the second, ...
     * @param selectedPagesNumbers  the list of selected page numbers
     * @param unselectedPageNumbers  the list of unselected page numbers
     * @return  the name for the split document
     */
    fun getSplitDocumentName(initialFileName: String, partNumber: Int, selectedPagesNumbers: List<Int>, unselectedPageNumbers: List<Int>): String

    /**
     * Returns the page ranges for selected and unselected pages that can be used for splitting
     *
     * @param selectedPagesNumbers  a list of page numbers (NOT indices) that were selected
     * @param unselectedPageNumbers a list of page numbers (NOT indices) that were not selected
     * @param numberOfPages the number of pages this pdf document has
     * @return  the list of page ranges (can be empty if selected pages and unselected pages were empty or the numbers were higher than the numberOfPages)
     */
    fun getPageRanges(selectedPagesNumbers: List<Int>, unselectedPageNumbers: List<Int>, numberOfPages: Int): List<PageRange>

    fun addTextAnnotationToPdf(title: String?, text: String, pageNumber: Int, x: Float, y: Float, bubbleSize: Float, bubbleColor: String): File

    fun addMarkupAnnotationToPdf(pageNumber: Int, x: Float, y: Float, size: Float, color: Color): File

    fun removeAnnotationFromPdf(pageNumber: Int, annotation: PdfAnnotation): File

    fun editAnnotationFromPdf(pageNumber: Int, annotation: PdfAnnotation, title: String?, text: String): File

    fun getHighlightAppearance(pdfDocument: PdfDocument, rectangle: Rectangle, color: Color): PdfFormXObject

    fun getTextAnnotationAppearance(pdfDocument: PdfDocument, colorString: String, bubbleSize: Float): PdfFormXObject?

    companion object Factory {
        fun create(context: Context, originalFileUri: Uri): PdfManipulator {
            return PdfManipulatorImpl(context, originalFileUri)
        }
    }


    fun getPdfDocumentInReadingMode(): PdfDocument
    fun getPdfDocumentInStampingMode(destFile: File): PdfDocument

}