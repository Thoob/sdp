//Robot Controller for SDP 13
//Authours: Vanya Petkova and Scott Postlethwaite
//January 2015

#include "SerialCommand.h"
#include "SDPArduino.h"
#include <Wire.h>

#define arduinoLED 13                        // Arduino LED on board
#define left 5                               // Left wheel motor
#define right 4                              // Right wheel motor
#define kicker 3                             // Kicker/catcher motor

SerialCommand sCmd;                          // The demo SerialCommand object
int leftspeed = 0;                           // Speed of left wheel
int rightspeed = 0;                          // Speed of right wheel
int minspeed = 25;                           // Minimum speed at which wheels will move

int KICKING = 0;                             // flag for kicking


void setup() {
  pinMode(arduinoLED, OUTPUT);               // Configure the onboard LED for output
  digitalWrite(arduinoLED, LOW);             // default to LED off
  
  SDPsetup();
  
  Serial.begin(115200);

  // Setup callbacks for SerialCommand commands
  //Test commands
  sCmd.addCommand("ON",    LED_on);          // Turns LED on
  sCmd.addCommand("OFF",   LED_off);         // Turns LED off
  
  //Movement commands
  sCmd.addCommand("MOVE", run_engine);       // Runs wheel motors
  sCmd.addCommand("FSTOP", force_stop);      // Force stops all motors
  sCmd.addCommand("KICK", move_kick);        // Runs kick script
  sCmd.addCommand("CATCH", move_catch);      // Runs catch script
  
  //Remote Control Commands
  sCmd.addCommand("RCFORWARD", rc_forward);
  sCmd.addCommand("RCBACKWARD", rc_backward);
  sCmd.addCommand("RCROTATL", rc_rotateL);
  sCmd.addCommand("RCROTATR", rc_rotateR);

  sCmd.setDefaultHandler(unrecognized);      // Handler for command that isn't matched

  Serial.println("I am completely operational, and all my circuits are functioning perfectly.");
}

void loop() {
  
  if(abs(leftspeed) < minspeed){
    motorStop(left);
  } else if(leftspeed < 0){
    motorBackward(left, abs(leftspeed));
  } else {
    motorForward(left, leftspeed);
  }
  
  if(abs(rightspeed) < minspeed){
    motorStop(right);
  } else if(rightspeed < 0){
    motorBackward(right, abs(rightspeed));
  } else {
    motorForward(right, rightspeed);
  }
  
  if( KICKING == 1 )
  {
    while(Serial.available())
      Serial.read();
  }else
  {
    sCmd.readSerial();                         // We don't do much, just process serial commands
  }
  
  
}

// Test Commands
void LED_on() {
  Serial.println("LED on");
  digitalWrite(arduinoLED, HIGH);
}

void LED_off() {
  Serial.println("LED off");
  digitalWrite(arduinoLED, LOW);
}



// Updates motor movement speeds
void run_engine() {
  
  char *arg1;
  char *arg2;
    
  arg1 = sCmd.next();
  leftspeed = atoi(arg1);
  
  arg2 = sCmd.next();
  rightspeed = atoi(arg2);
}

// Kick script
void move_kick() {
  
  char *arg1;
  char *arg2;
  int time;
  int power;
  
  arg1 = sCmd.next();
  time = atoi(arg1);
  
  arg2 = sCmd.next();
  power = atoi(arg2);
  
  Serial.println("Kicking");
  motorBackward(kicker, power);
  KICKING = 1; 
  
  delay(time);
  
  motorStop(kicker); 
  
  KICKING = 0;
}

// Catch script
void move_catch() {
  
  char *arg1;
  char *arg2;
  int time;
  int power;
  
  arg1 = sCmd.next();
  time = atoi(arg1);
  
  arg2 = sCmd.next();
  power = atoi(arg2);

  
  Serial.println("Catching");
  //lift and move forward
  motorBackward(3, 60);
  motorForward(4, 40);
  motorForward(5, 40);
  delay(450);
  motorStop(kicker);
  delay(250);
  //catch
  motorForward(3, 100);  
  delay(250);
  motorStop(kicker);
}

// Force stops all motors
void force_stop(){
  Serial.println("Stopping");
  
  motorAllStop();  
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
