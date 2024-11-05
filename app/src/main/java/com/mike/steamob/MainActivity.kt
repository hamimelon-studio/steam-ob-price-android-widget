package com.mike.steamob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mike.steamob.ui.MainViewModel
import com.mike.steamob.ui.theme.SteamObTheme

class MainActivity : ComponentActivity() {
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SteamObTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(viewModel: MainViewModel, modifier: Modifier = Modifier) {
//    val uiState by viewModel.uiState.collectAsState()

    Text(
        text = "hello, please use widget on home screen.",
        modifier = modifier
    )

//    uiState?.run {
//        Column {
//            Text(
//                text = "name: $name",
//                modifier = modifier
//            )
//            Text(
//                text = "discount: $discount",
//                modifier = modifier
//            )
//            Text(
//                text = "discountLevel: $discountLevel",
//                modifier = modifier
//            )
//            Text(
//                text = "initialPrice: $initialPrice",
//                modifier = modifier
//            )
//            Text(
//                text = "price: $price",
//                modifier = modifier
//            )
//        }
//    } ?: Text(
//        text = "loading...",
//        modifier = modifier
//    )
}