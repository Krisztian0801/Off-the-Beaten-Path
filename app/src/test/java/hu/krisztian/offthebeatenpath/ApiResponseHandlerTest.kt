import android.content.Context
import android.widget.Toast
import hu.krisztian.offthebeatenpath.helpers.ApiResponseHandler
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ApiResponseHandlerTest {

    @Mock
    private lateinit var mockContext: Context

    private lateinit var apiResponseHandler: ApiResponseHandler

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        apiResponseHandler = ApiResponseHandler(mockContext)
    }

    @Test
    fun testHandleApiResponse_BadRequest() {
        val jsonResponse = JSONObject().put("message", "Invalid input data")
        apiResponseHandler.handleApiResponse(400, jsonResponse)

        verifyToast("Bad Request: Invalid input data")
    }

    @Test
    fun testHandleApiResponse_Unauthorized() {
        val jsonResponse = JSONObject()
        apiResponseHandler.handleApiResponse(401, jsonResponse)

        verifyToast("Unauthorized access.")
    }

    @Test
    fun testHandleApiResponse_Forbidden() {
        val jsonResponse = JSONObject()
        apiResponseHandler.handleApiResponse(403, jsonResponse)

        verifyToast("Forbidden access.")
    }

    @Test
    fun testHandleApiResponse_ResourceNotFound() {
        val jsonResponse = JSONObject()
        apiResponseHandler.handleApiResponse(404, jsonResponse)

        verifyToast("Resource not found.")
    }

    @Test
    fun testHandleApiResponse_Conflict() {
        val jsonResponse = JSONObject().put("message", "Email already exists")
        apiResponseHandler.handleApiResponse(409, jsonResponse)

        verifyToast("Conflict: Email already exists")
    }

    @Test
    fun testHandleApiResponse_InternalServerError() {
        val jsonResponse = JSONObject()
        apiResponseHandler.handleApiResponse(500, jsonResponse)

        verifyToast("Internal Server Error.")
    }

    @Test
    fun testHandleApiResponse_UnknownError() {
        val jsonResponse = JSONObject()
        apiResponseHandler.handleApiResponse(418, jsonResponse) // Unknown status code

        verifyToast("Unknown error occurred.")
    }

    @Test
    fun testHandleApiResponse_MissingMessageField() {
        val jsonResponse = JSONObject() // No "message" field
        apiResponseHandler.handleApiResponse(400, jsonResponse)

        verifyToast("Bad Request: No error message available.")
    }

    @Test
    fun testHandleApiResponse_InvalidJson() {
        val invalidJson = JSONObject("{}") // Simulating bad JSON
        apiResponseHandler.handleApiResponse(400, invalidJson)

        verifyToast("Bad Request: JSON parsing error.")
    }

    // Helper function to verify Toast messages
    private fun verifyToast(expectedMessage: String) {
        verify(mockContext, times(1)).let {
            Toast.makeText(it, expectedMessage, Toast.LENGTH_LONG)
        }
    }
}
