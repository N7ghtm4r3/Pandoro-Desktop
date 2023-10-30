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
import helpers.InputStatus.*
import helpers.ScreenType.SignIn
import helpers.ScreenType.SignUp
import kotlinx.coroutines.CoroutineScope
import layouts.components.PandoroTextField
import navigator

/**
 * This is the layout for the connect screen
 *
 * @author Tecknobit - N7ghtm4r3
 * @see UIScreen
 */
class Connect : UIScreen() {

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
                                                            if (isSurnameValid(surname))
                                                                checkCredentials(email, password)
                                                            else
                                                                showAuthError("You must insert a correct surname")
                                                        } else
                                                            showAuthError("You must insert a correct name")
                                                    } else
                                                        showAuthError("You must insert a correct server address")
                                                }

                                                SignIn -> {
                                                    if (isServerAddressValid(serverAddress))
                                                        checkCredentials(email, password)
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
     * @param email: email to check
     * @param password: password to check
     * @return whether the credentials are valid as [Boolean]
     */
    private fun checkCredentials(email: String, password: String) {
        when (areCredentialsValid(email, password)) {
            OK -> {
                // TODO: MAKE REQUEST THEN
                navigator.navigate(home.name)
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

}