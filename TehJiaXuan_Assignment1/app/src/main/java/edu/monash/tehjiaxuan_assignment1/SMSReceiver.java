package edu.monash.tehjiaxuan_assignment1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
    // used as a channel to broadcast the message
    // any application aware of this channel can listen to the broadcasts
    public static final String SMS_FILTER = "SMS_FILTER";

    // within the broadcast, we would like to send information
    // and this will be the key to indetify that information, in this case the SMS message
    public static final String SMS_MSG_KEY = "SMS_MSG_KEY";


    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] messages= Telephony.Sms.Intents.getMessagesFromIntent(intent);

        for (int i = 0; i < messages.length; i++) {
            SmsMessage currentMessage = messages[i];
            String senderNum = currentMessage.getDisplayOriginatingAddress();
            String message = currentMessage.getDisplayMessageBody();
            Intent msgIntent = new Intent();
            msgIntent.setAction(SMS_FILTER);
            msgIntent.putExtra(SMS_MSG_KEY, message);
            context.sendBroadcast(msgIntent);
        }

    }

}