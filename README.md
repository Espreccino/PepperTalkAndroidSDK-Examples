PepperTalk Android SDK sample.
-----------------------------

## Generate ClientID and Client Secret
Generate Client ID & Client Secret to authenticate your application with PepperTalk. Follow these steps to generate Client ID & Client Secret:
* Go to [PepperTalk Console](https://console.getpeppertalk.com/dashboard/signup)
* Fill in the details and signup
* Validate your email address by clicking on the link you get in your email inbox
* Create a new application by selecting the "New Application" option from the menu on left hand side
* Enter the Application Description
* Optionally, select and enter push notification related information to support remote OS notifications
* Find Client ID & Client Secret in the 'Clients' tab on your application page

Update your client_id and client_secret in [strings.xml] [3]

```xml
    <string name="client_id">CLIENT_ID</string>
    <string name="client_secret">CLIENT_SECRET</string> 
```

Add PepperTalk to your application

Gradle dependency 
```xml
    compile 'com.espreccino:peppertalk:0.4.13'
```

[build.gradle] [2]

Add ContentProvider to AndroidManifest.xml (Unique authority)

<b>${applicationId}</b> will automatically load your application package name from build.gradle. For different flavours add a suffix.
```xml
    <provider
            android:name="com.espreccino.peppertalk.io.TalkProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:enabled="true"
            android:label="PepperTalk"
            android:syncable="true"
            tools:replace="android:authorities"/>
```
Initialize PepperTalk

```java
 PepperTalk.getInstance(context)
                .init(clientId,
                        clientSecret,
                        userId)
                .inAppNotificationsEnabled(true) // Enable in app notification
                .notificationStatIcon(R.drawable.ic_stat_notification) // notification stat icon
                .connect();
```

Start Conversation

```java
PepperTalk.getInstance(context)
                    .chatWithParticipant(userId)
                    .topicId(topicId)
                    .topicTitle("Let ride!")
                    .start();
```

Message Listener 
- New Message
- Unread count

```java
PepperTalk.getInstance(context)
                    .registerMessageListener(new PepperTalk.MessageListener() {
                        @Override
                        public void onNewMessage(String userId, String topicId, int unreadCount) {
                            //Update unread count in UI
                        }
                    });
```

### Register GCM ID with PepperTalk

```java
// Use Google Play and Client services to get Registration ID.

PepperTalk.getInstance(context).registerGcm(regId);

```

### Check is notification Intent is from PepperTalk [Code] [6]

```java
PepperTalk.getInstance(context).isNotificationFromPepperTalk(intent);
```

### Handle PepperTalk GCM notification [Code] [7]

```java

PepperTalk.getInstance(context).handleNotification(intent,
                    R.drawable.ic_stat_notification,
                    notificationSoundUri);
```

[Adding GCM service to manifest] [8]

---
### Adding PepperTalk to eclipse project
---
Add the following to your pom.xml [(more info using m2eclipse)] [4]
```xml
<dependency>
 <groupId>com.espreccino</groupId>
 <artifactId>peppertalk</artifactId>
 <version>0.4.13</version>
</dependency>
````

Download the latest aar [here] [5]

Detailed instructions on setting up Eclipse with ADT is [here](eclipse_setup.md)

[1]: https://console.getpeppertalk.com/ "PepperTalk"
[2]: https://github.com/Espreccino/PepperTalkAndroidSDK-Examples/blob/master/app/build.gradle "build.gralde"
[3]: https://github.com/Espreccino/PepperTalkAndroidSDK-Examples/blob/master/app/src/main/res/values/strings.xml#L6 "strings.xml"
[4]: http://books.sonatype.com/m2eclipse-book/reference/dependencies.html "m2eclipse"
[5]: https://search.maven.org/#browse%7C-793624875 "PepperTalk SNAPSHOT"
[6]: https://github.com/Espreccino/PepperTalkAndroidSDK-Examples/blob/master/app/src/main/java/com/espreccino/peppertalk/sample/gcm/GcmIntentService.java#L30
[7]: https://github.com/Espreccino/PepperTalkAndroidSDK-Examples/blob/master/app/src/main/java/com/espreccino/peppertalk/sample/gcm/GcmIntentService.java#L32
[8]: https://github.com/Espreccino/PepperTalkAndroidSDK-Examples/blob/master/app/src/main/AndroidManifest.xml#L24
