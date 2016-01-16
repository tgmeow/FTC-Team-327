package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;


/*****
 * TigerOp1
 *
 * Created by Tiger Mou on 9/28/2015.
 *
 * 1st test of custom configuration of an OpMode
 */
public class TigerOp1 extends OpMode {


    //Used in PushBotHardware for encoder things. MAY NOT BE NECESSARY!!!
    DcMotorController motorControllerFront;
    DcMotorController motorControllerRear;

    DcMotor motorFrontRight;
    DcMotor motorFrontLeft;

    DcMotor motorRearLeft;
    DcMotor motorRearRight;

    ServoController servoController;

    // !TODO ADD BETTER NAMES!!
    Servo servo1;
    Servo servo2;

    final static double ARM_MIN_RANGE  = 0.0;
    final static double ARM_MAX_RANGE  = 1.00;
    // position of the arm servo.
    private double armPosition;
    // amount to change the arm servo position.
    private final double ARM_DELTA = 0.1;

    //Button Press Manager
    private boolean pressALast = false;           //Becomes true when button is pressed, false after 200 ms or when released
    private double pressATime = 0;                //System time the button was last pressed
    private boolean pressYLast = false;           //Becomes true when button is pressed, false after 200 ms or when released
    private double pressYTime = 0;                //System time the button was last pressed
    private final double BUTTON_DELAY = 200;      //Button Delay



    /**
     * Constructor
     */
    public TigerOp1(){
    }

    /**
     * INIT
     */
    @Override
    public void init(){
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
        //SET initial ARM position
        armPosition = 0;
    }


    /**
     * MAIN LOOP
     */
    @Override
    public void loop() {


        /******************************************************************
         * MOTORS
         *
         ******************************************************************/

        // throttle: left_stick_y ranges from -1 to 1
        //      where -1 is full up
        //      and 1 is full down
        // direction: left_stick_x ranges from -1 to 1
        //      where -1 is full left
        //         and 1 is full right

        //Left Stick up down
        float leftStickY = -gamepad1.left_stick_y;
        //Left Stick left right
        float rightStickY = gamepad1.right_stick_y;

        float rightPower = rightStickY;
        float leftPower = leftStickY;

        // clip right/left values so that the values never exceed +/- 1
        rightPower = Range.clip(rightPower, -1, 1);
        leftPower = Range.clip(leftPower, -1, 1);

        // scale joystick value to make it easier to control more precisely at slower speeds.
        rightPower = (float)scaleInput(rightPower);
        leftPower =  (float)scaleInput(leftPower);

        // write values to the motors
        motorFrontLeft.setPower(leftPower);
        motorFrontRight.setPower(rightPower);
        motorRearLeft.setPower(leftPower);
        motorRearRight.setPower(rightPower);


        /******************************************************************
         * SERVOS
         *
         ******************************************************************/
        // update the position of the arm.
        //if button is pressed and was not pressed last loop
        if (gamepad1.a) {
            //If button was NOT pressed the last loop
            if(!pressALast) {
                pressATime = System.currentTimeMillis();
                armPosition += ARM_DELTA;
                pressALast = true;
            }
            //if button WAS pressed AND held for BUTTON_DELAY
            else if((System.currentTimeMillis()- pressATime) > BUTTON_DELAY){
                armPosition += (ARM_DELTA/2);
            }

        }
        //if button is pressed and was not pressed last loop
        if (gamepad1.y) {
            //If button was NOT pressed the last loop
            if(!pressYLast) {
                pressYTime = System.currentTimeMillis();
                armPosition -= ARM_DELTA;
                pressYLast = true;
            }
            //else if button WAS pressed AND held for BUTTON_DELAY
            else if((System.currentTimeMillis()- pressYTime) > BUTTON_DELAY){
                armPosition -= (ARM_DELTA/2);
            }
        }

        //When button is released....
        if(!gamepad1.a){
            pressALast = false;
        }
        //When button is released....
        if(!gamepad1.y){
            pressYLast = false;

        }


        // clip the position values so that they never exceed their allowed range.
        armPosition = Range.clip(armPosition, ARM_MIN_RANGE, ARM_MAX_RANGE);

        // write position values to the wrist and claw servo
        servo1.setPosition(armPosition);
        servo2.setPosition(armPosition);


		/*
		 * Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */
        telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("arm", "arm:  " + String.format("%.2f", armPosition));
        telemetry.addData("left tgt pwr",  "left  pwr: " + String.format("%.2f", leftPower));
        telemetry.addData("right tgt pwr", "right pwr: " + String.format("%.2f", rightPower));

    }


    /*
    * Code to run when the op mode is disabled
    */
    @Override
    public void stop() {

    }

    /*
     * Scales the joystick input so for low joystick values, the
     * scaled value is less than linear.  This is to make it easier to drive
     * the robot more precisely at slower speeds.
     */
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
