# Setting up the Sample Project without Gradle or Maven
We recommend using Gradle and Android Studio for building your apps. Or dev cycle uses this setup and so all our instructions will be primarily geared for this setup. However if you have an existing app with Eclipse and ADT and would like to integrate Pepper Talk this tutorial may guide you. This was tried on Eclipse Luna with ADT. Your mileage may vary.

## Dependencies

* Android Support Libraries Rev 21.0.3
  * v7 appcompat
  * v7 recyclerview
* AndroidAsync
* GSON
* GreenRobot EventBus

## Library Setup
### [v7 appcompat](https://developer.android.com/tools/support-library/features.html#v7-appcompat)
Follow the directions [here](https://developer.android.com/tools/support-library/setup.html#libs-with-res) to import v7 appcompat as an Android Library project.

### [v7 recyclerview](https://developer.android.com/tools/support-library/features.html#v7-recyclerview)
Follow the [same](https://developer.android.com/tools/support-library/setup.html#libs-with-res) directions to import v7 recyclerview as an Android Library project.

### Pepper Talk
Next import the [Pepper Talk AAR](https://search.maven.org/#browse%7C-793624875) latest version as a library. Follow the tutorial [here](http://commonsware.com/blog/2014/07/03/consuming-aars-eclipse.html) on how to import an AAR as a library project. 

* Add the earlier created library projects to the Android Library dependencies and mark this as a library (Project > Properties > Android)
* Download the latest version of [GSON jar](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.code.gson%22)
* Download the latest version of [eventbus](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22de.greenrobot%22%20AND%20a%3A%22eventbus%22)
* Download the latest version of [AndroidAsync](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.koushikdutta.async%22%20AND%20a%3A%22androidasync%22)
* Add the 3 jars to the libs folder of PepperTalk
* Ensure that the Android Dependencies are marked for export in (Project > Properties > Java Build Path > Order and Export)

### Setting up the sample app or your app
Now add the libraries and configure the manifest

* Skip this if you have are integrating into your existing app
  * Clone the [sample project](https://github.com/Espreccino/PepperTalkAndroidSDK-Examples.git) and import to eclipse
* Add PepperTalk as a library project dependency (Project > Properties > Android)
* PepperTalk requires these permissions, merge these into your manifest

```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
````
* Add the following services
```xml
        <service android:name="com.espreccino.peppertalk.io.ChatService" />
````
* Add the following content providers, replace __YOUR_APP_ID__ with your app id
```xml
    <provider
            android:name="com.espreccino.peppertalk.io.TalkProvider"
            android:authorities="__YOUR_APP_ID__.provider"
            android:exported="false"
            android:enabled="true"
            android:label="PepperTalk"
            android:syncable="true"/>
````
* Add the receiver
```xml
        <receiver
            android:name="com.espreccino.peppertalk.NetworkChangeReceiver"
            android:label="NetworkChangeReceiver" >
            <intent-filter>
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
                <action android:name="android.net.wifi.WIFI_STATE_CHANGED" />
            </intent-filter>
        </receiver>
````
* Add the activity
```xml
        <activity
            android:name="com.espreccino.peppertalk.ui.ChatActivity"
            android:launchMode="singleTop"
            android:screenOrientation="portrait"
            android:theme="@style/ChatActivityTheme"
            android:windowSoftInputMode="stateAlwaysHidden" />
````
* You manifest should now have these PepperTalk dependencies
```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
    
    <application>
        <service android:name="com.espreccino.peppertalk.io.ChatService" />
        <receiver
            android:name="com.espreccino.peppertalk.NetworkChangeReceiver"
            android:label="NetworkChangeReceiver" >
            <intent-filter>
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
                <action android:name="android.net.wifi.WIFI_STATE_CHANGED" />
            </intent-filter>
        </receiver>
    <provider
            android:name="com.espreccino.peppertalk.io.TalkProvider"
            android:authorities="__YOUR_APP_ID__.provider"
            android:exported="false"
            android:enabled="true"
            android:label="PepperTalk"
            android:syncable="true"/>
    
        <activity
            android:name="com.espreccino.peppertalk.ui.ChatActivity"
            android:launchMode="singleTop"
            android:screenOrientation="portrait"
            android:theme="@style/ChatActivityTheme"
            android:windowSoftInputMode="stateAlwaysHidden" />
    </application>
````
* If you are trying out the sample app remember to add you client\_id and client\_secret to res/values/strings.xml

## Done
Thats it you should be all set and ready to get chatting with Pepper Talk. [Let us know](mailto:info@espreccino.com) in case you have any issues. Happy chatting!