package com.cs407.budgetbuddy.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.budgetbuddy.databinding.DialogCategoryPickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoryPickerDialog : BottomSheetDialogFragment() {
    
    private var _binding: DialogCategoryPickerBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AddTransactionViewModel by activityViewModels()
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCategoryPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter { category ->
            viewModel.setSelectedCategory(category)
            dismiss()
        }

        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CategoryPickerDialog.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}