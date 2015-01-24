#include "SDPArduino.h"
#include "SerialCommand.h"
#include "Wire.h"

// Speed Constants
int MIN_SPEED = 30;
int MID_SPEED = 50;
int MAX_SPEED = 100;

// Communications
SerialCommand comm;


void setup(){
  SDPsetup();
  
  comm.addCommand("A_STOP", stop_motor);
  comm.addCommand("A_STOP_ALL", stop_all);
  
  comm.addCommand("A_MOVE", move_special);
  comm.addCommand("A_MOVE_UP", move_up);
  comm.addCommand("A_MOVE_DOWN", move_down);
  
  comm.addCommand("A_ROTATE", rotate);
  
  comm.addCommand("A_TURN", set_turn);
  
  comm.addCommand("A_SET_CATCH", set_catch);
  comm.addCommand("A_SET_KICK", set_kick);
  
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

void stop_motor_local(int motor_num)
{
    motorStop(motor_num);
  
}

void stop_all()
{
  motorAllStop();

}

void move_special()
{
  char *speed_left;
  char *speed_right;  
  char *direction_left;
  char *direction_right;
  
  int speed_l;
  int speed_r;
  int direction_l;
  int direction_r;
  
  direction_left = comm.next();
  speed_left = comm.next();
  
  direction_right = comm.next();
  speed_right = comm.next();
  
  if(speed_left != NULL && speed_right != NULL && direction_left != NULL && direction_right != NULL)
  {
    speed_l = atof(speed_left);
    speed_r = atof(speed_right);
    direction_l = atof(direction_left);
    direction_r = atof(direction_right);
    
    if(direction_l == 1)
    {
      motorForward(1, speed_l);
    }
    if(direction_r == 1)
    {
      motorForward(2, speed_r);
    }
    if(direction_l == 2)
    {
      motorBackward(1, speed_l);
    }
    if(direction_r == 2)
    {
      motorBackward(2, speed_r);
    }
    
  }
  
  
  
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

void set_turn()
{
    rotate();
    delay(400);
    stop_all();
}

void rotate()
{    
  
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
  motorForward(3, MIN_SPEED);
  delay(400);
  stop_motor_local(3);
  delay(100);
  motorBackward(3, MIN_SPEED);
  delay(400);
  stop_motor_local(3);
}

void set_kick()
{
  char *speed_in;
  int speed_kick;
  
  if(speed_in != NULL)
  {
    speed_kick = atof(speed_in);
  }
  else
  {
    speed_kick = MID_SPEED;
  }
  
  motorForward(3, speed_kick);
  delay(400);
  stop_motor_local(3);
  
}

void set_shoot()
{
  
}

  

