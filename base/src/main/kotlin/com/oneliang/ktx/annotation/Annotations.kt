package com.oneliang.ktx.annotation

import com.oneliang.ktx.Constants

annotation class Warning(val value: String = Constants.String.BLANK)

annotation class ThreadSafe(val value: String = Constants.String.BLANK)

annotation class ThreadUnsafe(val value: String = Constants.String.BLANK)