package com.oneliang.ktx.util.common

/**
 * is chinese
 */
fun Char.isChinese(): Boolean {
    val unicodeBlock: Character.UnicodeBlock = Character.UnicodeBlock.of(this)
//            println(unicodeBlock)
    return (unicodeBlock === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || unicodeBlock === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || unicodeBlock === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
}