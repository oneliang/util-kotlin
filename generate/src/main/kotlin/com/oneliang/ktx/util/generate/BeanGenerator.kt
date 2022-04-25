package com.oneliang.ktx.util.generate

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.replaceAllSlashToLeft
import java.io.File

object BeanGenerator {

    fun generate(
        beanFileList: List<String>,
        beanTemplateFullFilename: String,
        baseOutputDirectory: String
    ) {
        val option = Template.Option().apply {
            this.removeBlankLine = true
//        this.rewrite = false
        }
        beanFileList.forEach { fullFilename ->
            val beanDescriptionList = BeanDescription.buildListFromFile(fullFilename)
            beanDescriptionList.forEach { beanDescription ->
                val packageName = beanDescription.packageName
                val className = beanDescription.className
                val outputDirectory = File(baseOutputDirectory).absolutePath.replaceAllSlashToLeft() + Constants.Symbol.SLASH_LEFT + packageName.replace(Constants.Symbol.DOT, Constants.Symbol.SLASH_LEFT)
                option.instance = beanDescription
                Template.generate(beanTemplateFullFilename, outputDirectory + Constants.Symbol.SLASH_LEFT + "${className}.kt", option)
            }
        }
    }
}