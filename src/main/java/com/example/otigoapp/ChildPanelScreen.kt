package com.example.otigoapp.ui.child

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.otigoapp.R


@Composable
fun ChildPanelScreen(
    onGameClick: (Int) -> Unit,
    onBackToParent: () -> Unit
) {

    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7BC67B)) // yeşil zemin
    ) {

        // 🔧 AYARLAR BUTONU (SOL ÜST)
        IconButton(
            onClick = { showMenu = !showMenu },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Ayarlar",
                tint = Color.Black
            )
        }

        // 🎮 YANA KAYDIRILAN OYUNLAR
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 80.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            itemsIndexed(
                listOf(
                    R.drawable.game,
                    R.drawable.game,
                    R.drawable.game,
                    R.drawable.game,
                    R.drawable.game
                )
            ) { index, image ->
                GameItem(
                    imageRes = image,
                    title = "Oyun ${index + 1}",
                    onClick = { onGameClick(index) }
                )
            }
        }

        // 📂 SOL MENÜ (VELİ PANELİNE DÖN)
        if (showMenu) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(220.dp)
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Spacer(Modifier.height(40.dp))
                Button(
                    onClick = onBackToParent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Veli Paneline Dön")
                }
            }
        }
    }
}

@Composable
fun GameItem(
    imageRes: Int,
    title: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = title,
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        )
        Spacer(Modifier.height(8.dp))
        Text(title)
    }
}
