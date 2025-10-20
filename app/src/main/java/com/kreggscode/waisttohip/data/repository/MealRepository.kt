package com.kreggscode.waisttohip.data.repository

import com.kreggscode.waisttohip.data.local.MealDao
import com.kreggscode.waisttohip.data.local.MealEntity
import com.kreggscode.waisttohip.ui.screens.FoodItem
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepository @Inject constructor(
    private val mealDao: MealDao
) {
    fun getRecentMeals(): Flow<List<MealEntity>> = mealDao.getRecentMeals()
    
    fun getAllMeals(): Flow<List<MealEntity>> = mealDao.getAllMeals()
    
    suspend fun saveMeal(items: List<FoodItem>): Long {
        val totalCalories = items.sumOf { (it.calories * it.quantity).toInt() }
        val totalProtein = items.sumOf { (it.protein * it.quantity).toInt() }
        val totalCarbs = items.sumOf { (it.carbs * it.quantity).toInt() }
        val totalFat = items.sumOf { (it.fat * it.quantity).toInt() }
        
        val itemsJson = foodItemsToJson(items)
        
        val meal = MealEntity(
            timestamp = System.currentTimeMillis(),
            totalCalories = totalCalories,
            totalProtein = totalProtein,
            totalCarbs = totalCarbs,
            totalFat = totalFat,
            itemsJson = itemsJson
        )
        
        return mealDao.insertMeal(meal)
    }
    
    suspend fun deleteMeal(meal: MealEntity) {
        mealDao.deleteMeal(meal)
    }
    
    private fun foodItemsToJson(items: List<FoodItem>): String {
        val jsonArray = JSONArray()
        items.forEach { item ->
            val jsonObject = JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("calories", item.calories)
                put("protein", item.protein)
                put("carbs", item.carbs)
                put("fat", item.fat)
                put("quantity", item.quantity)
            }
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }
    
    fun jsonToFoodItems(json: String): List<FoodItem> {
        val items = mutableListOf<FoodItem>()
        val jsonArray = JSONArray(json)
        
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            items.add(
                FoodItem(
                    id = jsonObject.getString("id"),
                    name = jsonObject.getString("name"),
                    calories = jsonObject.getInt("calories"),
                    protein = jsonObject.getInt("protein"),
                    carbs = jsonObject.getInt("carbs"),
                    fat = jsonObject.getInt("fat"),
                    quantity = jsonObject.getDouble("quantity").toFloat()
                )
            )
        }
        
        return items
    }
}
