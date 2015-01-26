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
  sCmd.addCommand("P",     processCommand);  // Converts two arguments to integers and echos them back
  sCmd.addCommand("FORWARD", move_forward);
  sCmd.addCommand("ROTATE", move_rotate);
  sCmd.addCommand("STOP", move_stop);


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
  
  char *arg;
  int time;
  
  arg = sCmd.next();
  time = atoi(arg);
  
  if (time==NULL) {
    time = 1000;
  }
  Serial.println("Moving forward");
  motorForward(4, 100);
  motorForward(5, 100);
  
  delay(time);
  
  motorAllStop(); 
}

void move_rotate() {
  char *arg;
  int dir;
  
  arg = sCmd.next();
  dir = atoi(arg);
  
  Serial.println("Rotating");
  if (dir == 1) {
  motorForward(4, 50);
  motorBackward(5, 50);
  
    delay(2000);
  
  motorAllStop();
  
  }
  else if (dir==2) {
  motorForward(5, 50);
  motorBackward(4, 50);
  
  delay(2000);
  
  motorAllStop();
  
  }
}

void move_stop(){
  Serial.println("Stopping");
  
  motorAllStop();
  
}


void processCommand() {
  int aNumber;
  char *arg;

  Serial.println("We're in processCommand");
  arg = sCmd.next();
  if (arg != NULL) {
    aNumber = atoi(arg);    // Converts a char string to an integer
    Serial.print("First argument was: ");
    Serial.println(aNumber);
  }
  else {
    Serial.println("No arguments");
  }

  arg = sCmd.next();
  if (arg != NULL) {
    aNumber = atol(arg);
    Serial.print("Second argument was: ");
    Serial.println(aNumber);
  }
  else {
    Serial.println("No second argument");
  }
}

// This gets set as the default handler, and gets called when no other command matches.
void unrecognized(const char *command) {
  Serial.println("What?");
}