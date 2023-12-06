package usc.csci571.assignment4.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import usc.csci571.assignment4.adapter.SimilarItemAdapter
import usc.csci571.assignment4.bean.SimilarItem
import usc.csci571.assignment4.databinding.FragmentSimilarBinding
import usc.csci571.assignment4.gone
import usc.csci571.assignment4.viewmodel.InteractionViewModel
import usc.csci571.assignment4.visible

/**
 * author: wenjie
 * date: 2023/12/6 10:07
 * description:
 */
class SimilarFragment : Fragment() {

    private var _binding: FragmentSimilarBinding? = null

    private val binding get() = _binding!!

    private val viewModel by activityViewModels<InteractionViewModel>()

    private var originalData = listOf<SimilarItem>()

    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) {
        SimilarItemAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimilarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = mAdapter
        viewModel.productDetailData.observe(viewLifecycleOwner) { productDetail ->
            val data = productDetail.similarItems?.also { item ->
                item.map {
                    //P8DT14H50M43S
                    it.timeLeftInt = it.timeLeft?.substring(1, 2)?.toInt() ?: 0
                }
            }
            if (data.isNullOrEmpty()) {
                binding.recycleView.gone()
                binding.tvEmpty.visible()
                binding.spType.isEnabled = false
                binding.spSort.isEnabled = false
            } else {
                binding.spType.isEnabled = true
                binding.spSort.isEnabled = false
                originalData = data
                binding.recycleView.visible()
                binding.tvEmpty.gone()
                mAdapter.setNewData(data)
            }
        }

        binding.spType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.spSort.isEnabled = position != 0
                sortList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sortList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun sortList() {
        val typePosition = binding.spType.selectedItemPosition
        val sortPosition = binding.spSort.selectedItemPosition
        when (typePosition) {
            0 -> {
                //默认
                mAdapter.setNewData(originalData)
            }

            1 -> {
                //Name
                when (sortPosition) {
                    0 -> {
                        val data = originalData.sortedByDescending {
                            it.title
                        }.reversed()
                        mAdapter.setNewData(data)
                    }

                    1 -> {
                        val data = originalData.sortedByDescending {
                            it.title
                        }
                        mAdapter.setNewData(data)
                    }
                }
            }

            2 -> {
                //Price
                when (sortPosition) {
                    0 -> {
                        val data = originalData.sortedByDescending {
                            it.buyItNowPrice?.value
                        }.reversed()
                        mAdapter.setNewData(data)
                    }

                    1 -> {
                        val data = originalData.sortedByDescending {
                            it.buyItNowPrice?.value
                        }
                        mAdapter.setNewData(data)
                    }
                }
            }

            3 -> {
                //Days
                when (sortPosition) {
                    0 -> {
                        val data = originalData.sortedByDescending {
                            it.timeLeftInt
                        }.reversed()
                        mAdapter.setNewData(data)
                    }

                    1 -> {
                        val data = originalData.sortedByDescending {
                            it.timeLeftInt
                        }
                        mAdapter.setNewData(data)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): SimilarFragment = SimilarFragment()
    }
}