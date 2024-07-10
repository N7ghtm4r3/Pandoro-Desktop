package layouts.ui.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.netguru.multiplatform.charts.pie.PieChart
import com.tecknobit.apimanager.trading.TradingTools.computeProportion
import com.tecknobit.equinox.environment.records.EquinoxUser
import com.tecknobit.pandorocore.records.Project
import com.tecknobit.pandorocore.records.ProjectUpdate.Status
import com.tecknobit.pandorocore.records.ProjectUpdate.Status.*
import com.tecknobit.pandorocore.records.users.User
import com.tecknobit.pandorocore.ui.OverviewUIHelper
import helpers.*
import layouts.components.showOverviewChart
import layouts.ui.screens.SplashScreen.Companion.user
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pandoro.composeapp.generated.resources.*

/**
 * This is the layout for the overview section
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Section
 */
@OptIn(ExperimentalResourceApi::class)
class OverviewSection : Section() {

    /**
     * **overviewUIHelper** -> the manager of the overview UI
     */
    private lateinit var overviewUIHelper: OverviewUIHelper

    /**
     * Function to show the content of the [OverviewSection]
     *
     * No-any params required
     */
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun ShowSection() {
        overviewUIHelper = OverviewUIHelper(user.projects)
        LazyColumn(
            modifier = Modifier
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp,
                    bottom = 20.dp
                )
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = stringResource(Res.string.overview),
                    fontSize = 25.sp
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 20.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        createChartCard(
                            title = stringResource(Res.string.projects),
                            total = { user.projects.size },
                            personal = {
                                var personal = 0
                                user.projects.forEach { project ->
                                    if (!project.hasGroups())
                                        personal++
                                }
                                return@createChartCard personal
                            }
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        createChartCard(
                            title = stringResource(Res.string.updates),
                            total = {
                                var updates = 0
                                user.projects.forEach { project ->
                                    updates += project.updatesNumber
                                }
                                return@createChartCard updates
                            },
                            personal = {
                                var personalUpdates = 0
                                user.projects.forEach { project ->
                                    if (!project.hasGroups())
                                        personalUpdates += project.updatesNumber
                                }
                                return@createChartCard personalUpdates
                            }
                        )
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 20.dp
                        )
                ) {
                    Text(
                        text = stringResource(Res.string.updates_status),
                        fontSize = 20.sp
                    )
                    spaceContent(space = 10.dp)
                    Row(
                        modifier = Modifier
                            .padding(
                                top = 20.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            createChartCard(
                                title = stringResource(Res.string.scheduled),
                                offset = 20.dp,
                                total = { return@createChartCard fetchUpdates(SCHEDULED) },
                                personal = { return@createChartCard fetchPersonalUpdates(SCHEDULED) },
                                userAuthorFun = { return@createChartCard fetchUserAuthorUpdates(SCHEDULED) }
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            createChartCard(
                                title = stringResource(Res.string.in_development),
                                offset = 20.dp,
                                total = { return@createChartCard fetchUpdates(IN_DEVELOPMENT) },
                                personal = { return@createChartCard fetchPersonalUpdates(IN_DEVELOPMENT) },
                                userAuthorFun = { return@createChartCard fetchUserAuthorUpdates(IN_DEVELOPMENT) }
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            createChartCard(
                                title = stringResource(Res.string.published),
                                offset = 20.dp,
                                total = { return@createChartCard fetchUpdates(PUBLISHED) },
                                personal = { return@createChartCard fetchPersonalUpdates(PUBLISHED) },
                                userAuthorFun = { return@createChartCard fetchUserAuthorUpdates(PUBLISHED) }
                            )
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 20.dp,
                            bottom = 20.dp
                        )
                ) {
                    Text(
                        text = stringResource(Res.string.updates_performance),
                        fontSize = 20.sp
                    )
                    spaceContent()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 20.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            createChartCard(
                                title = stringResource(Res.string.development_days),
                                total = {
                                    var total = 0
                                    user.projects.forEach { project -> total += project.totalDevelopmentDays }
                                    return@createChartCard total
                                },
                                personal = {
                                    var personal = 0
                                    user.projects.forEach { project ->
                                        if (!project.hasGroups())
                                            personal += project.totalDevelopmentDays
                                    }
                                    return@createChartCard personal
                                }
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            createChartCard(
                                title = stringResource(Res.string.average_development_days),
                                total = {
                                    var total = 0
                                    user.projects.forEach { project -> total += project.averageDevelopmentTime }
                                    return@createChartCard total
                                },
                                personal = {
                                    var personal = 0
                                    user.projects.forEach { project ->
                                        if (!project.hasGroups())
                                            personal += project.averageDevelopmentTime
                                    }
                                    return@createChartCard personal
                                }
                            )
                        }
                    }
                }
            }
            overviewUIHelper.bestPersonalVProject = overviewUIHelper.getBestPersonalProject()
            overviewUIHelper.bestGroupVProject = overviewUIHelper.getBestGroupProject()
            if (overviewUIHelper.bestPersonalVProject != null || overviewUIHelper.bestGroupVProject != null) {
                item {
                    Text(
                        text = stringResource(Res.string.projects_performance),
                        fontSize = 20.sp
                    )
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 20.dp,
                                bottom = 20.dp
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            if (overviewUIHelper.bestPersonalVProject != null) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(Res.string.personal),
                                        fontSize = 18.sp
                                    )
                                    spaceContent(space = 10.dp)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = 20.dp
                                            ),
                                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth()
                                        ) {
                                            createPerformanceCard(
                                                title = stringResource(Res.string.best_performance),
                                                project = overviewUIHelper.bestPersonalVProject!!
                                            )
                                        }
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth()
                                        ) {
                                            val worstProject = overviewUIHelper.getWorstPersonalProject()
                                            if (worstProject != null) {
                                                createPerformanceCard(
                                                    title = stringResource(Res.string.worst_performance),
                                                    project = worstProject
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            if (overviewUIHelper.bestGroupVProject != null) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(Res.string.group),
                                        fontSize = 18.sp
                                    )
                                    spaceContent(space = 10.dp)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = 20.dp
                                            ),
                                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth()
                                        ) {
                                            createPerformanceCard(
                                                title = stringResource(Res.string.best_performance),
                                                project = overviewUIHelper.bestGroupVProject!!
                                            )
                                        }
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth()
                                        ) {
                                            val worstProject = overviewUIHelper.getWorstGroupProject()
                                            if (worstProject != null) {
                                                createPerformanceCard(
                                                    title = stringResource(Res.string.worst_performance),
                                                    project = worstProject
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Function to create a [Card] with a [PieChart] to show
     *
     * @param title: the title of the card
     * @param total: the function to invoke to obtain the total value for the chart
     * @param personal: the function to invoke to obtain the personal value for the chart
     * @param userAuthorFun: the function to invoke to obtain the user author value for the chart
     * @param offset: the offset to create between the chart and its legend, default 50.[Dp]
     */
    @Composable
    private fun createChartCard(
        title: String,
        total: () -> Int,
        personal: () -> Int,
        userAuthorFun: () -> Int = { 0 },
        offset: Dp = 50.dp
    ) {
        val totalValue = total.invoke()
        val personalValue = personal.invoke()
        val group = totalValue - personalValue
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        showOverviewChart(
                            offset = offset,
                            personal = personalValue,
                            group = group
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(
                                start = offset
                            ),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(
                                        end = 5.dp
                                    ),
                                text = stringResource(Res.string.total) + " -",
                                fontSize = 14.sp
                            )
                            Text(
                                text = "$totalValue",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier
                                .height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(
                                        end = 5.dp
                                    ),
                                text = stringResource(Res.string.personal) + " -",
                                fontSize = 14.sp
                            )
                            Text(
                                modifier = Modifier
                                    .padding(
                                        end = 5.dp
                                    ),
                                text = "$personalValue",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "(${computeProportion(totalValue.toDouble(), personalValue.toDouble(), 2)}%)",
                                color = PRIMARY_COLOR,
                                fontSize = 14.sp
                            )
                        }
                        Row(
                            modifier = Modifier
                                .height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(
                                        end = 5.dp
                                    ),
                                text = stringResource(Res.string.group) + " -",
                                fontSize = 14.sp
                            )
                            Text(
                                modifier = Modifier
                                    .padding(
                                        end = 5.dp
                                    ),
                                text = "$group",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "(${computeProportion(totalValue.toDouble(), group.toDouble(), 2)}%)",
                                color = GREEN_COLOR,
                                fontSize = 14.sp
                            )
                        }
                        val userAuthorValue = userAuthorFun.invoke()
                        if (userAuthorValue > 0) {
                            Row(
                                modifier = Modifier
                                    .height(50.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(
                                            end = 5.dp
                                        ),
                                    text = stringResource(Res.string.by_me) + " -",
                                    fontSize = 14.sp
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(
                                            end = 5.dp
                                        ),
                                    text = "$userAuthorValue",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "(${
                                        computeProportion(
                                            totalValue.toDouble(),
                                            userAuthorValue.toDouble(),
                                            2
                                        )
                                    }%)",
                                    color = YELLOW_COLOR,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Function to create a [Card] for a [Project] to show its performance
     *
     * @param title: the title of the card
     * @param project: the project to show
     */
    @Composable
    private fun createPerformanceCard(
        title: String,
        project: Project
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(185.dp),
            shape = RoundedCornerShape(15.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            onClick = { navToProject(Sections.Overview, project) },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(14f)
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp
                    )
                    Column(
                        modifier = Modifier
                            .padding(
                                start = 5.dp,
                                top = 10.dp
                            )
                    ) {
                        Text(
                            text = stringResource(Res.string.name) + ": ${project.name}",
                            fontSize = 14.sp
                        )
                        Text(
                            modifier = Modifier
                                .padding(
                                    top = 5.dp
                                ),
                            text = stringResource(Res.string.description) + ": ${project.shortDescription}",
                            fontSize = 14.sp
                        )
                        Text(
                            modifier = Modifier
                                .padding(
                                    top = 5.dp
                                ),
                            text = stringResource(Res.string.updates_number) + ": ${project.updatesNumber}",
                            fontSize = 14.sp
                        )
                        Text(
                            modifier = Modifier
                                .padding(
                                    top = 5.dp
                                ),
                            text = stringResource(Res.string.development_days) + ": ${project.totalDevelopmentDays}",
                            fontSize = 14.sp
                        )
                        Text(
                            modifier = Modifier
                                .padding(
                                    top = 5.dp
                                ),
                            text = stringResource(Res.string.average_development_time) + ": ${project.averageDevelopmentTime} "
                                    + stringResource(Res.string.days),
                            fontSize = 14.sp
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (title == stringResource(Res.string.best_performance))
                                    GREEN_COLOR
                                else
                                    RED_COLOR
                            )
                            .fillMaxHeight()
                            .width(10.dp)
                    ) {
                        Text(text = "")
                    }
                }
            }
        }
    }

    /**
     * Function to get the number of the updates by their status
     *
     * @param status: the status of the updates to fetch
     *
     * @return the number of the updates by their status as [Int]
     */
    private fun fetchUpdates(status: Status): Int {
        var updates = 0
        user.projects.forEach { project ->
            project.updates.forEach { update ->
                if (update.status == status)
                    updates++
            }
        }
        return updates
    }

    /**
     * Function to get the number of the personal updates by their status
     *
     * @param status: the status of the updates to fetch
     *
     * @return the number of the personal updates by their status as [Int]
     */
    private fun fetchPersonalUpdates(status: Status): Int {
        var updates = 0
        user.projects.forEach { project ->
            if (!project.hasGroups()) {
                project.updates.forEach { update ->
                    if (update.status == status)
                        updates++
                }
            }
        }
        return updates
    }

    /**
     * Function to get the number of the updates when the [User] is the author by their status
     *
     * @param status: the status of the updates to fetch
     *
     * @return the number of the updates when the [User] is the author by their status as [Int]
     */
    private fun fetchUserAuthorUpdates(status: Status): Int {
        var updates = 0
        user.projects.forEach { project ->
            project.updates.forEach { update ->
                val author: EquinoxUser? = when (status) {
                    SCHEDULED -> update.author
                    IN_DEVELOPMENT -> update.startedBy
                    PUBLISHED -> update.publishedBy
                }
                if (update.status == status && (author == null || author.id == user.id))
                    updates++
            }
        }
        return updates
    }

}