package com.oneliang.ktx.util.jxl

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.nullToBlank

class JxlMappingBean {
    companion object {
        const val TAG_BEAN = "bean"
        const val USE_FOR_IMPORT = "import"
        const val USE_FOR_EXPORT = "export"
    }
    /**
     * @return the useFor
     */
    /**
     * @param useFor the useFor to set
     */
    var useFor = USE_FOR_IMPORT
    /**
     * @return the type
     */
    /**
     * @param type the type to set
     */
    var type: String = Constants.String.BLANK
    val jxlMappingColumnBeanList = mutableListOf<JxlMappingColumnBean>()
    private val jxlMappingColumnBeanMap = mutableMapOf<String, JxlMappingColumnBean>()

    /**
     * get header
     * @param field
     * @return header
     */
    fun getHeader(field: String): String {
        val jxlMappingColumnBean = this.jxlMappingColumnBeanMap[field]
        return jxlMappingColumnBean?.header.nullToBlank()
    }

    /**
     * get field
     * @param header
     * @return field
     */
    @Deprecated("Deprecated")
    private fun getField(header: String): String {
        var field: String = Constants.String.BLANK
        for (jxlMappingColumnBean in jxlMappingColumnBeanList) {
            val columnHeader = jxlMappingColumnBean.header
            if (columnHeader == header) {
                field = jxlMappingColumnBean.field
                break
            }
        }
        return field
    }

    /**
     * get index
     * @param field
     * @return field index
     */
    fun getIndex(field: String): Int {
        val jxlMappingColumnBean = this.jxlMappingColumnBeanMap[field]
        return jxlMappingColumnBean?.index ?: -1
    }

    /**
     * @param jxlMappingColumnBean
     * @return boolean
     */
    fun addJxlMappingColumnBean(jxlMappingColumnBean: JxlMappingColumnBean): Boolean {
        if (jxlMappingColumnBean.field.isBlank()) {
            error("jxl mapping column field can not blank")
        }
        this.jxlMappingColumnBeanMap[jxlMappingColumnBean.field] = jxlMappingColumnBean
        return this.jxlMappingColumnBeanList.add(jxlMappingColumnBean)
    }
}
