package com.example.message;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SendMessage extends AppCompatActivity {

    private TextView phone;
    private TextView content;
    private Button takePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.sendmessage);
        phone = findViewById(R.id.phone);
        content = findViewById(R.id.content);
        EditText messageContent = findViewById(R.id.message_content);
        Button send = findViewById(R.id.send);
        takePhoto = findViewById(R.id.takePhoto);

        Intent intent=getIntent();
        String number=intent.getStringExtra("phone");
        phone.setText(number);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        MessageReceiver messageReceiver = new MessageReceiver();
        registerReceiver(messageReceiver,intentFilter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String contentMes = messageContent.getText().toString();
               content.setText(contentMes);
                Intent sentIntent = new Intent("SENT_SMS_ACTION");
                PendingIntent pi = PendingIntent.
                        getBroadcast(SendMessage.this,0,sentIntent,0);
                //实现发送短信
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null,
                        content.getText().toString(), pi, null);

            }
        });

        //点击拍照按钮
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new
                        Intent(SendMessage.this, TakePhoto.class);
                startActivity(intent1);
            }
        });
    }

    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            // 获取短信消息
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            String address = messages[0].getOriginatingAddress();
            String fullMessage = "";
            for (SmsMessage message : messages) {
                // 获取短信内容
                fullMessage += message.getMessageBody();
            }
            phone.setText(address);
            content.setText(fullMessage);
        }
    }
}