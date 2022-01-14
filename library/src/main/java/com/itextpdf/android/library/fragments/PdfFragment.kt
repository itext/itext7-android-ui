package com.itextpdf.android.library.fragments

import android.content.Context
import android.content.res.TypedArray
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.itextpdf.android.library.R
import com.itextpdf.android.library.databinding.FragmentPdfBinding
import com.itextpdf.android.library.pdfview.PdfReaderAdapter
import com.itextpdf.android.library.pdfview.PdfViewModel

/**
 * Fragment that can be used to display a pdf file. To pass the pdf file to the fragment set the uri
 * to the pdf via the public variable pdfUri before committing the fragment in code or by setting
 * the attribute app:file_uri in xml.
 */
open class PdfFragment : Fragment() {

    private lateinit var binding: FragmentPdfBinding
    private lateinit var pdfReaderAdapter: PdfReaderAdapter
    private lateinit var viewModel: PdfViewModel
    var fileName: String? = null
    var pdfUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPdfBinding.inflate(inflater, container, false)

        if (savedInstanceState != null) {
            // Restore last state
            fileName = savedInstanceState.getString(FILE_NAME) ?: ""
            val storedUri = savedInstanceState.getString(PDF_URI)
            if (!storedUri.isNullOrEmpty()) {
                pdfUri = Uri.parse(storedUri)
            }
        }

        pdfUri?.let {
            viewModel = ViewModelProvider(this).get(PdfViewModel::class.java)
            setAdapter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.tvFileName.text = fileName
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(FILE_NAME, fileName)
        outState.putString(PDF_URI, pdfUri.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::viewModel.isInitialized)
            viewModel.pdfRenderer?.close()
    }

    /**
     * Parse attributes during inflation from a view hierarchy into the
     * arguments we handle.
     */
    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        Log.v(TAG, "onInflate called")

        // get the attributes data set via xml
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PdfFragment)
        val attrFileName = a.getText(R.styleable.PdfFragment_file_name)
        if (attrFileName != null) {
            fileName = attrFileName.toString()
            Log.v(TAG, "Filename received : $attrFileName")
        }
        val attrUri = a.getText(R.styleable.PdfFragment_file_uri)
        if (attrUri != null) {
            pdfUri = Uri.parse(attrUri.toString())
            Log.v(TAG, "Pdf uri received : $attrUri")
        }
        a.recycle()
    }

    private fun setAdapter() {
        pdfUri?.path?.let { pdfPath ->
            if (::viewModel.isInitialized) {
                binding.toolbar.title = fileName
                viewModel.getPdfRenderer(pdfPath)
                viewModel.pdfRenderer?.let {
                    takeActionForPdfRendererNotNull(it)
                } ?: run {
                    takeActionForPdfRendererNull()
                }
            }
        }
    }

    private fun takeActionForPdfRendererNotNull(pdfRenderer: PdfRenderer) {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        binding.rvPdfView.visibility = View.VISIBLE
        binding.pdfWebView.visibility = View.GONE
        binding.rvPdfView.layoutManager = LinearLayoutManager(requireContext())
        pdfReaderAdapter = PdfReaderAdapter(pdfRenderer, width)
        binding.rvPdfView.adapter = pdfReaderAdapter
    }

    private fun takeActionForPdfRendererNull() {
        binding.rvPdfView.visibility = View.GONE
        binding.pdfWebView.visibility = View.VISIBLE
//        showPdfInWebView()
    }

    companion object {
        private const val TAG = "PdfFragment"
        private const val FILE_NAME = "FILE_NAME"
        private const val PDF_URI = "PDF_URI"
    }
}