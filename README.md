<h1>Android 小畫家 簽名 畫板</h1>

<ol>
  <li><a href="https://badgameshow.com/fly/android-小畫家-簽名-畫板/fly/android/#a">創建SignatureView</a></li>
  <li><a href="https://badgameshow.com/fly/android-小畫家-簽名-畫板/fly/android/#b">方法調用&布局</a></li>
  <li><a href="https://badgameshow.com/fly/android-小畫家-簽名-畫板/fly/android/#c">效果展示</a></li>
 <li><a href="https://badgameshow.com/fly/android-小畫家-簽名-畫板/fly/android/#d">Github</a></li>

</ol>

<hr />

<a id="a"></a>

<h3>1.創建SignatureView</h3>

<pre><code class="language-Kotlin line-numbers">package com.example.signatureview

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

/**
 * Author: Wade
 * E-mail: tony91097@gmail.com
 * Date: 2021/7/19
 */
@Suppress("DEPRECATION")
class SignatureView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val path = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private lateinit var mBitmapCanvas: Canvas
    private lateinit var mBitmap: Bitmap
    private var clickX = 0f
    private var clickY = 0f
    private var startX = 0f
    private var startY = 0f

    init {
        createBitmap(context)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
        mBitmapCanvas.drawPath(path, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        startX = event.x
        startY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                clickX = startX
                clickY = startY
                path.moveTo(startX, startY)
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.quadTo(clickX, clickY, (clickX + startX) / 2, (clickY + startY) / 2)
                clickX = startX
                clickY = startY
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> return true
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }

    //重製圖片
    fun resetCanvas(mainActivity: MainActivity) {
        path.reset()
        createBitmap(mainActivity)
        invalidate()
    }

    //儲存圖片
    fun savePicture(mainActivity: MainActivity) {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "draw.png")
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        val uri = mainActivity.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        val outputStream = mainActivity.contentResolver.openOutputStream(uri!!)
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream?.close()
    }

    //設定bitmap大小
    private fun createBitmap(context: Context?) {
        //獲取手機顯示相關
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.getRealMetrics(displayMetrics)
        } else {
            val windowManager: WindowManager =
                context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)
        }

        mBitmap = Bitmap.createBitmap(
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            Bitmap.Config.ARGB_8888
        )
        mBitmapCanvas = Canvas(mBitmap)
        mBitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }
}
</code></pre>

<a id="b"></a>

<h3>2.方法調用&布局</h3>

#### Layout

```XML
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.example.signatureview.SignatureView
        android:id="@+id/signatureView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toTopOf="@+id/appCompatButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />


    <androidx.appcompat.widget.AppCompatButton
        android:id="@+id/appCompatButton2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:onClick="reset"
        android:text="重畫"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toStartOf="@+id/appCompatButton"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent" />

    <androidx.appcompat.widget.AppCompatButton
        android:onClick="save"
        android:id="@+id/appCompatButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="儲存"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toEndOf="@+id/appCompatButton2" />


</androidx.constraintlayout.widget.ConstraintLayout>
```

#### 調用

<pre><code class="language-Kotlin line-numbers">package com.example.signatureview

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
</code></pre>

<h3>3.效果展示</h3>

<img src="https://badgameshow.com/fly/wp-content/uploads/2021/07/Screenrecorder-2021-07-19-17-40-32-684.gif" width="30%">

<h4>4.Github</h4>

<a class="wp-editor-md-post-content-link" href="https://github.com/MuHongWeiWei/SignatureView">Github</a>
