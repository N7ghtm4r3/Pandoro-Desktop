package layouts.ui.screens

import Routes.home
import Routes.splashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation.Companion.None
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.pandoro.controllers.PandoroController.IDENTIFIER_KEY
import com.tecknobit.pandoro.helpers.*
import com.tecknobit.pandoro.helpers.InputStatus.*
import com.tecknobit.pandoro.helpers.ScreenType.SignIn
import com.tecknobit.pandoro.helpers.ScreenType.SignUp
import com.tecknobit.pandoro.helpers.ui.LocalUser
import com.tecknobit.pandoro.records.users.User
import com.tecknobit.pandoro.services.UsersHelper.*
import helpers.*
import kotlinx.coroutines.CoroutineScope
import layouts.components.PandoroTextField
import layouts.ui.screens.Home.Companion.activeScreen
import layouts.ui.screens.Home.Companion.changelogs
import layouts.ui.screens.SplashScreen.Companion.isRefreshing
import layouts.ui.screens.SplashScreen.Companion.localAuthHelper
import layouts.ui.screens.SplashScreen.Companion.requester
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.screens.SplashScreen.Companion.userProfilePic
import layouts.ui.sections.NotesSection.Companion.notes
import layouts.ui.sections.ProfileSection.Companion.groups
import layouts.ui.sections.ProjectsSection.Companion.projectsList
import layouts.ui.sections.Section.Sections
import navigator
import org.json.JSONObject
import java.util.prefs.Preferences

/**
 * This is the layout for the connect screen
 *
 * @author Tecknobit - N7ghtm4r3
 * @see UIScreen
 */
class Connect : UIScreen() {

    /**
     * **sectionScaffoldState** -> the scaffold state for the scaffold of the page
     */
    private lateinit var scaffoldState: ScaffoldState

    /**
     * **sectionCoroutineScope** -> the coroutine scope to manage the coroutines of the [Scaffold]
     */
    private lateinit var coroutineScope: CoroutineScope

    /**
     * **screenType** -> the current [ScreenType]
     */
    private var screenType: MutableState<ScreenType> = mutableStateOf(SignIn)

