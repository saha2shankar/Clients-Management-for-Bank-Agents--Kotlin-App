package np.com.harishankarsah.tuntun.presentation.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun ClientGrowthChart( 
    data: List<FloatEntry>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return
    
    val chartEntryModel = remember(data) { entryModelOf(data) }
    
    val horizontalAxisValueFormatter = remember {
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            val millis = TimeUnit.DAYS.toMillis(value.toLong())
            SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(millis))
        }
    }

    Chart(
        chart = columnChart(),
        model = chartEntryModel,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(valueFormatter = horizontalAxisValueFormatter),
        modifier = modifier
    )
}

@Composable
fun PaymentHistoryChart(
    data: List<FloatEntry>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val chartEntryModel = remember(data) { entryModelOf(data) }

    val horizontalAxisValueFormatter = remember {
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            val millis = TimeUnit.DAYS.toMillis(value.toLong())
            SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(millis))
        }
    }

    Chart(
        chart = lineChart(),
        model = chartEntryModel,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(valueFormatter = horizontalAxisValueFormatter),
        modifier = modifier
    )
}
