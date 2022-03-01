package com.oneliang.ktx.util.validator

import java.lang.reflect.Field

object Validator {
    val DEFAULT_VALIDATE_PROCESSOR: ValidateProcessor = DefaultValidateProcessor()

    /**
     * validate
     * @param instance
     * @param validateProcessor
     * @return List<ViolateConstrain>
     * @throws Exception
    </ViolateConstrain> */
    @Throws(Exception::class)
    fun validate(instance: Any, validateProcessor: ValidateProcessor = DEFAULT_VALIDATE_PROCESSOR): List<ViolateConstraint> {
        val fields = instance.javaClass.declaredFields ?: return emptyList()
        val violateConstrainList = mutableListOf<ViolateConstraint>()
        for (field in fields) {
            val violateConstrain = validateProcessor.validateProcess(instance, field)
            if (violateConstrain != null) {
                violateConstrainList.add(violateConstrain)
            }
        }
        return violateConstrainList
    }

    interface ValidateProcessor {
        /**
         * validate processor
         * @param instance
         * @param field
         * @return ViolateConstraint
         * @throws Exception
         */
        @Throws(Exception::class)
        fun validateProcess(instance: Any, field: Field): ViolateConstraint?
    }

    class ViolateConstraint(val fieldName: String, val result: String)
}