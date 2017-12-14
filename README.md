# LedMove
This android app is used to send and receive data from an Arduino over Bluetooth 
The app provides a user interface to control an LED light as can be seen in the activity_led_on_off.xml file
buttonOn :- turns on the light
buttonOff :- turns off the light
textView1 :- is used to display data on app that is coming in from the Arduino
button1 :- on pressing this button data coming in from Arduino is displayed in textView1 
The code running on the Arduino reads data (using Serial.read()) that has been input from app(when buttonOn and buttonOff are pressed)
and accordingly turns on and off the light using digitalWrite function.
Arduino code is also used to send data to the app (using Serial.write()) which is displayed in textView1
