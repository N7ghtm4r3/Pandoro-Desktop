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
import com.tecknobit.pandoro.records.users.User
import com.tecknobit.pandoro.services.UsersHelper.*
import helpers.*
import kotlinx.coroutines.CoroutineScope
import layouts.components.PandoroTextField
import layouts.components.Sidebar.Companion.activeScreen
import layouts.ui.screens.SplashScreen.Companion.localAuthHelper
import layouts.ui.screens.SplashScreen.Companion.requester
import layouts.ui.screens.SplashScreen.Companion.user
import layouts.ui.screens.SplashScreen.Companion.userProfilePic
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
                                    text = "v. 1.0.0",
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
                                    Spacer(Modifier.height(20.dp))
                                    var name by remember { mutableStateOf("") }
                                    var surname by remember { mutableStateOf("") }
                                    if (screenType.value == SignUp) {
                                        PandoroTextField(
                                            modifier = Modifier.height(55.dp),
                                            label = "Name",
                                            isError = !isNameValid(name),
                                            onValueChange = { name = it.replace(" ", "") },
                                            value = name
                                        )
                                        Spacer(Modifier.height(20.dp))
                                        PandoroTextField(
                                            modifier = Modifier.height(55.dp),
                                            label = "Surname",
                                            isError = !isSurnameValid(surname),
                                            onValueChange = { surname = it.replace(" ", "") },
                                            value = surname
                                        )
                                        Spacer(Modifier.height(20.dp))
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
                                    Spacer(Modifier.height(20.dp))
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
                                                        if (isNameValid(name)) {
                                                            if (isSurnameValid(surname)) {
                                                                checkCredentials(
                                                                    serverAddress, email, password, name,
                                                                    surname
                                                                )
                                                            } else
                                                                showAuthError("You must insert a correct surname")
                                                        } else
                                                            showAuthError("You must insert a correct name")
                                                    } else
                                                        showAuthError("You must insert a correct server address")
                                                }

                                                SignIn -> {
                                                    if (isServerAddressValid(serverAddress))
                                                        checkCredentials(serverAddress, email, password, name, surname)
                                                    else
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
     * @param email: email to check
     * @param password: password to check
     * @return whether the credentials are valid as [Boolean]
     */
    private fun checkCredentials(
        serverAddress: String,
        email: String,
        password: String,
        name: String,
        surname: String,
    ) {
        when (areCredentialsValid(email, password)) {
            OK -> {
                if (requester == null)
                    requester = Requester(serverAddress)
                val response = if (name.isEmpty())
                    JsonHelper(requester!!.execSignIn(email, password))
                else
                    JsonHelper(requester!!.execSignUp(name, surname, email, password))
                if (requester!!.successResponse()) {
                    localAuthHelper.initUserSession(
                        response,
                        serverAddress,
                        name.ifEmpty { response.getString(NAME_KEY) },
                        surname.ifEmpty { response.getString(SURNAME_KEY) },
                        email,
                        password
                    )
                    navigator.navigate(home.name)
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
     */
    open inner class LocalAuthHelper {

        /**
         * **SERVER_ADDRESS_KEY** -> server address key
         */
        private val SERVER_ADDRESS_KEY = "server_address"

        /**
         * **preferences** -> the instance to manage the user preferences
         */
        private val preferences = Preferences.userRoot().node("/user/tecknobit/pandoro")

        /**
         * **host** -> the host to used in the requests
         */
        var host: String? = null

        /**
         * Function to init the user credentials
         *
         * No-any params required
         */
        fun initUserCredentials() {
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
        fun initUserSession(
            response: JsonHelper,
            host: String?,
            name: String,
            surname: String,
            email: String?,
            password: String?
        ) {
            storeUserValue(IDENTIFIER_KEY, response.getString(IDENTIFIER_KEY))
            storeUserValue(TOKEN_KEY, response.getString(TOKEN_KEY))
            storeHost(host)
            storeProfilePic(response.getString(PROFILE_PIC_KEY))
            storeName(name)
            storeSurname(surname)
            storeEmail(email)
            storePassword(password)
            initUserCredentials()
        }

        /**
         * Function to store the host value
         *
         * @param host: the host to used in the requests
         */
        @Wrapper
        fun storeHost(host: String?) {
            storeUserValue(SERVER_ADDRESS_KEY, host, false)
            this.host = host
        }

        /**
         * Function to store the profile pic value
         *
         * @param profilePic: the profile pic of the user
         * @param refreshUser: whether refresh the user
         */
        @Wrapper
        fun storeProfilePic(
            profilePic: String?,
            refreshUser: Boolean = false
        ) {
            val profilePicValue = "$host/$profilePic"
            userProfilePic.value = loadImageBitmap(profilePicValue)
            storeUserValue(PROFILE_PIC_KEY, profilePicValue, refreshUser)
        }

        /**
         * Function to store the name value
         *
         * @param name: the name of the user
         */
        @Wrapper
        private fun storeName(name: String?) {
            storeUserValue(NAME_KEY, name, false)
        }

        /**
         * Function to store the surname value
         *
         * @param surname: the surname of the user
         */
        @Wrapper
        private fun storeSurname(surname: String?) {
            storeUserValue(SURNAME_KEY, surname, false)
        }

        /**
         * Function to store the email value
         *
         * @param email: the email of the user
         * @param refreshUser: whether refresh the user
         */
        @Wrapper
        fun storeEmail(
            email: String?,
            refreshUser: Boolean = false
        ) {
            storeUserValue(EMAIL_KEY, email, refreshUser)
        }

        /**
         * Function to store the password value
         *
         * @param password: the password of the user
         * @param refreshUser: whether refresh the user
         */
        @Wrapper
        fun storePassword(
            password: String?,
            refreshUser: Boolean = false
        ) {
            storeUserValue(PASSWORD_KEY, password, refreshUser)
        }

        /**
         * Function to store a user value
         *
         * @param key: the key of the value to store
         * @param value: the value to store
         * @param refreshUser: whether refresh the user
         */
        private fun storeUserValue(
            key: String,
            value: String?,
            refreshUser: Boolean = false
        ) {
            preferences.put(key, value)
            if (refreshUser)
                initUserCredentials()
        }

        /**
         * Function to disconnect the user and clear the [preferences]
         *
         * No-any params required
         */
        fun logout() {
            preferences.clear()
            initUserCredentials()
            activeScreen.value = Sections.Projects
            navigator.navigate(splashScreen.name)
        }

    }

}