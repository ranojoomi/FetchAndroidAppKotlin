package com.example.kotlintest

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class DataFetch {

    fun getDataFromWeb(url: String): String? {
        val checkedURL = url.toHttpUrlOrNull() ?: return null
        val client = OkHttpClient()
        val request: Request = Request.Builder().url(checkedURL).build()
        client.newCall(request).execute().use { response -> return response.body!!.string() }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun convertToList(dataset: String): List<Item>? {
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<List<Item>> = moshi.adapter()

        try {
            val allItems: List<Item>? = jsonAdapter.fromJson(dataset)
            val filteredItems = ArrayList<Item>()

            if (allItems != null) {
                for (item in allItems) {
                    if (!item.name.isNullOrEmpty()) {
                        filteredItems.add(item)
                    }
                }
            } else return null

            return filteredItems

        } catch (e: IOException) {
            Log.d("IOE exception", "Data class different from requested data")
            return null
        }
    }

    fun groupByListId(allItems: List<Item>): Map<Int, List<Item>> {
        val toReturn: MutableMap<Int, MutableList<Item>> = HashMap()
        for (item in allItems) {
            if (item.listId in toReturn) {
                toReturn[item.listId]!!.add(item)
            }
            else {
                val newList = mutableListOf<Item>()
                newList.add(item)
                toReturn[item.listId] = newList
            }
        }

        toReturn.forEach { (_, value) ->
            value.sortBy {item ->
                item.name
            }
        }
        return toReturn.toSortedMap()
    }
}