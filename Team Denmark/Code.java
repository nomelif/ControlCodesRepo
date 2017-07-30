package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImpl;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp( name = "FINAL", group = "FINAL")
public class PickUpDriving extends LinearOpMode {

    DcMotorEx leftMotor0;
    DcMotorEx leftMotor1;
    DcMotorEx rightMotor0;
    DcMotorEx rightMotor1;

    DcMotor pullUpMotor0;
    DcMotor pullUpMotor1;


    DcMotor elevator;
    DcMotor pickUp;

    CRServoImpl sorterServo;

    Servo blueGateServo;
    Servo orangeGateServo;

    CRServoImpl armServo;

    ColorSensor colorSensor;

    private static final int A = 0;
    private static final int B = 1;
    private static final int X = 2;
    private static final int Y = 3;

    private ElapsedTime period = new ElapsedTime();

    private void waitForTick( long periodMS ) throws InterruptedException {
        long remaining = periodMS - (long) period.milliseconds();

        if (remaining > 0) {
            Thread.sleep(remaining);
        }

        period.reset();
    }

    private double abs( double in ) {
        return ( in < 0 )? -in : in;
    }
    //private int sign( double in ) { return ( in > 0 )? 1: -1; }
    private void setHardware() {
        leftMotor0 = (DcMotorEx) hardwareMap.dcMotor.get("leftMotor0");
        leftMotor1 = (DcMotorEx) hardwareMap.dcMotor.get("leftMotor1");
        rightMotor0 = (DcMotorEx) hardwareMap.dcMotor.get("rightMotor0");
        rightMotor1 = (DcMotorEx) hardwareMap.dcMotor.get("rightMotor1");

        elevator = hardwareMap.dcMotor.get("elevator");
        pickUp = hardwareMap.dcMotor.get("pickUp");

        leftMotor0.setDirection(DcMotorSimple.Direction.REVERSE);
        leftMotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftMotor1.setDirection(DcMotorSimple.Direction.REVERSE);
        leftMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotor0.setDirection(DcMotorSimple.Direction.FORWARD);
        rightMotor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotor1.setDirection(DcMotorSimple.Direction.FORWARD);
        rightMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pullUpMotor0 = hardwareMap.dcMotor.get("pullUp0");
        pullUpMotor0.setDirection(DcMotorSimple.Direction.REVERSE);
        pullUpMotor1 = hardwareMap.dcMotor.get("pullUp1");
        pullUpMotor1.setDirection(DcMotorSimple.Direction.FORWARD);



        elevator.setDirection(DcMotorSimple.Direction.REVERSE);
        pickUp.setDirection(DcMotorSimple.Direction.FORWARD);

        sorterServo = (CRServoImpl) hardwareMap.crservo.get("sorter");
        sorterServo.setDirection(DcMotorSimple.Direction.REVERSE);
        armServo = (CRServoImpl) hardwareMap.crservo.get("arm");
        armServo.setDirection(DcMotorSimple.Direction.FORWARD);
        blueGateServo = hardwareMap.servo.get("blue");
        orangeGateServo = hardwareMap.servo.get("orange");

        colorSensor = hardwareMap.colorSensor.get("color1");


        blueGateServo.setDirection( Servo.Direction.REVERSE );
        blueGateServo.scaleRange( 0.55, 0.70 );
        orangeGateServo.scaleRange( 0.35, 0.50 );

        elevator.setPower(0);
        pickUp.setPower(0);
    }
    private void drive() {


        //Get input from gamepad
        double RX = gamepad1.right_stick_x;
        double LX = gamepad1.left_stick_x;

        int drive = ( gamepad1.right_trigger > 0.5 || gamepad1.left_trigger > 0.5 ) ? 1 : 0;
        drive *= ( gamepad1.left_trigger > 0.5) ? -1 : 1;

        if ( abs( RX ) > 0.25 ) {
            rightSpeed = -RX;
            leftSpeed = RX;
        } else {
            leftSpeed = drive;
            rightSpeed = drive;

            double T = 1-abs(LX);

            leftSpeed *= (LX > 0.25)? 1-T: T;
            rightSpeed *= (LX < -0.25)? 1-T: T;
        }
    }

