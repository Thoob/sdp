// Demo Code for SerialCommand Library
// Steven Cogswell
// May 2011

#include "SerialCommand.h"
#include "SDPArduino.h"
#include <Wire.h>

#define arduinoLED 13   // Arduino LED on board

SerialCommand sCmd;     // The demo SerialCommand object


void setup() {
  pinMode(arduinoLED, OUTPUT);      // Configure the onboard LED for output
  digitalWrite(arduinoLED, LOW);    // default to LED off
  
  SDPsetup();
  
  Serial.begin(115200);

  // Setup callbacks for SerialCommand commands
  sCmd.addCommand("ON",    LED_on);          // Turns LED on
  sCmd.addCommand("OFF",   LED_off);         // Turns LED off
  sCmd.addCommand("HELLO", sayHello);        // Echos the string argument back
//  sCmd.addCommand("P",     processCommand);  // Converts two arguments to integers and echos them back
  sCmd.addCommand("FORWARD", move_forward);
  sCmd.addCommand("BACKWARD", move_backward);
  sCmd.addCommand("ROTATE", move_rotate);
  sCmd.addCommand("STOP", move_stop);
  sCmd.addCommand("KICK", move_kick);


  sCmd.setDefaultHandler(unrecognized);      // Handler for command that isn't matched  (says "What?")



  Serial.println("Ready");
}

void loop() {
  sCmd.readSerial();     // We don't do much, just process serial commands
}


void LED_on() {
  Serial.println("LED on");
  digitalWrite(arduinoLED, HIGH);
}

void LED_off() {
  Serial.println("LED off");
  digitalWrite(arduinoLED, LOW);
}

void sayHello() {
  char *arg;
  arg = sCmd.next();    // Get the next argument from the SerialCommand object buffer
  if (arg != NULL) {    // As long as it existed, take it
    Serial.print("Hello ");
    Serial.println(arg);
  }
  else {
    Serial.println("Hello, whoever you are");
  }
}

void move_forward() {
  
  char *arg1;
  char *arg2;
  
  int time;
  int power;
  
  arg1 = sCmd.next();
  time = atoi(arg1);
  
  arg2 = sCmd.next();
  power = atoi(arg2);
  
  if (time==NULL) {
    time = 1000;
  }
  Serial.println("Moving forward");
  motorForward(4, power);
  motorForward(5, power);
  
  delay(time);
  
  motorAllStop(); 
}

void move_backward() {
  
  char *arg;
  int time;
  
  arg = sCmd.next();
  time = atoi(arg);
  
  if (time==NULL) {
    time = 1000;
  }
  Serial.println("Moving backward");
  motorBackward(4, 100);
  motorBackward(5, 100);
  
  delay(time);
  
  motorAllStop(); 
}

void move_rotate() {
  char *arg1;
  char *arg2;
  char *arg3;
  int dir;
  int time;
  int power;
  
  arg1 = sCmd.next();
  dir = atoi(arg1);
  
  arg2 = sCmd.next();
  time = atoi(arg2);
  
  arg3 = sCmd.next();
  power = atoi(arg3);
  
  
  
  Serial.println("Rotating");
  if (dir == 1) {
  motorForward(4, power);
  motorBackward(5, power);
  
    delay(time);
  
  motorAllStop();
  
  }
  else if (dir==2) {
  motorForward(5, power);
  motorBackward(4, power);
  
  delay(time);
  
  motorAllStop();
  
  }
}

void move_stop(){
  Serial.println("Stopping");
  
  motorAllStop();
  
}

void move_kick() {
  
  char *arg1;
  char *arg2;
  int time;
  int power;
  
  arg1 = sCmd.next();
  time = atoi(arg1);
  
  arg2 = sCmd.next();
  power = atoi(arg2);
  
  
  
  if (time==NULL) {
    time = 1000;
  }
  
  if (power==NULL) {
    power = 50;
  }
  
  Serial.println("Kicking");
  motorBackward(4, power);
  
  delay(time);
  
  motorAllStop(); 
}

// This gets set as the default handler, and gets called when no other command matches.
void unrecognized(const char *command) {
  Serial.println("What?");
}
