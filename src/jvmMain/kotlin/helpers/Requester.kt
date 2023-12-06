package helpers

import com.tecknobit.apimanager.apis.APIRequest
import com.tecknobit.apimanager.apis.APIRequest.RequestMethod
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.pandoro.controllers.PandoroController
import com.tecknobit.pandoro.controllers.PandoroController.BASE_ENDPOINT
import com.tecknobit.pandoro.helpers.BaseRequester
import com.tecknobit.pandoro.services.UsersHelper

/**
 * The **Requester** class is useful to communicate with the Pandoro's backend
 *
 * @param host: the host where is running the Pandoro's backend
 * @param userId: the user identifier
 * @param userToken: the user token
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class Requester(
    private val host: String,
    override var userId: String?,
    override var userToken: String?
) : BaseRequester(host, userId, userToken) {

    /**
     * **apiRequest** -> the instance to communicate and make the requests to the backend
     */
    private val apiRequest: APIRequest = APIRequest()

    /**
     * **headers** -> the headers of the requests
     */
    private val headers: APIRequest.Headers = APIRequest.Headers()

    init {
        setAuthHeaders()
    }

    /**
     * Function to set the headers for the authentication of the user
     *
     * No-any params required
     */
    override fun setAuthHeaders() {
        if (userId != null && userToken != null) {
            headers.addHeader(PandoroController.IDENTIFIER_KEY, userId)
            headers.addHeader(UsersHelper.TOKEN_KEY, userToken)
        }
    }

    /**
     * Function to execute a request to the backend
     *
     * @param contentType: the content type of the request
     * @param endpoint: the endpoint which make the request
     * @param requestMethod: the method of the request to execute
     * @param payload: the payload of the request, default null
     * @param jsonPayload: whether the payload must be formatted as JSON, default true
     */
    override fun execRequest(
        contentType: String,
        endpoint: String,
        requestMethod: RequestMethod,
        payload: PandoroPayload?,
        jsonPayload: Boolean
    ): String {
        headers.addHeader("Content-Type", contentType)
        if (host.startsWith("https"))
            apiRequest.validateSelfSignedCertificate()
        return try {
            val requestUrl = host + BASE_ENDPOINT + endpoint
            if (payload != null) {
                if (jsonPayload)
                    apiRequest.sendJSONPayloadedAPIRequest(requestUrl, requestMethod, headers, payload)
                else
                    apiRequest.sendPayloadedAPIRequest(requestUrl, requestMethod, headers, payload)
            } else
                apiRequest.sendAPIRequest(requestUrl, requestMethod, headers)
            val response = apiRequest.response
            lastResponse = JsonHelper(response)
            response
        } catch (e: Exception) {
            lastResponse = JsonHelper(errorResponse)
            errorResponse
        }
    }

}