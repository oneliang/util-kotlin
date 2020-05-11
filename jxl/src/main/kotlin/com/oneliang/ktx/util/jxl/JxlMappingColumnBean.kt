package com.oneliang.ktx.util.jxl

import com.oneliang.ktx.Constants

class JxlMappingColumnBean {
    companion object {
        const val TAG_COLUMN = "column"
    }
    /**
     * @return the field
     */
    /**
     * @param field the field to set
     */
    var field: String = Constants.String.BLANK
    /**
     * @return the header
     */
    /**
     * @param header the header to set
     */
    var header: String = Constants.String.BLANK
    /**
     * @return the index
     */
    /**
     * @param index the index to set
     */
    var index = -1
}
