package com.oneliang.ktx.test.common

import com.oneliang.ktx.util.common.DefaultKotlinClassProcessor
import com.oneliang.ktx.util.common.JavaXmlUtil
import com.oneliang.ktx.util.common.KotlinClassUtil
import kotlin.reflect.KClass

fun main() {
    val xml = "<xml><ToUserName><![CDATA[wweb464df7a393a458]]></ToUserName><FromUserName><![CDATA[sys]]></FromUserName><CreateTime>1578967772</CreateTime><MsgType><![CDATA[event]]></MsgType><Event><![CDATA[sys_approval_change]]></Event><AgentID>3010040</AgentID><ApprovalInfo><SpNo>202001140001</SpNo><SpName><![CDATA[出差]]></SpName><SpStatus>4</SpStatus><TemplateId><![CDATA[Bs5KJAMyd51eMjFvoBG6ii2Q5QgXNLvJSaQ7arRr6]]></TemplateId><ApplyTime>1578967166</ApplyTime><Applyer><UserId><![CDATA[LuoZhouPeng]]></UserId><Party><![CDATA[1]]></Party></Applyer><SpRecord><SpStatus>1</SpStatus><ApproverAttr>1</ApproverAttr><Details><Approver><UserId><![CDATA[XiaoHuo]]></UserId></Approver><Speech><![CDATA[]]></Speech><SpStatus>1</SpStatus><SpTime>0</SpTime></Details></SpRecord><StatuChangeEvent>6</StatuChangeEvent></ApprovalInfo></xml>"

    val apiApprovalNoticeRequest = JavaXmlUtil.xmlToObject(xml, ApiApprovalNoticeRequest::class, mapOf("toUserName" to "ToUserName", "suiteId" to "ApprovalInfo"), object : DefaultKotlinClassProcessor() {
        override fun <T : Any> changeClassProcess(kClass: KClass<T>, values: Array<String>, fieldName: String): Any? {
            val classType = KotlinClassUtil.getClassType(kClass)
            println(classType.toString() + "," + fieldName + "," + values[0])
            return if (classType != null) {
                super.changeClassProcess(kClass, values, fieldName)
            } else {
                println(fieldName + "," + values[0])
                null
            }
        }
    })
    println(apiApprovalNoticeRequest.toUserName)
    println(apiApprovalNoticeRequest.suiteId)
}