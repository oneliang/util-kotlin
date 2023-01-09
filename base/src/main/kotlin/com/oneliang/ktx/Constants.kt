package com.oneliang.ktx

import java.util.*

object Constants {

    object Time {
        const val MILLISECONDS_OF_SECOND = 1000L
        const val MILLISECONDS_OF_MINUTE = 60 * MILLISECONDS_OF_SECOND
        const val MILLISECONDS_OF_HOUR = 60 * MILLISECONDS_OF_MINUTE
        const val MILLISECONDS_OF_DAY = 24 * MILLISECONDS_OF_HOUR
        const val SECOND_OF_MINUTE = 60L
        const val MINUTE_OF_HOUR = 60L
        const val SECONDS_OF_HOUR = SECOND_OF_MINUTE * MINUTE_OF_HOUR
        const val SECONDS_OF_DAY = SECONDS_OF_HOUR * 24

        const val YEAR = "yyyy"// year
        const val SIMPLE_YEAR = "yy"// suffix year
        const val MONTH = "MM"// month
        const val DAY = "dd"//day
        const val HOUR = "HH"//hour
        const val MINUTE = "mm"//minute
        const val SECOND = "ss"//second
        const val MILLISECOND = "SSS"//millisecond
        const val YEAR_MONTH = "yyyy-MM"// year month
        const val YEAR_MONTH_DAY = "yyyy-MM-dd"// year-month-day
        const val YEAR_MONTH_DAY_CN = "yyyy年M月d日"// year-month-day
        const val YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss"//always used for database
        const val YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND = "yyyy-MM-dd HH:mm:ss,SSS"//always used for log
        const val HOUR_MINUTE_SECOND = "HH:mm:ss"//hour:minute:second
        const val DEFAULT_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy"
        const val UNION_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND = "yyyyMMddHHmmssSSS"
        const val UNION_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyyMMddHHmmss"
        const val UNION_YEAR_MONTH_DAY_HOUR_MINUTE = "yyyyMMddHHmm"
        const val UNION_YEAR_MONTH_DAY_HOUR = "yyyyMMddHH"
        const val UNION_YEAR_MONTH_DAY = "yyyyMMdd"
        const val UNION_YEAR_MONTH = "yyyyMM"
    }

    object String {
        const val EXCEPTION = "exception"
        const val BLANK = ""
        const val SPACE = " "
        const val CRLF_STRING = "\r\n"
        const val CRLF_TRANSFER_STRING = "\\r\\n"
        const val CR_STRING = "\r"
        const val CR_TRANSFER_STRING = "\\r"
        const val LF_STRING = "\n"
        const val LF_TRANSFER_STRING = "\\n"
        const val TAB_STRING = "\t"
        const val CR = '\r'.code.toByte()
        const val LF = '\n'.code.toByte()
        const val TAB = '\t'.code.toByte()
        const val NULL = "null"
        const val ZERO = "0"
        val CRLF: ByteArray = ByteArray(2) { index ->
            when (index) {
                0 -> {
                    CR
                }

                1 -> {
                    LF
                }

                else -> {
                    0.toByte()
                }
            }
        }
        val NEW_LINE = System.lineSeparator() ?: CRLF_STRING
        const val ELLIPSIS = "..."
        const val EMPTY_OBJECT_JSON = Symbol.BIG_BRACKET_LEFT + Symbol.BIG_BRACKET_RIGHT
        const val EMPTY_ARRAY_JSON = Symbol.MIDDLE_BRACKET_LEFT + Symbol.MIDDLE_BRACKET_RIGHT
    }

    object Symbol {
        /**
         * dot "."
         */
        const val DOT = "."
        const val DOT_CHAR = '.'

        /**
         * comma ","
         */
        const val COMMA = ","

        /**
         * colon ":"
         */
        const val COLON = ":"

        /**
         * semicolon ";"
         */
        const val SEMICOLON = ";"

        /**
         * equal "="
         */
        const val EQUAL = "="

        /**
         * and "&"
         */
        const val AND = "&"

        /**
         * question mark "?"
         */
        const val QUESTION_MARK = "?"