    /**
     * Function to show the content of the [Connect]
     *
     * No-any params required
     */
    @Composable
    override fun showScreen() {
        scaffoldState = rememberScaffoldState()
        coroutineScope = rememberCoroutineScope()
        Box(
            modifier = Modifier.fillMaxSize().background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.align(alignment = Alignment.Center).width(1000.dp).height(700.dp),
                elevation = 10.dp,
                backgroundColor = Color.White,
                shape = RoundedCornerShape(15.dp)
            ) {
                Row {
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight().background(PRIMARY_COLOR)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp).fillMaxSize()
                        ) {
                            Text(
                                text = appName,
                                fontSize = 40.sp,
                                color = Color.White
                            )
                            Text(
                                modifier = Modifier.padding(top = 10.dp),
                                text = "Pandoro is an open source management software useful for easily managing your " +
                                        "projects and their updates",
                                fontSize = 14.sp,
                                textAlign = TextAlign.Justify,
                                color = Color.White
                            )
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    onClick = { openUrl("https://github.com/N7ghtm4r3/Pandoro-Desktop") }
                                ) {
                                    Icon(
                                        modifier = Modifier.size(35.dp),
                                        painter = painterResource("github-mark.svg"),
                                        tint = Color.White,
                                        contentDescription = null
                                    )
                                }
                                Text(
                                    text = "v. 1.0.1",
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight().background(BACKGROUND_COLOR)
                    ) {
                        Scaffold(
                            scaffoldState = scaffoldState,
                            snackbarHost = {
                                SnackbarHost(it) { data ->
                                    Snackbar(
                                        backgroundColor = BACKGROUND_COLOR,
                                        snackbarData = data
                                    )
                                }
                            }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(20.dp)
                            ) {
                                Column {
                                    Text(
                                        text =
                                        if (screenType.value == SignIn)
                                            "Welcome back,"
                                        else
                                            "Hello,",
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${screenType.value.createTitle(screenType.value)}!",
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    var serverAddress by remember { mutableStateOf("") }
                                    PandoroTextField(
                                        modifier = Modifier.height(55.dp),
                                        label = "Server address",
                                        isError = !isServerAddressValid(serverAddress),
                                        onValueChange = { serverAddress = it },
                                        value = serverAddress
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    var serverSecret by remember { mutableStateOf("") }
                                    var name by remember { mutableStateOf("") }
                                    var surname by remember { mutableStateOf("") }
                                    if (screenType.value == SignUp) {
                                        PandoroTextField(
                                            modifier = Modifier.height(55.dp),
                                            label = "Server secret",
                                            isError = !isServerSecretValid(serverSecret),
                                            onValueChange = { serverSecret = it },
                                            value = serverSecret
                                        )
                                        Spacer(Modifier.height(10.dp))
                                        PandoroTextField(
                                            modifier = Modifier.height(55.dp),
                                            label = "Name",
                                            isError = !isNameValid(name),
                                            onValueChange = { name = it },
                                            value = name
                                        )
                                        Spacer(Modifier.height(10.dp))
                                        PandoroTextField(
                                            modifier = Modifier.height(55.dp),
                                            label = "Surname",
                                            isError = !isSurnameValid(surname),
                                            onValueChange = { surname = it },
                                            value = surname
                                        )
                                        Spacer(Modifier.height(10.dp))
                                    }
                                    var email by remember { mutableStateOf("") }
                                    var password by remember { mutableStateOf("") }
                                    var isVisible by remember { mutableStateOf(false) }
                                    PandoroTextField(
                                        modifier = Modifier.height(55.dp),
                                        label = "Email",
                                        isError = !isEmailValid(email),
                                        onValueChange = { email = it.replace(" ", "") },
                                        value = email
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    PandoroTextField(
                                        modifier = Modifier.height(55.dp),
                                        visualTransformation = if (isVisible) None else PasswordVisualTransformation(),
                                        label = "Password",
                                        isError = !isPasswordValid(password),
                                        onValueChange = { password = it.replace(" ", "") },
                                        value = password,
                                        trailingIcon = {
                                            IconButton(
                                                onClick = { isVisible = !isVisible }
                                            ) {
                                                Icon(
                                                    imageVector = if (isVisible) Default.VisibilityOff else Default.Visibility,
                                                    contentDescription = null,
                                                )
                                            }
                                        }
                                    )
                                    Spacer(Modifier.height(25.dp))
                                    Button(
                                        modifier = Modifier.width(250.dp).height(50.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        onClick = {
                                            when (screenType.value) {
                                                SignUp -> {
                                                    if (isServerAddressValid(serverAddress)) {
                                                        if (isServerSecretValid(serverSecret)) {
                                                            if (isNameValid(name)) {
                                                                if (isSurnameValid(surname)) {
                                                                    checkCredentials(
                                                                        serverAddress = serverAddress,
                                                                        serverSecret = serverSecret,
                                                                        email = email,
                                                                        password = password,
                                                                        name = name,
                                                                        surname = surname
                                                                    )
                                                                } else
                                                                    showAuthError("You must insert a correct surname")
                                                            } else
                                                                showAuthError("You must insert a correct name")
                                                        } else
                                                            showAuthError("You must insert a correct server secret")
                                                    } else
                                                        showAuthError("You must insert a correct server address")
                                                }

                                                SignIn -> {
                                                    if (isServerAddressValid(serverAddress)) {
                                                        checkCredentials(
                                                            serverAddress = serverAddress,
                                                            email = email,
                                                            password = password,
                                                            name = name,
                                                            surname = surname
                                                        )
                                                    } else
                                                        showAuthError("You must insert a correct server address")
                                                }
                                            }
                                        }
                                    ) {
                                        Text(text = screenType.value.createTitle(screenType.value))
                                    }
                                    Text(
                                        modifier = Modifier.padding(top = 25.dp)
                                            .clickable(true, onClick = {
                                                name = ""
                                                surname = ""
                                                if (screenType.value == SignUp)
                                                    screenType.value = SignIn
                                                else
                                                    screenType.value = SignUp
                                            }),
                                        text = buildAnnotatedString {
                                            append(screenType.value.createMessage(screenType.value))
                                            withStyle(style = SpanStyle(color = RED_COLOR)) {
                                                append(" " + screenType.value.createTitleLink(screenType.value))
                                            }
                                        },
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Function to check the validity of the credentials
     *
     * @param serverAddress: the address of the Pandoro's backend
     * @param serverSecret: the secret of the Pandoro's backend
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param email: email to check
     * @param password: password to check
     */
    private fun checkCredentials(
        serverAddress: String,
        serverSecret: String? = null,
        email: String,
        password: String,
        name: String,
        surname: String,
    ) {
        when (areCredentialsValid(email, password)) {
            OK -> {
                requester = Requester(serverAddress, null, null)
                val response = if (serverSecret.isNullOrBlank())
                    JsonHelper(requester!!.execSignIn(email, password))
                else
                    JsonHelper(requester!!.execSignUp(serverSecret, name, surname, email, password))
                if (requester!!.successResponse()) {
                    localAuthHelper.initUserSession(
                        response,
                        serverAddress,
                        name.ifEmpty { response.getString(NAME_KEY) },
                        surname.ifEmpty { response.getString(SURNAME_KEY) },
                        email,
                        password
                    )
                } else
                    showSnack(coroutineScope, scaffoldState, requester!!.errorMessage())
            }
            WRONG_PASSWORD -> {
                showAuthError("You must insert a correct password")
            }
            WRONG_EMAIL -> {
                showAuthError("You must insert a correct email")
            }
        }
    }

    /**
     * Function to show an auth error
     *
     * @param errorMessage: error message to show
     */
    private fun showAuthError(errorMessage: String) {
        showSnack(
            scope = coroutineScope,
            scaffoldState = scaffoldState,
            message = errorMessage
        )
    }

    /**
     * This **LocalAuthHelper** class is useful to manage the auth credentials in local
     *
     * @author Tecknobit - N7ghtm4r3
     * @see LocalUser
     */
    open inner class LocalAuthHelper : LocalUser() {

        /**
         * **preferences** -> the instance to manage the user preferences
         */
        private val preferences = Preferences.userRoot().node("/user/tecknobit/pandoro")

        /**
         * Function to init the user credentials
         *
         * No-any params required
         */
        override fun initUserCredentials() {
            host = preferences.get(SERVER_ADDRESS_KEY, null)
            val userId = preferences.get(IDENTIFIER_KEY, null)
            val userToken = preferences.get(TOKEN_KEY, null)
            if (userId != null) {
                user = User(
                    JSONObject()
                        .put(IDENTIFIER_KEY, userId)
                        .put(TOKEN_KEY, userToken)
                        .put(PROFILE_PIC_KEY, preferences.get(PROFILE_PIC_KEY, null))
                        .put(NAME_KEY, preferences.get(NAME_KEY, null))
                        .put(SURNAME_KEY, preferences.get(SURNAME_KEY, null))
                        .put(EMAIL_KEY, preferences.get(EMAIL_KEY, null))
                        .put(PASSWORD_KEY, preferences.get(PASSWORD_KEY, null))
                )
                userProfilePic.value = loadImageBitmap(user.profilePic)
                requester = Requester(host!!, userId, userToken)
                isRefreshing.value = false
            } else {
                requester = null
                user = User()
            }
        }

        /**
         * Function to init the user credentials
         *
         * @param response: the response of the auth request
         * @param host: the host to used in the requests
         * @param name: the name of the user
         * @param surname: the surname of the user
         * @param email: the email of the user
         * @param password: the password of the user
         */
        override fun initUserSession(
            response: JsonHelper,
            host: String?,
            name: String,
            surname: String,
            email: String?,
            password: String?
        ) {
            super.initUserSession(response, host, name, surname, email, password)
            navigator.navigate(home.name)
        }

        /**
         * Function to store the profile pic value
         *
         * @param profilePic: the profile pic of the user
         * @param refreshUser: whether refresh the user
         */
        @Wrapper
        override fun storeProfilePic(
            profilePic: String?,
            refreshUser: Boolean
        ): String {
            userProfilePic.value = loadImageBitmap(super.storeProfilePic(profilePic, refreshUser))
            return ""
        }

        /**
         * Function to store a user value
         *
         * @param key: the key of the value to store
         * @param value: the value to store
         * @param refreshUser: whether refresh the user
         */
        override fun storeUserValue(
            key: String,
            value: String?,
            refreshUser: Boolean
        ) {
            preferences.put(key, value)
            super.storeUserValue(key, value, refreshUser)
        }

        /**
         * Function to disconnect the user and clear the [preferences]
         *
         * No-any params required
         */
        override fun logout() {
            preferences.clear()
            initUserCredentials()
            projectsList.clear()
            groups.clear()
            changelogs.clear()
            notes.clear()
            navigator.navigate(splashScreen.name)
            activeScreen.value = Sections.Projects
        }

    }

}