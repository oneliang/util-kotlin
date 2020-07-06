package com.oneliang.ktx.test

import com.oneliang.ktx.util.file.ZipUtil
import java.util.zip.ZipEntry

fun main() {
    val outputZipFile = "/D:/Dandelion/server/apache-tomcat-9.0.13/webapps/api-bi-test.war"
    val inputZipFile = "/D:/Dandelion/server/apache-tomcat-9.0.13/webapps/api-bi.war"
    ZipUtil.zip(outputZipFile, inputZipFile, listOf(ZipUtil.ZipEntryPath(
            fullFilename = "/D:/Dandelion/mix/metal-team/api-bi/src/test/resources/config/xml/ioc/system-ioc.xml",
            zipEntry = ZipEntry("WEB-INF/classes/config/xml/ioc/system-ioc.xml"),
            replace = true
    )))
}