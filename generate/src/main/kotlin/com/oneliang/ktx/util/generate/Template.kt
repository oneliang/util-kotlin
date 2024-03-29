package com.oneliang.ktx.util.generate

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.MD5String
import com.oneliang.ktx.util.common.readContentEachLine
import com.oneliang.ktx.util.file.FileUtil
import com.oneliang.ktx.util.file.fileExists
import com.oneliang.ktx.util.json.JsonUtil
import com.oneliang.ktx.util.json.toJson
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

object Template {
    private val logger = LoggerManager.getLogger(Template::class)

    private val scriptEngineManager = ScriptEngineManager()
    private val templateEngineMap = ConcurrentHashMap<String, ScriptEngine>()

    fun generate(templateContent: String, option: Option): String {
        try {
            val scriptEngine: ScriptEngine = templateEngineMap.getOrPut(templateContent.MD5String()) {
                scriptEngineManager.getEngineByExtension("js")
            }
            var json: String = Constants.String.BLANK
            val instance = option.instance
            if (instance != null) {
                json = instance.toJson(extendValueMap = option.instanceExtendValueMap, jsonProcessor = option.jsonProcessor)
            } else {
                if (option.json.isNotBlank()) {
                    json = option.json
                }
            }
            if (option.showLog) logger.verbose("data object json:$json")
            scriptEngine.eval(JavaScriptFunctionGenerator.template())
            val invocable = scriptEngine as Invocable
            if (option.showLog) logger.verbose("template content:%s", templateContent)
            var functionResult = invocable.invokeFunction(JavaScriptFunctionGenerator.FUNCTION_TEMPLATE, templateContent)
            val getResultFunction = JavaScriptFunctionGenerator.getResult(functionResult.toString())
            if (option.showLog) logger.verbose("get result content:%s", getResultFunction)
            scriptEngine.eval(getResultFunction)
            functionResult = invocable.invokeFunction(JavaScriptFunctionGenerator.FUNCTION_GET_RESULT, json)

            var result = if (functionResult != null && functionResult.toString().isNotBlank()) {
                functionResult.toString()
            } else {
                templateContent
            }
            if (option.removeBlankLine) {
                val resultBuilder = StringBuilder()
                result.byteInputStream().readContentEachLine {
                    if (it.isNotBlank()) {
                        resultBuilder.append(it)

                        resultBuilder.append(Constants.String.NEW_LINE)
                    }
                    true
                }
                result = resultBuilder.toString()
            }
            if (option.showLog) logger.debug("template engine map size:%s, result:%s", templateEngineMap.size, result)
            return result
        } catch (e: Exception) {
            logger.error(Constants.String.EXCEPTION, e)
            return templateContent
        }
    }

    fun generate(templateFullFilename: String, toFullFilename: String, option: Option) {
        generate(File(templateFullFilename), toFullFilename, option)
    }

    fun generate(template: File, toFullFilename: String, option: Option) {
        if (!option.rewrite && toFullFilename.fileExists()) {
            if (option.showLog) logger.debug("file exists and rewrite is false, can not rewrite the file:%s", toFullFilename)
            return
        }
        try {
            val stringBuilder = StringBuilder()
            FileUtil.readFileContentEachLine(template, Constants.Encoding.UTF8) { line ->
                stringBuilder.append(line)
                stringBuilder.append(Constants.String.NEW_LINE)
                true
            }
            val templateContent = stringBuilder.toString()
            val result = generate(templateContent, option)
            if (option.showLog) logger.debug("result:%s", result)
            val toFileByteArray = result.toByteArray(Charsets.UTF_8)
            FileUtil.writeFile(toFullFilename, toFileByteArray)
        } catch (e: Exception) {
            logger.error(Constants.String.EXCEPTION, e)
        }
    }

    fun removeTemplateEngine(templateContent: String) {
        this.templateEngineMap.remove(templateContent.MD5String())
    }

    class Option {
        var instance: Any? = null
        var json: String = Constants.String.BLANK
        var jsonProcessor: JsonUtil.JsonProcessor = JsonUtil.DEFAULT_JSON_PROCESSOR
        var showLog: Boolean = false
        var removeBlankLine: Boolean = false
        var rewrite: Boolean = true
        var instanceExtendValueMap: Map<String, Any> = emptyMap()
    }

    private object JavaScriptFunctionGenerator {
        const val FUNCTION_TEMPLATE = "template"
        const val FUNCTION_GET_RESULT = "getResult"
        fun template(): String {
            val stringBuilder = StringBuilder()
            val hashCode = stringBuilder.hashCode()
            val startKey = "@${hashCode}S@"
            val endKey = "@${hashCode}E@"
            stringBuilder.append("function $FUNCTION_TEMPLATE(string){")
            stringBuilder.append("string=string.replace(/[\\r]/g, \"\\\\r\");")
            stringBuilder.append("string=string.replace(/[\\t]/g, \"\\\\t\");")
            stringBuilder.append("string=string.replace(/[\\n]/g, \"\\\\n\");")
            stringBuilder.append("string=string.split(\"<%\").join(\"$startKey\");")
            stringBuilder.append("string=string.replace(/((^|%>)[^$startKey]*)'/g, \"$1$endKey\");")
            stringBuilder.append("string=string.replace(/$startKey=(.*?)%>/g, \"',$1,'\");")
            stringBuilder.append("string=string.split(\"$startKey\").join(\"');\");")
            stringBuilder.append("string=string.split(\"%>\").join(\"p.push('\");")
            stringBuilder.append("string=string.split(\"$endKey\").join(\"\\'\");")
            stringBuilder.append("return string;")
            stringBuilder.append("}")
            return stringBuilder.toString()
        }

        fun getResult(string: String): String {
            return "function $FUNCTION_GET_RESULT(json){var p=[];var object=eval('('+json+')');if(object!==null){with(object){p.push('$string');}}return p.join('');}"
        }
    }
}