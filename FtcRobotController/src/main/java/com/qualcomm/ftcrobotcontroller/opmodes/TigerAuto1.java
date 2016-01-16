package com.qualcomm.ftcrobotcontroller.opmodes;

import android.widget.Button;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.tiger.library.controller.ButtonState;
import com.tiger.library.controller.Controller;

/**
 * Created by tgmeow on 1/16/2016.
 * Basic Autonomous OpMode
 */
enum State {
    INITIALIZE, MOVE, CHECK, STOP

}
public class TigerAuto1 extends OpMode{

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

    State robot_state;
    double timerTime;

    /**
     * Constructor
     */
    public TigerAuto1(){
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

        robot_state = State.INITIALIZE;
    }
    public void loop() {
        // UPDATES CONTROLLERS FROM GAMEPADS
        controller1.update(gamepad1);
        controller2.update(gamepad2);

        switch (robot_state) {
            case INITIALIZE:
                robot_state = State.MOVE;
                resetTime();
                break;
            case MOVE:
                double motorPower = 0.7;
                while(this.getTime() < 3000 && controller1.b != ButtonState.PRESSED){
                    motorFrontLeft.setPower(motorPower);
                    motorFrontRight.setPower(motorPower);
                    motorRearLeft.setPower(motorPower);
                    motorRearRight.setPower(motorPower);
                }
                robot_state = State.STOP;
                break;
            case STOP:
                this.stop();
                break;
            default:
                robot_state = State.STOP;
                break;
        }

    }

    private void resetTime(){
        timerTime = System.currentTimeMillis();
    }
    private double getTime() {
        return (System.currentTimeMillis() - timerTime);
    }

}
