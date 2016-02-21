SPOOCR
======

SPOOCR = Securely Power-On/Off Computers Remotely

Use this Android app to monitor and control the state (power-on/off) of computers in your LAN via a secure connection.


This app can only connect to your SSH server. If you don't have an SSH server running yet, you can in some cases configure one in your internet router. If your router doesn't support SSH you may consider installing alternative firmware on it, like DD-WRT, Tomato, etc.

This app can only power-on a computer if
* that computer is connected to the network via a **cable** (wake via Wifi is not supported)
* the Wake-On-LAN feature of its network card has been enabled

This app can only shutdown
* a Windows pc with *Remote Registry* enabled
* a Linux machine to which you have root access via SSH
* a Mac to which you have root access via SSH
