package com.example.navhostleaktest

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navhostleaktest.ui.theme.NavHostLeakTestTheme

private const val DEFAULT_DESTINATION = "DEFAULT_DESTINATION"
private const val BS_VIEW_TAG = "BS_VIEW_TAG"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavHostLeakTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Demo(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    fun OpenBSButton(modifier: Modifier = Modifier) {
        Text(
            text = "Open BottomSheet",
            modifier = modifier
                .padding(32.dp)
                .clickable {
                    launchBS()
                }
        )
    }

    @Composable
    fun CloseBSButton(modifier: Modifier = Modifier) {
        Text(
            text = "Close BottomSheet",
            modifier = modifier
                .padding(32.dp)
                .clickable {
                    closeBS()
                }
        )
    }

    @Composable
    fun Demo(modifier: Modifier = Modifier) {
        NavHostLeakTestTheme {
            Column(modifier = modifier) {
                OpenBSButton()
                CloseBSButton()
            }
        }
    }

    private fun launchBS() {
        findViewById<ViewGroup>(android.R.id.content)
            ?.let { viewGroup ->
                val composeView = ComposeView(viewGroup.context).apply {
                    setViewCompositionStrategy(
                        ViewCompositionStrategy.DisposeOnDetachedFromWindow
                    )
                    setContent {
                        val navController = rememberNavController()
                        NavHost(navController, startDestination = DEFAULT_DESTINATION) {
                            composable(DEFAULT_DESTINATION) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "COMPOSE BOTTOMSHEET IS DISPLAYING",
                                        modifier = Modifier.padding(32.dp),
                                        color = Color.Red
                                    )
                                }
                            }
                        }
                        DisposableEffect(Unit) {
                            onDispose {
                                navController.popBackStack(DEFAULT_DESTINATION, true)
                            }
                        }
                    }
                    tag = BS_VIEW_TAG
                }
                viewGroup.addView(
                    composeView
                )
            }
    }


    private fun closeBS() {
        findViewById<ViewGroup>(android.R.id.content)
            ?.let { viewGroup ->
                val viewToRemove = viewGroup.findViewWithTag<ComposeView>(BS_VIEW_TAG)
                viewToRemove?.disposeComposition()
                viewGroup.removeView(viewToRemove)
            }
    }
}