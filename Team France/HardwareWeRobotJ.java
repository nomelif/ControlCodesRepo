package org.firstinspires.ftc.teamcode;

/**
 * Created by franck on 22/05/2017.
 */

//import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;



public class HardwareWeRobotJ {

    /* Public OpMode members. */
    public DcMotor  AvDMoteur   = null;
    public DcMotor  AvGMoteur  = null;
    public DcMotor  ArDMoteur   = null;
    public DcMotor  ArGMoteur  = null;
    //public DcMotor  HangerDroit   = null;
    public DcMotor  Hanger  = null;
    public DcMotorImplEx  montePanier    = null;
    public DcMotorImplEx  roueTri    = null;
    public Servo    sortieOranges    = null;
    public Servo    sortieBleues  = null;
    public Servo    verrouPanier  = null;
    public LynxI2cColorRangeSensor colorSensor = null;

    public static final double INIT_SERVB       =  0.4 ;
    public static final double INIT_SERVO       =  0.3 ;

    /* local OpMode members. */
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public HardwareWeRobotJ(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        AvDMoteur   = hwMap.dcMotor.get("avant_droit");
        AvGMoteur  = hwMap.dcMotor.get("avant_gauche");
        ArGMoteur   = hwMap.dcMotor.get("aeriere_gauche");
        ArDMoteur  = hwMap.dcMotor.get("arriere_droit");
        Hanger  = hwMap.dcMotor.get("Hgauche");
        montePanier    = (DcMotorImplEx) hwMap.dcMotor.get("montePanier");
        roueTri    = (DcMotorImplEx) hwMap.dcMotor.get("roueTri");

        // Define direction
        AvGMoteur.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        ArGMoteur.setDirection(DcMotor.Direction.FORWARD);
        Hanger.setDirection(DcMotor.Direction.FORWARD);
        AvDMoteur.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        ArDMoteur.setDirection(DcMotor.Direction.REVERSE);
        montePanier.setDirection(DcMotor.Direction.FORWARD);

        // Define ZeroPowerBehavior
        AvGMoteur.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        AvDMoteur.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        ArDMoteur.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        ArGMoteur.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        montePanier.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Hanger.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        roueTri.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Define TargetPositionTolerance (experimental)
        roueTri.setTargetPositionTolerance(2);
        montePanier.setTargetPositionTolerance(2);


        // Set all motors to zero power
        AvGMoteur.setPower(0);
        ArGMoteur.setPower(0);
        AvDMoteur.setPower(0);
        ArDMoteur.setPower(0);
        Hanger.setPower(0);
        montePanier.setPower(0);
        roueTri.setPower(0);

        // Set all motors to run with or without encoders.
        AvGMoteur.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ArGMoteur.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        AvDMoteur.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ArDMoteur.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        Hanger.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Hanger.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        montePanier.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        montePanier.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        roueTri.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        roueTri.setTargetPosition(0);
        roueTri.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Define and initialize ALL installed servos.
        sortieOranges = hwMap.servo.get("sortieOranges");
        sortieBleues = hwMap.servo.get("sortieBleues");
        verrouPanier = hwMap.servo.get("verrouPanier");
        sortieOranges.setPosition(INIT_SERVO);
        sortieBleues.setPosition(INIT_SERVB);
        verrouPanier.setPosition(1);

        // Define colorSensor.
        colorSensor = (LynxI2cColorRangeSensor) hwMap.colorSensor.get("colorSensor");

    }

    /***
     *
     * waitForTick implements a periodic delay. However, this acts like a metronome with a regular
     * periodic tick.  This is used to compensate for varying processing times for each cycle.
     * The function looks at the elapsed cycle time, and sleeps for the remaining time interval.
     *
     * @param periodMs  Length of wait cycle in mSec.
     */
    public void waitForTick(long periodMs) {

        long  remaining = periodMs - (long)period.milliseconds();

        // sleep for the remaining portion of the regular cycle period.
        if (remaining > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Reset the cycle clock for the next pass.
        period.reset();
    }
}
