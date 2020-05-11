package com.oneliang.ktx.util.logging

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.toFormatString
import java.util.*

fun Logger.generateLogContent(level: Logger.Level, message: String, throwable: Throwable?, extraInfo: AbstractLogger.ExtraInfo): String {
    val stringBuilder = StringBuilder()
    stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_LEFT)
    stringBuilder.append(Date(extraInfo.logTime).toFormatString(Constants.Time.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND))
    stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT)
    stringBuilder.append(Constants.String.SPACE)
    stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_LEFT)
    stringBuilder.append(level.name)
    stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT)
    stringBuilder.append(Constants.String.SPACE)
    stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_LEFT)
    stringBuilder.append(extraInfo.threadName + Constants.Symbol.COMMA + extraInfo.threadId)
    stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT)
    stringBuilder.append(Constants.String.SPACE)
    stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_LEFT)
    stringBuilder.append(message)
    stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT)
    stringBuilder.append(Constants.String.SPACE)
    stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_LEFT)
    stringBuilder.append(extraInfo.className + Constants.Symbol.DOT + extraInfo.methodName)
    stringBuilder.append(Constants.Symbol.BRACKET_LEFT + extraInfo.filename + Constants.Symbol.COLON + extraInfo.lineNumber + Constants.Symbol.BRACKET_RIGHT)
    stringBuilder.append(Constants.Symbol.BRACKET_LEFT + extraInfo.stackTraceInfo + Constants.Symbol.BRACKET_RIGHT)
    stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT)
    return stringBuilder.toString()
}