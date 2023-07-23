package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants

/**
 * plus
 * only support short, int, long, float, double
 */
fun Number.addNumber(other: Number): Number {
    return this.baseMathOperate(other, BaseMathOperateType.PLUS)
}

/**
 * minus
 * only support short, int, long, float, double
 */
fun Number.minusNumber(other: Number): Number {
    return this.baseMathOperate(other, BaseMathOperateType.MINUS)
}

/**
 * times, multiply
 * only support short, int, long, float, double
 */
fun Number.multiplyNumber(other: Number): Number {
    return this.baseMathOperate(other, BaseMathOperateType.MULTIPLY)
}

/**
 * divide
 * only support short, int, long, float, double
 */
fun Number.divideNumber(other: Number): Number {
    return this.baseMathOperate(other, BaseMathOperateType.DIVIDE)
}

/**
 * compare to
 * only support short, int, long, float, double
 */
operator fun Number.compareTo(number: Number): Int {
    return this.baseMathCompareTo(number)
}


private enum class BaseMathOperateType(val value: String) {
    PLUS(Constants.Symbol.PLUS),
    MINUS(Constants.Symbol.MINUS),
    MULTIPLY(Constants.Symbol.WILDCARD),
    DIVIDE(Constants.Symbol.SLASH_LEFT)
}

/**
 * only support short, int, long, float, double
 */
private fun Number.baseMathOperate(other: Number, baseMathOperateType: BaseMathOperateType): Number {
    return when {
        this is Short && other is Short -> {
            when (baseMathOperateType) {
                BaseMathOperateType.PLUS -> {
                    this + other
                }

                BaseMathOperateType.MINUS -> {
                    this - other
                }

                BaseMathOperateType.MULTIPLY -> {
                    this * other
                }

                BaseMathOperateType.DIVIDE -> {
                    this / other
                }
            }
        }

        this is Int && other is Int -> {
            when (baseMathOperateType) {
                BaseMathOperateType.PLUS -> {
                    this + other
                }

                BaseMathOperateType.MINUS -> {
                    this - other
                }

                BaseMathOperateType.MULTIPLY -> {
                    this * other
                }

                BaseMathOperateType.DIVIDE -> {
                    this / other
                }
            }
        }

        this is Long && other is Long -> {
            when (baseMathOperateType) {
                BaseMathOperateType.PLUS -> {
                    this + other
                }

                BaseMathOperateType.MINUS -> {
                    this - other
                }

                BaseMathOperateType.MULTIPLY -> {
                    this * other
                }

                BaseMathOperateType.DIVIDE -> {
                    this / other
                }
            }
        }

        this is Float && other is Float -> {
            when (baseMathOperateType) {
                BaseMathOperateType.PLUS -> {
                    this + other
                }

                BaseMathOperateType.MINUS -> {
                    this - other
                }

                BaseMathOperateType.MULTIPLY -> {
                    this * other
                }

                BaseMathOperateType.DIVIDE -> {
                    this / other
                }
            }
        }

        this is Double && other is Double -> {
            when (baseMathOperateType) {
                BaseMathOperateType.PLUS -> {
                    this + other
                }

                BaseMathOperateType.MINUS -> {
                    this - other
                }

                BaseMathOperateType.MULTIPLY -> {
                    this * other
                }

                BaseMathOperateType.DIVIDE -> {
                    this / other
                }
            }
        }

        else -> {
            error("only support short, int, long, float, double")
        }
    }
}

private fun Number.baseMathCompareTo(other: Number): Int {
    return when {
        this is Short && other is Short -> {
            this.compareTo(other)
        }

        this is Int && other is Int -> {
            this.compareTo(other)
        }

        this is Long && other is Long -> {
            this.compareTo(other)
        }

        this is Float && other is Float -> {
            this.compareTo(other)
        }

        this is Double && other is Double -> {
            this.compareTo(other)
        }

        else -> {
            error("only support short, int, long, float, double")
        }
    }
}