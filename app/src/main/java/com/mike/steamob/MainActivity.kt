package com.mike.steamob

import android.content.Context
import android.content.Intent
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

    companion object {
        fun intent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
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