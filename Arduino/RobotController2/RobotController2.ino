//Robot Controller for SDP 13
//Authours: Vanya Petkova and Scott Postlethwaite
//January 2015

//Extra comments version!

#include "SerialCommand.h"
#include "SDPArduino.h"
#include <Wire.h>

// TO DO: Check the slot for the KICKER motor, rework CATCH and KICK functions.

#define arduinoLED 13                        // Arduino LED on board
#define left 5                               // Left wheel motor
#define right 4                              // Right wheel motor
#define catcher 3                             // Catcher motor

#define kicker 1                              //Kicker motor MOTOR NUMBER TO BE CHECKED!

#define minpower 30                          // Minimum Speed of the motors in power%

SerialCommand sCmd;                          // The demo SerialCommand object
int leftpower = 0;                           // Speed of left wheel
int rightpower = 0;                          // Speed of right wheel
int catchflag = 0;                           // If 1, the ball is caught and ready to be kicked, otherwise it is not in grabber

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
  sCmd.addCommand("CFRESET", rst_catchflag); // Resets the catch flag (if we didn't catch the ball)

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



  Serial.print("LSpeed is: ");
  Serial.println(new_leftpower);

  Serial.print("RSpeed is: ");
  Serial.println(new_rightpower);


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
    new_leftpower = minpower * (new_leftpower/abs(new_leftpower));
  }

  if ((new_rightpower != 0) && (abs(new_rightpower) < minpower)) {
    new_rightpower = minpower  * (new_rightpower/abs(new_rightpower));
  }


  // Updates speed of left wheel motor only if a different power is given.
  if(new_leftpower != leftpower){
    leftpower = new_leftpower;
    if(leftpower < 0){
      Serial.println(leftpower);
      motorBackward(left, abs(leftpower));
    } 
    else {
      motorForward(left, leftpower);
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
}
//REWORK WITH NEW MOTORS
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

  catchflag = 0;

}

// Catch script
void move_catch() {

  if (catchflag == 0) {

    catchflag = 1;

    Serial.println("Catching");
    //lift and move forward
    motorBackward(1, 60);
    motorForward(4, 40);
    motorForward(5, 40);
    delay(450);
    motorStop(kicker);
    delay(250);
    //catch
    motorForward(1, 100);  
    delay(250);
    motorStop(kicker);
    force_stop();
    delay(1000);
  }
  else {
    Serial.println("Already catching");
  }

}

void rst_catchflag() {
  catchflag = 0;
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



