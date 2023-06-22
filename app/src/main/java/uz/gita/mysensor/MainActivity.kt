package uz.gita.mysensor

import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import uz.gita.mysensor.databinding.ActivityMainBinding
import java.util.Random
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val sensorManager by lazy { getSystemService(SENSOR_SERVICE) as SensorManager }
    private val sensor by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    private val pointEnd by lazy { Point(binding.container.width, binding.container.height) }
    private var lastEvent: SensorEvent? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            lastEvent = event
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        movementBoll()
    }

    private fun periodTimer(): Flow<Unit> = flow {
        while (true) {
            delay(50)
            emit(Unit)
        }
    }.flowOn(Dispatchers.IO)


    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(listener)
    }

    private fun movementBoll() {
        periodTimer().onEach {
            lastEvent?.let {

                binding.ball.x -= binding.ball.width * (it.values[0] / 5)

                if (binding.ball.x < 0) binding.ball.x = 0f
                if (binding.ball.x > pointEnd.x - binding.ball.width) binding.ball.x =
                    (pointEnd.x - binding.ball.width).toFloat()

                binding.ball.y += binding.ball.height * (it.values[1] / 5)

                if (binding.ball.y < 0f) binding.ball.y = 0f
                if (binding.ball.y > pointEnd.y - binding.ball.height) binding.ball.y =
                    (pointEnd.y - binding.ball.height).toFloat()
                win()
            }
        }.launchIn(scope)
    }

    private fun win() {
        binding.apply {
            val ballCenterX = ball.x + ball.width / 2
            val ballCenterY = ball.y + ball.height / 2
            val deepCenterX = deep.x + deep.width / 2
            val deepCenterY = deep.y + deep.height / 2
            val _height = abs(ballCenterY - deepCenterY)
            val _width = abs(ballCenterX - deepCenterX)

            if (_width <= (ball.width / 2) && _height <= (ball.height / 2)) {
                /*sensorManager.unregisterListener(listener)
                lastEvent = null*/
                runBlocking(Dispatchers.Main) {
                    ball.animate()
                        .setDuration(100)
                        .scaleX(0f)
                        .scaleY(0f)
                        .withEndAction {
                            ball.apply {
                                x = 0f
                                y = 1f
                                scaleX = 1f
                                scaleY = 1f
                            }
                        }
                        .start()
                }
            }
        }
    }


}