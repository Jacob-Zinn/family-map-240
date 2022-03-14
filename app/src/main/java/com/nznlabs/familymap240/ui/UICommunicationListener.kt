package com.nznlabs.familymap240.ui

import com.nznlabs.familymap240.util.MessageCallback
import com.nznlabs.familymap240.util.Response

interface UICommunicationListener {

    fun onResponseReceived(
        response: Response,
        messageCallback: MessageCallback
    )
}