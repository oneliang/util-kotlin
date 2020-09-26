package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.io.File
import java.math.BigDecimal
import java.math.MathContext
import java.text.SimpleDateFormat
import java.util.*

fun String?.toIntSafely(defaultValue: Int = 0): Int = perform({ this?.toInt() ?: defaultValue }, failure = { defaultValue })

fun String?.toLongSafely(defaultValue: Long = 0): Long = perform({ this?.toLong() ?: defaultValue }, failure = { defaultValue })

fun String?.toFloatSafely(defaultValue: Float = 0f): Float = perform({ this?.toFloat() ?: defaultValue }, failure = { defaultValue })

fun String?.toDoubleSafely(defaultValue: Double = 0.0): Double = perform({ this?.toDouble() ?: defaultValue }, failure = { defaultValue })

fun String?.toBigDecimalSafely(defaultValue: BigDecimal = BigDecimal(0), mathContext: MathContext = MathContext.UNLIMITED): BigDecimal = perform({ BigDecimal(this, mathContext) }, failure = { defaultValue })

fun String?.toBooleanSafely(defaultValue: Boolean = false): Boolean {
    this ?: return defaultValue
    return when {
        this.isBlank() -> defaultValue
        this.equals(true.toString(), true) -> true
        this.equals(false.toString(), true) -> false
        else -> defaultValue
    }
}

fun String.hexStringToByteArray(): ByteArray = ByteArray(this.length / 2) { this.substring(it * 2, it * 2 + 2).toInt(16).toByte() }

private val MATCH_PATTERN_REGEX = "[\\*]+".toRegex()
private const val MATCH_PATTERN = Constants.Symbol.WILDCARD
private const val MATCH_PATTERN_REPLACEMENT = "[\\\\S|\\\\s]*"

fun CharSequence.matchesPattern(pattern: String): Boolean {
    if (pattern.indexOf(MATCH_PATTERN) >= 0) {
        val matchPattern = Constants.Symbol.XOR + pattern.replace(MATCH_PATTERN_REGEX, MATCH_PATTERN_REPLACEMENT) + Constants.Symbol.DOLLAR
        return this.matches(matchPattern)
    } else {
        if (this == pattern) {
            return true
        }
    }
    return false
}

/**
 * the regex support full match(use ^ $) and partial match(not include ^ &) for string
 */
fun CharSequence.finds(regex: String): Boolean {
    return this.finds(regex.toRegex())
}

fun CharSequence.finds(regex: Regex): Boolean {
    return regex.find(this, 0) != null
}

fun CharSequence.matches(regex: String): Boolean {
    val matchRegex = regex.toRegex()
    return this.matches(matchRegex)
}

/**
 * Method:only for regex,parse regex group when regex include group
 * @param regex
 * @return List<String>
 */
fun CharSequence.parseRegexGroup(regex: String): List<String> {
    val groupList = mutableListOf<String>()
    val matcher = regex.toRegex()
    var matchResult = matcher.find(this)
    while (matchResult != null) {
        val groups = matchResult.groups
        val groupCount = groups.size
        for (index in 1 until groupCount) {
            val group = groups[index]
            if (group != null) {
                groupList.add(group.value)
            }
        }
        matchResult = matchResult.next()
    }
    return groupList
}

fun String?.nullToBlank(): String {
    return this ?: Constants.String.BLANK
}

object UnicodeRegex {
    const val REGEX_ALL = "\\\\u([A-Za-z0-9]*)"
    const val REGEX_CHINESE = "\\\\u([A-Za-z0-9]{4})"
    const val REGEX_ENGLISH_AND_NUMBER = "\\\\u([A-Za-z0-9]{2})"
    const val REGEX_SPECIAL = "\\\\u([A-Za-z0-9]{1})"
    internal const val FIRST_REGEX = "\\\\u"
}

fun String.toUnicode(): String {
    val stringBuilder = StringBuilder()
    val charArray = this
    for (char in charArray) {
        stringBuilder.append("\\u" + char.toInt().toString(radix = 16).toUpperCase())
    }
    return stringBuilder.toString()
}

fun String.unicodeToString(regex: String = UnicodeRegex.REGEX_ALL): String {
    val groupList = this.parseRegexGroup(regex)
    var result: String = this
    for (group in groupList) {
        result = result.replaceFirst(regex.toRegex(), group.toInt(radix = 16).toChar().toString())
    }
    return result
}

fun String?.transformQuotes(): String {
    return this.nullToBlank().replace(Constants.Symbol.DOUBLE_QUOTE, Constants.Symbol.SLASH_RIGHT + Constants.Symbol.DOUBLE_QUOTE)
}

fun String?.transformLines(): String {
    return this.nullToBlank().replace(Constants.String.CR_STRING, Constants.String.CR_TRANSFER_STRING).replace(Constants.String.LF_STRING, Constants.String.LF_TRANSFER_STRING)
}

fun String?.replaceAllSpace(): String {
    return this.nullToBlank().replace("\\s".toRegex(), Constants.String.BLANK)
}

fun String?.replaceAllLines(): String {
    return this.nullToBlank().replace(Constants.String.CR_STRING, Constants.String.BLANK).replace(Constants.String.LF_STRING, Constants.String.BLANK)
}

fun String?.replaceAllSlashToLeft(): String {
    return this.nullToBlank().replace(Constants.Symbol.SLASH_RIGHT, Constants.Symbol.SLASH_LEFT)
}

fun String.toUtilDate(format: String = Constants.Time.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, locale: Locale = Locale.getDefault()): Date {
    val simpleDateFormat = SimpleDateFormat(format, locale)
    return simpleDateFormat.parse(this)
}

fun String?.toUtilDateSafely(format: String = Constants.Time.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, locale: Locale = Locale.getDefault()): Date {
    perform(
            block = {
                val simpleDateFormat = SimpleDateFormat(format, locale)
                return simpleDateFormat.parse(this) },
            failure = { return Date() }
    )
}

fun String.toFile(): File = File(this)

fun String.splitForCsv(): List<String> {
    val fixLine = this + Constants.Symbol.COMMA
    val regex = "\"(.+?)\","
    var newLine = fixLine
    val replaceMap = mutableMapOf<String, String>()
    fixLine.parseRegexGroup(regex).forEachIndexed { index, string ->
        val key = "[@${index}]"
        replaceMap[key] = string
        newLine = newLine.replaceFirst(regex.toRegex(), key + Constants.Symbol.COMMA)
    }
    val list = newLine.split(Constants.Symbol.COMMA)
    val newList = mutableListOf<String>()
    list.forEachIndexed { index, string ->
        if (index == list.size - 1) {
            return@forEachIndexed
        }
        newList += replaceMap.getOrDefault(string, string)
    }
    return newList
}

fun String.splitForWhitespace(): List<String> {
    val list = mutableListOf<String>()
    var beginIndex = 0
    var endIndex = 0
    val stringLength = this.length
    this.toCharArray().forEach {
        if (it.isWhitespace()) {
            if (endIndex > beginIndex) {//for continuation white space
                list += this.substring(beginIndex, endIndex)
            }
            endIndex++
            beginIndex = endIndex
        } else {
            endIndex++
            if (endIndex == stringLength && endIndex > beginIndex) {//for end position
                list += this.substring(beginIndex, endIndex)
            }
        }
    }
    return list
}

fun String.isEmail(): Boolean {
    val regex = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w{2,3}){1,3})\$"
    return this.matches(regex)
}