package uz.fido.universaldigital.ui.fragments.products.season



import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class SnowfallView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {


    private val snowflakes = mutableListOf<Snowflake>()
    private val paint = Paint()
    private val random = Random(System.currentTimeMillis())
    private var viewWidth = 0
    private var viewHeight = 0

    init {
        paint.color = Color.WHITE
        paint.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Ekran o'lchamlari o'zgarganida, o'lchamlarni saqlaymiz
        viewWidth = w
        viewHeight = h

        // 100 ta qor parchasini yaratish
        for (i in 0 until 100) {
            val snowflake = Snowflake(
                x = random.nextInt(viewWidth).toFloat(),
                y = random.nextInt(viewHeight).toFloat(),
                speed = random.nextFloat() * 5 + 1f,
                size = random.nextInt(3) + 2f
            )
            snowflakes.add(snowflake)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Qor parchalarini chizish
        for (snowflake in snowflakes) {
            canvas.drawCircle(snowflake.x, snowflake.y, snowflake.size, paint)
        }

        // Qor parchalarini harakatlantirish
        for (snowflake in snowflakes) {
            snowflake.y += snowflake.speed

            // Y koordinatasi ekran chegarasiga yetganida, qor parchasini yuqoriga qaytarish
            if (snowflake.y > viewHeight) {
                snowflake.y = 0f
                snowflake.x = random.nextInt(viewWidth).toFloat()
            }
        }

        // Ekranni yangilash uchun invalidate() chaqirish
        invalidate()
    }

    data class Snowflake(var x: Float, var y: Float, val speed: Float, val size: Float)


}