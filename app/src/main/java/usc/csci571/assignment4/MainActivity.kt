package usc.csci571.assignment4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import usc.csci571.assignment4.adapter.HomeFragmentAdapter
import usc.csci571.assignment4.databinding.ActivityMainBinding
import usc.csci571.assignment4.fragment.SearchFragment
import usc.csci571.assignment4.fragment.WishListFragment

/**
 * author: wenjie
 * date: 2023/12/5 15:38
 * description:
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val titles = listOf("SEARCH", "WISHLIST")

    private val pagerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        HomeFragmentAdapter(
            this,
            listOf(SearchFragment.newInstance() , WishListFragment.newInstance())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Assignment4)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager2.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = titles[position]
        }.attach()

    }
}