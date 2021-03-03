package com.oneliang.ktx

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
        const val CR = '\r'.toByte()
        const val LF = '\n'.toByte()
        const val TAB = '\t'.toByte()
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
        val ELLIPSIS = "..."
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
        const val POUND_KEY = "#"
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
            PUT("PUT"),
            DELETE("DELETE"),
            GET("GET"),
            POST("POST"),
            HEAD("HEAD"),
            OPTIONS("OPTIONS"),
            TRACE("TRACE")
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
}