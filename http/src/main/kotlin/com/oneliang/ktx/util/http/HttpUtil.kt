package com.oneliang.ktx.util.http

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.net.*
import java.nio.charset.Charset

object HttpUtil {

    private val logger = LoggerManager.getLogger(HttpUtil::class)
    const val DEFAULT_TIMEOUT = 20000

    /**
     * send request by get method
     * @param httpUrl
     * @param httpHeaderList
     * @param returnEncoding
     * @param advancedOption
     * @return String
     */
    fun sendRequestGet(httpUrl: String, httpHeaderList: List<HttpNameValue> = emptyList(), timeout: Int = DEFAULT_TIMEOUT, returnEncoding: String = Constants.Encoding.UTF8, advancedOption: AdvancedOption? = null): String {
        val byteArray = sendRequestGetWithReturnBytes(httpUrl, httpHeaderList, timeout, advancedOption)
        var result: String = Constants.String.BLANK
        if (byteArray.isNotEmpty()) {
            try {
                result = String(byteArray, Charset.forName(returnEncoding))
            } catch (e: UnsupportedEncodingException) {
                logger.error(Constants.String.EXCEPTION, e)
            }
        }
        return result
    }

    /**
     * send request with return bytes by get method,most for collect data
     * return bytes means response is bytes
     * @param httpUrl
     * @param httpHeaderList
     * @param timeout
     * @param advancedOption
     * @return byte[]
     */
    fun sendRequestGetWithReturnBytes(httpUrl: String, httpHeaderList: List<HttpNameValue> = emptyList(), timeout: Int = DEFAULT_TIMEOUT, advancedOption: AdvancedOption? = null): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        sendRequestGet(httpUrl, httpHeaderList, timeout, advancedOption, object : Callback {
            @Throws(Throwable::class)
            override fun httpOkCallback(headerFieldMap: Map<String, List<String>>, inputStream: InputStream, contentLength: Int) {
                inputStream.copyTo(byteArrayOutputStream)
                byteArrayOutputStream.close()
            }

            override fun exceptionCallback(throwable: Throwable) {
                logger.error(Constants.String.EXCEPTION, throwable)
            }

            @Throws(Throwable::class)
            override fun httpNotOkCallback(responseCode: Int, headerFieldMap: Map<String, List<String>>, errorInputStream: InputStream?) {
                logger.debug("Response code:%s", responseCode)
                errorInputStream?.copyTo(byteArrayOutputStream)
                byteArrayOutputStream.close()
            }
        })
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * send request by get method
     * @param httpUrl
     * @param httpHeaderList
     * @param timeout
     * @param advancedOption
     * @param callback
     */
    fun sendRequestGet(httpUrl: String, httpHeaderList: List<HttpNameValue> = emptyList(), timeout: Int = DEFAULT_TIMEOUT, advancedOption: AdvancedOption? = null, callback: Callback) {
        sendRequest(httpUrl, Constants.Http.RequestMethod.GET.value, httpHeaderList, emptyList(), ByteArray(0), null, timeout, null, advancedOption, callback)
    }

