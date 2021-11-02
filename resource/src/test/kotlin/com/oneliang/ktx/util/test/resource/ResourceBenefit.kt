package com.oneliang.ktx.util.test.resource

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.differs
import com.oneliang.ktx.util.common.toFormatString
import com.oneliang.ktx.util.common.toUtilDate
import com.oneliang.ktx.util.logging.LoggerManager

object ResourceBenefit {

    private val logger = LoggerManager.getLogger(ResourceBenefit::class)

    enum class Type {
        PLAN_IN, PLAN_OUT
    }

    fun calculateBenefit(itemList: List<Item>, endTime: Long = 0L, type: Type = Type.PLAN_OUT) {
        if (itemList.isEmpty()) {
            error("item list is empty")
        }
        val negative = type == Type.PLAN_IN
        val planItemList = mutableListOf<Item>()
        val actualItemList = mutableListOf<Item>()
        val planItemKeyMap = mutableMapOf<String, String>()
        val actualItemKeyMap = mutableMapOf<String, String>()
        itemList.forEach {
            when (it.type) {
                Item.Type.PLAN -> {
                    if (endTime == 0L || (endTime > 0 && it.time <= endTime)) {
                        planItemList += it
                        planItemKeyMap[it.key] = it.key
                    }
                }
                Item.Type.ACTUAL -> {
                    if (endTime == 0L || (endTime > 0 && it.time <= endTime)) {
                        actualItemList += it
                        actualItemKeyMap[it.key] = it.key
                    }
                }
            }
        }

        //one plan, multi actual
        val sortedPlanItemList = planItemList.sortedBy { it.time }
        val sortedActualItemList = actualItemList.sortedBy { it.time }
        val actualItemMap = sortedActualItemList.groupBy { it.key }
        var globalBeginTime = 0L
        var globalEndTime = 0L//if (endTime > 0) endTime else lastActualItem.time
        if (sortedPlanItemList.isNotEmpty() && sortedActualItemList.isEmpty()) {
            val firstItem = sortedPlanItemList.first()
            globalBeginTime = firstItem.time
            globalEndTime = endTime
        } else if (sortedPlanItemList.isEmpty() && sortedActualItemList.isNotEmpty()) {
            val firstItem = sortedActualItemList.first()
            globalBeginTime = firstItem.time
            globalEndTime = endTime
        } else if (sortedPlanItemList.isNotEmpty() && sortedActualItemList.isNotEmpty()) {
            val firstItem = sortedPlanItemList.first()
            val lastItem = sortedActualItemList.last()
            globalBeginTime = firstItem.time
            globalEndTime = if (endTime > 0) endTime else lastItem.time
        } else {//all empty
            error("plan item list and actual item list are all empty, end time:$endTime")
        }
        logger.verbose("global begin time:%s, global end time:%s", globalBeginTime, globalEndTime)

        var totalRateValue = 0.0
        sortedPlanItemList.forEach {
            val planBeginTime = it.time
            val subActualItemList = actualItemMap[it.key]//had sorted
            if (subActualItemList == null) {
                logger.warning("plan:%s, no actual item", it.key)
            } else {
                var planTotalActualValue = 0.0
                var planTotalRateValue = 0.0
                subActualItemList.forEach { actualItem ->
                    //between plan to actual
                    val duration = actualItem.time - planBeginTime
                    planTotalActualValue += actualItem.value
                    val rateValue = it.calculateRateValue(actualItem.value, duration)
                    logger.verbose("item:%s, addition value:%s", actualItem, rateValue)
                    planTotalRateValue += if (negative) -rateValue else rateValue
                    //between actual to end, negative benefit
                    val actualDuration = globalEndTime - actualItem.time
                    val actualRateValue = actualItem.calculateRateValue(actualItem.value, actualDuration)
                    planTotalRateValue -= if (negative) -actualRateValue else actualRateValue
                    logger.verbose("item:%s, negative addition value:%s", actualItem, actualRateValue)
                }
                when {
                    planTotalActualValue > it.value -> {
                        error("it is wrong, total actual value can not > plan value")
                    }
                    it.value == planTotalActualValue -> {//plan == actual

                    }
                    else -> {//total actual value < plan value
                        val leastValue = it.value - planTotalActualValue
                        val duration = globalEndTime - it.time
                        val rateValue = it.calculateRateValue(leastValue, duration)
                        logger.verbose("least value:%s, addition value:%s", leastValue, rateValue)
                        planTotalRateValue += if (negative) -rateValue else rateValue
                    }
                }
                totalRateValue += planTotalRateValue
                logger.verbose("plan total rate value:%s", planTotalRateValue)
            }
        }
        val differActualItemKeyList = actualItemKeyMap.differs(planItemKeyMap)
        differActualItemKeyList.forEach {
            val subActualItemList = actualItemMap[it]//had sorted
            if (subActualItemList == null) {
                logger.warning("it is impossible, maybe a bug, please check it")
            } else {
                subActualItemList.forEach { actualItem ->
                    //between actual to end, negative benefit
                    val actualDuration = globalEndTime - actualItem.time
                    val actualRateValue = actualItem.calculateRateValue(actualItem.value, actualDuration)
                    totalRateValue -= if (negative) -actualRateValue else actualRateValue
                    logger.verbose("item:%s, negative addition value:%s", actualItem, actualRateValue)
                }
            }
        }

        logger.verbose("total rate value:%s", totalRateValue)
        val benefit = totalRateValue / ((globalEndTime - globalBeginTime).toDouble() / Constants.Time.MILLISECONDS_OF_DAY)
        logger.verbose("benefit:%s", benefit)
    }

    class Item(
        val key: String,
        val type: Type,
        val time: Long,
        val value: Double,
        private val ratePerDay: Double = 0.0
    ) {
        enum class Type {
            PLAN, ACTUAL
        }

        override fun toString(): String {
            return "[key=${this.key}, type=${this.type}, time=${this.time.toUtilDate().toFormatString()}, value=${this.value}]"
        }

        fun calculateRateValue(value: Double, duration: Long): Double {
            return value * this.ratePerDay * (duration.toDouble() / Constants.Time.MILLISECONDS_OF_DAY)
        }
    }
}