package com.example.signatureview

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