package layouts.ui.screens

import Routes.home
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
import helpers.*
import kotlinx.coroutines.CoroutineScope
import layouts.components.PandoroTextField
import layouts.components.popups.isEmailValid
import layouts.ui.screens.Connect.ScreenType.SignIn
import layouts.ui.screens.Connect.ScreenType.SignUp
import navigator
import org.apache.commons.validator.routines.UrlValidator
import toImportFromLibrary.User.*

/**
 * This is the layout for the connect screen
 *
 * @author Tecknobit - N7ghtm4r3
 * @see UIScreen
 */
class Connect : UIScreen() {

    companion object {

        /**
         * **urlValidator** -> the validator to check the validity of the URLS
         */
        val urlValidator: UrlValidator = UrlValidator.getInstance()

        /**
         * Function to check the validity of a password
         *
         * @param password: password to check
         * @return whether the password is valid as [Boolean]
         */
        // TODO: PACK IN LIBRARY
        fun isPasswordValid(password: String): Boolean {
            return password.length in PASSWORD_MIN_LENGTH..PASSWORD_MAX_LENGTH
        }

    }

    /**
     * **ScreenType** -> list of available types for the connect screen
     */
    // TODO: PACK IN THE LIBRARY
    private enum class ScreenType {

        /**
         * **SignUp** -> when the user need to sign up for the first time
         */
        SignUp,

        /**
         * **SignIn** -> when the user need to sign in its account
         */
        SignIn;

        /**
         * Function to create the title to show
         *
         * @param screenType: the type from create the title
         * @return title to show as [String]
         */
        fun createTitle(screenType: ScreenType): String {
            return when (screenType) {
                SignUp -> "Sign up"
                SignIn -> "Sign In"
            }
        }

        /**
         * Function to create the message to show
         *
         * @param screenType: the type from create the message
         * @return message to show as [String]
         */
        fun createMessage(screenType: ScreenType): String {
            return when (screenType) {
                SignIn -> "Are you new to Pandoro?"
                SignUp -> "Have an account?"
            }
        }

        /**
         * Function to create the title link to show
         *
         * @param screenType: the type from create the title link
         * @return title link to show as [String]
         */
        fun createTitleLink(screenType: ScreenType): String {
            return when (screenType) {
                SignUp -> "Sign In"
                SignIn -> "Sign up"
            }
        }

    }


    /**
     * **scaffoldState** -> the scaffold state for the scaffold of the page
     */
    private lateinit var scaffoldState: ScaffoldState

    /**
     * **coroutineScope** -> the coroutine scope to manage the coroutines of the [Scaffold]
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
            // TODO: CHECK IF SET A CUSTOM IMAGE
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
                                            onValueChange = { name = it },
                                            value = name
                                        )
                                        Spacer(Modifier.height(20.dp))
                                        PandoroTextField(
                                            modifier = Modifier.height(55.dp),
                                            label = "Surname",
                                            isError = !isSurnameValid(surname),
                                            onValueChange = { surname = it },
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
                                        onValueChange = { email = it },
                                        value = email
                                    )
                                    Spacer(Modifier.height(20.dp))
                                    PandoroTextField(
                                        modifier = Modifier.height(55.dp),
                                        visualTransformation = if (isVisible) None else PasswordVisualTransformation(),
                                        label = "Password",
                                        isError = !isPasswordValid(password),
                                        onValueChange = { password = it },
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
                                                                if (areCredentialsValid(email, password)) {
                                                                    // TODO: MAKE REQUEST THEN
                                                                    navigator.navigate(home.name)
                                                                }
                                                            } else
                                                                showAuthError("You must insert a correct surname")
                                                        } else
                                                            showAuthError("You must insert a correct name")
                                                    } else
                                                        showAuthError("You must insert a correct server address")
                                                }

                                                SignIn -> {
                                                    if (isServerAddressValid(serverAddress)) {
                                                        if (areCredentialsValid(email, password)) {
                                                            // TODO: MAKE REQUEST THEN
                                                            navigator.navigate(home.name)
                                                        }
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
     * Function to check the validity of a server address
     *
     * @param serverAddress: server address to check
     * @return whether the server address is valid as [Boolean]
     */
    // TODO: PACK IN LIBRARY
    private fun isServerAddressValid(serverAddress: String): Boolean {
        return urlValidator.isValid(serverAddress)
    }

    /**
     * Function to check the validity of a name
     *
     * @param name: name to check
     * @return whether the name is valid as [Boolean]
     */
    // TODO: PACK IN LIBRARY
    private fun isNameValid(name: String): Boolean {
        return name.length in 1..USER_NAME_MAX_LENGTH
    }

    /**
     * Function to check the validity of a surname
     *
     * @param surname: surname to check
     * @return whether the surname is valid as [Boolean]
     */
    // TODO: PACK IN LIBRARY
    private fun isSurnameValid(surname: String): Boolean {
        return surname.length in 1..USER_SURNAME_MAX_LENGTH
    }

    /**
     * Function to check the validity of the credentials
     *
     * @param email: email to check
     * @param password: password to check
     * @return whether the credentials are valid as [Boolean]
     */
    // TODO: PACK IN LIBRARY
    private fun areCredentialsValid(email: String, password: String): Boolean {
        if (isEmailValid(email)) {
            if (isPasswordValid(password))
                return true
            else
                showAuthError("You must insert a correct password")
        } else
            showAuthError("You must insert a correct email")
        return false
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

}