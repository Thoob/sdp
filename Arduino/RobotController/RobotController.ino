//Robot Controller for SDP 13
//Authours: Vanya Petkova and Scott Postlethwaite
//January 2015

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
  //Test commands
  sCmd.addCommand("ON",    LED_on);          // Turns LED on
  sCmd.addCommand("OFF",   LED_off);         // Turns LED off
  sCmd.addCommand("HELLO", sayHello);        // Echos the string argument back
  
  //Movement commands
  sCmd.addCommand("FORWARD", move_forward);
  sCmd.addCommand("BACKWARD", move_backward);
  sCmd.addCommand("ROTATE", move_rotate);
  sCmd.addCommand("STOP", move_stop);
  sCmd.addCommand("KICK", move_kick);
  sCmd.addCommand("CATCH", move_catch);
  sCmd.addCommand("ROTATEH", move_rotateh);

  
  //Remote Control Commands
  sCmd.addCommand("RCFORWARD", rc_forward);
  sCmd.addCommand("RCBACKWARD", rc_backward);
  sCmd.addCommand("RCROTATL", rc_rotateL);
  sCmd.addCommand("RCROTATR", rc_rotateR);


  sCmd.setDefaultHandler(unrecognized);      // Handler for command that isn't matched  (says "What?")



  Serial.println("I am completely operational, and all my circuits are functioning perfectly.");
}

void loop() {
  sCmd.readSerial();     // We don't do much, just process serial commands
}

//Test Commands

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


//Movement with argument commands

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
  motorBackward(3, power);
  
  delay(time);
  
  motorAllStop(); 
}

void move_catch() {
  
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
  
  Serial.println("Catching");
  //lift
  motorBackward(3, 60);
  motorForward(4, 40);
  motorForward(5, 40);
  delay(450);
  motorStop(3);
  delay(250);
  
  motorForward(3, 100);  
  delay(250);
  motorAllStop();
}

void move_stop(){
  Serial.println("Stopping");
  
  motorAllStop();
  
}


void move_rotateh() {
  char *arg1;
  double heading;
  
  arg1 = sCmd.next();
  heading = atof(arg1);
  
  int motor_power;
  
  motor_power = map(heading, 0.00, 3.30, 0, 100);
  
  Serial.print("Motor power is: ");
  Serial.println(motor_power);
  
  if (motor_power < 30) {
    motor_power = 30;
  }
  
  motorForward(4, motor_power);
  motorBackward(5, motor_power);
  
  
}

//Remote Control Commands

void rc_forward() {
  
  Serial.println("Moving forward");
  motorForward(4, 100);
  motorForward(5, 100);
  
}

void rc_backward() {
  
  Serial.println("Moving backward");
  motorBackward(4, 100);
  motorBackward(5, 100);
  
}

void rc_rotateL() {
   Serial.println("Rotating Left");
  motorForward(4, 100);
  motorBackward(5, 100);
}

void rc_rotateR() {
   Serial.println("Rotating Right");
  motorForward(5, 100);
  motorBackward(4, 100);
}


// This gets set as the default handler, and gets called when no other command matches.
void unrecognized(const char *command) {
  Serial.println("I'm sorry, Dave. I'm afraid I can't do that.");
}
