package com.oneliang.ktx.util.common

class CircleIterator<out T>(elements: Array<T>, initialIndex: Int = 0) : Iterator<T> {

    private val headNode: Node<T>
    private var currentNode: Node<T>

    init {
        if (elements.isEmpty()) {
            error("parameter[elements] can not be empty")
        }
        this.headNode = Node(elements[0])
        this.headNode.nextNode = this.headNode
        this.currentNode = this.headNode
        var previousNode = this.headNode
        for (i in 1 until elements.size) {
            val element = elements[i]
            val node = Node(element).also { it.nextNode = this.headNode }
            previousNode.nextNode = node
            previousNode = node
            if (i == initialIndex) {
                this.currentNode = node
            }
        }
    }

    /**
     * next
     * @return T
     */
    override fun next(): T {
        val value = this.currentNode.value
        this.currentNode = this.currentNode.nextNode
        return value
    }

    /**
     * has next
     * @return Boolean always true in circle iterator
     */
    override fun hasNext(): Boolean {
        return true // this.currentNode != null
    }

    /**
     * inner static class
     */
    private class Node<T>(val value: T) {
        lateinit var nextNode: Node<T>
    }
}