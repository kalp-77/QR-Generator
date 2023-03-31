package com.example.qrgenerator.fragments

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.qrgenerator.databinding.FragmentQrCodeBinding
import com.example.qrgenerator.viewmodel.QrCodeViewmodel
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter


class QrCodeFragment : Fragment() {

    private var _binding: FragmentQrCodeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: QrCodeViewmodel
    private var filePath: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQrCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = activity?.run {
            ViewModelProvider(this)[QrCodeViewmodel::class.java]
        } ?: throw Exception("Invalid Activity")

        val data = viewModel.data.value
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
        val bmp = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
        val timestamp = System.currentTimeMillis()
        val fileName = "Img_$timestamp.png"
        try {
            for(x in 0 until bitMatrix.width) {
                for(y in 0 until bitMatrix.height) {
                    bmp.setPixel(
                        x, y,
                        if(bitMatrix[x,y]) Color.CYAN else Color.WHITE
                    )
                }
                binding.qrImage.setImageBitmap(bmp)
                binding.save.setOnClickListener {
                    filePath = saveImageToGallery(bmp,fileName)
                    Log.d("tag", "save: $filePath")
                    Toast.makeText(requireContext(), "QR code saved to gallery", Toast.LENGTH_SHORT).show()

                }
//                binding.share.setOnClickListener {
//                    if(filePath != null){
//                        val uri = FileProvider.getUriForFile(requireContext(), "${activity?.packageName}.fileprovider", File(filePath))
//                        Log.d("tag", "share: $uri")
//                        val shareIntent = Intent(Intent.ACTION_SEND)
//                        shareIntent.type = "image/png"
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
//                        startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
//                    }
//                    else {
//                        Toast.makeText(requireContext(), "save to gallery first", Toast.LENGTH_SHORT).show()
//                    }
//
//                }
            }
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageToGallery(bitmap: Bitmap, fileName: String): String? {
        val folderName = "QR Code"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/$folderName")
        }
        val contentResolver = activity?.applicationContext?.contentResolver
        val uri = contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        return uri?.let {
            contentResolver.openOutputStream(it).use { outputStream ->
                outputStream?.let {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                    outputStream.close()
                }
            }
            uri.path
        }
    }

}