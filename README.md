Android SMS Spoofer
===================

Proof of Concept app which takes advantage of Android's `SmsReceiverService` being exported to fake an incoming SMS with no permissions.

On 2012-10-30 NCSU notified Google about a "Smishing" vulnerability ([1]) in Android. The vulnerability appears to be due to Android exporting `SmsReceiverService` in the `com.android.mms` app with no apparent restrictions. A third party app can therefore pass an explicit Intent to the SMS app containing a fake SMS message and the SMS app will process it.

This issue has been known about and used for some time ([2],[3],[4]) by test apps and apps designed to intercept, alter and pass on SMS messages. NCSU were the first to publically highlight the security vulnerability that arises from this functionality, namely that a user can be tricked into taking action on a faked SMS message.

Google commited a fix to Android 4.2_r1 a few days after notification on 2012-11-02.

This PoC app simply wraps existing code already made public so that the issue can be validated and countermeasures designed while users wait for the patch.

[Download APK](https://github.com/thomascannon/android-sms-spoof/SMSSpoofer-v1.apk/qr_code)

[1]: http://www.csc.ncsu.edu/faculty/jiang/smishing.html
[2]: http://d.hatena.ne.jp/thorikawa/20100930/p1
[3]: http://blog.dev001.net/post/14085892020/android-generate-incoming-sms-from-within-your-app
[4]: http://stackoverflow.com/a/12338541

![Screenshot](https://github.com/thomascannon/android-sms-spoof/raw/master/screenshot-2012-11-03-025014.png) 
