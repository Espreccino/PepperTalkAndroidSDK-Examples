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

Gradle dependency (Sonatype Snapshot)
```xml
    compile 'com.espreccino:peppertalk:0.4.3-SNAPSHOT'
```

[build.gradle] [2]

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
    compile 'com.espreccino:peppertalk:0.4.3-SNAPSHOT'
}

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

---
### Adding PepperTalk to eclipse project
---
Add the following to your pom.xml [(more info using m2eclipse)] [4]
```xml
<dependency>
 <groupId>com.espreccino</groupId>
 <artifactId>peppertalk</artifactId>
 <version>0.4.3-SNAPSHOT</version>
</dependency>
````

Download the latest aar [here] [5]


[1]: https://console.getpeppertalk.com/ "PepperTalk"
[2]: https://github.com/Espreccino/PepperTalkAndroidSDK-Examples/blob/master/app/build.gradle "build.gralde"
[3]: https://github.com/Espreccino/PepperTalkAndroidSDK-Examples/blob/master/app/src/main/res/values/strings.xml#L6 "strings.xml"
[4]: http://books.sonatype.com/m2eclipse-book/reference/dependencies.html "m2eclipse"
[5]: https://oss.sonatype.org/content/repositories/snapshots/com/espreccino/peppertalk/ "PepperTalk SNAPSHOT"
