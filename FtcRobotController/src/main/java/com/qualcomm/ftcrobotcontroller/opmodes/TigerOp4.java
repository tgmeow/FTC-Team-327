package com.qualcomm.ftcrobotcontroller.opmodes;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.ftcrobotcontroller.R;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;
import com.tiger.library.controller.ButtonState;
import com.tiger.library.controller.Controller;

/**
 * TigerOp4
 * Last Modified 12-1-2015
 * Using ButtonState and Controller from lasarobotics. Added button timed delay to Controller
 * USES NXT MOTOR CONTROLLERS!!!
 */
public class TigerOp4 extends OpMode {

    //FINAL VARIABLES
    final double ARM_MIN_RANGE  = 0.0;  //ARM or servo max min
    final double ARM_MAX_RANGE  = 1.00; //ARM or servo max min
    private final double ARM_DELTA = 0.1;       // amount to change the arm servo position.
    private final double BUTTON_DELAY = 250;    //Custom StandardButton Delay

    // Objects and stuff
    Controller controller1, controller2;
    //    DcMotorController motorControllerFront, motorControllerRear;
    DcMotor motorsLeft, motorsRight;
    ServoController servoController;
    Servo servo1, servo2;


    //FOR NXT
    DcMotorController wheelsControllerNXT;
    int numOpLoops = 1;
    DcMotorController.DeviceMode devMode;

//    View relativeLayout;
//    ColorSensor colorSensor;

    double servo1Position = 0.5; // position of the arm servo.
    double servo2Position = 0.5; // position of the arm servo.

    /**
     * Constructor
     */
    public TigerOp4(){
    }