        /**
         * wildcard "*"
         */
        const val WILDCARD = "*"

        /**
         * underline "_"
         */
        const val UNDERLINE = "_"

        /**
         * at "@"
         */
        const val AT = "@"

        /**
         * plus "+"
         */
        const val PLUS = "+"

        /**
         * minus "-"
         */
        const val MINUS = "-"

        /**
         * logic and "&&"
         */
        const val LOGIC_AND = "&&"

        /**
         * logic or "||"
         */
        const val LOGIC_OR = "||"

        /**
         * brackets begin "("
         */
        const val BRACKET_LEFT = "("

        /**
         * brackets end ")"
         */
        const val BRACKET_RIGHT = ")"

        /**
         * middle bracket left "["
         */
        const val MIDDLE_BRACKET_LEFT = "["

        /**
         * middle bracket right "]"
         */
        const val MIDDLE_BRACKET_RIGHT = "]"

        /**
         * big bracket "{"
         */
        const val BIG_BRACKET_LEFT = "{"

        /**
         * big bracket "}"
         */
        const val BIG_BRACKET_RIGHT = "}"

        /**
         * slash "/"
         */
        const val SLASH_LEFT = "/"

        /**
         * slash "\"
         */
        const val SLASH_RIGHT = "\\"

        /**
         * xor or regex begin "^"
         */
        const val XOR = "^"

        /**
         * dollar or regex end "$"
         */
        const val DOLLAR = "$"

        /**
         * single quote "'"
         */
        const val SINGLE_QUOTE = "'"

        /**
         * double quote "\""
         */
        const val DOUBLE_QUOTE = "\""

        /**
         * less than "<"
         */
        const val LESS_THAN = "<"

        /**
         * greater than ">"
         */
        const val GREATER_THAN = ">"

        /**
         * tilde "~"
         */
        const val TILDE = "~"

        /**
         * accent "`"
         */
        const val ACCENT = "`"

        /**
         * percent "%"
         */
        const val PERCENT = "%"

        /**
         * pound key "#"
         */
        const val POUND = "#"
    }

    object Encoding {
        /**
         * encoding
         */
        const val ISO88591 = "ISO-8859-1"
        const val GB2312 = "GB2312"
        const val GBK = "GBK"
        const val UTF8 = "UTF-8"
    }

    object TimeZone {
        const val ASIA_SHANGHAI = "Asia/Shanghai"
    }

    object Http {
        enum class RequestMethod(val value: kotlin.String) {
            PUT("PUT"), DELETE("DELETE"), GET("GET"), POST("POST"), HEAD("HEAD"), OPTIONS("OPTIONS"), TRACE("TRACE")
        }

        object HeaderKey {
            /**
             * for request,response header
             */
            const val CONTENT_TYPE = "Content-Type"
            const val CONTENT_DISPOSITION = "Content-Disposition"
            const val ACCEPT_CHARSET = "Accept-Charset"
            const val CONTENT_ENCODING = "Content-Encoding"
            const val ACCESS_TOKEN = "Access-Token"
            const val ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin"
            const val ORIGIN = "Origin"
        }

        object ContentType {
            /**
             * for request,response content type
             */
            const val TEXT_PLAIN = "text/plain"
            const val APPLICATION_X_DOWNLOAD = "application/x-download"
            const val APPLICATION_ANDROID_PACKAGE = "application/vnd.android.package-archive"
            const val MULTIPART_FORM_DATA = "multipart/form-data"
            const val APPLICATION_OCTET_STREAM = "application/octet-stream"
            const val BINARY_OCTET_STREAM = "binary/octet-stream"
            const val APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded"
            const val APPLICATION_JSON = "application/json"
        }

