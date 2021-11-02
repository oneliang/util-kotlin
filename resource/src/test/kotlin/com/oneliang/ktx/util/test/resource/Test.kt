package com.oneliang.ktx.util.test.resource

import com.oneliang.ktx.util.common.toUtilDate

fun main() {
    val itemList = listOf(
        ResourceBenefit.Item("A", ResourceBenefit.Item.Type.PLAN, "2021-01-01 00:00:00".toUtilDate().time, 100.0, 0.01),
        ResourceBenefit.Item("B", ResourceBenefit.Item.Type.PLAN, "2021-01-02 00:00:00".toUtilDate().time, 200.0, 0.01),
        ResourceBenefit.Item("A", ResourceBenefit.Item.Type.ACTUAL, "2021-01-03 00:00:00".toUtilDate().time, 30.0, 0.00),
        ResourceBenefit.Item("A", ResourceBenefit.Item.Type.ACTUAL, "2021-01-05 00:00:00".toUtilDate().time, 50.0, 0.00),
        ResourceBenefit.Item("B", ResourceBenefit.Item.Type.ACTUAL, "2021-01-04 00:00:00".toUtilDate().time, 100.0, 0.00),
//        ResourceBenefit.Item("C", ResourceBenefit.Item.Type.ACTUAL, "2021-01-03 00:00:00".toUtilDate().time, 100.0, 0.00)
    )
    ResourceBenefit.calculateBenefit(itemList, "2021-01-05 00:00:00".toUtilDate().time, ResourceBenefit.Type.PLAN_IN)
}