package com.nznlabs.familymap240.ui

import android.content.Context
import androidx.fragment.app.Fragment
import com.nznlabs.familymap240.util.MessageCallback
import com.nznlabs.familymap240.util.MessageType
import com.nznlabs.familymap240.util.Response
import com.nznlabs.familymap240.util.UIComponentType
import timber.log.Timber

abstract class BaseFragment: Fragment()
{
    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch(e: ClassCastException){
            Timber.e(e,"$context must implement UICommunicationListener" )
        }
    }

    fun showInfoDialog(infoMessage: String) {
        uiCommunicationListener.onResponseReceived(
            response = Response(
                message = infoMessage,
                uiComponentType = UIComponentType.Dialog,
                messageType = MessageType.Info
            ),
            messageCallback = object : MessageCallback {
                override fun removeMessage() {
                    // do nothing
                }
            }
        )
    }

}