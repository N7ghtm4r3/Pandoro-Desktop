package layouts.ui.screens

import Routes.home
import Routes.splashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
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
import com.tecknobit.equinox.environment.records.EquinoxUser.*
import com.tecknobit.equinox.inputs.InputValidator.*
import com.tecknobit.pandorocore.helpers.InputsValidator.Companion.isServerAddressValid
import com.tecknobit.pandorocore.helpers.InputsValidator.ScreenType
import com.tecknobit.pandorocore.helpers.InputsValidator.ScreenType.SignIn
import com.tecknobit.pandorocore.helpers.InputsValidator.ScreenType.SignUp
import com.tecknobit.pandorocore.helpers.PandoroRequester
import com.tecknobit.pandorocore.records.structures.PandoroItem.IDENTIFIER_KEY
import com.tecknobit.pandorocore.records.users.User
import com.tecknobit.pandorocore.records.users.User.LANGUAGE_KEY
import com.tecknobit.pandorocore.ui.LocalUser
import currentProfilePic
import helpers.BACKGROUND_COLOR
import helpers.PRIMARY_COLOR
import helpers.RED_COLOR
import helpers.openUrl
import kotlinx.coroutines.CoroutineScope
import layouts.components.PandoroTextField
import layouts.ui.screens.SplashScreen.Companion.user
import navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.json.JSONObject
import pandoro.composeapp.generated.resources.*
import pandoro.composeapp.generated.resources.Res.string
import viewmodels.ConnectViewModel
import viewmodels.PandoroViewModel.Companion.requester
import java.util.prefs.Preferences

/**
 * This is the layout for the connect screen
 *
 * @author Tecknobit - N7ghtm4r3
 * @see UIScreen
 */
class Connect : UIScreen() {

    /**
     * **sectionCoroutineScope** -> the coroutine scope to manage the coroutines of the [Scaffold]
     */
    private lateinit var coroutineScope: CoroutineScope

    /**
     * **screenType** -> the current [ScreenType]
     */
    private var screenType: MutableState<ScreenType> = mutableStateOf(SignIn)

    /**
     * **snackbarHostState** the host to launch the snackbars
     */
    private val snackbarHostState by lazy {
        SnackbarHostState()
    }

    /**
     * **projectsScreen** -> the screen to show the projects
     */
    private val viewModel = ConnectViewModel(
        snackbarHostState = snackbarHostState
    )

