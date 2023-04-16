package com.aiafmaster.gpt.ui.image

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aiafmaster.gpt.ChatGPTApplication
import com.aiafmaster.gpt.R
import com.aiafmaster.gpt.databinding.FragmentImageVariationBinding
import com.aiafmaster.gpt.databinding.ImageVariationItemViewBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ImageVariationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImageVariationFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private val viewModel: ImageViewModel by activityViewModels {
        val app = requireActivity().applicationContext as ChatGPTApplication
        ImageViewModelFactory(app.apiKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_image_variation, container, false)
        val binding = FragmentImageVariationBinding.bind(view)
        val imageAdapter = ImageVariationRecyclerViewAdapter()
        with(binding.imageVariationRecycleView) {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(this@ImageVariationFragment.requireContext())
        }
        binding.swipeContainer.setOnRefreshListener {
            binding.swipeContainer.isRefreshing = false
        }
        viewModel.imageFile.observe(viewLifecycleOwner) {
            binding.swipeContainer.isRefreshing = false
            imageAdapter.onImagesUpdate(it)
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            errorMessage ->
            Snackbar.make(binding.imageVariationRecycleView, errorMessage,
                BaseTransientBottomBar.LENGTH_SHORT).show()
            binding.swipeContainer.isRefreshing = false
        }
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentImageVariationBinding.bind(view)
        binding.swipeContainer.isRefreshing = true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ImageVariationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImageVariationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

class ImageVariationRecyclerViewAdapter : RecyclerView.Adapter<ImageVariationRecyclerViewHolder>() {
    private val images : MutableList<Bitmap> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageVariationRecyclerViewHolder {
        val binding = ImageVariationItemViewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ImageVariationRecyclerViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ImageVariationRecyclerViewHolder, position: Int) {
        holder.imageView.setImageBitmap(images[position])
    }

    fun onImagesUpdate(newImages: List<Bitmap>) {
        val size = images.size
        images.clear()
        images.addAll(newImages)
        notifyItemRangeChanged(size, newImages.size)
    }
}

class ImageVariationRecyclerViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView = ImageVariationItemViewBinding.bind(itemView).imageView
}