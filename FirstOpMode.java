package org.firstglobal;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Created by Timo Loomets on 26/04/2017.
 */
@TeleOp(name="My First Op Mode", group="Practice-Bot")
public class FirstOpMode extends LinearOpMode {
    private DcMotorEx leftMotorBack;
    private DcMotorEx rightMotorBack;
    private DcMotorEx leftMotorFront;
    private DcMotorEx rightMotorFront;

    private DcMotorEx Hook1;
    private DcMotorEx Hook2;

    private DcMotor FrontRoller;
    private DcMotor BackRoller;
    private Servo MyServo;
    private Servo Red_door;
    private Servo Blue_door;
    private Servo Blocker;

    private ColorSensor MySensor1;
    private ColorSensor MySensor2;
    private ColorSensor MySensor3;
    private ColorSensor MySensor4;

    private ElapsedTime period = new ElapsedTime();

    private void waitForTick(long periodMs) throws java.lang.InterruptedException
    {
        long remaining = periodMs - (long)period.milliseconds();
        if (remaining > 0)
        {
            Thread.sleep(remaining);
        }
        period.reset();
    }

    private void MotorMode(DcMotor.RunMode mode)
    {
        leftMotorBack.setMode(mode);
        leftMotorFront.setMode(mode);
        rightMotorBack.setMode(mode);
        rightMotorFront.setMode(mode);
    }
    private void MotorPower(double power)
    {
        leftMotorFront.setPower(power);
        leftMotorBack.setPower(power);
        rightMotorFront.setPower(power);
        rightMotorBack.setPower(power);
    }

