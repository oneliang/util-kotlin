package com.oneliang.ktx.test.common

import com.oneliang.ktx.Constants

class ApiApprovalNoticeRequest {

    companion object {
        const val MSG_TYPE_EVENT = "event"

        const val APPROVAL_NOTICE = "sys_approval_change"

        const val SUCCESS = "success"

    }

    var toUserName = Constants.String.BLANK
    var suiteId = Constants.String.BLANK
    var timeStamp = Constants.String.BLANK
    var suiteTicket = Constants.String.BLANK
    var authCode = Constants.String.BLANK
    var authCorpId = Constants.String.BLANK
    var changeType = Constants.String.BLANK

    var agentId = Constants.String.BLANK

    var msgType = Constants.String.BLANK

    var event = Constants.String.BLANK

    var approvalInfo = Constants.String.BLANK
    var nodeStatus = Constants.String.BLANK

}