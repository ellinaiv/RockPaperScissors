**Documentation**
Wireless connection: WiFi P2P and Nearby APIs

* P2P connections with Wi-Fi Direct

* Inter device communication

* Limited coverage range ?-> not specified in the task

Set up: </br>

1. permissions </br>
```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.android.nsdchat"
    ...
    <uses-permission
        android:required="true"
        android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission
        android:required="true"
        android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission
        android:required="true"
        android:name="android.permission.CHANGE_WIFI_STATE"/>
    <uses-permission
        android:required="true"
        android:name="android.permission.INTERNET"/>
```

2. BroadcastReceiver </br>
Needed to listen to broadcast intents </br>
To enable this functionality: </br>
 instantiate IntentFilter </br>
    1. set it to listen for the following </br>
```
WIFI_P2P_STATE_CHANGED_ACTION </br>
WIFI_P2P_PEERS_CHANGED_ACTION </br>
WIFI_P2P_CONNECTION_CHANGED_ACTION </br>
WIFI_P2P_THIS_DEVICE_CHANGED_ACTION </br>
```



 





