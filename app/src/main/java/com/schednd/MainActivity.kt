package com.schednd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.schednd.ui.navigation.SchedndNavGraph
import com.schednd.ui.theme.SchedndTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchedndTheme {
                val navController = rememberNavController()
                SchedndNavGraph(navController = navController)
            }
        }
    }
}
