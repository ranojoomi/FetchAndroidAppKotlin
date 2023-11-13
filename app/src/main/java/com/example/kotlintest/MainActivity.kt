package com.example.kotlintest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlintest.ui.theme.KotLinTestTheme

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Item (val id: Int, val listId: Int, val name: String?)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataFetch = DataFetch()
        var res: String? = null
        val t1 = Thread {
            run {
                res = dataFetch.getDataFromWeb("https://fetch-hiring.s3.amazonaws.com/hiring.json")
            }
        }

        t1.start()
        t1.join()

        val allItem: List<Item>? = res?.let { dataFetch.convertToList(it) }

        val finalMap: Map<Int, List<Item>>? = if (allItem != null) {
            dataFetch.groupByListId(allItem)
        } else null

        setContent {
            KotLinTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (finalMap != null) DisplayList(finalMap = finalMap)
                    else ErrorHandle()
                }
            }
        }
    }

    @Composable
    private fun DisplayList(
        finalMap: Map<Int, List<Item>>,
    ) {
        LazyColumn {
            finalMap.forEach { (key, value) ->
                item {
                    CategoryHeader(text = key.toString())
                }
                items(value.size) { index ->
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryItem(item = value[index])
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    @Composable
    private fun CategoryHeader(
        text: String,
        modifier: Modifier = Modifier
    ) {
        Text(
            text = "List Id: $text",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(16.dp)
        )
    }

    @Composable
    private fun CategoryItem(
        item: Item,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            item.name?.let { Text(
                text = it,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            }
            Text(text = "listId: ${item.listId}")
            Text(text = "Id: ${item.id}")
        }
    }

    @Composable
    private fun ErrorHandle(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error getting results",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
        }
    }

}