    /**
     * Function to show the content of the [Connect]
     *
     * No-any params required
     */
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun showScreen() {
        coroutineScope = rememberCoroutineScope()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .align(
                        alignment = Alignment.Center
                    )
                    .width(1000.dp)
                    .height(700.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Row {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(PRIMARY_COLOR)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxSize()
                        ) {
                            Text(
                                text = stringResource(string.app_name),
                                fontSize = 40.sp,
                                color = Color.White
                            )
                            Text(
                                modifier = Modifier
                                    .padding(
                                        top = 10.dp
                                    ),
                                text = stringResource(Res.string.description_subtext),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Justify,
                                color = Color.White
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .padding(
                                            bottom = 10.dp
                                        ),
                                    onClick = { openUrl("https://github.com/N7ghtm4r3/Pandoro-Desktop") }
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .size(35.dp),
                                        painter = painterResource("github.svg"),
                                        tint = Color.White,
                                        contentDescription = null
                                    )
                                }
                                Text(
                                    text = "v. ${stringResource(string.app_version)}",
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(BACKGROUND_COLOR)
                    ) {
                        Scaffold(
                            snackbarHost = {
                                SnackbarHost(hostState = snackbarHostState) {
                                    Snackbar(
                                        containerColor = PRIMARY_COLOR,
                                        contentColor = BACKGROUND_COLOR,
                                        snackbarData = it
                                    )
                                }
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp)
                            ) {
                                viewModel.serverAddress = remember { mutableStateOf("") }
                                viewModel.serverSecret = remember { mutableStateOf("") }
                                viewModel.name = remember { mutableStateOf("") }
                                viewModel.surname = remember { mutableStateOf("") }
                                viewModel.email = remember { mutableStateOf("") }
                                viewModel.password = remember { mutableStateOf("") }
                                Column {
                                    Text(
                                        text = stringResource(
                                            if (screenType.value == SignIn)
                                                string.welcome_back
                                            else
                                                string.hello,
                                        ),
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${screenType.value.createTitle(screenType.value)}!",
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    PandoroTextField(
                                        modifier = Modifier
                                            .height(55.dp),
                                        label = string.server_address,
                                        value = viewModel.serverAddress,
                                        isError = !isServerAddressValid(viewModel.serverAddress.value)
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .height(10.dp)
                                    )
                                    if (screenType.value == SignUp) {
                                        PandoroTextField(
                                            modifier = Modifier
                                                .height(55.dp),
                                            label = string.server_secret,
                                            value = viewModel.serverSecret,
                                            isError = !isServerSecretValid(viewModel.serverSecret.value),
                                        )
                                        Spacer(
                                            modifier = Modifier
                                                .height(10.dp)
                                        )
                                        PandoroTextField(
                                            modifier = Modifier
                                                .height(55.dp),
                                            label = string.name,
                                            value = viewModel.name,
                                            isError = !isNameValid(viewModel.name.value),
                                        )
                                        Spacer(
                                            modifier = Modifier
                                                .height(10.dp)
                                        )
                                        PandoroTextField(
                                            modifier = Modifier
                                                .height(55.dp),
                                            label = string.surname,
                                            value = viewModel.surname,
                                            isError = !isSurnameValid(viewModel.surname.value)
                                        )
                                        Spacer(
                                            modifier = Modifier
                                                .height(10.dp)
                                        )
                                    }
                                    PandoroTextField(
                                        modifier = Modifier
                                            .height(55.dp),
                                        label = string.email,
                                        value = viewModel.email,
                                        isError = !isEmailValid(viewModel.email.value),
                                        onValueChange = {
                                            viewModel.email.value = it.replace(" ", "")
                                        }
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .height(10.dp)
                                    )
                                    var isVisible by remember { mutableStateOf(false) }
                                    PandoroTextField(
                                        modifier = Modifier
                                            .height(55.dp),
                                        visualTransformation = if (isVisible) None
                                        else
                                            PasswordVisualTransformation(),
                                        label = string.password,
                                        value = viewModel.password,
                                        isError = !isPasswordValid(viewModel.password.value),
                                        onValueChange = {
                                            viewModel.password.value = it.replace(" ", "")
                                        },
                                        trailingIcon = {
                                            IconButton(
                                                onClick = { isVisible = !isVisible }
                                            ) {
                                                Icon(
                                                    imageVector = if (isVisible)
                                                        Default.VisibilityOff
                                                    else
                                                        Default.Visibility,
                                                    contentDescription = null,
                                                )
                                            }
                                        }
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .height(25.dp)
                                    )
                                    Button(
                                        modifier = Modifier
                                            .width(250.dp)
                                            .height(50.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        onClick = {
                                            when (screenType.value) {
                                                SignUp -> viewModel.signUp()
                                                SignIn -> viewModel.signIn()
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = stringResource(
                                                if(screenType.value == SignIn)
                                                    string.sign_in
                                                else
                                                    string.sign_up
                                            )
                                        )
                                    }
                                    Text(
                                        modifier = Modifier
                                            .padding(
                                                top = 25.dp
                                            )
                                            .clickable {
                                                viewModel.name.value = ""
                                                viewModel.surname.value = ""
                                                if (screenType.value == SignUp)
                                                    screenType.value = SignIn
                                                else
                                                    screenType.value = SignUp
                                            },
                                        text = buildAnnotatedString {
                                            append(
                                                stringResource(
                                                    if(screenType.value == SignIn)
                                                        string.are_you_new_to_pandoro
                                                    else
                                                        string.have_an_account
                                                )
                                            )
                                            withStyle(style = SpanStyle(color = RED_COLOR)) {
                                                append(" " +
                                                    stringResource(
                                                        if(screenType.value == SignIn)
                                                            string.sign_up
                                                        else
                                                            string.sign_in
                                                    )
                                                )
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
                        .put(LANGUAGE_KEY, preferences.get(LANGUAGE_KEY, DEFAULT_LANGUAGE))
                )
                requester = PandoroRequester(
                    host = host!!,
                    userId = userId,
                    userToken = userToken
                )
            } else
                user = User()
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
         * @param language: the language of the user
         *
         */
        override fun initUserSession(
            response: JsonHelper,
            host: String?,
            name: String,
            surname: String,
            email: String?,
            password: String?,
            language: String?
        ) {
            super.initUserSession(response, host, name, surname, email, password, language)
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
            currentProfilePic.value = super.storeProfilePic(profilePic, refreshUser)
            return currentProfilePic.value
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
            navigator.navigate(splashScreen.name)
        }

    }

}