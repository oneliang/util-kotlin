package com.oneliang.ktx.util.json

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringReader

class JsonTokener(reader: Reader) {
    private var character: Long = 0
    private var eof: Boolean = false
    private var index: Long = 0
    private var line: Long = 0
    private var previous: Char = ' '
    private val reader: Reader
    private var usePrevious: Boolean = false

    /**
     * Construct a JSONTokener from a Reader.
     *
     * @param reader A reader.
     */
    init {
        this.reader = if (reader.markSupported())
            reader
        else
            BufferedReader(reader)
        this.eof = false
        this.usePrevious = false
        this.previous = 0.toChar()
        this.index = 0
        this.character = 1
        this.line = 1
    }

    /**
     * Construct a JSONTokener from an InputStream.
     */
    @Throws(JsonException::class)
    constructor(inputStream: InputStream?) : this(InputStreamReader(inputStream)) {
    }

    /**
     * Construct a JSONTokener from a string.
     *
     * @param s A source string.
     */
    constructor(s: String?) : this(StringReader(s)) {}

    /**
     * Back up one character. This provides a sort of lookahead capability,
     * so that you can test for a digit or letter before attempting to parse
     * the next number or identifier.
     */
    @Throws(JsonException::class)
    fun back() {
        if (this.usePrevious || this.index <= 0) {
            throw JsonException("Stepping back two steps is not supported")
        }
        this.index -= 1
        this.character -= 1
        this.usePrevious = true
        this.eof = false
    }

    fun end(): Boolean {
        return this.eof && !this.usePrevious
    }

    /**
     * Determine if the source string still contains characters that next()
     * can consume.
     * @return true if not yet at the end of the source.
     */
    @Throws(JsonException::class)
    fun more(): Boolean {
        this.next()
        if (this.end()) {
            return false
        }
        this.back()
        return true
    }

    /**
     * Get the next character in the source string.
     *
     * @return The next character, or 0 if past the end of the source string.
     */
    @Throws(JsonException::class)
    fun next(): Char {
        var c: Int
        if (this.usePrevious) {
            this.usePrevious = false
            c = this.previous.toInt()
        } else {
            try {
                c = this.reader.read()
            } catch (exception: IOException) {
                throw JsonException(exception)
            }
            if (c <= 0) { // End of stream
                this.eof = true
                c = 0
            }
        }
        this.index += 1
        if (this.previous == '\r') {
            this.line += 1
            this.character = (if (c == '\n'.toInt()) 0 else 1).toLong()
        } else if (c == '\n'.toInt()) {
            this.line += 1
            this.character = 0
        } else {
            this.character += 1
        }
        this.previous = c.toChar()
        return this.previous
    }

    /**
     * Consume the next character, and check that it matches a specified
     * character.
     * @param c The character to match.
     * @return The character.
     * @throws JsonException if the character does not match.
     */
    @Throws(JsonException::class)
    fun next(c: Char): Char {
        val n = this.next()
        if (n != c) {
            throw this.syntaxError(("Expected '" + c + "' and instead saw '" +
                    n + "'"))
        }
        return n
    }

    /**
     * Get the next n characters.
     *
     * @param n The number of characters to take.
     * @return A string of n characters.
     * @throws JsonException
     * Substring bounds error if there are not
     * n characters remaining in the source string.
     */
    @Throws(JsonException::class)
    fun next(n: Int): String? {
        if (n == 0) {
            return ""
        }
        val chars = CharArray(n)
        var pos = 0
        while (pos < n) {
            chars[pos] = this.next()
            if (this.end()) {
                throw this.syntaxError("Substring bounds error")
            }
            pos += 1
        }
        return String(chars)
    }

    /**
     * Get the next char in the string, skipping whitespace.
     * @throws JsonException
     * @return A character, or 0 if there are no more characters.
     */
    @Throws(JsonException::class)
    fun nextClean(): Char {
        while (true) {
            val c = this.next()
            if (c.toInt() == 0 || c > ' ') {
                return c
            }
        }
    }

    /**
     * Return the characters up to the next close quote character.
     * Backslash processing is done. The formal JSON format does not
     * allow strings in single quotes, but an implementation is allowed to
     * accept them.
     * @param quote The quoting character, either
     * <code>"</code>&nbsp;<small>(double quote)</small> or
     * <code>'</code>&nbsp;<small>(single quote)</small>.
     * @return A String.
     * @throws JsonException Unterminated string.
     */
    @Throws(JsonException::class)
    fun nextString(quote: Char): String {
        var c: Char
        val sb = StringBuffer()
        while (true) {
            c = this.next()
            when (c) {
                0.toChar(), '\n', '\r' -> throw this.syntaxError("Unterminated string")
                '\\' -> {
                    c = this.next()
                    when (c) {
                        'b' -> sb.append('\b')
                        't' -> sb.append('\t')
                        'n' -> sb.append('\n')
                        'f' -> sb.append('\u000C')// equal \f
                        'r' -> sb.append('\r')
                        'u' -> sb.append(Integer.parseInt(this.next(4), 16).toChar())
                        '"', '\'', '\\', '/' -> sb.append(c)
                        else -> throw this.syntaxError("Illegal escape.")
                    }
                }
                else -> {
                    if (c == quote) {
                        return sb.toString()
                    }
                    sb.append(c)
                }
            }
        }
    }

