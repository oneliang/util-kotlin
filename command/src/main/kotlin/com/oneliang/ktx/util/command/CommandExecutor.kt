package com.oneliang.ktx.util.command

interface CommandExecutor {

    /**
     * execute command
     * @param args
     */
    fun executeCommand(args: Array<String>)
}
