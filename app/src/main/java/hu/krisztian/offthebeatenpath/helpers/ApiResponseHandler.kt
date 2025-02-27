package hu.krisztian.offthebeatenpath.helpers

import android.content.Context
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject

class ApiResponseHandler(private val context: Context) {

    fun handleApiResponse(statusCode: Int, response: JSONObject) {
        val duration = Toast.LENGTH_LONG

        when (statusCode) {
            200, 201 -> {
                Toast.makeText(context, "Registration Successful!", duration).show()
            }
            400 -> {
                Toast.makeText(context, "Bad Request: ${getErrorMessage(response)}", duration).show()
            }
            401 -> {
                Toast.makeText(context, "Unauthorized access.", duration).show()
            }
            403 -> {
                Toast.makeText(context, "Forbidden access.", duration).show()
            }
            404 -> {
                Toast.makeText(context, "Resource not found.", duration).show()
            }
            409 -> {
                Toast.makeText(context, "Conflict: ${getErrorMessage(response)}", duration).show()
            }
            500 -> {
                Toast.makeText(context, "Internal Server Error.", duration).show()
            }
            else -> {
                Toast.makeText(context, "Unknown error occurred.", duration).show()
            }
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
