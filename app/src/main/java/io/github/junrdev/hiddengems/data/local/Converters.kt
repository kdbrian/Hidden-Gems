package io.github.junrdev.hiddengems.data.local

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.junrdev.hiddengems.data.model.Review
import org.json.JSONArray
import org.json.JSONObject

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromLatLng(latlng: LatLng): String {
        val array = JSONArray()
        array.put(latlng.latitude)
        array.put(latlng.longitude)
        return JSONObject().put("latlng", array).toString()
    }

    @TypeConverter
    fun toLatLng(x: String): LatLng {
        val string = JSONObject(x)
        val array = string.getJSONArray("latlng")
        return LatLng(array.optDouble(0, 0.0), array.optDouble(1, 0.0))
    }


    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        return if (value == null) emptyList() else gson.fromJson(value, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun stringListToString(list: List<String>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromReviewList(value: String?): List<Review> {
        return if (value == null) emptyList() else gson.fromJson(value, object : TypeToken<List<Review>>() {}.type)
    }

    @TypeConverter
    fun reviewListToString(list: List<Review>?): String {
        return gson.toJson(list)
    }


}