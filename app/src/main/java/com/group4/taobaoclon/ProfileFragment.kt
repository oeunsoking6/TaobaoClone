package com.group4.taobaoclon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.group4.taobaoclon.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 1. SETUP TOP STATS ---
        setupItem(binding.statWishlist.root, "Wishlist", android.R.drawable.btn_star) {
            Toast.makeText(context, "Opening Wishlist...", Toast.LENGTH_SHORT).show()
        }
        setupItem(binding.statFollowing.root, "Following", android.R.drawable.ic_menu_myplaces) {
            Toast.makeText(context, "Opening Following...", Toast.LENGTH_SHORT).show()
        }
        setupItem(binding.statHistory.root, "History", android.R.drawable.ic_menu_recent_history) {
            val intent = Intent(context, HistoryActivity::class.java)
            // Fix: Pass a default product ID or handle it in HistoryActivity to show all
            intent.putExtra("PRODUCT_ID", 1)
            startActivity(intent)
        }
        setupItem(binding.statVouchers.root, "Vouchers", android.R.drawable.ic_menu_agenda) {
            Toast.makeText(context, "Opening Vouchers...", Toast.LENGTH_SHORT).show()
        }

        // --- 2. SETUP ORDERS ---
        val openOrdersAction = {
            val intent = Intent(context, OrdersActivity::class.java)
            startActivity(intent)
        }
        setupItem(binding.orderPay.root, "To pay", android.R.drawable.ic_menu_agenda, openOrdersAction)
        setupItem(binding.orderShip.root, "To ship", android.R.drawable.ic_menu_send, openOrdersAction)
        setupItem(binding.orderReceive.root, "To receive", android.R.drawable.ic_menu_view, openOrdersAction)
        setupItem(binding.orderReview.root, "To review", android.R.drawable.ic_menu_edit, openOrdersAction)
        setupItem(binding.orderRefund.root, "Refunds", android.R.drawable.ic_menu_revert, openOrdersAction)

        // --- 3. LOGOUT LOGIC ---
        binding.logoutButton.setOnClickListener {
            performLogout()
        }

        // --- 4. LOAD REAL USER DATA ---
        loadUserProfile()
    }

    private fun loadUserProfile() {
        // CRITICAL: Use "TaobaoStore" to match Login/Register/History
        val sharedPrefs = activity?.getSharedPreferences("TaobaoStore", Context.MODE_PRIVATE)

        val role = sharedPrefs?.getString("ROLE", "GUEST") ?: "GUEST"
        val userId = sharedPrefs?.getString("USER_ID", "Not Logged In") ?: "Unknown"

        // Update the UI with IDs we added to XML
        binding.tvUserRole.text = "Account Type: $role"
        binding.tvUserId.text = "ID: $userId"
    }

    private fun performLogout() {
        // 1. Clear ALL data from "TaobaoStore"
        val sharedPrefs = activity?.getSharedPreferences("TaobaoStore", Context.MODE_PRIVATE)
        sharedPrefs?.edit()?.clear()?.apply()

        // 2. Navigate back to Login Activity
        val intent = Intent(activity, LoginActivity::class.java)
        // Clear back stack so they can't go back by pressing the Android Back button
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    private fun setupItem(rootView: View, title: String, iconRes: Int, onClick: () -> Unit) {
        val titleView = rootView.findViewById<TextView>(R.id.title)
        val iconView = rootView.findViewById<ImageView>(R.id.icon)

        if (titleView != null && iconView != null) {
            titleView.text = title
            iconView.setImageResource(iconRes)
            iconView.setColorFilter(android.graphics.Color.parseColor("#555555"))
            rootView.setOnClickListener { onClick() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}