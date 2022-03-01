package com.oneliang.ktx.util.test.validator

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.isEmail
import com.oneliang.ktx.util.validator.*

class Email : ConstraintValidator<String> {
    override fun validate(fieldName: String, fieldValue: String): Validator.ViolateConstraint? {
        return if (fieldValue.isEmail()) {
            null
        } else {
            Validator.ViolateConstraint(fieldName, "not email")
        }
    }
}

class TestModel {

    @Length(min = 1, max = 2)
    var name = Constants.String.BLANK

    @Numeric(min = 0, max = 10)
    var numeric = 0

    @Decimal(min = 0.0, max = 10.0)
    var decimal = 0.0

    @Constraint<String, Email>(Email::class)
    var email = Constants.String.BLANK
}