        object StatusCode {
            const val CONTINUE = 100
            const val SWITCHING_PROTOCOLS = 101
            const val PROCESSING = 102
            const val OK = 200
            const val CREATED = 201
            const val ACCEPTED = 202
            const val NON_AUTHORITATIVE_INFORMATION = 203
            const val NO_CONTENT = 204
            const val RESET_CONTENT = 205
            const val PARTIAL_CONTENT = 206
            const val MULTI_STATUS = 207
            const val MULTIPLE_CHOICES = 300
            const val MOVED_PERMANENTLY = 301
            const val FOUND = 302
            const val SEE_OTHER = 303
            const val NOT_MODIFIED = 304
            const val USE_PROXY = 305
            const val SWITCH_PROXY = 306
            const val TEMPORARY_REDIRECT = 307
            const val BAD_REQUEST = 400
            const val UNAUTHORIZED = 401
            const val PAYMENT_REQUIRED = 402
            const val FORBIDDEN = 403
            const val NOT_FOUND = 404
            const val METHOD_NOT_ALLOWED = 405
            const val NOT_ACCEPTABLE = 406
            const val REQUEST_TIMEOUT = 408
            const val CONFLICT = 409
            const val GONE = 410
            const val LENGTH_REQUIRED = 411
            const val PRECONDITION_FAILED = 412
            const val REQUEST_URI_TOO_LONG = 414
            const val EXPECTATION_FAILED = 417
            const val TOO_MANY_CONNECTIONS = 421
            const val UNPROCESSABLE_ENTITY = 422
            const val LOCKED = 423
            const val FAILED_DEPENDENCY = 424
            const val UNORDERED_COLLECTION = 425
            const val UPGRADE_REQUIRED = 426
            const val RETRY_WITH = 449
            const val INTERNAL_SERVER_ERROR = 500
            const val NOT_IMPLEMENTED = 501
            const val BAD_GATEWAY = 502
            const val SERVICE_UNAVAILABLE = 503
            const val GATEWAY_TIMEOUT = 504
            const val HTTP_VERSION_NOT_SUPPORTED = 505
            const val VARIANT_ALSO_NEGOTIATES = 506
            const val INSUFFICIENT_STORAGE = 507
            const val LOOP_DETECTED = 508
            const val BANDWIDTH_LIMIT_EXCEEDED = 509
            const val NOT_EXTENDED = 510
            const val UNPARSEABLE_RESPONSE_HEADERS = 600
        }
    }

    object RequestScope {
        const val SESSION = "session"
    }

    object RequestParameter {
        const val RETURN_URL = "returnUrl"
    }

    object Database {
        object MySql {
            /**
             * pagination
             */
            const val PAGINATION = "LIMIT"
        }

        const val COLUMN_NAME_TOTAL = "TOTAL"
        const val MYSQL = "mysql"
        const val SQLITE = "sqlite"
        const val ORACLE = "oracle"
    }

    object Capacity {
        /**
         * bytes per kilobytes
         */
        const val BYTES_PER_KB = 1024

        /**
         * bytes per millionbytes
         */
        const val BYTES_PER_MB = BYTES_PER_KB * BYTES_PER_KB
    }

    object Method {
        const val PREFIX_SET = "set"
        const val PREFIX_GET = "get"
        const val PREFIX_IS = "is"
        const val GET_CLASS = "getClass"
    }

    object File {
        object Header {
            val CSV = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
        }

        const val CLASS = "class"
        const val JPEG = "jpeg"
        const val JPG = "jpg"
        const val GIF = "gif"
        const val JAR = "jar"
        const val JAVA = "java"
        const val EXE = "exe"
        const val DEX = "dex"
        const val AIDL = "aidl"
        const val SO = "so"
        const val XML = "xml"
        const val CSV = "csv"
        const val TXT = "txt"
        const val APK = "apk"
        const val XLS = "xls"
        const val ZIP = "zip"
    }

    object Protocol {
        const val FILE = "file://"
        const val HTTP = "http://"
        const val FTP = "ftp://"
    }

    object Language {
        const val zh_CN = "zh_CN"
        const val zh_HK = "zh_HK"
        const val zh_MO = "zh_MO"
        const val zh_TW = "zh_TW"
        const val en = "en"
    }

    object CompressType {
        const val GZIP = "gzip"
        const val ZIP = "zip"
    }

    object Date {
        val DEFAULT = Date(0)
    }

    object Data {
        val EMPTY_BYTE_ARRAY = ByteArray(0)
    }

