package layouts.ui.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
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
import helpers.*
import layouts.components.showOverviewChart
import layouts.ui.screens.SplashScreen.Companion.user
import toImportFromLibrary.Project
import toImportFromLibrary.Update.Status
import toImportFromLibrary.Update.Status.*
import toImportFromLibrary.User

/**
 * This is the layout for the overview section
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Section
 */
class OverviewSection : Section() {

    /**
     * **bestPersonalProject** -> the best personal project
     */
    private var bestPersonalProject: Project? = null

    /**
     * **bestGroupProject** -> the best group project
     */
    private var bestGroupProject: Project? = null

    /**
     * Function to show the content of the [OverviewSection]
     *
     * No-any params required
     */
    @Composable
    override fun showSection() {
        LazyColumn(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 20.dp).fillMaxSize()
        ) {
            item {
                Text(
                    text = "Overview",
                    fontSize = 25.sp
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    ) {
                        createChartCard(
                            title = "Projects",
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
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    ) {
                        createChartCard(
                            title = "Updates",
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
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                ) {
                    Text(
                        text = "Updates status",
                        fontSize = 20.sp
                    )
                    spaceContent(space = 10.dp)
                    Row(
                        modifier = Modifier.padding(top = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        ) {
                            createChartCard(
                                title = "Scheduled",
                                offset = 20.dp,
                                total = { return@createChartCard fetchUpdates(SCHEDULED) },
                                personal = { return@createChartCard fetchPersonalUpdates(SCHEDULED) },
                                userAuthorFun = { return@createChartCard fetchUserAuthorUpdates(SCHEDULED) }
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        ) {
                            createChartCard(
                                title = "In development",
                                offset = 20.dp,
                                total = { return@createChartCard fetchUpdates(IN_DEVELOPMENT) },
                                personal = { return@createChartCard fetchPersonalUpdates(IN_DEVELOPMENT) },
                                userAuthorFun = { return@createChartCard fetchUserAuthorUpdates(IN_DEVELOPMENT) }
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        ) {
                            createChartCard(
                                title = "Published",
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
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 20.dp)
                ) {
                    Text(
                        text = "Updates performance",
                        fontSize = 20.sp
                    )
                    spaceContent()
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        ) {
                            createChartCard(
                                title = "Development days",
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
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        ) {
                            createChartCard(
                                title = "Average development time (days)",
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
            bestPersonalProject = getBestPersonalProject()
            bestGroupProject = getBestGroupProject()
            if (bestPersonalProject != null || bestGroupProject != null) {
                item {
                    Text(
                        text = "Projects performance",
                        fontSize = 20.sp
                    )
                }
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            if (bestPersonalProject != null) {
                                Column(
                                    modifier = Modifier.weight(1f).fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Personal",
                                        fontSize = 18.sp
                                    )
                                    spaceContent(space = 10.dp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f).fillMaxWidth()
                                        ) {
                                            createPerformanceCard(
                                                title = "Best",
                                                project = bestPersonalProject!!
                                            )
                                        }
                                        Column(
                                            modifier = Modifier.weight(1f).fillMaxWidth()
                                        ) {
                                            val worstProject = getWorstPersonalProject()
                                            if (worstProject != null) {
                                                createPerformanceCard(
                                                    title = "Worst",
                                                    project = worstProject
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            if (bestGroupProject != null) {
                                Column(
                                    modifier = Modifier.weight(1f).fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Group",
                                        fontSize = 18.sp
                                    )
                                    spaceContent(space = 10.dp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f).fillMaxWidth()
                                        ) {
                                            createPerformanceCard(
                                                title = "Best",
                                                project = bestGroupProject!!
                                            )
                                        }
                                        Column(
                                            modifier = Modifier.weight(1f).fillMaxWidth()
                                        ) {
                                            val worstProject = getWorstGroupProject()
                                            if (worstProject != null) {
                                                createPerformanceCard(
                                                    title = "Worst",
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
            modifier = Modifier.fillMaxWidth().height(350.dp),
            shape = RoundedCornerShape(15.dp),
            backgroundColor = Color.White,
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp
                )
                Row(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    ) {
                        showOverviewChart(
                            offset = offset,
                            personal = personalValue,
                            group = group
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f).fillMaxSize().padding(start = offset),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(end = 5.dp),
                                text = "Total -",
                                fontSize = 14.sp
                            )
                            Text(
                                text = "$totalValue",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier.height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(end = 5.dp),
                                text = "Personal -",
                                fontSize = 14.sp
                            )
                            Text(
                                modifier = Modifier.padding(end = 5.dp),
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
                            modifier = Modifier.height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(end = 5.dp),
                                text = "Group -",
                                fontSize = 14.sp
                            )
                            Text(
                                modifier = Modifier.padding(end = 5.dp),
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
                                modifier = Modifier.height(50.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier.padding(end = 5.dp),
                                    text = "From me -",
                                    fontSize = 14.sp
                                )
                                Text(
                                    modifier = Modifier.padding(end = 5.dp),
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
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun createPerformanceCard(title: String, project: Project) {
        Card(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            shape = RoundedCornerShape(15.dp),
            backgroundColor = Color.White,
            onClick = { navToProject(Sections.Overview, project) },
            elevation = 2.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.weight(14f).fillMaxWidth().padding(20.dp)
                ) {
                    Text(
                        text = "$title performance",
                        fontSize = 18.sp
                    )
                    Column(
                        modifier = Modifier.padding(start = 5.dp, top = 10.dp)
                    ) {
                        Text(
                            text = "Name: ${project.name}",
                            fontSize = 14.sp
                        )
                        Text(
                            modifier = Modifier.padding(top = 5.dp),
                            text = "Description: ${project.shortDescription}",
                            fontSize = 14.sp
                        )
                        Text(
                            modifier = Modifier.padding(top = 5.dp),
                            text = "Updates number: ${project.updatesNumber}",
                            fontSize = 14.sp
                        )
                        Text(
                            modifier = Modifier.padding(top = 5.dp),
                            text = "Development days: ${project.totalDevelopmentDays}",
                            fontSize = 14.sp
                        )
                        Text(
                            modifier = Modifier.padding(top = 5.dp),
                            text = "Average development time: ${project.averageDevelopmentTime} days",
                            fontSize = 14.sp
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.background(if (title == "Best") GREEN_COLOR else RED_COLOR)
                            .fillMaxHeight().width(10.dp)
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
     * Function to get the number of the updates when the [user] is the author by their status
     *
     * @param status: the status of the updates to fetch
     *
     * @return the number of the updates when the [user] is the author by their status as [Int]
     */
    private fun fetchUserAuthorUpdates(status: Status): Int {
        var updates = 0
        user.projects.forEach { project ->
            project.updates.forEach { update ->
                val author: User? = when (status) {
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

    /**
     * Function to get the personal best project in terms of performance
     *
     * No-any params required
     *
     * @return the best personal project in terms of performance as [Project]
     */
    private fun getBestPersonalProject(): Project? {
        var bestProject: Project? = null
        var updatesNumber = 0
        var developmentDays = 0
        var averageDevelopmentTime = 0
        user.projects.forEach { project ->
            if (!project.hasGroups()) {
                val pAverageDevelopmentTime = project.averageDevelopmentTime
                if (pAverageDevelopmentTime > averageDevelopmentTime) {
                    averageDevelopmentTime = pAverageDevelopmentTime
                    developmentDays = project.totalDevelopmentDays
                }
            }
        }
        user.projects.forEach { project ->
            val pUpdatesNumber = project.updatesNumber
            if (!project.hasGroups() && pUpdatesNumber > 0) {
                val pDevelopmentDays = project.totalDevelopmentDays
                val pAverageDevelopmentTime = project.averageDevelopmentTime
                if (pUpdatesNumber >= updatesNumber && pDevelopmentDays <= developmentDays) {
                    if (pAverageDevelopmentTime < averageDevelopmentTime || bestProject == null) {
                        bestProject = project
                        updatesNumber = pUpdatesNumber
                        developmentDays = pDevelopmentDays
                        averageDevelopmentTime = pAverageDevelopmentTime
                    }
                }
            }
        }
        return bestProject
    }

    /**
     * Function to get the worst personal project in terms of performance
     *
     * No-any params required
     *
     * @return the worst personal project in terms of performance as [Project]
     */
    private fun getWorstPersonalProject(): Project? {
        var worstProject: Project? = null
        var updatesNumber = 0
        var developmentDays = 0
        var averageDevelopmentTime = 0
        if (bestPersonalProject != null) {
            user.projects.forEach { project ->
                if (!project.hasGroups() && !project.id.equals(bestPersonalProject!!.id)) {
                    val pUpdatesNumber = project.updatesNumber
                    if (pUpdatesNumber > updatesNumber) {
                        updatesNumber = pUpdatesNumber
                        developmentDays = project.totalDevelopmentDays
                    }
                }
            }
            user.projects.forEach { project ->
                val pUpdatesNumber = project.updatesNumber
                if (!project.hasGroups() && pUpdatesNumber > 0 && !project.id.equals(bestPersonalProject!!.id)) {
                    val pDevelopmentDays = project.totalDevelopmentDays
                    val pAverageDevelopmentTime = project.averageDevelopmentTime
                    if (pUpdatesNumber <= updatesNumber && pDevelopmentDays >= developmentDays) {
                        if (pAverageDevelopmentTime > averageDevelopmentTime || worstProject == null) {
                            worstProject = project
                            updatesNumber = pUpdatesNumber
                            developmentDays = pDevelopmentDays
                            averageDevelopmentTime = pAverageDevelopmentTime
                        }
                    }
                }
            }
        }
        return worstProject
    }

    /**
     * Function to get the best group project in terms of performance
     *
     * No-any params required
     *
     * @return the best group project in terms of performance as [Project]
     */
    private fun getBestGroupProject(): Project? {
        var bestProject: Project? = null
        var updatesNumber = 0
        var developmentDays = 0
        var averageDevelopmentTime = 0
        user.projects.forEach { project ->
            if (project.hasGroups()) {
                val pAverageDevelopmentTime = project.averageDevelopmentTime
                if (pAverageDevelopmentTime > averageDevelopmentTime) {
                    averageDevelopmentTime = pAverageDevelopmentTime
                    developmentDays = project.totalDevelopmentDays
                }
            }
        }
        user.projects.forEach { project ->
            val pUpdatesNumber = project.updatesNumber
            if (project.hasGroups() && pUpdatesNumber > 0) {
                val pDevelopmentDays = project.totalDevelopmentDays
                val pAverageDevelopmentTime = project.averageDevelopmentTime
                if (pUpdatesNumber >= updatesNumber && pDevelopmentDays <= developmentDays) {
                    if (pAverageDevelopmentTime < averageDevelopmentTime || bestProject == null) {
                        bestProject = project
                        updatesNumber = pUpdatesNumber
                        developmentDays = pDevelopmentDays
                        averageDevelopmentTime = pAverageDevelopmentTime
                    }
                }
            }
        }
        return bestProject
    }

    /**
     * Function to get the worst group project in terms of performance
     *
     * No-any params required
     *
     * @return the worst group project in terms of performance as [Project]
     */
    private fun getWorstGroupProject(): Project? {
        var worstProject: Project? = null
        var updatesNumber = 0
        var developmentDays = 0
        var averageDevelopmentTime = 0
        if (bestGroupProject != null) {
            user.projects.forEach { project ->
                if (project.hasGroups() && !project.id.equals(bestGroupProject!!.id)) {
                    val pUpdatesNumber = project.updatesNumber
                    if (pUpdatesNumber > updatesNumber) {
                        updatesNumber = pUpdatesNumber
                        developmentDays = project.totalDevelopmentDays
                    }
                }
            }
            user.projects.forEach { project ->
                val pUpdatesNumber = project.updatesNumber
                if (project.hasGroups() && pUpdatesNumber > 0 && !project.id.equals(bestGroupProject!!.id)) {
                    val pDevelopmentDays = project.totalDevelopmentDays
                    val pAverageDevelopmentTime = project.averageDevelopmentTime
                    if (pUpdatesNumber <= updatesNumber && pDevelopmentDays >= developmentDays) {
                        if (pAverageDevelopmentTime > averageDevelopmentTime || worstProject == null) {
                            worstProject = project
                            updatesNumber = pUpdatesNumber
                            developmentDays = pDevelopmentDays
                            averageDevelopmentTime = pAverageDevelopmentTime
                        }
                    }
                }
            }
        }
        return worstProject
    }

}