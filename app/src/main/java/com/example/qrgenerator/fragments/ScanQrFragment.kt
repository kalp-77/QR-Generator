package com.example.qrgenerator.fragments

import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.qrgenerator.R
import com.example.qrgenerator.databinding.FragmentScanQrBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class ScanQrFragment : Fragment() {



    private var _binding: FragmentScanQrBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner
    private lateinit var result : TextView
    private lateinit var search : Button
    private lateinit var copy : ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentScanQrBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA), 123)
        }
        else {
            scan()
        }
    }

    private fun scan() {
        codeScanner = CodeScanner(requireContext(), binding.scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false
        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                if (URLUtil.isValidUrl(it.text)) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(it.text)
                    startActivity(intent)
                } else {
                    openDialog(it.text.toString())
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "Camera Inititalisation failed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==123) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "camera permission granted", Toast.LENGTH_SHORT).show()
                scan()
            }
            else {
                Toast.makeText(requireContext(), "camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(::codeScanner.isInitialized){
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if(::codeScanner.isInitialized){
            codeScanner.releaseResources()
        }
        super.onPause()
    }

    private fun openDialog(data:String) {
        val dialogView: View = layoutInflater.inflate(R.layout.bottom_sheet, null)
        result = dialogView.findViewById(R.id.res)
        result.text = data

        search = dialogView.findViewById(R.id.search)
        search.setOnClickListener {
            val intent = Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, data);
            startActivity(intent)
        }

        copy = dialogView.findViewById(R.id.copy)
        copy.setOnClickListener{
            val clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", data)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_LONG).show()
        }

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogView)

        dialog.show()
    }
}