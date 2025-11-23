package com.group4.taobaoclon

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.group4.taobaoclon.databinding.FragmentSelectionSheetBinding

class SelectionBottomSheet(
    private val product: Product,
    private val imageUrl: String?
) : BottomSheetDialogFragment() {

    private var _binding: FragmentSelectionSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectionSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set Data
        binding.sheetPrice.text = "$${product.price}"
        Glide.with(this).load(imageUrl).into(binding.sheetProductImage)

        // Handle "Buy Now" Click -> Go to Checkout
        binding.sheetBuyButton.setOnClickListener {
            dismiss() // Close the sheet

            val intent = Intent(activity, CheckoutActivity::class.java)
            intent.putExtra("PRODUCT_NAME", product.name)
            intent.putExtra("PRODUCT_PRICE", product.price)
            intent.putExtra("PRODUCT_IMAGE", imageUrl)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}