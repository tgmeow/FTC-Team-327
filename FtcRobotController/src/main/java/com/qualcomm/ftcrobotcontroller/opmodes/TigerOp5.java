package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;
import com.tiger.library.controller.ButtonState;
import com.tiger.library.controller.Controller;

/**
 * TigerOp4
 * Last Modified 12-8-2015
 * Using ButtonState and Controller from lasarobotics. Added button timed delay to Controller
 */
public class TigerOp5 extends OpMode {

    //FINAL VARIABLES
    final double ARM_MIN_RANGE  = 0.0;  //ARM or servo max min
    final double ARM_MAX_RANGE  = 1.00; //ARM or servo max min
    private final double ARM_DELTA = 0.1;       // amount to change the arm servo position.
    private final double BUTTON_HOLD_DELAY = 250;    //Custom StandardButton Delay
    private final int WHEEL_ARM_DELTA = 36;

    /*******************
     *  OBJECTS AND STUFF
     **********************/
    Controller controller1, controller2;
    DcMotorController motorControllerArm;
    DcMotor motorRearLeft, motorRearRight, motorFrontLeft, motorFrontRight;
    DcMotor motorArm1, motorArm2, motorLift1, motorLift2;
    ServoController servoController;
    Servo servo1, servo2;

    double servo1Position = 0.5; // position of the arm servo.
    double servo2Position = 0.5; // position of the arm servo.

    /**
     * Constructor
     */
    public TigerOp5(){
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
        motorControllerArm = hardwareMap.dcMotorController.get("motor_controller_arm");
        //Left Drive Motors
        motorFrontLeft = hardwareMap.dcMotor.get("motor_front_left");
        motorFrontRight = hardwareMap.dcMotor.get("motor_front_right");
        //Right Drive Motors
        motorRearLeft = hardwareMap.dcMotor.get("motor_rear_left");
        motorRearRight = hardwareMap.dcMotor.get("motor_rear_right");
        //SET MOTOR DIRECTIONS
        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE);
        motorRearLeft.setDirection(DcMotor.Direction.REVERSE);
        motorFrontRight.setDirection(DcMotor.Direction.FORWARD);
        motorRearRight.setDirection(DcMotor.Direction.FORWARD);

        //MOTOR ARMS
        motorArm1 = hardwareMap.dcMotor.get("motor_arm_1");

        motorArm2 = hardwareMap.dcMotor.get("motor_arm_2");
        motorArm2.setDirection(DcMotor.Direction.FORWARD);
//        motorArm2.setPower(1);
//        motorArm2.setMode(DcMotorController.RunMode.RESET_ENCODERS);
//        motorArm2.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
//        motorArm2.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
 //       motorArm2.setTargetPosition(motorArm1.getCurrentPosition());


        //MOTOR LIFT
        motorLift1 = hardwareMap.dcMotor.get("motor_lift_1");
        motorLift2 = hardwareMap.dcMotor.get("motor_lift_2");

        //Servo Controller
        servoController = hardwareMap.servoController.get("servo_controller");
        //Servo Motors
        servo1 = hardwareMap.servo.get("servo1"); //!TODO RENAME THIS!!
        servo2 = hardwareMap.servo.get("servo2"); //!TODO RENAME THIS!!

        /*************************
         * END OBJECTS
         *************************/

