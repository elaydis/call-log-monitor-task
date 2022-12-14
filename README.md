# Call Log Monitor

This application implements the "Call Monitor Task" as described [here](files/technical-task-call-log.pdf).

## Installing and running the app

### Install via Android Studio
In order to run the application, first import the project into Android Studio and then, once it has been properly imported and indexed, you can run the app from there via the "Run" button.

### Running the app and interacting with the server
Upon startup the application will display information about the server status and a button to start the server, which will prompt the user to grant all required permissions, and start the server, given that all permissions have been granted.

Once the server is started the application will display the address at which the server can be accessed and an indication that the server is running.

Additionally, a notification will appear, informing the user about the fact that the server is currently running and giving them the option to stop the server.

#### Endpoints
The server can be accessed through the local wifi network by calling the following endpoints (paste the server address into your web browser and append the specified endpoint path).

##### Root
###### {ip:port}/

returns information about the server and it's existing endpoints

##### Status
###### {ip:port}/status

returns information about ongoing call or null if there is none

##### Logs
###### {ip:port}/logs

returns information about calls that took place while the server was running and have already ended

## Known issues

### Instrumentation tests

The instrumentation tests rely on the standard phone app by Google being installed on the device, in order to make a phone call and test call logging. The phone app needs to have been opened before so that there are no additional popups disrupting the test flow.

## Relevant third-party libraries

### Ktor
The HTTP framework Ktor is used to run a server in the app. It provides a straightforward way of writing HTTP servers in Kotlin and can be hosted in an Android application.

For more information see https://ktor.io/

### Moshi
Moshi is a modern JSON library for Kotlin and Java. It is used to serve the responses as JSON objects.

### RxJava/RxKotlin 3
Rx is used to keep the UI in sync with the server by subscribing to server state and call log entry changes, which are collected in the server, and displaying them in the activity.

### Dagger Hilt
Dagger Hilt is used for dependency injection.
