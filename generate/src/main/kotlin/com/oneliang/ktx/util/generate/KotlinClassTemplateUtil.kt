package com.oneliang.ktx.util.generate

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.nullToBlank
import com.oneliang.ktx.util.common.toFile
import com.oneliang.ktx.util.common.toJavaXmlDocument
import java.io.File

object KotlinClassTemplateUtil {

    fun buildKotlinClassTemplateBeanListFromXml(kotlinClassXml: String): List<KotlinClassTemplateBean> {
        val kotlinClassXmlFile = kotlinClassXml.toFile()
        return buildKotlinClassTemplateBeanListFromXml(kotlinClassXmlFile)
    }

    fun buildKotlinClassTemplateBeanListFromXml(kotlinClassXmlFile: File): List<KotlinClassTemplateBean> {
        if (!kotlinClassXmlFile.exists() || !kotlinClassXmlFile.isFile) {
            error("xml does not exists or is not a file, input file [%s]".format(kotlinClassXmlFile.absolutePath))
        }
        val document = kotlinClassXmlFile.toJavaXmlDocument()
        val root = document.documentElement
        val kotlinClassElementList = root.getElementsByTagName(KotlinClassTemplateBean.TAG_KOTLIN_CLASS)
        val kotlinClassTemplateBeanList = mutableListOf<KotlinClassTemplateBean>()
        for (index in 0 until kotlinClassElementList.length) {
            val kotlinClassTemplateBean = KotlinClassTemplateBean()
            val kotlinClassNode = kotlinClassElementList.item(index)
            val kotlinClassAttributeMap = kotlinClassNode.attributes
            kotlinClassTemplateBean.packageName = kotlinClassAttributeMap.getNamedItem(KotlinClassTemplateBean.ATTRIBUTE_KOTLIN_CLASS_PACKAGE_NAME)?.nodeValue ?: Constants.String.BLANK
            kotlinClassTemplateBean.className = kotlinClassAttributeMap.getNamedItem(KotlinClassTemplateBean.ATTRIBUTE_KOTLIN_CLASS_CLASS_NAME)?.nodeValue ?: Constants.String.BLANK
            kotlinClassTemplateBean.superClassNames = kotlinClassAttributeMap.getNamedItem(KotlinClassTemplateBean.ATTRIBUTE_KOTLIN_CLASS_SUPER_CLASS_NAMES)?.nodeValue ?: Constants.String.BLANK
            val importHashSet = hashSetOf<String>()
            val columnList = mutableListOf<KotlinClassTemplateBean.Field>()
            val codeInClassList = mutableListOf<String>()
            val kotlinClassChildNodeList = kotlinClassNode.childNodes
            for (kotlinClassChildNodeIndex in 0 until kotlinClassChildNodeList.length) {
                val kotlinClassChildNode = kotlinClassChildNodeList.item(kotlinClassChildNodeIndex)
                val kotlinClassChildNodeAttributeMap = kotlinClassChildNode.attributes
                when (kotlinClassChildNode.nodeName) {
                    KotlinClassTemplateBean.TAG_KOTLIN_CLASS_IMPORT -> {
                        importHashSet += kotlinClassChildNodeAttributeMap.getNamedItem(KotlinClassTemplateBean.ATTRIBUTE_KOTLIN_CLASS_IMPORT_VALUE).nodeValue
                    }
                    KotlinClassTemplateBean.TAG_KOTLIN_CLASS_FIELD -> {
                        //model field
                        val field = KotlinClassTemplateBean.Field()
                        val overrideNode = kotlinClassChildNodeAttributeMap.getNamedItem(KotlinClassTemplateBean.ATTRIBUTE_KOTLIN_CLASS_FIELD_OVERRIDE)
                        field.override = overrideNode?.nodeValue?.toBoolean() ?: false
                        field.name = kotlinClassChildNodeAttributeMap.getNamedItem(KotlinClassTemplateBean.ATTRIBUTE_KOTLIN_CLASS_FIELD_NAME)?.nodeValue ?: Constants.String.BLANK
                        val type = kotlinClassChildNodeAttributeMap.getNamedItem(KotlinClassTemplateBean.ATTRIBUTE_KOTLIN_CLASS_FIELD_TYPE)?.nodeValue ?: field.type
                        field.type = type
                        val nullableNode = kotlinClassChildNodeAttributeMap.getNamedItem(KotlinClassTemplateBean.ATTRIBUTE_KOTLIN_CLASS_FIELD_NULLABLE)
                        field.nullable = nullableNode?.nodeValue?.toBoolean() ?: false
                        field.defaultValue = kotlinClassChildNodeAttributeMap.getNamedItem(KotlinClassTemplateBean.ATTRIBUTE_KOTLIN_CLASS_FIELD_DEFAULT_VALUE)?.nodeValue?.nullToBlank() ?: Constants.String.BLANK

                        columnList += field
                    }
                    KotlinClassTemplateBean.TAG_KOTLIN_CLASS_CODE_IN_CLASS -> {
                        codeInClassList += kotlinClassChildNode.textContent
                    }
                }
            }
            kotlinClassTemplateBean.importArray = importHashSet.toTypedArray()
            kotlinClassTemplateBean.fieldArray = columnList.toTypedArray()
            kotlinClassTemplateBean.codeInClassArray = codeInClassList.toTypedArray()

            kotlinClassTemplateBeanList += kotlinClassTemplateBean
        }
        return kotlinClassTemplateBeanList
    }
}