        servo1Position = 0.6; //SET INITIAL ARM POSITION
        servo2Position = 0.6;
        //SWITCH SERVO DIRECTIONS EASILY
        servo1.setDirection(Servo.Direction.FORWARD);
        servo2.setDirection(Servo.Direction.REVERSE);
    }

    @Override
    public void loop() {
        // UPDATES CONTROLLERS FROM GAMEPADS
        controller1.update(gamepad1);
        controller2.update(gamepad2);

        /*****************************************************************
         * DRIVE MOTORS
         ****************************************************************/
        float leftStickY = controller1.left_stick_y;  //Left Stick up down
        float rightStickY = controller1.right_stick_y; //Left Stick left right

        // OPTIONAL: Allows for math calculations before set power
        float rightPower = rightStickY;
        float leftPower = leftStickY;

        // CLIP VALUES so that they never exceed +/- 1
        rightPower = Range.clip(rightPower, -1, 1);
        leftPower = Range.clip(leftPower, -1, 1);

        // SCALE JOYSTICK VALUES to make it easier to control more precisely at slower speeds.
        rightPower = (float) scaleInput(rightPower);
        leftPower = (float) scaleInput(leftPower);

        // write the values to the motors
        motorFrontRight.setPower(rightPower);
        motorRearRight.setPower(rightPower);
        motorFrontLeft.setPower(leftPower);
        motorRearLeft.setPower(leftPower);

        /******************************************************************
         * MOTOR ARM
         ******************************************************************/
        float leftStick1Y = controller2.left_stick_y;
        float leftStick1X = controller2.left_stick_x;

        float armDrive1 = leftStick1X;   //WHEEL
        float armDrive2 = leftStick1Y;  //ARM
        armDrive1 = Range.clip(armDrive1, -1, 1);
        armDrive2 = Range.clip(armDrive2, -1, 1);

        armDrive2 = (float) scaleInput(armDrive2);
        armDrive1 = (float) scaleInput(armDrive1);
        motorArm1.setPower(armDrive1);
        telemetry.addData("armDrive2", armDrive2);

//        if(armDrive2 > 0.05 || armDrive2 < -0.05){
            armDrive2 = (float) scaleInput(armDrive2 * 1.1);
            motorArm2.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            motorArm2.setPower(armDrive2);
 //       }
//        else if(controller1.dpad_down == ButtonState.PRESSED) {
//            motorArm2.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
//            motorArm2.setPower(1);
//            motorArm2.setTargetPosition(motorArm2.getCurrentPosition() + WHEEL_ARM_DELTA);
//        } else if(controller1.dpad_down == ButtonState.HELD) {
//            if (System.currentTimeMillis() - controller1.dpad_downHoldTime >= BUTTON_HOLD_DELAY) {
//                motorArm2.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
//                motorArm2.setTargetPosition(motorArm2.getCurrentPosition() + (int) (WHEEL_ARM_DELTA / 3.0));
//                motorArm2.setPower(1);
//            }
//        }  else if(controller1.dpad_up == ButtonState.PRESSED) {
//            motorArm2.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
//            motorArm2.setTargetPosition(motorArm2.getCurrentPosition() - WHEEL_ARM_DELTA);
//            motorArm2.setPower(1);
//        } else if(controller1.dpad_up == ButtonState.HELD) {
//            if (System.currentTimeMillis() - controller1.dpad_upHoldTime >= BUTTON_HOLD_DELAY) {
//                motorArm2.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
//                motorArm2.setTargetPosition(motorArm2.getCurrentPosition() - (int) (WHEEL_ARM_DELTA / 3.0));
//                motorArm2.setPower(1);
//            }
//        }
//        else {
//            motorArm2.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
//            motorArm2.setTargetPosition(motorArm2.getCurrentPosition());
//            motorArm2.setPower(0.3);
//        }


        /******************************************************************
         * MOTOR RULER GRAB
         ****************************************************************/
        float rightStick2Y = controller2.right_stick_y;

        float liftArm = rightStick2Y;
//        float liftArm1 = rightStick2Y;

        liftArm = Range.clip(liftArm, -1, 1);
//        liftArm1 = Range.clip(liftArm1, -1, 1);

        liftArm = (float) scaleInput(liftArm);
//        liftArm1 = (float) scaleInput(liftArm1);

        motorLift1.setPower(liftArm);
//        motorLift2.setPower(liftArm1);

        /******************************************************************
         * SERVO MOTORS
         ******************************************************************/
        servo1Position = 0.5 + (controller1.left_trigger/2.0);
        servo2Position = 0.5 + (controller1.right_trigger/2.0);
//        if(controller1.a == ButtonState.PRESSED){
//            servo1Position += ARM_DELTA;
//            servo2Position -= ARM_DELTA;
//        }
//        else if(controller1.a == ButtonState.HELD){
//            if(System.currentTimeMillis() - controller1.aHoldTime >= BUTTON_HOLD_DELAY) {
//                servo1Position += (ARM_DELTA / 3.0);
//                servo2Position -= (ARM_DELTA / 3.0);
//            }
//        }
//        if(controller1.y == ButtonState.PRESSED){
//            servo1Position -= ARM_DELTA;
//            servo2Position += ARM_DELTA;
//        }
//        else if(controller1.y == ButtonState.HELD){
//            if(System.currentTimeMillis() - controller1.yHoldTime >= BUTTON_HOLD_DELAY) {
//                servo1Position -= ARM_DELTA / 3.0;
//                servo2Position += ARM_DELTA / 3.0;
//            }
//        }

        // clip the position values so that they never exceed their allowed range.
        servo1Position = Range.clip(servo1Position, ARM_MIN_RANGE, ARM_MAX_RANGE);
        servo2Position = Range.clip(servo2Position, ARM_MIN_RANGE, ARM_MAX_RANGE);

        // write position values to the wrist and claw servo
        servo1.setPosition(servo1Position);
        servo2.setPosition(servo2Position);

        // Send telemetry data back to driver station.
        telemetry.addData("arm1", servo1.getPosition());
        telemetry.addData("arm2", servo2.getPosition());
        telemetry.addData("leftF motor", motorFrontLeft.getPower());
        telemetry.addData("rightF motor", motorFrontRight.getPower());
    }

    /*****
     * Runs when the op mode is disabled
     */
    @Override
    public void stop() {
    }

    /******
     * Scales the joystick input so for low joystick values, the
     * scaled value is less than linear.  This is to make it easier to drive the robot more precisely at slower speeds.
     * (Decreases values below .7ish and increases above)
     *******/
    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };
        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);
        if (index < 0) {
            index = -index;
        } if (index > 16) {
            index = 16;
        }

        if (dVal < 0)
            return -scaleArray[index];
         else return scaleArray[index];

    }
}
