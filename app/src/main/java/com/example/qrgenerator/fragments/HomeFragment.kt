package com.example.qrgenerator.fragments

import android.R.attr.button
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.qrgenerator.R
import com.example.qrgenerator.databinding.FragmentHomeBinding
import com.example.qrgenerator.viewmodel.QrCodeViewmodel


class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: QrCodeViewmodel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProvider(this)[QrCodeViewmodel::class.java]
        } ?: throw Exception("Invalid Activity")

        binding.textEt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.create.isEnabled = s.toString().trim { it <= ' ' }.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {
                if(s.isNotEmpty()){
                    binding.create.setBackgroundColor(Color.CYAN)
                    binding.create.setOnClickListener {
                        val textToEncode = binding.textEt.text.toString()
                        if (textToEncode != null) {
                            viewModel.data.value = textToEncode
                            val transaction = fragmentManager?.beginTransaction()
                            transaction?.replace(R.id.container, QrCodeFragment())
                            transaction?.commit()
                        } else {
                            Toast.makeText(requireContext(), "Please enter text to generate Qr", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else {
                    binding.create.setBackgroundColor(Color.BLACK)
                    binding.create.setOnClickListener {
                        Toast.makeText(requireContext(), "Please enter text to generate Qr", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.isNullOrBlank()){
                    binding.create.setOnClickListener {
                        Toast.makeText(requireContext(), "Please enter text to generate Qr", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
        binding.create.setOnClickListener {
            val textToEncode = binding.textEt.text.toString()
            if (textToEncode.isNotEmpty()) {
                viewModel.data.value = textToEncode
                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.container, QrCodeFragment())
                transaction?.commit()
            } else {
                Toast.makeText(requireContext(), "Please enter text to generate Qr", Toast.LENGTH_SHORT).show()
            }
        }
    }

}