    /**
     * Get the text up but not including the specified character or the
     * end of line, whichever comes first.
     * @param delimiter A delimiter character.
     * @return A string.
     */
    @Throws(JsonException::class)
    fun nextTo(delimiter: Char): String? {
        val sb = StringBuffer()
        while (true) {
            val c = this.next()
            if (c == delimiter || c.toInt() == 0 || c == '\n' || c == '\r') {
                if (c.toInt() != 0) {
                    this.back()
                }
                return sb.toString().trim()
            }
            sb.append(c)
        }
    }

    /**
     * Get the text up but not including one of the specified delimiter
     * characters or the end of line, whichever comes first.
     * @param delimiters A set of delimiter characters.
     * @return A string, trimmed.
     */
    @Throws(JsonException::class)
    fun nextTo(delimiters: String): String {
        var c: Char
        val sb = StringBuffer()
        while (true) {
            c = this.next()
            if ((delimiters.indexOf(c) >= 0 || c.toInt() == 0 ||
                            c == '\n' || c == '\r')) {
                if (c.toInt() != 0) {
                    this.back()
                }
                return sb.toString().trim()
            }
            sb.append(c)
        }
    }

    /**
     * Get the next value. The value can be a Boolean, Double, Integer,
     * JSONArray, JSONObject, Long, or String, or the JSONObject.NULL object.
     * @throws JsonException If syntax error.
     *
     * @return An object.
     */
    @Throws(JsonException::class)
    fun nextValue(): Any {
        var c = this.nextClean()
        val string: String?
        when (c) {
            '"', '\'' -> return this.nextString(c)
            '{' -> {
                this.back()
                return JsonObject(this)
            }
            '[' -> {
                this.back()
                return JsonArray(this)
            }
        }
/*
* Handle unquoted text. This could be the values true, false, or
* null, or it can be a number. An implementation (such as this one)
* is allowed to also accept non-standard forms.
*
* Accumulate characters until we reach the end of the text or a
* formatting character.
*/
        val sb = StringBuffer()
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c)
            c = this.next()
        }
        this.back()
        string = sb.toString().trim()
        if ("".equals(string)) {
            throw this.syntaxError("Missing value")
        }
        return JsonObject.stringToValue(string)
    }

    /**
     * Skip characters until the next character is the requested character.
     * If the requested character is not found, no characters are skipped.
     * @param to A character to skip to.
     * @return The requested character, or zero if the requested character
     * is not found.
     */
    @Throws(JsonException::class)
    fun skipTo(to: Char): Char {
        var c: Char
        try {
            val startIndex = this.index
            val startCharacter = this.character
            val startLine = this.line
            this.reader.mark(1000000)
            do {
                c = this.next()
                if (c.toInt() == 0) {
                    this.reader.reset()
                    this.index = startIndex
                    this.character = startCharacter
                    this.line = startLine
                    return c
                }
            } while (c != to)
        } catch (exc: IOException) {
            throw JsonException(exc)
        }
        this.back()
        return c
    }

    /**
     * Make a JsonException to signal a syntax error.
     *
     * @param message The error message.
     * @return A JsonException object, suitable for throwing
     */
    fun syntaxError(message: String): JsonException {
        return JsonException(message + this.toString())
    }

    /**
     * Make a printable string of this JSONTokener.
     *
     * @return " at {index} [character {character} line {line}]"
     */
    override fun toString(): String {
        return (" at " + this.index + " [character " + this.character + " line " +
                this.line + "]")
    }

    companion object {
        /**
         * Get the hex value of a character (base16).
         * @param c A character between '0' and '9' or between 'A' and 'F' or
         * between 'a' and 'f'.
         * @return An int between 0 and 15, or -1 if c was not a hex digit.
         */
        fun dehexchar(c: Char): Int {
            if (c >= '0' && c <= '9') {
                return c - '0'
            }
            if (c >= 'A' && c <= 'F') {
                return c.toInt() - ('A'.toInt() - 10)
            }
            if (c >= 'a' && c <= 'f') {
                return c.toInt() - ('a'.toInt() - 10)
            }
            return -1
        }
    }
}