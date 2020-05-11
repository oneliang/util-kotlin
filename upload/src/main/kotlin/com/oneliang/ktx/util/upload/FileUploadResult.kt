package com.oneliang.ktx.util.upload

import java.io.Serializable

class FileUploadResult : Serializable {

    /**
     * @return the success
     */
    /**
     * @param success the success to set
     */
    var isSuccess = false
    /**
     * @return the originalFilename
     */
    /**
     * @param originalFilename the originalFilename to set
     */
    var originalFilename: String? = null
    /**
     * @return the filename
     */
    /**
     * @param filename the filename to set
     */
    var filename: String? = null
    /**
     * @return the filePath
     */
    /**
     * @param filePath the filePath to set
     */
    var filePath: String? = null
}
