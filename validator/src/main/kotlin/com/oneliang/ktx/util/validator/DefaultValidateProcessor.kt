package com.oneliang.ktx.util.validator

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.KotlinClassUtil
import com.oneliang.ktx.util.common.getterOrIsMethodInvoke
import com.oneliang.ktx.util.common.matches
import java.lang.reflect.Field

open class DefaultValidateProcessor : Validator.ValidateProcessor {

    /**
     * validate processor
     * @param instance
     * @param field
     * @return ViolateConstrain
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun validateProcess(instance: Any, field: Field): Validator.ViolateConstraint? {
        var violateConstraint: Validator.ViolateConstraint? = null
        val fieldName = field.name
        if (field.isAnnotationPresent(Numeric::class.java)) {
            val numeric = field.getAnnotation(Numeric::class.java)
            val fieldValue = instance.getterOrIsMethodInvoke(fieldName)
            if (fieldValue != null) {
                val value: Long = KotlinClassUtil.changeType(Long::class, arrayOf(fieldValue.toString())) ?: 0L
                val min = numeric.min
                val max = numeric.max
                when {
                    min > max -> {
                        error("min:%s is larger then max:%s".format(min, max))
                    }
                    value < min -> {
                        violateConstraint = Validator.ViolateConstraint(fieldName, "The field:[%s] is violate constraint, value:[%s] must >= %s".format(fieldName, value, min))
                    }
                    value > max -> {
                        violateConstraint = Validator.ViolateConstraint(fieldName, "The field:[%s] is violate constraint, value:[%s] must <= %s".format(fieldName, value, max))
                    }
                }
            } else {
                val nullable = numeric.nullable
                if (!nullable) {
                    violateConstraint = Validator.ViolateConstraint(fieldName, "The field:[%s] is violate constraint, can not be null".format(fieldName))
                }
            }
        } else if (field.isAnnotationPresent(Decimal::class.java)) {
            val decimal = field.getAnnotation(Decimal::class.java)
            val fieldValue = instance.getterOrIsMethodInvoke(fieldName)
            if (fieldValue != null) {
                val value: Double = KotlinClassUtil.changeType(Double::class, arrayOf(fieldValue.toString())) ?: 0.0
                val min = decimal.min
                val max = decimal.max
                when {
                    min > max -> {
                        error("min:%s is larger then max:%s".format(min, max))
                    }
                    value < min -> {
                        violateConstraint = Validator.ViolateConstraint(fieldName, "The field:[%s] is violate constraint, value:[%s] must >= %s".format(fieldName, value, min))
                    }
                    value > max -> {
                        violateConstraint = Validator.ViolateConstraint(fieldName, "The field:[%s] is violate constraint, value:[%s] must <= %s".format(fieldName, value, max))
                    }
                }
            } else {
                val nullable = decimal.nullable
                if (!nullable) {
                    violateConstraint = Validator.ViolateConstraint(fieldName, "The field:[%s] is violate constraint, can not be null".format(fieldName))
                }
            }
        } else if (field.isAnnotationPresent(Length::class.java)) {
            val length = field.getAnnotation(Length::class.java)
            val fieldValue = instance.getterOrIsMethodInvoke(fieldName)
            if (fieldValue != null) {
                val value: String = KotlinClassUtil.changeType(String::class, arrayOf(fieldValue.toString())) ?: Constants.String.BLANK
                val min = length.min
                val max = length.max
                when {
                    min > max -> {
                        error("min:%s is larger then max:%s".format(min, max))
                    }
                    value.length < min -> {
                        violateConstraint = Validator.ViolateConstraint(fieldName, "The field:[%s] is violate constraint, value:[%s]'s length must >= %s".format(fieldName, value, min))
                    }
                    value.length > max -> {
                        violateConstraint = Validator.ViolateConstraint(fieldName, "The field:[%s] is violate constraint, value:[%s]'s length must <= %s".format(fieldName, value, max))
                    }
                }
            } else {
                val nullable = length.nullable
                if (!nullable) {
                    violateConstraint = Validator.ViolateConstraint(fieldName, "The field:[%s] is violate constraint, can not be null".format(fieldName))
                }
            }
        } else if (field.isAnnotationPresent(Regex::class.java)) {
            val regex = field.getAnnotation(Regex::class.java)
            val fieldValue = instance.getterOrIsMethodInvoke(fieldName)
            if (fieldValue != null) {
                val value: String = KotlinClassUtil.changeType(String::class, arrayOf(fieldValue.toString())) ?: Constants.String.BLANK
                val regexArray = regex.value
                for (regexValue in regexArray) {
                    if (!value.matches(regexValue)) {
                        violateConstraint = Validator.ViolateConstraint(fieldName, "The field:[%s] is violate constraint, value:[%s] mush match regex value:%s".format(fieldName, fieldValue, regexValue))
                        break
                    }
                }
            } else {
                val nullable = regex.nullable
                if (!nullable) {
                    violateConstraint = Validator.ViolateConstraint(fieldName, "The field:[%s] is violate constraint, can not be null".format(fieldName))
                }
            }
        } else if (field.isAnnotationPresent(Constraint::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val constraint = field.getAnnotation(Constraint::class.java) as Constraint<Any, ConstraintValidator<Any>>
            val fieldValue = instance.getterOrIsMethodInvoke(fieldName)
            if (fieldValue != null) {
                violateConstraint = constraint.validatedBy.java.newInstance().validate(fieldName, fieldValue)
            } else {
                val nullable = constraint.nullable
                if (!nullable) {
                    violateConstraint = Validator.ViolateConstraint(fieldName, "The field:[%s] is violate constraint, can not be null".format(fieldName))
                }
            }
        }
        return violateConstraint
    }
}