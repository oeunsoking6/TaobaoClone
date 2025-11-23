package com.group4.taobaoclon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

        // Use a safe helper function to set up each item
        // We pass the 'root' view of the included layout (which is accessible via binding.id)

        // Setup Stats (Top Row)
        setupStatItem(binding.statWishlist.root, "Wishlist", android.R.drawable.btn_star)
        setupStatItem(binding.statFollowing.root, "Following", android.R.drawable.ic_menu_myplaces)
        setupStatItem(binding.statHistory.root, "History", android.R.drawable.ic_menu_recent_history)
        setupStatItem(binding.statVouchers.root, "Vouchers", android.R.drawable.ic_menu_agenda)

        // Setup Orders (Middle Row)
        setupStatItem(binding.orderPay.root, "To pay", android.R.drawable.ic_menu_agenda)
        setupStatItem(binding.orderShip.root, "To ship", android.R.drawable.ic_menu_send)
        setupStatItem(binding.orderReceive.root, "To receive", android.R.drawable.ic_menu_view)
        setupStatItem(binding.orderReview.root, "To review", android.R.drawable.ic_menu_edit)
        setupStatItem(binding.orderRefund.root, "Refunds", android.R.drawable.ic_menu_revert)

        // Logout Logic
        binding.logoutButton.setOnClickListener {
            val sharedPrefs = activity?.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            sharedPrefs?.edit()?.remove("USER_TOKEN")?.apply()

            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun setupStatItem(rootView: View, title: String, iconRes: Int) {
        // We find the views manually inside the included root view
        // This prevents the casting crashes
        val titleView = rootView.findViewById<TextView>(R.id.title)
        val iconView = rootView.findViewById<ImageView>(R.id.icon)

        if (titleView != null && iconView != null) {
            titleView.text = title
            iconView.setImageResource(iconRes)
            iconView.setColorFilter(android.graphics.Color.parseColor("#555555"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}