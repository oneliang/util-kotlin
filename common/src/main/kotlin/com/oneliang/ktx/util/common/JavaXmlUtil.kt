package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import java.io.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.reflect.KClass

object JavaXmlUtil {

    val DEFAULT_CLASS_PROCESSOR = KotlinClassUtil.DEFAULT_KOTLIN_CLASS_PROCESSOR

    /**
     * get document builder
     * @return DocumentBuilder
     */
    private val documentBuilder: DocumentBuilder
        @Throws(Exception::class)
        get() {
            val documentBuilder: DocumentBuilder
            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            try {
                documentBuilderFactory.isValidating = false
                documentBuilder = documentBuilderFactory.newDocumentBuilder()
            } catch (e: Exception) {
                throw JavaXmlUtilException(e)
            }

            return documentBuilder
        }

    val emptyDocument: Document
        @Throws(Exception::class)
        get() {
            val document: Document
            try {
                val documentBuilder = documentBuilder
                document = documentBuilder.newDocument()
                document.normalize()
            } catch (e: Exception) {
                throw JavaXmlUtilException(e)
            }
            return document
        }

    /**
     * parse
     * @param fullFilename
     * @return Document
     */
    fun parse(fullFilename: String): Document {
        if (fullFilename.isBlank()) {
            throw JavaXmlUtilException("full filename is blank")
        }
        return parse(File(fullFilename))
    }

    /**
     * parse
     * @param file
     * @return Document
     */
    @Throws(Exception::class)
    fun parse(file: File): Document {
        if (!file.exists()) {
            throw JavaXmlUtilException("file is not exist, file:${file.absolutePath}")
        }
        val document: Document
        try {
            val documentBuilder = documentBuilder
            document = documentBuilder.parse(file)
            document.normalize()
        } catch (e: Exception) {
            throw JavaXmlUtilException(e)
        }
        return document
    }

    /**
     * parse
     * @param inputStream
     * @return Document
     */
    @Throws(Exception::class)
    fun parse(inputStream: InputStream): Document {
        val document: Document
        try {
            val documentBuilder = documentBuilder
            document = documentBuilder.parse(inputStream)
            document.normalize()
        } catch (e: Exception) {
            throw JavaXmlUtilException(e)
        }
        return document
    }

    /**
     * save document
     * @param document
     * @param outputFullFilename
     */
    @Throws(Exception::class)
    fun saveDocument(document: Document, outputFullFilename: String) {
        var outputStream: OutputStream? = null
        try {
            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()
            val domSource = DOMSource(document)
            transformer.setOutputProperty(OutputKeys.ENCODING, Constants.Encoding.UTF8)
            outputStream = FileOutputStream(outputFullFilename)
            val result = StreamResult(outputStream)
            transformer.transform(domSource, result)
        } catch (e: Exception) {
            throw JavaXmlUtilException(e)
        } finally {
            try {
                outputStream?.close()
            } catch (e: Exception) {
                throw JavaXmlUtilException(e)
            }
        }
    }

    /**
     * initialize from attribute map
     * @param objectValue
     * @param namedNodeMap
     * @param classProcessor
     */
    @Throws(Exception::class)
    fun initializeFromAttributeMap(objectValue: Any, namedNodeMap: NamedNodeMap, classProcessor: KotlinClassUtil.KotlinClassProcessor = DEFAULT_CLASS_PROCESSOR) {
        val methods = objectValue.javaClass.methods
        for (method in methods) {
            val methodName = method.name
            val fieldName = if (methodName.startsWith(Constants.Object.Method.PREFIX_SET)) {
                ObjectUtil.methodNameToFieldName(Constants.Object.Method.PREFIX_SET, methodName)
            } else {
                Constants.String.BLANK
            }
            if (fieldName.isBlank()) {
                continue
            }
            val node = namedNodeMap.getNamedItem(fieldName) ?: continue
            val classes = method.parameterTypes
            if (classes.size == 1) {
                val objectClass = classes[0].kotlin
                val attributeValue = node.nodeValue
                val value = KotlinClassUtil.changeType(objectClass, arrayOf(attributeValue), classProcessor = classProcessor)
                try {
                    method.invoke(objectValue, value)
                } catch (e: Exception) {
                    throw JavaXmlUtilException(e)
                }
            }
        }
    }

