package com.example.signatureview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    lateinit var signatureView: SignatureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signatureView = findViewById(R.id.signatureView)
    }

    fun save(view: View) {
        signatureView.savePicture(this)
    }

    fun reset(view: View) {
        signatureView.resetCanvas(this)
    }
}