    /**
     * send request by post method
     * @param httpUrl
     * @param httpHeaderList
     * @param httpParameterList
     * @param timeout
     * @param returnEncoding
     * @param advancedOption
     * @return String
     */
    fun sendRequestPost(httpUrl: String, httpHeaderList: List<HttpNameValue> = emptyList(), httpParameterList: List<HttpNameValue> = emptyList(), timeout: Int = DEFAULT_TIMEOUT, returnEncoding: String = Constants.Encoding.UTF8, advancedOption: AdvancedOption? = null): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        sendRequestPost(httpUrl, httpHeaderList, httpParameterList, timeout, advancedOption, object : Callback {
            @Throws(Throwable::class)
            override fun httpOkCallback(headerFieldMap: Map<String, List<String>>, inputStream: InputStream, contentLength: Int) {
                inputStream.copyTo(byteArrayOutputStream)
                byteArrayOutputStream.close()
            }

            override fun exceptionCallback(throwable: Throwable) {
                logger.error(Constants.String.EXCEPTION, throwable)
            }

            @Throws(Throwable::class)
            override fun httpNotOkCallback(responseCode: Int, headerFieldMap: Map<String, List<String>>, errorInputStream: InputStream?) {
                logger.debug("Response code:%s", responseCode)
                errorInputStream?.copyTo(byteArrayOutputStream)
                byteArrayOutputStream.close()
            }
        })
        val byteArray = byteArrayOutputStream.toByteArray()
        var result: String = Constants.String.BLANK
        if (byteArray.isNotEmpty()) {
            try {
                result = String(byteArray, Charset.forName(returnEncoding))
            } catch (e: UnsupportedEncodingException) {
                logger.error(Constants.String.EXCEPTION, e)
            }
        }
        return result
    }

    /**
     * send request by post method,most for download
     * @param httpUrl
     * @param httpHeaderList
     * @param httpParameterList
     * @param timeout
     * @param advancedOption
     * @param callback
     */
    fun sendRequestPost(httpUrl: String, httpHeaderList: List<HttpNameValue> = emptyList(), httpParameterList: List<HttpNameValue> = emptyList(), timeout: Int = DEFAULT_TIMEOUT, advancedOption: AdvancedOption? = null, callback: Callback) {
        sendRequestPost(httpUrl, httpHeaderList, httpParameterList, ByteArray(0), null, timeout, null, advancedOption, callback)
    }

    /**
     * send request with bytes by post method,most for upload
     * @param httpUrl
     * @param httpHeaderList
     * @param byteArray
     * @param timeout
     * @param advancedOption
     * @return String
     */
    fun sendRequestPostWithBytes(httpUrl: String, httpHeaderList: List<HttpNameValue> = emptyList(), byteArray: ByteArray = ByteArray(0), timeout: Int = DEFAULT_TIMEOUT, advancedOption: AdvancedOption? = null): String {
        val tempByteArray = sendRequestPostWithWholeBytes(httpUrl, httpHeaderList, byteArray, timeout, advancedOption)
        var result: String = Constants.String.BLANK
        if (tempByteArray.isNotEmpty()) {
            try {
                result = String(tempByteArray, Charsets.UTF_8)
            } catch (e: UnsupportedEncodingException) {
                logger.error(Constants.String.EXCEPTION, e)
            }
        }
        return result
    }

    /**
     * send request with whole bytes by post method,most for communication,whole
     * bytes means request and response are bytes
     * @param httpUrl
     * @param httpHeaderList
     * @param byteArray
     * @param timeout
     * @param advancedOption
     * @return byte[]
     */
    fun sendRequestPostWithWholeBytes(httpUrl: String, httpHeaderList: List<HttpNameValue> = emptyList(), byteArray: ByteArray = ByteArray(0), timeout: Int = DEFAULT_TIMEOUT, advancedOption: AdvancedOption? = null): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        sendRequestPostWithBytes(httpUrl, httpHeaderList, byteArray, timeout, advancedOption, object : Callback {
            @Throws(Throwable::class)
            override fun httpOkCallback(headerFieldMap: Map<String, List<String>>, inputStream: InputStream, contentLength: Int) {
                inputStream.copyTo(byteArrayOutputStream)
                byteArrayOutputStream.close()
            }

            override fun exceptionCallback(throwable: Throwable) {
                logger.error(Constants.String.EXCEPTION, throwable)
            }

            @Throws(Throwable::class)
            override fun httpNotOkCallback(responseCode: Int, headerFieldMap: Map<String, List<String>>, errorInputStream: InputStream?) {
                logger.debug("Response code:%s, errorInputStream:%s", responseCode, errorInputStream)
                errorInputStream?.copyTo(byteArrayOutputStream)
                byteArrayOutputStream.close()
            }
        })
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * send request with bytes by post method,most for upload
     * @param httpUrl
     * @param httpHeaderList
     * @param byteArray
     * @param timeout
     * @param advancedOption
     * @param callback
     */
    fun sendRequestPostWithBytes(httpUrl: String, httpHeaderList: List<HttpNameValue> = emptyList(), byteArray: ByteArray = ByteArray(0), timeout: Int = DEFAULT_TIMEOUT, advancedOption: AdvancedOption? = null, callback: Callback) {
        sendRequestPost(httpUrl, httpHeaderList, emptyList(), byteArray, null, timeout, null, advancedOption, callback)
    }

    /**
     * send request with input stream by post method, most for upload
     * @param httpUrl
     * @param httpHeaderList
     * @param inputStream
     * @param timeout
     * @param advancedOption
     * @return String
     */
    fun sendRequestPostWithInputStream(httpUrl: String, httpHeaderList: List<HttpNameValue> = emptyList(), inputStream: InputStream, timeout: Int = DEFAULT_TIMEOUT, advancedOption: AdvancedOption? = null): String {
        return sendRequestPostWithInputStream(httpUrl = httpUrl, httpHeaderList = httpHeaderList, inputStream = inputStream, timeout = timeout, inputStreamProcessor = object : InputStreamProcessor {
            @Throws(Throwable::class)
            override fun process(inputStream: InputStream, outputStream: OutputStream) {
                val buffer = ByteArray(Constants.Capacity.BYTES_PER_KB)
                var dataLength = inputStream.read(buffer, 0, buffer.size)
                while (dataLength != -1) {
                    outputStream.write(buffer, 0, dataLength)
                    outputStream.flush()
                    dataLength = inputStream.read(buffer, 0, buffer.size)
                }
            }
        }, advancedOption = advancedOption)
    }

    /**
     * send request with input stream by post method, most for upload
     * @param httpUrl
     * @param httpHeaderList
     * @param inputStream
     * @param timeout
     * @param inputStreamProcessor
     * @param returnEncoding
     * @param advancedOption
     * @return String
     */
    fun sendRequestPostWithInputStream(httpUrl: String, httpHeaderList: List<HttpNameValue> = emptyList(), inputStream: InputStream, timeout: Int = DEFAULT_TIMEOUT, inputStreamProcessor: InputStreamProcessor, returnEncoding: String = Constants.Encoding.UTF8, advancedOption: AdvancedOption? = null): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        sendRequestPostWithInputStream(httpUrl, httpHeaderList, inputStream, timeout, inputStreamProcessor, advancedOption, object : Callback {
            @Throws(Throwable::class)
            override fun httpOkCallback(headerFieldMap: Map<String, List<String>>, inputStream: InputStream, contentLength: Int) {
                inputStream.copyTo(byteArrayOutputStream)
                byteArrayOutputStream.close()
            }

            override fun exceptionCallback(throwable: Throwable) {
                logger.error(Constants.String.EXCEPTION, throwable)
            }

            @Throws(Throwable::class)
            override fun httpNotOkCallback(responseCode: Int, headerFieldMap: Map<String, List<String>>, errorInputStream: InputStream?) {
                logger.debug("Response code:%s", responseCode)
                errorInputStream?.copyTo(byteArrayOutputStream)
                byteArrayOutputStream.close()
            }
        })
        val byteArray = byteArrayOutputStream.toByteArray()
        var result: String = Constants.String.BLANK
        if (byteArray.isNotEmpty()) {
            try {
                result = String(byteArray, Charset.forName(returnEncoding))
            } catch (e: UnsupportedEncodingException) {
                logger.error(Constants.String.EXCEPTION, e)
            }

        }
        return result
    }

    /**
     * send request with input stream by post method, most for upload
     * @param httpUrl
     * @param httpHeaderList
     * @param inputStream
     * @param timeout
     * @param callback
     */
    fun sendRequestPostWithInputStream(httpUrl: String, httpHeaderList: List<HttpNameValue>, inputStream: InputStream, timeout: Int, advancedOption: AdvancedOption, callback: Callback) {
        sendRequestPostWithInputStream(httpUrl, httpHeaderList, inputStream, timeout, object : InputStreamProcessor {
            @Throws(Throwable::class)
            override fun process(inputStream: InputStream, outputStream: OutputStream) {
                val buffer = ByteArray(Constants.Capacity.BYTES_PER_KB)
                var dataLength = inputStream.read(buffer, 0, buffer.size)
                while (dataLength != -1) {
                    outputStream.write(buffer, 0, dataLength)
                    outputStream.flush()
                    dataLength = inputStream.read(buffer, 0, buffer.size)
                }
            }
        }, advancedOption, callback)
    }

    /**
     * send request with input stream by post method, most for upload
     * @param httpUrl
     * @param httpHeaderList
     * @param inputStream
     * @param timeout
     * @param inputStreamProcessor
     * @param advancedOption
     * @param callback
     */
    fun sendRequestPostWithInputStream(httpUrl: String, httpHeaderList: List<HttpNameValue> = emptyList(), inputStream: InputStream, timeout: Int = DEFAULT_TIMEOUT, inputStreamProcessor: InputStreamProcessor? = null, advancedOption: AdvancedOption? = null, callback: Callback) {
        sendRequestPost(httpUrl, httpHeaderList, emptyList(), ByteArray(0), inputStream, timeout, inputStreamProcessor, advancedOption, callback)
    }

    /**
     * send request by post method
     * @param httpUrl
     * @param httpHeaderList
     * @param httpParameterList
     * @param streamByteArray
     * @param inputStream
     * @param timeout
     * @param inputStreamProcessor
     * @param advancedOption
     * @param callback
     */
    private fun sendRequestPost(httpUrl: String, httpHeaderList: List<HttpNameValue> = emptyList(), httpParameterList: List<HttpNameValue> = emptyList(), streamByteArray: ByteArray = ByteArray(0), inputStream: InputStream? = null, timeout: Int = DEFAULT_TIMEOUT, inputStreamProcessor: InputStreamProcessor? = null, advancedOption: AdvancedOption?, callback: Callback) {
        sendRequest(httpUrl, Constants.Http.RequestMethod.POST.value, httpHeaderList, httpParameterList, streamByteArray, inputStream, timeout, inputStreamProcessor, advancedOption, callback)
    }

    /**
     * send request
     * @param httpUrl
     * @param method
     * @param httpHeaderList
     * @param httpParameterList
     * @param streamByteArray
     * @param inputStream
     * @param timeout
     * @param inputStreamProcessor
     * @param advancedOption
     * @param callback
     */
    fun sendRequest(httpUrl: String, method: String, httpHeaderList: List<HttpNameValue> = emptyList(), httpParameterList: List<HttpNameValue> = emptyList(), streamByteArray: ByteArray = ByteArray(0), inputStream: InputStream? = null, timeout: Int = DEFAULT_TIMEOUT, inputStreamProcessor: InputStreamProcessor? = null, advancedOption: AdvancedOption? = null, callback: Callback? = null) {
        try {
            val url = URL(httpUrl)
            var proxy = Proxy.NO_PROXY
            if (advancedOption != null && advancedOption.proxyHostname.isNotBlank() && advancedOption.proxyPort > 0) {
                val inetSocketAddress = InetSocketAddress(advancedOption.proxyHostname, advancedOption.proxyPort)
                proxy = Proxy(advancedOption.proxyType, inetSocketAddress)
            }
            val httpUrlConnection = url.openConnection(proxy) as HttpURLConnection
            httpUrlConnection.doOutput = true
            httpUrlConnection.doInput = true
            httpUrlConnection.requestMethod = method
            httpUrlConnection.useCaches = false
            httpUrlConnection.instanceFollowRedirects = true
            httpUrlConnection.connectTimeout = timeout
            httpUrlConnection.readTimeout = timeout
            if (httpHeaderList.isNotEmpty()) {
                for (httpParameter in httpHeaderList) {
                    httpUrlConnection.setRequestProperty(httpParameter.name, httpParameter.value)
                }
            }
            val content = StringBuilder()
            if (httpParameterList.isNotEmpty()) {
                content.append(httpParameterList.joinToString(Constants.Symbol.AND) {
                    it.name + Constants.Symbol.EQUAL + URLEncoder.encode(it.value, Constants.Encoding.UTF8)
                })
            }
            httpUrlConnection.connect()
            if (method.isNotBlank() && method.equals(Constants.Http.RequestMethod.POST.value, ignoreCase = true)) {
                val outputStream = httpUrlConnection.outputStream
                outputStream.write(content.toString().toByteArray(Charsets.UTF_8))
                if (streamByteArray.isNotEmpty()) {
                    outputStream.write(streamByteArray)
                    outputStream.flush()
                } else {
                    if (inputStreamProcessor != null && inputStream != null) {
                        inputStreamProcessor.process(inputStream, outputStream)
                    }
                }
                outputStream.close()
            }
            val responseCode = httpUrlConnection.responseCode
            val headerFieldMap = httpUrlConnection.headerFields ?: emptyMap()
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (callback != null) {
                    val contentLength = httpUrlConnection.contentLength
                    val responseInputStream = httpUrlConnection.inputStream
                    try {
                        callback.httpOkCallback(headerFieldMap, responseInputStream, contentLength)
                    } catch (throwable: Throwable) {
                        callback.exceptionCallback(throwable)
                    }

                    responseInputStream.close()
                }
            } else {
                if (callback != null) {
                    val errorInputStream = httpUrlConnection.errorStream
                    try {
                        callback.httpNotOkCallback(responseCode, headerFieldMap, errorInputStream)
                    } catch (throwable: Throwable) {
                        callback.exceptionCallback(throwable)
                    }
                }
            }
            httpUrlConnection.disconnect()
        } catch (throwable: Throwable) {
            callback?.exceptionCallback(throwable)
        }
    }

    interface InputStreamProcessor {
        /**
         * process
         * @param inputStream
         * @param outputStream
         * @throws Exception
         */
        @Throws(Throwable::class)
        fun process(inputStream: InputStream, outputStream: OutputStream)
    }

    interface Callback {
        /**
         * http ok callback only for http status 200
         * @param headerFieldMap
         * @param inputStream
         * @param contentLength
         * @throws Throwable
         */
        @Throws(Throwable::class)
        fun httpOkCallback(headerFieldMap: Map<String, List<String>>, inputStream: InputStream, contentLength: Int)

        /**
         * request process throw exception callback like timeout unknown host and
         * so on
         * @param throwable
         */
        fun exceptionCallback(throwable: Throwable)

        /**
         * http not ok callback
         * @param responseCode
         * @param headerFieldMap
         * @param errorInputStream
         * @throws Throwable
         */
        @Throws(Throwable::class)
        fun httpNotOkCallback(responseCode: Int, headerFieldMap: Map<String, List<String>>, errorInputStream: InputStream?)
    }

    class HttpNameValue(var name: String, var value: String)

    class AdvancedOption {
        var proxyType = Proxy.Type.HTTP
        var proxyHostname: String = Constants.String.BLANK
        var proxyPort = 0
    }
}