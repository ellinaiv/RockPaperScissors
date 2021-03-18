**Documentation**
The application a solution for Rock Paper Scissors game. </br>
The application is a multi-user play and in order to achieve this property different solutions were tried. </br>
Firebase server/ database was finally chosen for the implementation. </br> 





Set up Firebase: </br>

1. Create new firebase project </br>
```
https://console.firebase.google.com/u/0/project/_/database
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

[Resource]( https://developer.android.com/training/connect-devices-wirelessly/wifi-direct "Developer.android")
 





