package usc.csci571.assignment4.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import usc.csci571.assignment4.databinding.FragmentShippingBinding

/**
 * author: wenjie
 * date: 2023/12/6 10:07
 * description:
 */
class ShippingFragment : Fragment() {

    private var _binding: FragmentShippingBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShippingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): ShippingFragment = ShippingFragment()
    }
}