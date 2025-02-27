package hu.krisztian.offthebeatenpath.helpers

import android.content.Context
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject

class ApiResponseHandler(private val context: Context) {

    fun handleApiResponse(statusCode: Int, response: JSONObject) {
        val duration = Toast.LENGTH_LONG

        if (statusCode in 400..599) {
            val errorMessage = when (statusCode) {
                400 -> "Bad Request: ${getErrorMessage(response)}"
                401 -> "Unauthorized access."
                403 -> "Forbidden access."
                404 -> "Resource not found."
                409 -> "Conflict: ${getErrorMessage(response)}"
                500 -> "Internal Server Error."
                else -> "Unknown error occurred."
            }

            Toast.makeText(context, errorMessage, duration).show()
        }
    }

    private fun getErrorMessage(response: JSONObject): String {
        return try {
            if (response.has("message")) {
                response.getString("message")
            } else {
                "No error message available."
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            "JSON parsing error."
        }
    }
}
