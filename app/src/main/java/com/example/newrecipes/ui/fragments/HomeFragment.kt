package com.example.newrecipes.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newrecipes.R
import com.example.newrecipes.adapters.CategoriesRecyclerAdapter
import com.example.newrecipes.adapters.MostPopularRecyclerAdapter
import com.example.newrecipes.adapters.OnItemClick
import com.example.newrecipes.adapters.OnLongItemClick
import com.example.newrecipes.data.pojo.*
import com.example.newrecipes.databinding.FragmentHomeBinding
import com.example.newrecipes.mvvm.DetailsMVVM
import com.example.newrecipes.mvvm.MainFragMVVM
import com.example.newrecipes.ui.activites.MealActivity
import com.example.newrecipes.ui.MealBottomDialog
import com.example.newrecipes.ui.activites.MealDetailesActivity


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var meal: RandomMealResponse
    private lateinit var detailMvvm: DetailsMVVM
    private var randomMealId = ""


    companion object{
        const val MEAL_ID="com.example.newrecipes.ui.fragments.idMeal"
        const val MEAL_NAME="com.example.newrecipes.ui.fragments.nameMeal"
        const val MEAL_THUMB="com.example.newrecipes.ui.fragments.thumbMeal"
        const val CATEGORY_NAME=" com.example.newrecipes.ui.fragments.categoryName"
        const val MEAL_STR=" com.example.newrecipes.ui.fragments.strMeal"
        const val MEAL_AREA=" com.example.newrecipes.ui.fragments.strArea"


    }



    private lateinit var myAdapter: CategoriesRecyclerAdapter
    private lateinit var mostPopularFoodAdapter: MostPopularRecyclerAdapter
    private lateinit var binding:FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ViewModelProvider ile ViewModel'i oluştur
        detailMvvm = ViewModelProvider(this).get(DetailsMVVM::class.java)

        // FragmentHomeBinding'i inflate et ve binding değişkenine ata
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // RecyclerView için adapter'ları oluştur
        myAdapter = CategoriesRecyclerAdapter()
        mostPopularFoodAdapter = MostPopularRecyclerAdapter()

        // Root view'ı dön
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainFragMVVM = ViewModelProvider(this)[MainFragMVVM::class.java]
        showLoadingCase()


        prepareCategoryRecyclerView()
        preparePopularMeals()
        onRndomMealClick()
        onRandomLongClick()

// observeMealByCategory
        mainFragMVVM.observeMealByCategory().observe(viewLifecycleOwner, Observer { mealsResponse ->
            mealsResponse?.let { response ->
                val meals = response.meals
                setMealsByCategoryAdapter(meals)
                cancelLoadingCase()
            }
        })

// observeCategories
        mainFragMVVM.observeCategories().observe(viewLifecycleOwner, Observer { categoryResponse ->
            categoryResponse?.let { response ->
                val categories = response.categories
                setCategoryAdapter(categories)
            }
        })

// observeRandomMeal
        mainFragMVVM.observeRandomMeal().observe(viewLifecycleOwner, Observer { randomMealResponse ->
            randomMealResponse?.let { response ->
                val mealImage = view?.findViewById<ImageView>(R.id.img_random_meal)
                val imageUrl = response.meals[0].strMealThumb
                randomMealId = response.meals[0].idMeal
                Glide.with(this@HomeFragment)
                    .load(imageUrl)
                    .into(mealImage!!)
                meal = response
            }
        })


        mostPopularFoodAdapter.setOnClickListener(object : OnItemClick {
            override fun onItemClick(meal: Meal) {
                val intent = Intent(activity, MealDetailesActivity::class.java)
                intent.putExtra(MEAL_ID, meal.idMeal)
                intent.putExtra(MEAL_STR, meal.strMeal)
                intent.putExtra(MEAL_THUMB, meal.strMealThumb)
                startActivity(intent)
            }

        })

        myAdapter.onItemClicked(object : CategoriesRecyclerAdapter.OnItemCategoryClicked {
            override fun onClickListener(category: Category) {
                val intent = Intent(activity, MealActivity::class.java)
                intent.putExtra(CATEGORY_NAME, category.strCategory)
                startActivity(intent)
            }

        })

        mostPopularFoodAdapter.setOnLongCLickListener(object : OnLongItemClick {
            override fun onItemLongClick(meal: Meal) {
                detailMvvm.getMealByIdBottomSheet(meal.idMeal)
            }

        })

        detailMvvm.observeMealBottomSheet()
            .observe(viewLifecycleOwner, Observer<List<MealDetail>> { t ->
                t?.let {
                    val bottomSheetFragment = MealBottomDialog()
                    val b = Bundle().apply {
                        putString(CATEGORY_NAME, it[0].strCategory)
                        putString(MEAL_AREA, it[0].strArea)
                        putString(MEAL_NAME, it[0].strMeal)
                        putString(MEAL_THUMB, it[0].strMealThumb)
                        putString(MEAL_ID, it[0].idMeal)
                    }

                    bottomSheetFragment.arguments = b
                    bottomSheetFragment.show(childFragmentManager, "BottomSheetDialog")
                }
            })


        binding.imgSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }


    private fun onRndomMealClick() {
        binding.randomMeal.setOnClickListener {
            val temp = meal.meals[0]
            val intent = Intent(activity, MealDetailesActivity::class.java)
            intent.putExtra(MEAL_ID, temp.idMeal)
            intent.putExtra(MEAL_STR, temp.strMeal)
            intent.putExtra(MEAL_THUMB, temp.strMealThumb)
            startActivity(intent)
        }

    }

    private fun onRandomLongClick() {

        binding.randomMeal.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(p0: View?): Boolean {
                detailMvvm.getMealByIdBottomSheet(randomMealId)
                return true
            }

        })
    }

    private fun showLoadingCase() {
        binding.apply {
            header.visibility = View.INVISIBLE
            tvWouldLikeToEat.visibility = View.INVISIBLE
            randomMeal.visibility= View.INVISIBLE
            tvOverPupItems.visibility= View.INVISIBLE
            recViewMealsPopular.visibility= View.INVISIBLE
            tvCategory.visibility= View.INVISIBLE
            categoryCard.visibility= View.INVISIBLE
            loadingGif.visibility= View.VISIBLE
            rootHome.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.g_loading))

        }
    }

    private fun cancelLoadingCase() {
        binding.apply {
            header.visibility = View.VISIBLE
            tvWouldLikeToEat.visibility= View.VISIBLE
            randomMeal.visibility= View.VISIBLE
            tvOverPupItems.visibility= View.VISIBLE
            recViewMealsPopular.visibility= View.VISIBLE
            tvCategory.visibility= View.VISIBLE
            categoryCard.visibility = View.VISIBLE
            loadingGif.visibility= View.INVISIBLE
            rootHome.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

        }
    }

    private fun setMealsByCategoryAdapter(meals: List<Meal>) {
        mostPopularFoodAdapter.setMealList(meals)
    }

    private fun setCategoryAdapter(categories: List<Category>) {
        myAdapter.setCategoryList(categories)
    }

    private fun prepareCategoryRecyclerView() {
        binding.recyclerView.apply {
            adapter = myAdapter
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun preparePopularMeals() {
        binding.recViewMealsPopular.apply {
            adapter = mostPopularFoodAdapter
            layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        }
    }

}
