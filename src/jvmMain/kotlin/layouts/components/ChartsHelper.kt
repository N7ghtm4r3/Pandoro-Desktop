package layouts.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.netguru.multiplatform.charts.ChartAnimation
import com.netguru.multiplatform.charts.bar.*
import com.netguru.multiplatform.charts.line.LineChart
import com.netguru.multiplatform.charts.pie.PieChart
import com.netguru.multiplatform.charts.pie.PieChartConfig
import com.netguru.multiplatform.charts.pie.PieChartData
import helpers.GREEN_COLOR
import helpers.PRIMARY_COLOR
import helpers.RED_COLOR
import toImportFromLibrary.Update

/**
 * Function to show the [LineChart] of a project
 *
 * @param publishedUpdates: the published updates of the project
 */
@Composable
fun showProjectChart(publishedUpdates: ArrayList<Update>) {
    val updates = mutableListOf<BarChartEntry>()
    publishedUpdates.forEach { update ->
        updates.add(
            BarChartEntry(
                x = "v. ${update.targetVersion}",
                y = update.developmentDuration.toFloat(),
                color = RED_COLOR
            )
        )
    }
    BarChart(
        data = BarChartData(
            categories = listOf(
                BarChartCategory(
                    name = "Development duration",
                    entries = updates
                )
            )
        ),
        overlayDataEntryLabel = { dataName, value ->
            Text(
                text = "$dataName: ${(value as Float).toInt()} days",
                color = Color.Black,
                fontSize = 14.sp
            )
        },
        config = BarChartConfig(
            thickness = 75.dp,
            cornerRadius = 5.dp,
            barsSpacing = 10.dp
        ),
        modifier = Modifier.height(300.dp),
        animation = ChartAnimation.Sequenced(),
    )
}

/**
 * Function to show the [PieChart] for an overview
 *
 * @param personalDataColor: the [Color] for the personal data set
 * @param personal: the personal value
 * @param groupDataColor: the [Color] for the group data set
 * @param group: the group value
 * @param offset: the offset to create between the chart and its legend
 */
@Composable
fun showOverviewChart(
    personalDataColor: Color = PRIMARY_COLOR,
    personal: Int,
    groupDataColor: Color = GREEN_COLOR,
    group: Int,
    offset: Dp = 50.dp
) {
    val data = listOf(
        PieChartData(
            name = "Personal",
            color = personalDataColor,
            value = personal.toDouble()
        ),
        PieChartData(
            name = "Group",
            color = groupDataColor,
            value = group.toDouble()
        )
    )
    PieChart(
        modifier = Modifier.size(240.dp).padding(start = offset).shadow(elevation = 10.dp, shape = CircleShape),
        data = data,
        config = PieChartConfig(
            thickness = 60.dp
        )
    )
}