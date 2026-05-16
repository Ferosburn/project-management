package com.ferosburn.project_management.utils

import io.ktor.client.request.*
import kotlinx.serialization.json.*

inline fun <reified T> HttpRequestBuilder.setQueryParams(params: T) {
    Json.encodeToJsonElement(params).jsonObject.forEach { (key, value) ->
        if (value !is JsonNull) {
            parameter(key, value.jsonPrimitive.content)
        }
    }
}