    @Override
    public void init(){

        //SET NXT DEV MODE
        devMode = DcMotorController.DeviceMode.WRITE_ONLY;

        //SETUP CONTROLLERS
        controller1 = new Controller(gamepad1);
        controller2 = new Controller(gamepad2);

        /*************************
         * INITIALIZE OBJECTS
         *************************/
        //DC Motor Controllers
//        motorControllerFront = hardwareMap.dcMotorController.get("motor_controller_front");
//        motorControllerRear = hardwareMap.dcMotorController.get("motor_controller_rear");
        wheelsControllerNXT = hardwareMap.dcMotorController.get("legacy_motor_controller");
        //Front Motors
//        motorFrontLeft = hardwareMap.dcMotor.get("motor_front_left");
//        motorFrontRight = hardwareMap.dcMotor.get("motor_front_right");
        //motorFrontLeft.setDirection(DcMotor.Direction.REVERSE);
        //Rear Motors
        motorsLeft = hardwareMap.dcMotor.get("l_motor1");
        motorsRight = hardwareMap.dcMotor.get("l_motor2");
        //Servo Controller
        servoController = hardwareMap.servoController.get("servo_controller");
        //Servo Motors
        servo1 = hardwareMap.servo.get("servo1"); //!TODO RENAME THIS!!
        servo2 = hardwareMap.servo.get("servo2"); //!TODO RENAME THIS!!

//        colorSensor = hardwareMap.colorSensor.get("color_sensor");

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
         * !!NXT!! DRIVE MOTORS
         ******************************************************************/

        // throttle: left_stick_y ranges from -1 to 1
        //      where -1 is full up
        //      and 1 is full down
        // direction: left_stick_x ranges from -1 to 1
        //      where -1 is full left
        //         and 1 is full right
// The op mode should only use "write" methods (setPower, setMode, etc) while in

        // WRITE_ONLY mode or SWITCHING_TO_WRITE_MODE
        if (allowedToWrite()) {
            float leftStickY = -gamepad1.left_stick_y;  //Left Stick up down
            float rightStickY = gamepad1.right_stick_y; //Left Stick left right

            // OPTIONAL: Allows for math calculations before set power
            float rightPower = rightStickY;
            float leftPower = leftStickY;

            // Nxt devices start up in "write" mode by default, so no need to switch modes here.
            motorsLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
            motorsRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

            // CLIP VALUES so that they never exceed +/- 1
            rightPower = Range.clip(rightPower, -1, 1);
            leftPower = Range.clip(leftPower, -1, 1);

            // SCALE JOYSTICK VALUES to make it easier to control more precisely at slower speeds.
            rightPower = (float) scaleInput(rightPower);
            leftPower = (float) scaleInput(leftPower);

            // write the values to the motors
            motorsRight.setPower(rightPower);
            motorsLeft.setPower(leftPower);
        }
        /******************************************************************
         * SERVO MOTORS
         ******************************************************************/
        if(controller1.a == ButtonState.PRESSED){
            servo1Position += ARM_DELTA;
            servo2Position -= ARM_DELTA;

        }
        else if(controller1.a == ButtonState.HELD){
            long timeDifference = System.currentTimeMillis() - controller1.aHoldTime;
            telemetry.addData("HoldTime", timeDifference);
            if(timeDifference >= BUTTON_DELAY) {
                servo1Position += (ARM_DELTA / 3.0);
                servo2Position -= (ARM_DELTA / 3.0);
            }
        }
        if(controller1.y == ButtonState.PRESSED){
            servo1Position -= ARM_DELTA;
            servo2Position += ARM_DELTA;
        }
        else if(controller1.y == ButtonState.HELD){
            long timeDifference = System.currentTimeMillis() - controller1.yHoldTime;
            if(timeDifference  >= BUTTON_DELAY) {
                servo1Position -= ARM_DELTA / 3.0;
                servo2Position += ARM_DELTA / 3.0;
            }
        }

        // clip the position values so that they never exceed their allowed range.
        servo1Position = Range.clip(servo1Position, ARM_MIN_RANGE, ARM_MAX_RANGE);
        servo2Position = Range.clip(servo2Position, ARM_MIN_RANGE, ARM_MAX_RANGE);

        //SWITCH SERVO DIRECTIONS EASILY
        servo1.setDirection(Servo.Direction.FORWARD);
        servo2.setDirection(Servo.Direction.FORWARD);

        // write position values to the wrist and claw servo
        servo1.setPosition(servo1Position);
        servo2.setPosition(servo2Position);


        // Send telemetry data back to driver station.
//        telemetry.addData("Text", "*** Robot Data***");
            telemetry.addData("arm1", servo1.getPosition());
            telemetry.addData("arm2", servo2.getPosition());

//        telemetry.addData("Clear", colorSensor.alpha());
//        telemetry.addData("RGB", "" + red * red + ", " + green * green + ", " + blue * blue);
//        telemetry.addData("Hue", hsvValues[0]);



        //NXT MOTOR THINGS
        if (numOpLoops % 17 == 0){
            // Note: If you are using the NxtDcMotorController, you need to switch into "read" mode
            // before doing a read, and into "write" mode before doing a write. This is because
            // the NxtDcMotorController is on the I2C interface, and can only do one at a time. If you are
            // using the USBDcMotorController, there is no need to switch, because USB can handle reads
            // and writes without changing modes. The NxtDcMotorControllers start up in "write" mode.
            // This method does nothing on USB devices, but is needed on Nxt devices.
            wheelsControllerNXT.setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
        }

        // Every 17 loops, switch to read mode so we can read data from the NXT device.
        // Only necessary on NXT devices.
        if (wheelsControllerNXT.getMotorControllerDeviceMode() == DcMotorController.DeviceMode.READ_ONLY) {

            // Update the reads after some loops, when the command has successfully propagated through.
            telemetry.addData("left motor", motorsLeft.getPower());
            telemetry.addData("right motor", motorsRight.getPower());
            telemetry.addData("RunMode: ", motorsLeft.getMode().toString());

            // Only needed on Nxt devices, but not on USB devices
            wheelsControllerNXT.setMotorControllerDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);

            // Reset the loop
            numOpLoops = 0;
        }

        // Update the current devMode
        devMode = wheelsControllerNXT.getMotorControllerDeviceMode();
        numOpLoops++;
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
        } else { dScale = scaleArray[index]; }

        return dScale;
    }
    // If the device is in either of these two modes, the op mode is allowed to write to the HW.
    private boolean allowedToWrite(){
        return (devMode == DcMotorController.DeviceMode.WRITE_ONLY);
    }


}
