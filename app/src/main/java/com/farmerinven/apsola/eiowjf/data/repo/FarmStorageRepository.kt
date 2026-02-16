package com.farmerinven.apsola.eiowjf.data.repo

import android.util.Log
import com.farmerinven.apsola.eiowjf.domain.model.FarmStorageEntity
import com.farmerinven.apsola.eiowjf.domain.model.FarmStorageParam
import com.farmerinven.apsola.eiowjf.presentation.app.FarmStorageApplication.Companion.FARM_STORAGE_MAIN_TAG
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer



private const val FARM_STORAGE_MAIN = "https://farmstorrage.com/config.php"

class FarmStorageRepository {


    private val farmStorageKtorClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
            requestTimeoutMillis = 30000
        }

    }

    suspend fun farmStorageGetClient(
        farmStorageParam: FarmStorageParam,
        farmStorageConversion: MutableMap<String, Any>?
    ): FarmStorageEntity? =
        withContext(Dispatchers.IO) {
            farmStorageKtorClient.plugin(HttpSend).intercept { request ->
                Log.d(FARM_STORAGE_MAIN_TAG, "Ktor: Intercept body ${request.body}")
                execute(request)
            }
            val farmStorageJson = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
            Log.d(
                FARM_STORAGE_MAIN_TAG,
                "Ktor: conversation json: ${farmStorageConversion.toString()}"
            )
            val farmStorageBody = farmStorageMergeToFlatJson(
                json = farmStorageJson,
                param = farmStorageParam,
                conversation = farmStorageConversion
            )
            Log.d(
                FARM_STORAGE_MAIN_TAG,
                "Ktor: request json: $farmStorageBody"
            )
            return@withContext try {
                val response = farmStorageKtorClient.post(FARM_STORAGE_MAIN) {
                    contentType(ContentType.Application.Json) // обязательно JSON
                    accept(ContentType.Application.Json)
                    setBody(farmStorageBody) // JsonObject
                }
                val code = response.status.value
                Log.d(FARM_STORAGE_MAIN_TAG, "Ktor: Request status code: $code")
                if (code == 200) {
                    val rawBody = response.bodyAsText() // читаем ответ как текст
                    val farmStorageEntity = Json { ignoreUnknownKeys = true }
                        .decodeFromString(FarmStorageEntity.serializer(), rawBody)
                    Log.d(FARM_STORAGE_MAIN_TAG, "Ktor: Get request success")
                    Log.d(FARM_STORAGE_MAIN_TAG, "Ktor: $farmStorageEntity")
                    farmStorageEntity
                } else {
                    Log.d(FARM_STORAGE_MAIN_TAG, "Ktor: Status code invalid, return null")
                    Log.d(FARM_STORAGE_MAIN_TAG, "Ktor: ${response.body<String>()}")
                    null
                }

            } catch (e: Exception) {
                Log.d(FARM_STORAGE_MAIN_TAG, "Ktor: Get request failed")
                Log.d(FARM_STORAGE_MAIN_TAG, "Ktor: ${e.message}")
                null
            }
        }

    private inline fun <reified T> Json.farmStorageEncodeToJsonObject(value: T): JsonObject =
        encodeToJsonElement(serializer(), value).jsonObject

    private inline fun <reified T> farmStorageMergeToFlatJson(
        json: Json,
        param: T,
        conversation: Map<String, Any>?
    ): JsonObject {

        val paramJson = json.farmStorageEncodeToJsonObject(param)

        return buildJsonObject {
            // поля из param
            paramJson.forEach { (key, value) ->
                put(key, value)
            }

            // динамические поля
            conversation?.forEach { (key, value) ->
                put(key, farmStorageAnyToJsonElement(value))
            }
        }
    }

    private fun farmStorageAnyToJsonElement(value: Any?): JsonElement {
        return when (value) {
            null -> JsonNull
            is String -> JsonPrimitive(value)
            is Number -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Map<*, *> -> buildJsonObject {
                value.forEach { (k, v) ->
                    if (k is String) {
                        put(k, farmStorageAnyToJsonElement(v))
                    }
                }
            }
            is List<*> -> buildJsonArray {
                value.forEach {
                    add(farmStorageAnyToJsonElement(it))
                }
            }
            else -> JsonPrimitive(value.toString())
        }
    }


}
