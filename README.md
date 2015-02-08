PepperTalk Android SDK sample.
-----------------------------

Get your client id and client secret [here](0)

Add PepperTalk to your application

Gradle dependency (Sonatype Snapshot)
```xml
    compile 'com.espreccino:peppertalk:0.4.2-SNAPSHOT'
```
[build.gradle](2)
```groovy
buildscript {
    repositories {
        maven { url 'https://oss.sonatype.org/content/repositories/snapshots'}
    }
}
...
repositories {
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots'}
}
...

dependencies {
    compile 'com.espreccino:peppertalk:0.4.2-SNAPSHOT'
}

```

Update your client_id and client_secret in [strings.xml](1)
```xml
    <string name="client_id">CLIENT_ID</string>
    <string name="client_secret">CLIENT_SECRET</string>
```

Initialize PepperTalk
```java
 PepperTalk.getInstance(context)
                .init(clientId,
                        clientSecret,
                        userId)
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
                    .setMessageListener(new PepperTalk.MessageListener() {
                        @Override
                        public void onNewMessage(String userId, String topicId, int unreadCount) {
                            //Update unread count in UI
                        }
                    });
```

(0)http://console.getpeppertalk.com/
(1)https://github.com/Espreccino/PepperTalkAndroidSDK-Examples/blob/master/app/src/main/res/values/strings.xml#L6
(2)https://github.com/Espreccino/PepperTalkAndroidSDK-Examples/blob/master/app/build.gradle
