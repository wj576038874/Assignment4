package usc.csci571.assignment4.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import usc.csci571.assignment4.adapter.PhotoAdapter
import usc.csci571.assignment4.adapter.PictureAdapter
import usc.csci571.assignment4.databinding.FragmentPhotoBinding
import usc.csci571.assignment4.databinding.FragmnetProductBinding
import usc.csci571.assignment4.databinding.SearchBinding
import usc.csci571.assignment4.viewmodel.InteractionViewModel

/**
 * author: wenjie
 * date: 2023/12/6 10:07
 * description:
 */
class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null

    private val binding get() = _binding!!

    private val viewModel by activityViewModels<InteractionViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.productDetailData.observe(viewLifecycleOwner) {
            val images = it?.photosResponse?.mapNotNull {
                it.link
            }
            val photoAdapter = PhotoAdapter(images ?: listOf())
            binding.recycleView.adapter = photoAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): PhotoFragment = PhotoFragment()
    }
}