    object Ascii {
        const val NUL = 0x00.toChar()//(null)
        const val SOH = 0x01.toChar()//(start of headline)
        const val STX = 0x02.toChar()//(start of text)
        const val ETX = 0x03.toChar()//(end of text)
        const val EOT = 0x04.toChar()//(end of transmission)
        const val ENQ = 0x05.toChar()//(enquiry)
        const val ACK = 0x06.toChar()//(acknowledge)
        const val BEL = 0x07.toChar()//(bell)
        const val BS = 0x08.toChar()//(backspace)
        const val HT = 0x09.toChar()//(horizontal tab)
        const val LF = 0x0A.toChar()//(NL line feed, new line)
        const val VT = 0x0B.toChar()//(vertical tab)
        const val FF = 0x0C.toChar()//(NP form feed, new page)
        const val CR = 0x0D.toChar()//(carriage return)
        const val SO = 0x0E.toChar()//(shift out)
        const val SI = 0x0F.toChar()//(shift in)
        const val DLE = 0x10.toChar()//(data link escape)
        const val DC1 = 0x11.toChar()//(device control 1)
        const val DC2 = 0x12.toChar()//(device control 2)
        const val DC3 = 0x13.toChar()//(device control 3)
        const val DC4 = 0x14.toChar()//(device control 4)
        const val NAK = 0x15.toChar()//(negative acknowledge)
        const val SYN = 0x16.toChar()//(synchronous idle)
        const val ETB = 0x17.toChar()//(end of trans. block)
        const val CAN = 0x18.toChar()//(cancel)
        const val EM = 0x19.toChar()//(end of medium)
        const val SUB = 0x1A.toChar()//(substitute)
        const val ESC = 0x1B.toChar()//(escape)
        const val FS = 0x1C.toChar()//(file separator)
        const val GS = 0x1D.toChar()//(group separator)
        const val RS = 0x1E.toChar()//(record separator)
        const val US = 0x1F.toChar()//(unit separator)
        const val SPACE = 0x20.toChar()//(space)
        const val EXCLAMATION_MARK = 0x21.toChar()//!//!
        const val DOUBLE_QUOTE = 0x22.toChar()//"//"
        const val POUND = 0x23.toChar()//#//#
        const val DOLLAR = 0x24.toChar()//$//$
        const val PERCENT = 0x25.toChar()//%//%
        const val AND = 0x26.toChar()//&//&
        const val SINGLE_QUOTE = 0x27.toChar()//'//'
        const val BRACKET_LEFT = 0x28.toChar()//(//(
        const val BRACKET_RIGHT = 0x29.toChar()//)//)
        const val WILDCARD = 0x2A.toChar()//*//*
        const val PLUS = 0x2B.toChar()//+//+
        const val COMMA = 0x2C.toChar()//,//,
        const val MINUS = 0x2D.toChar()//-//-
        const val DOT = 0x2E.toChar()//.//.
        const val DOUBLE_SLASH_LEFT = 0x2F.toChar()//////
        const val ZERO = 0x30.toChar()//0//0
        const val ONE = 0x31.toChar()//1//1
        const val TWO = 0x32.toChar()//2//2
        const val THREE = 0x33.toChar()//3//3
        const val FOUR = 0x34.toChar()//4//4
        const val FIVE = 0x35.toChar()//5//5
        const val SIX = 0x36.toChar()//6//6
        const val SEVEN = 0x37.toChar()//7//7
        const val EIGHT = 0x38.toChar()//8//8
        const val NIGHT = 0x39.toChar()//9//9
        const val THEN = 0x3A.toChar()//://:
        const val SEMICOLON = 0x3B.toChar()//;//;
        const val LESS_THAN = 0x3C.toChar()//<//<
        const val EQUAL = 0x3D.toChar()//=//=
        const val GREATER_THAN = 0x3E.toChar()//>//>
        const val QUESTION_MARK = 0x3F.toChar()//?//?
        const val AT = 0x40.toChar()//@//@
        const val UPPERCASE_A = 0x41.toChar()//A//A
        const val UPPERCASE_B = 0x42.toChar()//B//B
        const val UPPERCASE_C = 0x43.toChar()//C//C
        const val UPPERCASE_D = 0x44.toChar()//D//D
        const val UPPERCASE_E = 0x45.toChar()//E//E
        const val UPPERCASE_F = 0x46.toChar()//F//F
        const val UPPERCASE_G = 0x47.toChar()//G//G
        const val UPPERCASE_H = 0x48.toChar()//H//H
        const val UPPERCASE_I = 0x49.toChar()//I//I
        const val UPPERCASE_J = 0x4A.toChar()//J//J
        const val UPPERCASE_K = 0x4B.toChar()//K//K
        const val UPPERCASE_L = 0x4C.toChar()//L//L
        const val UPPERCASE_M = 0x4D.toChar()//M//M
        const val UPPERCASE_N = 0x4E.toChar()//N//N
        const val UPPERCASE_O = 0x4F.toChar()//O//O
        const val UPPERCASE_P = 0x50.toChar()//P//P
        const val UPPERCASE_Q = 0x51.toChar()//Q//Q
        const val UPPERCASE_R = 0x52.toChar()//R//R
        const val UPPERCASE_S = 0x53.toChar()//S//S
        const val UPPERCASE_T = 0x54.toChar()//T//T
        const val UPPERCASE_U = 0x55.toChar()//U//U
        const val UPPERCASE_V = 0x56.toChar()//V//V
        const val UPPERCASE_W = 0x57.toChar()//W//W
        const val UPPERCASE_X = 0x58.toChar()//X//X
        const val UPPERCASE_Y = 0x59.toChar()//Y//Y
        const val UPPERCASE_Z = 0x5A.toChar()//Z//Z
        const val MIDDLE_BRACKET_LEFT = 0x5B.toChar()//[//[
        const val SLASH_RIGHT = 0x5C.toChar()//\//\
        const val MIDDLE_BRACKET_RIGHT = 0x5D.toChar()//]//]
        const val XOR = 0x5E.toChar()//^//^
        const val UNDERLINE = 0x5F.toChar()//_//_
        const val ACCENT = 0x60.toChar()//`//`
        const val LOWERCASE_A = 0x61.toChar()//a//a
        const val LOWERCASE_B = 0x62.toChar()//b//b
        const val LOWERCASE_C = 0x63.toChar()//c//c
        const val LOWERCASE_D = 0x64.toChar()//d//d
        const val LOWERCASE_E = 0x65.toChar()//e//e
        const val LOWERCASE_F = 0x66.toChar()//f//f
        const val LOWERCASE_G = 0x67.toChar()//g//g
        const val LOWERCASE_H = 0x68.toChar()//h//h
        const val LOWERCASE_I = 0x69.toChar()//i//i
        const val LOWERCASE_J = 0x6A.toChar()//j//j
        const val LOWERCASE_K = 0x6B.toChar()//k//k
        const val LOWERCASE_L = 0x6C.toChar()//l//l
        const val LOWERCASE_M = 0x6D.toChar()//m//m
        const val LOWERCASE_N = 0x6E.toChar()//n//n
        const val LOWERCASE_O = 0x6F.toChar()//o//o
        const val LOWERCASE_P = 0x70.toChar()//p//p
        const val LOWERCASE_Q = 0x71.toChar()//q//q
        const val LOWERCASE_R = 0x72.toChar()//r//r
        const val LOWERCASE_S = 0x73.toChar()//s//s
        const val LOWERCASE_T = 0x74.toChar()//t//t
        const val LOWERCASE_U = 0x75.toChar()//u//u
        const val LOWERCASE_V = 0x76.toChar()//v//v
        const val LOWERCASE_W = 0x77.toChar()//w//w
        const val LOWERCASE_X = 0x78.toChar()//x//x
        const val LOWERCASE_Y = 0x79.toChar()//y//y
        const val LOWERCASE_Z = 0x7A.toChar()//z//z
        const val BIG_BRACKET_LEFT = 0x7B.toChar()//{//{
        const val SINGLE_VERTICAL_LINE = 0x7C.toChar()//|//|
        const val BIG_BRACKET_RIGHT = 0x7D.toChar()//}//}
        const val TILDE = 0x7E.toChar()//~//~
        const val DELETE = 0x7F.toChar()//DEL//(delete)
    }
}