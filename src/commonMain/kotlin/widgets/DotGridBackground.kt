package widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

/**
 * The deck's background: near-black with a subtle amber dot grid.
 * Drawn behind the slides in the Presentation decoration.
 */
@Composable
fun BoxScope.DotGridBackground() {
    Canvas(Modifier.matchParentSize()) {
        val spacing = 24.dp.toPx()
        val radius = 1.dp.toPx()
        val color = Livewire.Amber.copy(alpha = 0.14f)
        var y = spacing / 2
        while (y < size.height) {
            var x = spacing / 2
            while (x < size.width) {
                drawCircle(color, radius, Offset(x, y))
                x += spacing
            }
            y += spacing
        }
    }
}
