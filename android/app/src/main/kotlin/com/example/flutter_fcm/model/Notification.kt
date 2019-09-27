package com.example.flutter_fcm.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class Notification(
        @SerializedName("title")
        @Expose var title: String?,

        @SerializedName("body")
        @Expose var body: String?
)