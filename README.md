![image](src/main/resources/logo.jpg)

The Contributors:
 - Alper Durmuş
   
 - Mehmet Can Bıyık

 - Umut Başar Demir

 - Kağan Rehber

 - Ghazi Al-Obeid
------
**How to execute the program:**

When you open the program you will be welcomed by the login page. You can enter a username and password.

This password will be used in order to decrypt your messages, so If you enter the wrong password then you will not be able to see the

contents of the messages.

After you log in, you will see the news page of our program. In that page, you can see the latest news pulled from the internet.

The links of those news clickable. Agter you click it the real news page will be opened in your default browser.

Also we have the chat features. First you need to create a Super Group in order to decentralize your chatting experience.

In order to create a chat group between two users you need to know the username and the UUID of the users you want to add. You can get your UUID by opening About menu in the menu bar and then clicking the Copy UUID option. This will copy your UUID to your clipboard.

After the creation of your Super Group you can chat between the users easily.

Lastly, the command keywords. you can integrate real-time data by using command keywords. For example: \avgPts{LeBron James}.

You use that in inplace of the stat itself. Before your message is sent the command will be replaced by our program.

-----
 **DEPENDENCIES:**

- com.fasterxml.jackson.core:jackson-databind

- com.fasterxml.jackson.datatype:jackson-datatype-jsr310

- com.formdev:flatlaf

- org.jgroups:jgroups

- org.jsoup:jsoup

- org.json.simple

Note: You can use IntelliJ IDEA to run the project. By openning it inside the IDE and then adding the dependencies via the pom.xml provided. You need to open multiple applications and this can be done by adding multiple run configurations as Application configuration in the IDE.
