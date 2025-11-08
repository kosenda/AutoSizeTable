package ksnd.autosizetable

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Configuration for 2D drag-based scrolling behavior.
 *
 * @param velocityMultiplier Multiplier for fling velocity. Higher values result in longer scroll distance.
 * @param decelerationFactor Deceleration rate during fling animation (0.0 to 1.0).
 * @param animationSteps Number of animation frames for fling.
 * @param frameDurationMs Duration of each animation frame in milliseconds.
 */
data class DragScroll2DConfig(
    val velocityMultiplier: Float = 1.0f,
    val decelerationFactor: Float = 0.88f,
    val animationSteps: Int = 50,
    val frameDurationMs: Long = 12L,
)

/**
 * 2D drag-based scroll state management class that handles vertical, horizontal, and diagonal scrolling.
 *
 * This class provides unified management of scroll velocities in both directions and
 * applies smooth fling animation with proper physics for diagonal scrolling.
 * It detects drag gestures and translates them into smooth 2D scrolling with inertia.
 *
 * @param horizontalScrollState The scroll state for horizontal scrolling.
 * @param verticalScrollState The scroll state for vertical scrolling.
 * @param config Configuration for fling animation behavior.
 */
class DragScroll2DState(
    private val horizontalScrollState: ScrollState,
    private val verticalScrollState: ScrollState,
    private val config: DragScroll2DConfig = DragScroll2DConfig(),
) {
    // Velocity tracking (private - internal use only)
    private var horizontalVelocity by mutableFloatStateOf(0f)
    private var verticalVelocity by mutableFloatStateOf(0f)

    // Fling animation state (public - for UI feedback)
    var isFlingActive by mutableStateOf(false)
        private set

    // Fling animation job
    private var flingJob: Job? = null

    /**
     * Called during drag to update velocities and apply scroll.
     */
    fun onDrag(dragAmountX: Float, dragAmountY: Float) {
        horizontalVelocity = dragAmountX
        verticalVelocity = dragAmountY

        // Apply horizontal scroll (dispatchRawDelta handles boundary checking)
        if (dragAmountX != 0f) {
            horizontalScrollState.dispatchRawDelta(-dragAmountX)
        }

        // Apply vertical scroll (dispatchRawDelta handles boundary checking)
        if (dragAmountY != 0f) {
            verticalScrollState.dispatchRawDelta(-dragAmountY)
        }
    }

    /**
     * Called when drag starts.
     */
    fun onDragStart() {
        flingJob?.cancel()
        horizontalVelocity = 0f
        verticalVelocity = 0f
        isFlingActive = false
    }

    /**
     * Called when drag is cancelled.
     */
    fun onDragCancel() {
        flingJob?.cancel()
        horizontalVelocity = 0f
        verticalVelocity = 0f
        isFlingActive = false
    }

    /**
     * Apply fling animation with smooth deceleration.
     * This handles diagonal scrolling by maintaining velocity ratios.
     *
     * @param coroutineScope The coroutine scope to launch the fling animation in.
     */
    fun onDragEnd(coroutineScope: CoroutineScope) {
        // Calculate the magnitude of velocity (diagonal velocity)
        val velocityMagnitude = sqrt(
            horizontalVelocity * horizontalVelocity + verticalVelocity * verticalVelocity
        )

        // If velocity is negligible, don't start fling animation
        if (velocityMagnitude < 1f) {
            return
        }

        // Cancel any existing fling animation
        flingJob?.cancel()

        isFlingActive = true

        // Normalize velocities to maintain direction during fling
        val normalizedHorizontal = if (velocityMagnitude > 0) {
            horizontalVelocity / velocityMagnitude
        } else {
            0f
        }

        val normalizedVertical = if (velocityMagnitude > 0) {
            verticalVelocity / velocityMagnitude
        } else {
            0f
        }

        // Launch fling animation with smooth exponential deceleration
        flingJob = coroutineScope.launch {
            repeat(config.animationSteps) { step ->
                val decayFactor = config.decelerationFactor.pow(step.toFloat())
                val currentMagnitude = velocityMagnitude * config.velocityMultiplier * decayFactor

                val currentHorizontalVelocity = normalizedHorizontal * currentMagnitude
                val currentVerticalVelocity = normalizedVertical * currentMagnitude

                // Apply scroll for both axes simultaneously (enables diagonal fling)
                // dispatchRawDelta handles boundary checking automatically
                if (currentHorizontalVelocity.absoluteValue > 0.1f) {
                    horizontalScrollState.dispatchRawDelta(-currentHorizontalVelocity)
                }

                if (currentVerticalVelocity.absoluteValue > 0.1f) {
                    verticalScrollState.dispatchRawDelta(-currentVerticalVelocity)
                }

                // Wait for next frame
                if (step < config.animationSteps - 1) {
                    kotlinx.coroutines.delay(config.frameDurationMs)
                }
            }

            // Animation complete
            horizontalVelocity = 0f
            verticalVelocity = 0f
            isFlingActive = false
        }
    }
}

/**
 * Create and remember a DragScroll2DState instance.
 *
 * @param horizontalScrollState The scroll state for horizontal scrolling. If not provided, a new one is created.
 * @param verticalScrollState The scroll state for vertical scrolling. If not provided, a new one is created.
 * @param config Configuration for fling animation behavior. See [DragScroll2DConfig] for customization options.
 * @return A remembered [DragScroll2DState] instance that survives recomposition.
 *
 * @sample
 * ```
 * // Use with default configuration
 * val dragScroll2DState = rememberDragScroll2DState()
 *
 * // Use with custom configuration
 * val customConfig = DragScroll2DConfig(
 *     velocityMultiplier = 2.0f,
 *     decelerationFactor = 0.85f,
 *     animationSteps = 60
 * )
 * val dragScroll2DState = rememberDragScroll2DState(config = customConfig)
 * ```
 */
@Composable
fun rememberDragScroll2DState(
    horizontalScrollState: ScrollState = rememberScrollState(),
    verticalScrollState: ScrollState = rememberScrollState(),
    config: DragScroll2DConfig = DragScroll2DConfig(),
): DragScroll2DState {
    return remember(horizontalScrollState, verticalScrollState, config) {
        DragScroll2DState(horizontalScrollState, verticalScrollState, config)
    }
}
