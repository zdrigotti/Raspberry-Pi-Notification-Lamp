# Raspberry Pi Notification Lamp
This repository contains the Android application code for a project I completed for my Embedded System Design class in December 2015. This project allows a user to specify mappings between Android applications installed on their device and a chosen hexadecimal color. With these mappings (and a configurable IP address and port), the application will monitor incoming and removed notifications on the user's device, and update the server via an HTTP request with the colors of the active notifictions.

The server, which in this was case was implemented using a Raspberry Pi, is responsible for cycling through the colors of the active notifications and displaying them using an LED strip. With this system in place, the LED strip indicates the notifications that are currently active on the user's device in a more visually obvious way.

Upon review of this project several years on, there are a number of efficiences and enhancements I would like to make, and intend to at some point in the future.