    private boolean isGamepadButtonPress( int b ) {
        if ( b == X ) {
            if (gamepad1.x && !isPressingX) {
                isPressingX = true;
                return true;
            }
            if (!gamepad1.x) isPressingX = false;
        } else if ( b == Y) {
            if ( gamepad1.y && !isPressingY ) {
                isPressingY = true;
                return true;
            }
            if ( !gamepad1.y ) isPressingY = false;
        } else if ( b == A) {
            if ( gamepad1.a && !isPressingA ) {
                isPressingA = true;
                return true;
            }
            if ( !gamepad1.a ) isPressingA = false;
        } else if ( b == B) {
            if ( gamepad1.b && !isPressingB ) {
                isPressingB = true;
                return true;
            }
            if ( !gamepad1.b ) isPressingB = false;
        }

        return false;
    }
    private void colorDetection() {

        //Color input
        float blue = colorSensor.blue();
        float red = colorSensor.red();

        orangeTimer -= (orangeTimer > 0)? deltaTime : 0;

        if ( red > blue + 40f && red >  80f  ) {
            sorterServo.setPower(1);
            orangeTimer = 0.7;
            elevatorSpeed = 0.8f;
        }

        if ( orangeTimer <=0 )
        {
            elevatorSpeed = 1;
            sorterServo.setPower(0);
        }

        return;
    }
    private void pickUpSystem() {


        pickUpSystemRunning = ( isGamepadButtonPress(X))? !pickUpSystemRunning : pickUpSystemRunning;
        int _dir = (gamepad1.dpad_right)? -1: 1;
        //Set the pickUpSystem on/off
        elevator.setPower( (pickUpSystemRunning)? 1 * elevatorSpeed * _dir: 0 );
        pickUp.setPower( (pickUpSystemRunning)? 1 * _dir: 0 );
    }
    private void arm() {

        if ( gamepad1.dpad_up ) {
            armSpeed = 1;
            armServoStart = true;
        } else if ( gamepad1.dpad_down ) {
            armSpeed = -1;
            armServoStart = false;
        } else if ( !armServoStart ) {
            armSpeed = 0;
        }

        armServo.setPower(armSpeed);

    }
    private void pullUp() {

        if ( gamepad1.a ) {
            pullUpMotor0.setPower(1);
            pullUpMotor1.setPower(1);
        } else if ( gamepad1.b ) {
            pullUpMotor0.setPower(-1);
            pullUpMotor1.setPower(-1);
        } else {
            pullUpMotor0.setPower(0);
            pullUpMotor1.setPower(0);
        }
    }

    private boolean pickUpSystemRunning = false;
    private float elevatorSpeed = 1;
    private double leftSpeed = 0;
    private double rightSpeed = 0;
    private boolean isPressingX = false;
    private boolean isPressingY = false;
    private boolean isPressingA = false;
    private boolean isPressingB = false;
    private double orangeTimer = 0;
    private boolean armServoStart = false;
    private double armSpeed = 0;

    private double deltaTime;

    public void runOpMode() {

        try {

            setHardware();

            double _largeDeltatime = -1;

            waitForStart();

            ElapsedTime _deltaTimeClock = new ElapsedTime();
            ElapsedTime _time = new ElapsedTime();

            while ( opModeIsActive()) {

                //Gets the time since last time iteration

                deltaTime = _deltaTimeClock.milliseconds() / 1000;
                _deltaTimeClock = new ElapsedTime();

                if ( armServoStart && (int) _time.seconds() % 3 == 0 ) armSpeed = 0.1;
                else armSpeed = 0;

                if ( _time.seconds() > 1 && deltaTime > _largeDeltatime ) _largeDeltatime = deltaTime;

                if ( gamepad1.y ) {
                    int dir = ( _time.milliseconds() % 200 > 100 ) ? -1: 1;
                    leftMotor0.setPower( dir );
                    leftMotor1.setPower( dir );
                    rightMotor0.setPower( dir );
                    rightMotor1.setPower( dir );
                } else {
                    drive();
                }
                colorDetection();

                pickUpSystem();

                arm();

                pullUp();


                telemetry.addData("Y: ", gamepad1.y);
                telemetry.addData("Largest DeltaTime:", _largeDeltatime);
                telemetry.addData("Time: ", _time.seconds());
                telemetry.update();

                blueGateServo.setPosition( (gamepad1.right_bumper)? 1: 0);
                orangeGateServo.setPosition( (gamepad1.left_bumper)? 1: 0);

                //Set the speed of the the motors
                rightMotor0.setPower( rightSpeed );
                rightMotor1.setPower( rightSpeed );
                leftMotor0.setPower( leftSpeed );
                leftMotor1.setPower( leftSpeed );

                waitForTick(40);
            }
        }
        catch (InterruptedException exc) {

            telemetry.addData("ERROR: ", exc );
            telemetry.update();

            leftMotor0.setPower(0);
            leftMotor1.setPower(0);
            rightMotor0.setPower(0);
            rightMotor1.setPower(0);
            pickUp.setPower(0);
            elevator.setPower(0);
            armServo.setPower(0);

            return;
        }
        finally {

        }

    }
}

