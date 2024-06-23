package com.example.newrecipes.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.newrecipes.R
import com.example.newrecipes.adapters.CategoriesRecyclerAdapter
import com.example.newrecipes.data.pojo.Category
import com.example.newrecipes.databinding.FragmentCategoryBinding
import com.example.newrecipes.mvvm.CategoryMVVM
import com.example.newrecipes.ui.activites.MealActivity
import com.example.newrecipes.ui.fragments.HomeFragment
import com.example.newrecipes.ui.fragments.HomeFragment.Companion.CATEGORY_NAME


class CategoryFragment : Fragment(R.layout.fragment_category) {
    private lateinit var binding:FragmentCategoryBinding
    private lateinit var myAdapter:CategoriesRecyclerAdapter
    private lateinit var categoryMvvm:CategoryMVVM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myAdapter = CategoriesRecyclerAdapter()
        categoryMvvm = ViewModelProvider(this)[CategoryMVVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareRecyclerView()
        categoryMvvm = ViewModelProvider(this)[CategoryMVVM::class.java]
        observeCategories()
        onCategoryClick()
    }

    private fun onCategoryClick() {
       myAdapter.onItemClicked(object : CategoriesRecyclerAdapter.OnItemCategoryClicked{
           override fun onClickListener(category: Category) {
               val intent = Intent(context, MealActivity::class.java)
               intent.putExtra(CATEGORY_NAME,category.strCategory)
               startActivity(intent)
           }
       })
    }


    private fun observeCategories() {
        categoryMvvm.observeCategories().observe(viewLifecycleOwner, Observer<List<Category>> { categories ->
            myAdapter.setCategoryList(categories)
        })
    }

    private fun prepareRecyclerView() {
        binding.favoriteRecyclerView.apply {
              myAdapter= CategoriesRecyclerAdapter()
            adapter = myAdapter
            layoutManager = GridLayoutManager(context,3,GridLayoutManager.VERTICAL,false)
        }
    }


}