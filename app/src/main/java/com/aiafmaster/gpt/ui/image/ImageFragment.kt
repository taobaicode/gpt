package com.aiafmaster.gpt.ui.image

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.aiafmaster.gpt.ChatGPTApplication
import com.aiafmaster.gpt.R
import com.aiafmaster.gpt.databinding.ImageFragmentMainBinding
import java.io.File

class ImageFragment : Fragment() {
    companion object {
        fun newInstance() = ImageFragment()
        private const val TAG="ImageFragment"
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                uri -> uri?.let { Log.d (TAG,"Select URI $uri")
                    binding.selectedImageView.setImageURI(uri)
                    imageURI =uri
                }
    }

    private lateinit var binding: ImageFragmentMainBinding
    private val viewModel: ImageViewModel by activityViewModels {
        val app = requireContext().applicationContext as ChatGPTApplication
        ImageViewModelFactory(app.apiKey)
    }

    private var imageURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // viewModel = ViewModelProvider(this, ImageViewModelFactory(app.apiKey))[ImageViewModel::class.java]

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ImageFragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonImageChooser.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.buttonSendImage.setOnClickListener {
            imageURI?.let {
                viewModel.fetchImageVariations(imageURI!!, requireContext())
                findNavController().navigate(R.id.action_imageFragment_to_imageVariationFragment)
            }
        }
    }
}