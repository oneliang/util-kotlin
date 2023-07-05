package com.oneliang.ktx.util.common

/**
 * is chinese
 */
fun Char.isChinese(): Boolean {
    val unicodeBlock: Character.UnicodeBlock = Character.UnicodeBlock.of(this)
    return (unicodeBlock === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || unicodeBlock === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || unicodeBlock === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
}

/**
 * is chinese symbol
 */
fun Char.isChineseSymbol(): Boolean {
    val unicodeBlock: Character.UnicodeBlock = Character.UnicodeBlock.of(this)
    return (unicodeBlock === Character.UnicodeBlock.GENERAL_PUNCTUATION
            || unicodeBlock === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            || unicodeBlock === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
            || unicodeBlock === Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
            || unicodeBlock === Character.UnicodeBlock.VERTICAL_FORMS)
}

/**
 * is english symbol
 */
fun Char.isEnglishSymbol(): Boolean {
    val charCode = this.code
    if (charCode in 0x20..0x2F) return true
    if (charCode in 0x3A..0x40) return true
    if (charCode in 0x5E..0x60) return true
    if (charCode == 0x7E) return true
    return false
}

fun Char.isSymbol(): Boolean {
    return this.isEnglishSymbol() || this.isChineseSymbol()
}

fun main() {
    println('你'.isSymbol())
}