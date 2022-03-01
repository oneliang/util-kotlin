package com.oneliang.ktx.util.validator

fun Any.validate(validateProcessor: Validator.ValidateProcessor = Validator.DEFAULT_VALIDATE_PROCESSOR) = Validator.validate(this, validateProcessor)

fun Any.validateSimply(validateProcessor: Validator.ValidateProcessor = Validator.DEFAULT_VALIDATE_PROCESSOR) = this.validate(validateProcessor).isEmpty()