    @Override
    public void runOpMode()
    {
        int running = 0;
        float PIIR = 0f;
        double ServoPos = 0.5;
        double ServoPos2 = 0.5;
        float lowSpeed = 0.35f;
        long targetTime = System.currentTimeMillis();
        long shakeTime = System.currentTimeMillis();
        boolean shakeCheck = true;
        boolean isBusy;
        double redPos = 0.05;
        double bluePos = 0.7;
        boolean switched = false;

        boolean seenRed3 = false;
        boolean seenBlue3 = false;
        boolean seenRed4 = false;
        boolean seenBlue4 = false;

        long waitRed3 = System.currentTimeMillis();
        long waitBlue3 = System.currentTimeMillis();
        long waitRed4 = System.currentTimeMillis();
        long waitBlue4 = System.currentTimeMillis();

        float S0 = 0.60f;
        float S1 = 0.95f;
        float P0 = 0.5f;
        float P1 = 0.86f;

        leftMotorBack = (DcMotorEx) hardwareMap.get("left_back");
        leftMotorFront = (DcMotorEx) hardwareMap.get("left_front");
        rightMotorBack = (DcMotorEx) hardwareMap.get("right_back");
        rightMotorFront = (DcMotorEx) hardwareMap.get("right_front");

        Hook1 = (DcMotorEx) hardwareMap.get("hook1");
        Hook2 = (DcMotorEx) hardwareMap.get("hook2");
        Hook2.setDirection(DcMotorEx.Direction.REVERSE);
        Hook1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        Hook2.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        leftMotorFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftMotorBack.setDirection(DcMotorSimple.Direction.REVERSE);
        FrontRoller = hardwareMap.dcMotor.get("front_roller");
        BackRoller = hardwareMap.dcMotor.get("back_roller");
        MyServo = hardwareMap.servo.get("servo1");
        Red_door = hardwareMap.servo.get("red_door");
        Blue_door = hardwareMap.servo.get("blue_door");
        Red_door.setDirection(Servo.Direction.REVERSE);

        Blocker = hardwareMap.servo.get("blocker");

        MySensor1 = hardwareMap.colorSensor.get("color_sensor1");
        MySensor1.enableLed(true);
        MySensor2 = hardwareMap.colorSensor.get("color_sensor2");
        MySensor2.enableLed(true);
        MySensor3 = hardwareMap.colorSensor.get("color_sensor3");
        MySensor3.enableLed(true);
        MySensor4 = hardwareMap.colorSensor.get("color_sensor4");
        MySensor4.enableLed(true);

        leftMotorFront.setPower(0);
        rightMotorFront.setPower(0);
        leftMotorBack.setPower(0);
        rightMotorBack.setPower(0);

        Blue_door.setPosition(S0);
        Red_door.setPosition(P0);
        Blocker.setPosition(0.5);

        telemetry.addData("Say", "Hello Driver");
        telemetry.update();

        waitForStart();

        try
        {
            while(opModeIsActive()) {
                float lsx1 = -gamepad1.left_stick_x/2;
                boolean lbumper1 = gamepad1.left_bumper;
                boolean rbumper1 = gamepad1.right_bumper;
                float ltr1 = gamepad1.left_trigger;
                float rtr1 = gamepad1.right_trigger;
                boolean a1 = gamepad1.a;
                boolean b1 = gamepad1.b;
                boolean x1 = gamepad1.x;
                boolean y1 = gamepad1.y;
                boolean left1 = gamepad1.dpad_left;
                boolean right1 = gamepad1.dpad_right;
                boolean up1 = gamepad1.dpad_up;
                boolean down1 = gamepad1.dpad_down;

                boolean lbumper2 = gamepad2.left_bumper;
                boolean rbumper2 = gamepad2.right_bumper;
                float ltr2 = gamepad2.left_trigger;
                float rtr2 = gamepad2.right_trigger;
                boolean x2 = gamepad2.x;
                boolean b2 = gamepad2.b;
                boolean y2 = gamepad2.y;
                boolean a2 = gamepad2.a;
                boolean up2 = gamepad2.dpad_up;
                boolean down2 = gamepad2.dpad_down;
                boolean left2 = gamepad2.dpad_left;
                boolean right2 = gamepad2.dpad_right;

                int red1 = MySensor1.red();
                int blue1 = MySensor1.blue();
                int red2 = MySensor2.red();
                int blue2 = MySensor2.blue();
                int red3 = MySensor3.red();
                int blue3 = MySensor3.blue();
                int red4 = MySensor4.red();
                int blue4 = MySensor4.blue();

                if(up1){
                    Hook1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
                    Hook2.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
                    Hook1.setPower(1);
                    Hook2.setPower(1);
                }
                else if(down1){
                    Hook1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
                    Hook2.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
                    Hook1.setPower(-1);
                    Hook2.setPower(-1);
                }
                else if(a1){
                    Hook1.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
                    Hook2.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
                    Hook1.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
                    Hook2.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
                    Hook1.setTargetPosition(300);
                    Hook2.setTargetPosition(300);
                }
                else if(Hook1.getMode() == DcMotorEx.RunMode.RUN_USING_ENCODER){
                    Hook1.setPower(0);
                    Hook2.setPower(0);
                }

                isBusy = Blocker.getPosition() != ServoPos2 || (MyServo.getPosition() != 0.32);

                if (b2)
                {
                    redPos = 0.05;
                    bluePos = 0.7;
                    switched = false;
                }
                else if (x2)
                {
                    redPos = 0.7;
                    bluePos = 0.05;
                    switched = true;
                }

                if (red3 - blue3 < -50) {
                    seenBlue3 = true;
                    waitBlue3 = System.currentTimeMillis() + 1500;
                } else if (waitBlue3 - System.currentTimeMillis() <= 0) {
                    seenBlue3 = false;
                }
                if (red3 - blue3 > 50) {
                    seenRed3 = true;
                    waitRed3 = System.currentTimeMillis() + 1500;
                } else if (waitRed3 - System.currentTimeMillis() <= 0) {
                    seenRed3 = false;
                }
                if (red4 - blue4 < -50) {
                    seenBlue4 = true;
                    waitBlue4 = System.currentTimeMillis() + 1500;
                } else if (waitBlue4 - System.currentTimeMillis() <= 0) {
                    seenBlue4 = false;
                }
                if (red4 - blue4 > 50) {
                    seenRed4 = true;
                    waitRed4 = System.currentTimeMillis() + 1500;
                } else if (waitRed4 - System.currentTimeMillis() <= 0) {
                    seenRed4 = false;
                }

                if ((red1 - blue1 < -50 && targetTime - System.currentTimeMillis() <= 0) || (red2 - blue2 < -50 && targetTime - System.currentTimeMillis() <= 0))
                {
                    ServoPos = redPos;
                    targetTime = System.currentTimeMillis() + 750;
                } else if ((red1 - blue1 > 50 && targetTime - System.currentTimeMillis() <= 0) || (red2 - blue2 > 50 && targetTime - System.currentTimeMillis() <= 0))
                    ServoPos = bluePos;
                    targetTime = System.currentTimeMillis() + 750;
                } else if (targetTime - System.currentTimeMillis() <= 0)
                {
                    ServoPos = 0.32;
                }

                if(left2){
                    ServoPos = redPos;
                }
                else if(right2){
                    ServoPos = bluePos;
                }

                if (seenBlue3 && seenRed4) {
                    ServoPos2 = 0.1;
                } else if (seenRed3 && seenBlue4) {
                    ServoPos2 = 0.1;
                } else {
                    ServoPos2 = 0.5;
                }

                Blocker.setPosition(ServoPos2);

                MyServo.setPosition(ServoPos);

                if(right1){
                    MotorPower(0);
                    MotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    MotorMode(DcMotor.RunMode.RUN_TO_POSITION);
                    leftMotorFront.setTargetPosition(390);
                    leftMotorBack.setTargetPosition(390);
                    rightMotorFront.setTargetPosition(-390);
                    rightMotorBack.setTargetPosition(-390);
                    MotorPower(1);
                } else if (left1) {
                    MotorPower(0);
                    MotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    MotorMode(DcMotor.RunMode.RUN_TO_POSITION);
                    leftMotorFront.setTargetPosition(-390);
                    leftMotorBack.setTargetPosition(-390);
                    rightMotorFront.setTargetPosition(390);
                    rightMotorBack.setTargetPosition(390);
                    MotorPower(1);
                } else if (rtr1 > PIIR) {
                    MotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    leftMotorBack.setPower(Math.max(Math.min(1, 1 - lsx1*2), -1));
                    rightMotorBack.setPower(Math.max(Math.min(1, 1 + lsx1*2), -1));
                    leftMotorFront.setPower(Math.max(Math.min(1, 1 - lsx1*2), -1));
                    rightMotorFront.setPower(Math.max(Math.min(1, 1 + lsx1*2), -1));
                } else if (rbumper1) {
                    MotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    leftMotorBack.setPower(Math.max(Math.min(1, lowSpeed - lsx1), -1));
                    rightMotorBack.setPower(Math.max(Math.min(1, lowSpeed + lsx1), -1));
                    leftMotorFront.setPower(Math.max(Math.min(1, lowSpeed - lsx1), -1));
                    rightMotorFront.setPower(Math.max(Math.min(1, lowSpeed + lsx1), -1));
                } else if (ltr1 > PIIR) {
                    MotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    leftMotorBack.setPower(Math.max(Math.min(1, -1 - lsx1*2), -1));
                    rightMotorBack.setPower(Math.max(Math.min(1, -1 + lsx1*2), -1));
                    leftMotorFront.setPower(Math.max(Math.min(1, -1 - lsx1*2), -1));
                    rightMotorFront.setPower(Math.max(Math.min(1, -1 + lsx1*2), -1));
                } else if (lbumper1) {
                    MotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    leftMotorBack.setPower(Math.max(Math.min(1, -lowSpeed - lsx1), -1));
                    rightMotorBack.setPower(Math.max(Math.min(1, -lowSpeed + lsx1), -1));
                    leftMotorFront.setPower(Math.max(Math.min(1, -lowSpeed - lsx1), -1));
                    rightMotorFront.setPower(Math.max(Math.min(1, -lowSpeed + lsx1), -1));
                } else if(lsx1 > 0.25 || lsx1 < -0.25) {
                    MotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    leftMotorFront.setVelocity(-lsx1/Math.abs(lsx1)*375, AngleUnit.DEGREES);
                    leftMotorBack.setVelocity(-lsx1/Math.abs(lsx1)*375, AngleUnit.DEGREES);
                    rightMotorFront.setVelocity(lsx1/Math.abs(lsx1)*375, AngleUnit.DEGREES);
                    rightMotorBack.setVelocity(lsx1/Math.abs(lsx1)*375, AngleUnit.DEGREES);
                } else if(y1){
                    MotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    if(shakeCheck && shakeTime < System.currentTimeMillis()){
                        leftMotorFront.setPower(1);
                        leftMotorBack.setPower(1);
                        rightMotorFront.setPower(1);
                        rightMotorBack.setPower(1);
                        shakeTime = System.currentTimeMillis() + 500;
                        shakeCheck = false;
                    }
                    else if (!shakeCheck && shakeTime < System.currentTimeMillis()) {
                        leftMotorFront.setPower(-1);
                        leftMotorBack.setPower(-1);
                        rightMotorFront.setPower(-1);
                        rightMotorBack.setPower(-1);
                        shakeTime = System.currentTimeMillis() + 500;
                        shakeCheck = true;
                    }
                } else if(leftMotorFront.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
                    leftMotorFront.setPower(1);
                    leftMotorBack.setPower(1);
                    rightMotorFront.setPower(1);
                    rightMotorBack.setPower(1);
                } else if(leftMotorFront.getMode() == DcMotor.RunMode.RUN_USING_ENCODER){
                    leftMotorBack.setPower(2/(1+Math.pow(Math.E,(leftMotorBack.getVelocity(AngleUnit.DEGREES)/180)))-1);//y = 2/(1+e^-x)-1
                    rightMotorBack.setPower(2/(1+Math.pow(Math.E,(rightMotorBack.getVelocity(AngleUnit.DEGREES)/180)))-1);
                    leftMotorFront.setPower(2/(1+Math.pow(Math.E,(leftMotorFront.getVelocity(AngleUnit.DEGREES)/180)))-1);
                    rightMotorFront.setPower(2/(1+Math.pow(Math.E,(rightMotorFront.getVelocity(AngleUnit.DEGREES)/180)))-1);
                }

                if (isBusy) {
                    FrontRoller.setPower(0);
                    BackRoller.setPower(0);
                } else if (rtr2 > PIIR) {
                    FrontRoller.setPower(1);
                    BackRoller.setPower(1);
                } else if (rbumper2) {
                    FrontRoller.setPower(0.25);
                    BackRoller.setPower(0.25);
                } else if (ltr2 > PIIR) {
                    FrontRoller.setPower(-1);
                    BackRoller.setPower(-1);
                } else if (lbumper2) {
                    FrontRoller.setPower(-0.25);
                    BackRoller.setPower(-0.25);
                } else {
                    FrontRoller.setPower(0);
                    BackRoller.setPower(0);
                }

                if (switched) {
                    if (b1) {
                        Red_door.setPosition(P1);
                    } else {
                        Red_door.setPosition(P0);
                    }
                    if (x1) {
                        Blue_door.setPosition(S1);
                    } else {
                        Blue_door.setPosition(S0);
                    }
                } else {
                    if (x1) {
                        Red_door.setPosition(P1);
                    } else {
                        Red_door.setPosition(P0);
                    }
                    if (b1) {
                        Blue_door.setPosition(S1);
                    } else {
                        Blue_door.setPosition(S0);
                    }
                }

                running++;
                telemetry.addData("Running", Integer.toString(running));
                telemetry.update();

                waitForTick(40);
            }
        }
        catch (java.lang.InterruptedException exc)
        {
            return;
        }
        finally {
            leftMotorBack.setPower(0);
            rightMotorBack.setPower(0);
            leftMotorFront.setPower(0);
            rightMotorFront.setPower(0);
        }
    }
}
