package com.oneliang.ktx.test

interface Element<P : Props> {
    //    operator fun invoke(block: P.() -> Unit){}
    var id: String
    var properties: P
    operator fun <P : Props> Element<P>.invoke(block: Element<P>.() -> Unit)

    val children: List<Element<*>>
}

interface  Props{
    fun create():Props
}
//interface Builder {
//
//    operator fun <P : Props> Element<P>.invoke(block: Builder.(props:P) -> Unit)
//}
//
//class BuilderImpl : Builder {
//    private val children: MutableList<Element<*>> = mutableListOf()
//    override fun <P : Props> Element<P>.invoke(block: Builder.(props:P) -> Unit) {
//        children.add(this)
//    }
//}

//class PropsImpl : Props
class ElementImpl<P : Props>(override var id: String) : Element<P> {

    private val childElementList: MutableList<Element<*>> = mutableListOf()
    override lateinit var properties: P
    override fun <P : Props> Element<P>.invoke(block: Element<P>.() -> Unit) {
        this@ElementImpl.childElementList.add(this)
        block(this)
    }

    override val children: List<Element<*>>
        get() = this.childElementList
}

inline fun <reified P : Props> element(id: String = "", block: Element<P>.(props: P) -> Unit): Element<P> {
    val element = ElementImpl<P>(id)
    val properties = P::class.java.newInstance()
    element.properties = properties
    block(element, properties)
    return element
}

val a = element<Props>("a") {
    it
}
val b = element<Props>("b") {

}
fun main() {

    val root = element<Props>("root") {
        a {
            b {

            }
        }
    }
}
