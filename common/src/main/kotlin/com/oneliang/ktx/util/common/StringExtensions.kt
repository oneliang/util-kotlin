package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.io.File
import java.math.BigDecimal
import java.math.MathContext
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

fun String?.toIntSafely(defaultValue: Int = 0): Int = try {
    this?.toInt() ?: defaultValue
} catch (e: Throwable) {
    defaultValue
}

fun String?.toLongSafely(defaultValue: Long = 0): Long = try {
    this?.toLong() ?: defaultValue
} catch (e: Throwable) {
    defaultValue
}

fun String?.toFloatSafely(defaultValue: Float = 0f): Float = try {
    this?.toFloat() ?: defaultValue
} catch (e: Throwable) {
    defaultValue
}

fun String?.toDoubleSafely(defaultValue: Double = 0.0): Double = try {
    this?.toDouble() ?: defaultValue
} catch (e: Throwable) {
    defaultValue
}

fun String?.toBigDecimalSafely(defaultValue: BigDecimal = BigDecimal(0), mathContext: MathContext = MathContext.UNLIMITED): BigDecimal = try {
    BigDecimal(this, mathContext)
} catch (e: Throwable) {
    defaultValue
}

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
/**
 * the regex support full match(use ^ $) and partial match(not include ^ &) for string
 */
fun CharSequence.finds(regex: Regex): Boolean {
    return regex.find(this, 0) != null
}

/**
 * Returns true if this char sequence matches the given regular expression
 * only support full match(use ^ $ default)
 * @param regex
 */
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

fun String?.toUtilDateSafely(format: String = Constants.Time.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, locale: Locale = Locale.getDefault()): Date = try {
    this?.toUtilDate(format, locale) ?: Date()
} catch (e: Throwable) {
    Date()
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

fun String.isHourMinute(): Boolean {
    val reg = "^([0-1]?[0-9]|2[0-3]):([0-5][0-9])\$"
    return this.matches(reg)
}

fun String.urlEncode(encoding: String = Constants.Encoding.UTF8) = URLEncoder.encode(this, encoding).nullToBlank()

fun String.urlDecode(encoding: String = Constants.Encoding.UTF8) = URLDecoder.decode(this, encoding).nullToBlank()

fun String.splitPiece(pieceSize: Int, defaultArraySize: Int = 0): Array<String> {
    val pieceCount = ceil(this.length.toDouble() / pieceSize).toInt()
    val arraySize = if (defaultArraySize == 0) {
        pieceCount
    } else {
        if (defaultArraySize <= pieceCount) {
            pieceCount
        } else {
            defaultArraySize
        }
    }
    val stringArray = Array(arraySize) { Constants.String.BLANK }
    for (index in 0 until pieceCount) {
        var pieceIndex = index
        val (begin, end) = if (pieceIndex < pieceCount - 1) {
            (pieceIndex * pieceSize) to ((pieceIndex + 1) * pieceSize)
        } else {//last piece
            pieceIndex = pieceCount - 1
            (pieceIndex * pieceSize) to this.length
        }
        stringArray[index] = this.substring(begin, end)
    }
    return stringArray
}

fun String.toBriefString(retainLength: Int = this.length): String {
    if (retainLength < 0) {
        error("retain length can not be less than 0")
    }
    val totalLength = this.length
    return if (totalLength > retainLength) {
        this.substring(0, retainLength) + Constants.String.ELLIPSIS + totalLength
    } else {
        this
    }
}

@Deprecated("will delete it in future", replaceWith = ReplaceWith("this.ifNullOrBlank { defaultValue }"), level = DeprecationLevel.WARNING)
fun String?.toDefaultWhenIsNullOrBlank(defaultValue: String): String {
    return this.ifNullOrBlank { defaultValue }
}

fun String?.ifNullOrBlank(block: () -> String): String {
    return if (this.isNullOrBlank()) {
        block()
    } else {
        this
    }
}

fun String.toUnifyFilePathString(): String {
    //linux
    return if (this.startsWith(Constants.Symbol.SLASH_LEFT)) {
        this
    } else {
        //windows
        Constants.Symbol.SLASH_LEFT + this
    }
}

fun String.toFileProtocolString(): String {
    return when {
        //file protocol
        this.startsWith(Constants.Protocol.FILE) -> {
            this
        }
        //linux || windows
        else -> {

            Constants.Protocol.FILE + this.toUnifyFilePathString()
        }
    }
}

fun String.toFileProtocolURL(): URL {
    return URL(this.toFileProtocolString())
}

inline fun String.ifNotBlank(block: (String) -> Unit) {
    if (this.isNotBlank()) {
        block(this)
    }
}

inline fun String.ifNotEmpty(block: (String) -> Unit) {
    if (this.isNotEmpty()) {
        block(this)
    }
}