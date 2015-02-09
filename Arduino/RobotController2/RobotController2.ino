//Robot Controller for SDP 13
//Authours: Vanya Petkova and Scott Postlethwaite
//January 2015

//Extra comments version!

#include "SerialCommand.h"
#include "SDPArduino.h"
#include <Wire.h>

#define arduinoLED 13                        // Arduino LED on board
#define left 5                               // Left wheel motor
#define right 4                              // Right wheel motor
#define kicker 3                             // Kicker/catcher motor
#define minpower 30                          // Minimum Speed of the motors in power%

SerialCommand sCmd;                          // The demo SerialCommand object
int leftpower = 0;                           // Speed of left wheel
int rightpower = 0;                          // Speed of right wheel
boolean rotateStopped = true;                // Rotation stopped flag

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
  sCmd.addCommand("RSTOP", rotate_stop);     // Stops rotation strongly
  sCmd.addCommand("KICK", move_kick);        // Runs kick script
  sCmd.addCommand("CATCH", move_catch);      // Runs catch script

  sCmd.addCommand("SROTL", move_shortrotL);
  sCmd.addCommand("SROTR", move_shortrotR);

  //Remote Control Commands
  sCmd.addCommand("RCFORWARD", rc_forward);
  sCmd.addCommand("RCBACKWARD", rc_backward);
  sCmd.addCommand("RCROTATL", rc_rotateL);
  sCmd.addCommand("RCROTATR", rc_rotateR);

  sCmd.setDefaultHandler(unrecognized);      // Handler for command that isn't matched

    Serial.println("I am completely operational, and all my circuits are functioning perfectly.");
}

void loop() {
  sCmd.readSerial();                         // We don't do much, just process serial commands
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



// Movement with argument commands
void run_engine() {

  //This is how arguments are tokenised with SerialCommand. Extra can be added
  //by repeating this.
  char *arg1;
  char *arg2;

  arg1 = sCmd.next();
  int new_leftpower = atoi(arg1);

  arg2 = sCmd.next();
  int new_rightpower = atoi(arg2);




  //Stops the motors when the signal given is 0
  if(new_leftpower == 0){
    motorStop(left);
  }
  if(new_rightpower == 0){
    motorStop(right);
  }


  //Checks if the given speed is less than the minimum speed. If it is, 
  //it sets the given power to the minimum power. Left motor.
  if ((new_leftpower != 0) && (abs(new_leftpower) < minpower)) {
    new_leftpower = 1.4*minpower * (new_leftpower/abs(new_leftpower));
  }

  if ((new_rightpower != 0) && (abs(new_rightpower) < minpower)) {
    new_rightpower = minpower  * (new_rightpower/abs(new_rightpower));
  }


  // Updates speed of left wheel motor only if a different power is given.
  if(new_leftpower != leftpower){
    leftpower = new_leftpower;
    if(leftpower < 0){
      Serial.println(leftpower);
      leftpower = abs(leftpower);
      
      if(leftpower >= 30 && leftpower < 70){
          motorBackward(left, 1.4*leftpower);
      }
      else if(leftpower >= 70 && leftpower < 80){
          motorBackward(left, 1.29*leftpower);
      }
      else if(leftpower >= 80 && leftpower < 90){
          motorBackward(left, 1.19*leftpower);
      }
      else if(leftpower >= 90 && leftpower < 100){
          motorBackward(left, 1.08*leftpower);
      }
      else{
          motorBackward(left, leftpower);
      }
    } 
    else {
      if(leftpower >= 30 && leftpower < 70){
          motorForward(left, 1.4*leftpower);
      }
      else if(leftpower >= 70 && leftpower < 80){
          motorForward(left, 1.29*leftpower);
      }
      else if(leftpower >= 80 && leftpower < 90){
          motorForward(left, 1.19*leftpower);
      }
      else if(leftpower >= 90 && leftpower < 100){
          motorForward(left, 1.08*leftpower);
      }
      else{
          motorForward(left, leftpower);
      }
    }
  }

  // Updates speed of right wheel motor only if a different power is given.
  if(new_rightpower != rightpower){
    rightpower = new_rightpower;
    if(rightpower < 0){
      motorBackward(right, abs(rightpower));
    } 
    else {
      motorForward(right, rightpower);
    }
  }
  rotateStopped = true;
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

  delay(time);

  motorStop(kicker); 

  delay(1000);

}

// Catch script
void move_catch() {


  Serial.println("Catching");
  //lift and move forward
  motorBackward(kicker, 60);
  motorForward(right, 70);
  motorForward(left, 60);
  delay(450);
  motorStop(kicker);
  delay(250);
  
  //catch
  motorForward(kicker, 100);  
  delay(250);
  motorStop(kicker);
  
  leftpower = 0;
  motorStop(left);
  
  rightpower = 0;
  motorStop(right);
  delay(500);


}




// Force stops all motors
void force_stop(){
  Serial.println("Force stopping");
  leftpower = 0;
  rightpower = 0;
  motorAllStop(); 
}

// Stops rotation by briefly rotating in the opposite direction
void rotate_stop(){
  if(leftpower>rightpower && rotateStopped){
    motorForward(right, 100);
    motorBackward(left, 100);
  } 
  else if (rightpower>leftpower && rotateStopped){
    motorForward(left, 100);
    motorBackward(right, 100);
  }
  rotateStopped = false;
}

//Script to rotate the robot quickly to the left for a specified time.
void move_shortrotL() {

  char *arg1;
  char *arg2;
  int time;
  int power;

  arg1 = sCmd.next();
  time = atoi(arg1);

  arg2 = sCmd.next();
  power = atoi(arg2);

  if (time == NULL) {
    time = 250;
  }
  if (power == NULL) {
    power = 100;
  }


  motorForward(right, power);
  motorBackward(left, power);

  delay(time);

  leftpower = 0;
  motorStop(left);
  
  rightpower = 0;
  motorStop(right);

}

//Script to rotate the robot quickly to the right for a specified time.
void move_shortrotR() {

  char *arg1;
  char *arg2;
  int time;
  int power;

  arg1 = sCmd.next();
  time = atoi(arg1);

  arg2 = sCmd.next();
  power = atoi(arg2);

  if (time == NULL) {
    time = 250;
  }
  if (power == NULL) {
    power = 100;
  }

  motorForward(left, power);
  motorBackward(right, power);

  delay(time);

  leftpower = 0;
  motorStop(left);
  
  rightpower = 0;
  motorStop(right);

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




