package com.oneliang.ktx.util.command

import com.oneliang.ktx.Constants
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class CommandLine(private val maxCommandKeyLength: Int) {
    companion object {
        /**
         * parse
         * @param command
         * @return String[]
         */
        fun parse(command: String): Array<String> {
            if (command.isBlank()) {
                return emptyArray()
            }
            val argumentList = ArrayList<String>()
            val array = command.split(Constants.String.SPACE).toTypedArray()
            for (string in array) {
                if (string.isBlank()) {
                    continue
                }
                argumentList.add(string.trim())
            }
            return argumentList.toTypedArray()
        }

        /**
         * to command key
         *
         * @param commandArray
         * @return String
         */
        fun toCommandKey(commandArray: Array<String>): String {
            if (commandArray.isEmpty()) {
                return Constants.String.BLANK
            }
            val stringBuilder = StringBuilder()
            for (command in commandArray) {
                stringBuilder.append(command.trim())
                stringBuilder.append(Constants.String.SPACE)
            }
            val length = stringBuilder.length
            if (length > 0) {
                stringBuilder.delete(length - 1, length)
            }
            return stringBuilder.toString()
        }
    }

    private val commandExecutorMap = ConcurrentHashMap<String, CommandExecutor>()

    /**
     * add command executor
     *
     * @param commandKey
     * @param commandExecutor
     */
    fun addCommandExecutor(commandKey: String, commandExecutor: CommandExecutor) {
        this.commandExecutorMap[commandKey] = commandExecutor
    }

    /**
     * execute
     *
     * @param args
     */
    fun execute(args: Array<String>) {
        if (args.isEmpty()) {
            return
        }

        for (i in this.maxCommandKeyLength downTo 1) {
            val commandArray = Array<String>(i) { Constants.String.BLANK }
            for (j in 0 until i) {
                commandArray[j] = args[j]
            }
            val commandKey = toCommandKey(commandArray)
            if (!this.commandExecutorMap.containsKey(commandKey)) {
                continue
            }
            val commandExecutor = this.commandExecutorMap[commandKey]
            commandExecutor?.executeCommand(args)
            break
        }
    }

    /**
     * destroy
     */
    fun destroy() {
        this.commandExecutorMap.clear()
    }
}
