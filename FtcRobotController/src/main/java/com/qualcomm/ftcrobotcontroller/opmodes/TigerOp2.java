package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

import com.qualcomm.robotcore.util.Range;
import com.tiger.library.controller.ButtonState;
import com.tiger.library.controller.Controller;

/**
 * TigerOp2
 * Created on 10-23-2015
 * Second test of opmodes, now using ButtonState and Controller from lasarobotics
 */
public class TigerOp2 extends OpMode {

    //FINAL VARIABLES
    final double ARM_MIN_RANGE  = 0.0;  //ARM or servo max min
    final double ARM_MAX_RANGE  = 1.00; //ARM or servo max min
    private final double ARM_DELTA = 0.1;       // amount to change the arm servo position.
    private final double BUTTON_DELAY = 150;    //Custom StandardButton Delay

    // Objects and stuff
    Controller controller1, controller2;
    DcMotorController motorControllerFront, motorControllerRear;
    DcMotor motorFrontRight, motorFrontLeft, motorRearLeft, motorRearRight;
    ServoController servoController;
    Servo servo1, servo2;
    ColorSensor colorSensor;

    double servo1Position = 0.5; // position of the arm servo.
    double servo2Position = 0.5; // position of the arm servo.

    /**
     * Constructor
     */
    public TigerOp2(){

    }

    @Override
    public void init(){
        //SETUP CONTROLLERS
        controller1 = new Controller(gamepad1);
        controller2 = new Controller(gamepad2);

        /*************************
         * INITIALIZE OBJECTS
         *************************/
        //DC Motor Controllers
        motorControllerFront = hardwareMap.dcMotorController.get("motor_controller_front");
        motorControllerRear = hardwareMap.dcMotorController.get("motor_controller_rear");
        //Front Motors
        motorFrontLeft = hardwareMap.dcMotor.get("motor_front_left");
        motorFrontRight = hardwareMap.dcMotor.get("motor_front_right");
        //motorFrontLeft.setDirection(DcMotor.Direction.REVERSE);
        //Rear Motors
        motorRearLeft = hardwareMap.dcMotor.get("motor_rear_left");
        motorRearRight = hardwareMap.dcMotor.get("motor_rear_right");
        //Servo Controller
        servoController = hardwareMap.servoController.get("servo_controller");
        //Servo Motors
        servo1 = hardwareMap.servo.get("servo1"); //!TODO RENAME THIS!!
        servo2 = hardwareMap.servo.get("servo2"); //!TODO RENAME THIS!!

        //colorSensor = hardwareMap.colorSensor.get("color_sensor");
     //   colorSensor.alpha(); //TODO!

        /*************************
         * END OBJECTS
         *************************/


        servo1Position = 0.5; //SET INITIAL ARM POSITION
        servo2Position = 0.5;


    }


    @Override
    public void loop() {
        // UPDATES CONTROLLERS FROM GAMEPADS
        controller1.update(gamepad1);
        controller2.update(gamepad2);


        /******************************************************************
         * DRIVE MOTORS
         ******************************************************************/

        // throttle: left_stick_y ranges from -1 to 1
        //      where -1 is full up
        //      and 1 is full down
        // direction: left_stick_x ranges from -1 to 1
        //      where -1 is full left
        //         and 1 is full right

        float leftStickY = -gamepad1.left_stick_y;  //Left Stick up down
        float rightStickY = gamepad1.right_stick_y; //Left Stick left right

        // OPTIONAL: Allows for math calculations before set power
        float rightPower = rightStickY;
        float leftPower = leftStickY;

        // CLIP VALUES so that they never exceed +/- 1
        rightPower = Range.clip(rightPower, -1, 1);
        leftPower = Range.clip(leftPower, -1, 1);

        // SCALE JOYSTICK VALUES to make it easier to control more precisely at slower speeds.
        rightPower = (float)scaleInput(rightPower);
        leftPower =  (float)scaleInput(leftPower);

        // WRITE VALUES to the motors
        motorFrontLeft.setPower(leftPower);
        motorFrontRight.setPower(rightPower);
        motorRearLeft.setPower(leftPower);
        motorRearRight.setPower(rightPower);


        /******************************************************************
         * SERVO MOTORS
         ******************************************************************/
        if(controller1.a == ButtonState.PRESSED){
            servo1Position += ARM_DELTA;
            servo2Position -= ARM_DELTA;

        }
        else if(controller1.a == ButtonState.HELD){
            if(controller1.aHoldTime - System.currentTimeMillis() > BUTTON_DELAY) {
                servo1Position += ARM_DELTA / 2.0;
                servo2Position -= ARM_DELTA / 2.0;
            }
        }
        if(controller1.y == ButtonState.PRESSED){
            servo1Position -= ARM_DELTA;
            servo2Position += ARM_DELTA;
        }
        else if(controller1.y == ButtonState.HELD){
            if(controller1.yHoldTime - System.currentTimeMillis() > BUTTON_DELAY) {
                servo1Position -= ARM_DELTA / 2.0;
                servo2Position += ARM_DELTA / 2.0;
            }
        }

        // clip the position values so that they never exceed their allowed range.
        servo1Position = Range.clip(servo1Position, ARM_MIN_RANGE, ARM_MAX_RANGE);
        servo2Position = Range.clip(servo2Position, ARM_MIN_RANGE, ARM_MAX_RANGE);

        servo1.setDirection(Servo.Direction.FORWARD);
        servo2.setDirection(Servo.Direction.FORWARD);

        // write position values to the wrist and claw servo
        servo1.setPosition(servo1Position);
        servo2.setPosition(servo2Position);


        // Send telemetry data back to driver station.
        telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("arm1", "arm1:  " + String.format("%.2f", servo1Position));
        telemetry.addData("arm2", "arm2:  " + String.format("%.2f", servo2Position));
        telemetry.addData("left tgt pwr", "left  pwr: " + String.format("%.2f", leftPower));
        telemetry.addData("right tgt pwr", "right pwr: " + String.format("%.2f", rightPower));

    }

    /*****
     * Runs when the op mode is disabled
     */
    @Override
    public void stop() {

    }

    /******
     * Scales the joystick input so for low joystick values, the
     * scaled value is less than linear.  This is to make it easier to drive
     * the robot more precisely at slower speeds.
     * (Decreases values below .7ish and increases above)
     *******/
    double scaleInput(double dVal)  {

        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);
        if (index < 0) {
            index = -index;
        } else if (index > 16) {
            index = 16;
        }

        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        return dScale;
    }

}
