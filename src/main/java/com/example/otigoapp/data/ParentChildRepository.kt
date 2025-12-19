package com.example.otigoapp.data

import androidx.compose.runtime.mutableStateListOf
import com.example.otigoapp.R
import com.example.otigoapp.ui.parent.ParentChild

object ParentChildRepository {

    // uygulama boyunca yaşayan liste
    val children = mutableStateListOf<ParentChild>()

    fun addChild(name: String, age: Int) {
        children.add(
            ParentChild(
                name = name,
                age = age,
                avatar = R.drawable.avatar_1
            )
        )
    }
}
