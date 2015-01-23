#include "SDPArduino.h"
#include "SerialCommand.h"
#include "Wire.h"

// Speed Constants
int MAX_SPEED = 100;
int MIN_SPEED = 10;

// Communications
SerialCommand comm;


void setup(){
  SDPsetup();
  
  comm.addCommand("A_STOP", stop_motor);
  comm.addCommand("A_STOP_ALL", stop_all);
  
  comm.addCommand("A_MOVE_UP", move_up);
  comm.addCommand("A_MOVE_DOWN", move_down);
  
  comm.addCommand("A_ROTATE", rotate);

  comm.addCommand("A_TURN_LEFT", turn_left);
  comm.addCommand("A_TURN_RIGHT", turn_right);
  
  comm.addCommand("A_SET_CATCH", set_catch);
  comm.addCommand("A_SET_KICK", set_kick);
  comm.addCommand("A_SET_GRAB", set_grab);
  comm.addCommand("A_SET_SHOOT", set_shoot);

}

void loop(){
  
  comm.readSerial();
  
}

void stop_motor()
{
  char *motor_num_in;
  int motor_num;
  motor_num_in = comm.next();
  
  if(motor_num_in != NULL)
  {
    motor_num = atof(motor_num_in);
    motorStop(motor_num);
  }
  
}

void stop_all()
{
  motorAllStop();

}

void move_up()
{
  set_move(1);
}

void move_down()
{
  set_move(2);
}


void set_move(int dir)
{
  char *left_in;
  char *right_in;
  int left_speed;
  int right_speed;
  left_in = comm.next();
  right_in = comm.next();
  
  if (left_in != NULL && right_in != NULL)
  {
    left_speed = atof(left_in);
    right_speed = atof(right_in);
    if (left_speed < MIN_SPEED)
    {
      left_speed = MIN_SPEED;
    }
    if (left_speed > MAX_SPEED)
    {
      left_speed = MAX_SPEED;
    }
    if (right_speed < MIN_SPEED)
    {
      right_speed = MIN_SPEED;
    }
    if (right_speed > MAX_SPEED)
    {
      right_speed = MAX_SPEED;
    }
    
    /*Check whether we are moving up or down */
    if(dir == 1)
    {
      /*motorForward(motorNum, motorPower)*/
      motorForward(1, left_speed); /* set the power of the left motor (num 1)*/
      motorForward(2, right_speed); /* set the power of the right motor (num 2)*/
    }
    if(dir == 2)
    {
      /*motorForward(motorNum, motorPower)*/
      motorBackward(1, left_speed);
      motorBackward(2, right_speed);
    }
    
    
    
  } 
}

void turn_left()
{
  set_turn(1);
}

void turn_right()
{
  set_turn(2);
}

/*if direction = 1 => LEFT, if direction = 2 => RIGHT*/
void set_turn(int dir)
{
  /* TURN LEFT */
  if(dir == 1)
  {
  }
  
  /* TURN RIGHT */
  if(dir == 2)
  {
  }
}

void rotate(){    
  
  char *dir_in;
  int dir = 1;
  
  dir_in = comm.next();
  
  dir = atoi(dir_in);
  
   //Rotates the robot by moving one engine forward and the other backwards
    if (dir == 1) {
      motorBackward(1, 50);
      motorForward(2, 50);
    }
    else if (dir == 2) {
      motorBackward(2, 50);
      motorForward(1, 50);
    }
   
}

void set_catch()
{
  
}

void set_kick()
{
  
}

void set_grab()
{
  
}

void set_shoot()
{
  
}

  

