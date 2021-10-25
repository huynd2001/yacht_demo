package com.csds393.yacht

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Used as base for testing JSON serialization.
 * To use, extend this class and inject the test cases and serializer in the constructor
 * [T] is the type of the object being serialized
 * @param testCasesWithPlainStrings  Map of String to Object. !String values must not include quotation marks internally
 * @property serializer  the serializer to be tested
 */
abstract class AbstractSerializationTest<T>(
    testCasesWithPlainStrings: Map<String, T>,
    private val serializer: KSerializer<T>
) {
    /** Map of JSON strings to corresponding [T] */
    private val testCases = testCasesWithPlainStrings.mapKeys { "\"${it.key}\"" } // wrap in quotation marks

    /** Scenario of decoding externally received JSON */
    @Test
    fun fromJsonStringToObject() {
        for (case in testCases.entries) assertEquals("$case", case.value, Json.decodeFromString(serializer, case.key))
    }

    /** Scenario of serializing data in memory to disk, then reading back later  */
    @Test
    fun fromObjectToJSONStringBackToObject() {
        for (case in testCases.entries) {
            val recodedValue = Json.decodeFromString(serializer, Json.encodeToString(serializer, case.value))
            assertEquals("$case", case.value, recodedValue)
        }
    }
}
