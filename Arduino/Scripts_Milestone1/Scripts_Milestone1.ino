#include "SDPArduino.h"
#include "Wire.h"

// Speed Constants
int MIN_SPEED = 30;
int MID_SPEED = 50;
int MAX_SPEED = 100;


void setup(){
  SDPsetup();
}

void loop(){
  
 forwards_10cm();
 delay(2000);
 forwards_50cm();
 delay(2000);
 backwards();
 delay(2000);
 
}

void forwards_10cm()
{
  motorForward(4, 50);
  motorForward(5, 50);
  delay(1000);
  motorAllStop();
}

void forwards_50cm()
{
  motorForward(4, 50);
  motorForward(5, 50);
  delay(2500);
  motorAllStop(); 
  
}

void backwards()
{
  motorBackward(4,50);
  motorBackward(5,50);
  delay(2500);
  motorAllStop();
  
}

