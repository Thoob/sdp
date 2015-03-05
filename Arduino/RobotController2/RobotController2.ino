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
#define catcher 2                            // Catcher motor
#define kicker 3                             // Kicker motor
#define minpower 30                          // Minimum Speed of the motors in power%
#define default_power 50                     // If no power argument is given, this is the default

SerialCommand sCmd;                          // The SerialCommand object

//Global powers for both movement motors
int left_power = 0;                           // Speed of left wheel
int right_power = 0;                          // Speed of right wheel

//These global variables are used to keep track of the short rotate function
int srot_power = default_power;             // Power on motor in short rotate
int interval = 300;                         // Defines the interval between commands in the short rotate functions
unsigned long function_run_time;            // Used in the Short Rotate Functions to measure time they have been running
int function_running = 0;                   // Used to determine if there is a function running and which function it is

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
  sCmd.addCommand("BRAKE", brake_motor);
  sCmd.addCommand("FSTOP", force_stop);      // Force stops all motors
  sCmd.addCommand("KICK", move_kick);        // Runs kick script
  sCmd.addCommand("CATCHUP", move_catchup);      // Runs catch script
  sCmd.addCommand("CATCHDOWN", move_catchdown);      // Runs catch script

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
  /* The program will enter the loop and first use the switch statement to check which
   function was set to be running last.
   */

  switch(function_running) {
    //Non script function
  case 0:
    break;

    //Left short rotate
  case 1:
    if (millis() > function_run_time + interval) {
      leftStop();
      rightStop();
      function_running = 0;
    }
    break;

    //Right short rotate
  case 2:
    if (millis() > function_run_time + interval) {
      leftStop();
      rightStop();
      function_running = 0;
    }
    break;

  default :
    Serial.println("I shouldn't be here");
  }

  sCmd.readSerial();                         // Processes serial commands

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

  function_running = 0;   //Non scripted function, takes priority over scripted rotates

  //This is how arguments are tokenised with SerialCommand. Extra can be added
  //by repeating this.
  char *arg1;
  char *arg2;

  arg1 = sCmd.next();
  int new_left_power = atoi(arg1);

  arg2 = sCmd.next();
  int new_right_power = atoi(arg2);




  //Stops the motors when the signal given is 0
  if(new_left_power == 0){
    motorStop(left);
  }
  if(new_right_power == 0){
    motorStop(right);
  }


  //Checks if the given speed is less than the minimum speed. If it is, 
  //it sets the given power to the minimum power. Left motor.
  if ((new_left_power != 0) && (abs(new_left_power) < minpower)) {
    new_left_power = 1.4*minpower * (new_left_power/abs(new_left_power));
  }

  if ((new_right_power != 0) && (abs(new_right_power) < minpower)) {
    new_right_power = minpower  * (new_right_power/abs(new_right_power));
  }


  // Updates speed of left wheel motor only if a different power is given.
  //This part of the function also takes into account that our left motor
  //is slower than our right motor, and so fixes the values accordingly.
  if(new_left_power != left_power){
    left_power = new_left_power;
    if(left_power < 0){
      int negleft_power = abs(left_power);

      if(negleft_power >= 30 && negleft_power < 70){
        motorBackward(left, 1.4*negleft_power);
      }
      else if(negleft_power >= 70 && negleft_power < 80){
        motorBackward(left, 1.29*negleft_power);
      }
      else if(negleft_power >= 80 && negleft_power < 90){
        motorBackward(left, 1.19*left_power);
      }
      else if(negleft_power >= 90 && negleft_power < 100){
        motorBackward(left, 1.08*negleft_power);
      }
      else{
        motorBackward(left, negleft_power);
      }
    } 
    else {
      if(left_power >= 30 && left_power < 70){
        motorForward(left, 1.4*left_power);
      }
      else if(left_power >= 70 && left_power < 80){
        motorForward(left, 1.29*left_power);
      }
      else if(left_power >= 80 && left_power < 90){
        motorForward(left, 1.19*left_power);
      }
      else if(left_power >= 90 && left_power < 100){
        motorForward(left, 1.08*left_power);
      }
      else{
        motorForward(left, left_power);
      }
    }
  }

  // Updates speed of right wheel motor only if a different power is given.
  if(new_right_power != right_power){
    right_power = new_right_power;
    if(right_power < 0){
      motorBackward(right, abs(right_power));
    } 
    else {
      motorForward(right, right_power);
    }
  }

}


// Kick script
void move_kick() {

  function_running = 0; // Set as a non scripted function. Is still blocking currently, may be changed

  char *arg1;
  char *arg2;
  int time;
  int power;

  arg1 = sCmd.next();
  time = atoi(arg1);

  arg2 = sCmd.next();
  power = atoi(arg2);

  leftStop();
  rightStop();

  Serial.println("Kicking");

  motorBackward(catcher, 100);
  delay(300);

  motorForward(kicker, power);


  delay(time);
  motorForward(catcher, 100);
  motorStop(kicker);

  delay(100);
  motorStop(catcher);


  delay(1000);

}


// Moves the catcher to a catching position
void move_catchup() {


  Serial.println("Catcher on");
  //lift and move forward
  motorBackward(catcher, 100);

}

// Stops lifting the catcher so it falls on the ball
void move_catchdown() {


  Serial.println("Catcher off");
  //lift and move forward
  motorStop(catcher);

}

// Function to stop left motor, also sets the speed to 0
// (These are used so I don't have to copy and paste this code everywhere)
void leftStop() {
  left_power = 0;
  motorStop(left);
}

// Function to stop right motor, also sets the speed to 0
void rightStop() {
  right_power = 0;
  motorStop(right);
}

// Force stops all motors
void force_stop(){
  Serial.println("Force stopping");
  left_power = 0;
  right_power = 0;
  motorAllStop(); 
}

//Script to rotate the robot quickly to the left for a specified time.
void move_shortrotL() {

  if(function_running != 1) {
    char *arg1;
    char *arg2;

    arg1 = sCmd.next();
    arg2 = sCmd.next();

    if (arg1 != NULL) {
      interval = atoi(arg1);
    } 
    else
    {
      interval = 250;
    }

    if (arg2 != NULL) {
      srot_power = atoi(arg2);
    }
    else
    {
      srot_power = default_power;
    }

    motorForward(right, srot_power);
    motorBackward(left, srot_power);

    function_running = 1;
    function_run_time = millis();
  }
}

//Script to rotate the robot quickly to the right for a specified time.
void move_shortrotR() {

  if(function_running != 2) {
    char *arg1;
    char *arg2;

    arg1 = sCmd.next();
    arg2 = sCmd.next();

    if (arg1 != NULL) {
      interval = atoi(arg1);
    } 
    else
    {
      //Left motor slightly weaker forwards, compensated here
      interval = 300;
    }

    if (arg2 != NULL) {
      srot_power = atoi(arg2);
    }
    else
    {
      srot_power = default_power;
    }

    motorForward(left, srot_power);
    motorBackward(right, srot_power);

    function_running = 2;
    function_run_time = millis();
  }

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

void brake_motor(){
  motorBrake(left, 100);
  motorStop(right);
}