    /**
     * xml to list
     * @param <T>
     * @param xml
     * @param xmlObjectTag
     * @param kClass
     * @param mapping
     * @return List<T>
    </T></T> */
    @Throws(Exception::class)
    fun <T : Any> xmlToObjectList(xml: String, xmlObjectTag: String, kClass: KClass<T>, mapping: Map<String, String>, classProcessor: KotlinClassUtil.KotlinClassProcessor = DEFAULT_CLASS_PROCESSOR): List<T> {
        val list = mutableListOf<T>()
        try {
            val inputStream = ByteArrayInputStream(xml.toByteArray(Charsets.UTF_8))
            val document = parse(inputStream)
            val root = document.documentElement
            val nodeList = root.getElementsByTagName(xmlObjectTag)
            for (i in 0 until nodeList.length) {
                val node = nodeList.item(i)
                list.add(xmlToObject(node as Element, kClass, mapping, classProcessor))
            }
        } catch (e: Exception) {
            throw JavaXmlUtilException(e)
        }
        return list
    }

    /**
     * xml to object
     * @param <T>
     * @param element
     * @param kClass
     * @param mapping
     * @return <T>
    </T></T> */
    @Throws(Exception::class)
    private fun <T : Any> xmlToObject(element: Element, kClass: KClass<T>, mapping: Map<String, String>, classProcessor: KotlinClassUtil.KotlinClassProcessor = DEFAULT_CLASS_PROCESSOR): T {
        val objectValue: T
        try {
            objectValue = kClass.java.newInstance()
            val methods = kClass.java.methods
            for (method in methods) {
                val methodName = method.name
                val classes = method.parameterTypes
                if (!methodName.startsWith(Constants.Object.Method.PREFIX_SET)) {
                    continue
                }
                val fieldName = ObjectUtil.methodNameToFieldName(Constants.Object.Method.PREFIX_SET, methodName)
                if (fieldName.isBlank()) {
                    continue
                }
                val xmlTagName = mapping[fieldName] ?: continue
                val nodeList = element.getElementsByTagName(xmlTagName)
                if (nodeList.length == 0) {
                    continue
                }
                val node = nodeList.item(0) ?: continue
                val childNodeList = node.childNodes
                val xmlTagValue = if (childNodeList.length == 1) {
                    val childNode = childNodeList.item(0)
                    if (childNode.nodeType == Node.TEXT_NODE || childNode.nodeType == Node.CDATA_SECTION_NODE) {
                        node.textContent.nullToBlank()
                    } else {
                        nodeToString(node)
                    }
                } else {
                    nodeToString(node)
                }
                if (classes.size == 1) {
                    val value = KotlinClassUtil.changeType(classes[0].kotlin, arrayOf(xmlTagValue), fieldName, classProcessor)
                    method.invoke(objectValue, value)
                }
            }
        } catch (e: Exception) {
            throw JavaXmlUtilException(e)
        }
        return objectValue
    }

    /**
     * xml to object
     * @param <T>
     * @param xml
     * @param kClass
     * @param mapping
     * @return <T>
    </T></T> */
    @Throws(Exception::class)
    fun <T : Any> xmlToObject(xml: String, kClass: KClass<T>, mapping: Map<String, String>, classProcessor: KotlinClassUtil.KotlinClassProcessor = DEFAULT_CLASS_PROCESSOR): T {
        val objectValue: T
        try {
            val inputStream = ByteArrayInputStream(xml.toByteArray(Charsets.UTF_8))
            val document = parse(inputStream)
            val root = document.documentElement
            objectValue = xmlToObject(root, kClass, mapping, classProcessor)
        } catch (e: Exception) {
            throw JavaXmlUtilException(e)
        }
        return objectValue
    }

    private fun nodeToString(node: Node): String {
        val childNodeList = node.childNodes
        return if (childNodeList.length == 0) {
            node.textContent.nullToBlank()
        } else {
            val nodeString = StringBuilder()
            nodeString.append(Constants.Symbol.LESS_THAN + node.nodeName + Constants.Symbol.GREATER_THAN)
            for (index in 0 until childNodeList.length) {
                val childNode = childNodeList.item(index) ?: continue
                nodeString.append(nodeToString(childNode))
            }
            nodeString.append(Constants.Symbol.LESS_THAN + Constants.Symbol.SLASH_LEFT + node.nodeName + Constants.Symbol.GREATER_THAN)
            nodeString.toString()
        }
    }


    class JavaXmlUtilException : Exception {
        constructor(message: String) : super(message)
        constructor(cause: Throwable) : super(cause)
        constructor(message: String, cause: Throwable) : super(message, cause)
    }
}
