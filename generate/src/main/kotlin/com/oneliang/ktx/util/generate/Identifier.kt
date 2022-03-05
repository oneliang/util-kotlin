package com.oneliang.ktx.util.generate

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.toFillZeroString
import com.oneliang.ktx.util.common.toFormatString
import java.util.*

object Identifier {
    private const val SIMPLE_YEAR = "${Constants.Symbol.MIDDLE_BRACKET_LEFT}Y${Constants.Symbol.MIDDLE_BRACKET_RIGHT}"
    private const val YEAR = "${Constants.Symbol.MIDDLE_BRACKET_LEFT}YYYY${Constants.Symbol.MIDDLE_BRACKET_RIGHT}"
    private const val MONTH = "${Constants.Symbol.MIDDLE_BRACKET_LEFT}M${Constants.Symbol.MIDDLE_BRACKET_RIGHT}"
    private const val DAY = "${Constants.Symbol.MIDDLE_BRACKET_LEFT}D${Constants.Symbol.MIDDLE_BRACKET_RIGHT}"
    fun generate(template: String, keyTemplate: String = Constants.String.BLANK, afterIdentifierKeyGenerate: (identifierKey: String) -> Map<String, String> = { _ -> emptyMap() }): String {
        val currentTime = Date()
        val simpleYearString = currentTime.toFormatString(Constants.Time.SIMPLE_YEAR)
        val yearString = currentTime.toFormatString(Constants.Time.YEAR)
        val simpleMonthString = currentTime.toFormatString(Constants.Time.MONTH)
        val simpleDayString = currentTime.toFormatString(Constants.Time.DAY)
        val identifierKey = keyTemplate.replace(SIMPLE_YEAR, simpleYearString).replace(MONTH, simpleMonthString).replace(DAY, simpleDayString)
        val templateKeyValueMap = afterIdentifierKeyGenerate(identifierKey)
        var identifier = template.trim().replace(YEAR, yearString).replace(SIMPLE_YEAR, simpleYearString).replace(MONTH, simpleMonthString).replace(DAY, simpleDayString)
        templateKeyValueMap.forEach { (key, value) ->
            identifier = identifier.replace(key, value)
        }
        return identifier
    }
}

fun main() {
    var template = "GLL[Y]"
    println(Identifier.generate(template))
    template = "GLL[Y]_JG[ORDER]"
    val keyTemplate = "GLL[Y]_JG"
    println(Identifier.generate(template, keyTemplate) { identifierKey ->
        println(identifierKey)
        mapOf("[ORDER]" to 1.toFillZeroString(4))
    })
}
