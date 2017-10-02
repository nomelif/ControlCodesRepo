package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

//import static org.firstinspires.ftc.teamcode.OPMode.rtPosCible;


public class Gamepad_handler_2 implements Runnable {
    double initSortieO = 0.3;
    double initSortieB = 0.4;
    protected double /*R1x,*/ R1y, L1y, L1x;
    private HardwareWeRobotJ robot;
    private Gamepad gamepad;
    private boolean killed = false;

    public Gamepad_handler_2(HardwareWeRobotJ robot, Gamepad gamepad) {
        Thread thread = new Thread(this);
        this.robot = robot;
        this.gamepad = gamepad;
        initGamepad();
        thread.start();
    }
    public void initGamepad()
    {
        /*R1x = gamepad.right_stick_x;*/
        R1y = -gamepad.right_stick_y;
        L1y = gamepad.left_stick_y;
        L1x = gamepad.left_stick_x;
    }
/////////////////////////////FONCTION MOUVEMENTS ROBOTS////////////////////

    protected void elevePanier(int pos)
    {
        robot.montePanier.setPower(0);
        robot.montePanier.setTargetPosition(pos);/*-70*/
        robot.montePanier.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.montePanier.setPower(1);
       /*- while (robot.montePanier.isBusy()) {
            if (! OPMode.opModActif){break;}
        }
        robot.montePanier.setTargetPosition(pos);
        robot.montePanier.setPower(0.4);*/
        while (robot.montePanier.isBusy()) {
            if (!OPMode.opModActif) {
                break;
            }
        }
        //robot.montePanier.setPower(1);
        robot.montePanier.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        /*robot.montePanier.setMode(DcMotor.RunMode.RUN_USING_ENCODER);*/
    }

    protected void elevePanier2(int pos)
    {
        robot.montePanier.setPower(0);
        robot.montePanier.setTargetPosition(pos);/*-40*/
        robot.montePanier.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.montePanier.setPower(1);
        while (robot.montePanier.getCurrentPosition()<pos-70){
            if (! OPMode.opModActif){break;}
        }
        robot.montePanier.setPower(0.4);
        while (robot.montePanier.isBusy()) {
            if (! OPMode.opModActif){break;}
        }
        /*robot.montePanier.setTargetPosition(pos);
        robot.montePanier.setPower(0.4);
        while (robot.montePanier.isBusy()) {
            if (!OPMode.opModActif) {
                break;
            }
        }*/
        //robot.montePanier.setPower(1);
        robot.montePanier.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        /*robot.montePanier.setMode(DcMotor.RunMode.RUN_USING_ENCODER);*/
    }

    protected void descendPanier(int pos){
        robot.montePanier.setPower(0);
       // robot.montePanier.setTargetPosition(pos+70);
        robot.montePanier.setTargetPosition(pos);//+30
        robot.montePanier.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.montePanier.setPower(0.8);
        while (robot.montePanier.getCurrentPosition()>pos+2)/*+30*/ {
            if (!OPMode.opModActif){break;}
        }
       /* robot.montePanier.setPower(0.4);
        robot.montePanier.setTargetPosition(pos);

        while (robot.montePanier.getCurrentPosition()>pos+5) {
            if (!OPMode.opModActif){break;}
        }*/
        //robot.montePanier.setTargetPosition(robot.montePanier.getCurrentPosition());
        //robot.montePanier.setPower(1);
        /*robot.montePanier.setPower(-0.2);
        robot.montePanier.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        while (robot.montePanier.getCurrentPosition()>-90){
            if (!OPMode.opModActif) {
                break;
            }
        }*/
        robot.montePanier.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.montePanier.setPower(0);


    }

    protected void gestionPanier() {
        //robot.montePanier.setPower(0);
        if (gamepad.dpad_up) {
            robot.verrouPanier.setPosition(0);
            this.elevePanier(0);
            robot.verrouPanier.setPosition(1);
        } else if (gamepad.dpad_down) {
            robot.verrouPanier.setPosition(0);
            this.descendPanier(-97);
        }
    }




    /////////////////////////BOUCLE PRINCIPALE/////////////////////




    @Override
    public void run() {
        while (!killed) {
            initGamepad();


            ////////// Suspension /////////////////

            if (Math.abs(L1y) > 0.2 && Math.abs(L1y)<0.8) {
                robot.Hanger.setPower(L1y / Math.abs(L1y) * 0.5);
            }
            else if (Math.abs(L1y)>0.8){
                robot.Hanger.setPower(L1y / Math.abs(L1y) * 0.8);
            }
            else {
                robot.Hanger.setPower(0);
            }

            if (Math.abs(R1y) > 0.2) {
                robot.montePanier.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                // robot.montePanier.setPower(GestionDeplacement.convertPowerToCurve( R1y));
                //robot.montePanier.setPower(Math.abs(R1y)/R1y);
                robot.montePanier.setPower(0);
                if (robot.montePanier.getCurrentPosition() < 0) {
                    robot.montePanier.setPower(Math.abs(R1y) / R1y);
                } else {
                    robot.montePanier.setPower(0.4);
                }

            } else {
                robot.montePanier.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                robot.montePanier.setPower(0);
                robot.montePanier.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }


            ////////// Tri manuel ///////////////

            if (Math.abs(L1x) > 0.2) {

                OPMode.rtPosCible -= L1x / Math.abs(L1x) * 96;
                robot.roueTri.setTargetPosition(OPMode.rtPosCible);
                while (robot.roueTri.isBusy()) {
                    if (!OPMode.opModActif) break;
                    ;
                }
            }


            ///////// Reglage fin roue tri ////////

            if (gamepad.dpad_left) {
                OPMode.rtPosCible += 3;
                robot.roueTri.setTargetPosition(OPMode.rtPosCible);
                while (robot.roueTri.isBusy()) {
                    if (!OPMode.opModActif) break;
                    ;
                }
            }
            if (gamepad.dpad_right) {
                OPMode.rtPosCible -= 3;
                robot.roueTri.setTargetPosition(OPMode.rtPosCible);
                while (robot.roueTri.isBusy()) {
                    if (!OPMode.opModActif) break;
                    ;
                }
            }

            gestionPanier();

            //////// Ejection boules /////////////////

            if (gamepad.y) {
                /*if(OPMode.modeTri == ModeTri.DROITE || OPMode.modeTri == ModeTri.UNI) {
                    robot.sortieOranges.setPosition(initSortieO + .25);
                    robot.waitForTick(500);
                    robot.sortieOranges.setPosition(initSortieO);
                }
                else if (OPMode.modeTri == ModeTri.GAUCHE) {
                    robot.sortieBleues.setPosition(initSortieB - .25);
                    robot.waitForTick(500);
                    robot.sortieBleues.setPosition(initSortieB);
                }*/
                if (OPMode.modeTri == ModeTri.GAUCHE) {
                    robot.sortieBleues.setPosition(initSortieB - .25);
                    robot.waitForTick(500);
                    robot.sortieBleues.setPosition(initSortieB);
                } else {
                    robot.sortieOranges.setPosition(initSortieO + .25);
                    robot.waitForTick(500);
                    robot.sortieOranges.setPosition(initSortieO);
                }
            }


            if (gamepad.x) {
               /* if (OPMode.modeTri == ModeTri.DROITE || OPMode.modeTri == ModeTri.UNI) {
                    robot.sortieBleues.setPosition(initSortieB - .25);
                    robot.waitForTick(500);
                    robot.sortieBleues.setPosition(initSortieB);
                }
                else if(OPMode.modeTri == ModeTri.GAUCHE ){
                    robot.sortieOranges.setPosition(initSortieO + .25);
                    robot.waitForTick(500);
                    robot.sortieOranges.setPosition(initSortieO);
                }*/
                if (OPMode.modeTri == ModeTri.GAUCHE) {
                    robot.sortieOranges.setPosition(initSortieO + .25);
                    robot.waitForTick(500);
                    robot.sortieOranges.setPosition(initSortieO);
                } else {
                    robot.sortieBleues.setPosition(initSortieB - .25);
                    robot.waitForTick(500);
                    robot.sortieBleues.setPosition(initSortieB);
                }
            }

            ///////////// Verrou Panier ////////////////

            if (gamepad.b) {
                robot.verrouPanier.setPosition(1);
            }
            if (gamepad.a) {
                robot.verrouPanier.setPosition(0);
            }

            sleep(10);

        }
    }

    void kill(){
        killed = true;
        stopMotors();
        OPMode.telemetryProxy.addLine("GamePad thread has been killed!");
        OPMode.telemetryProxy.update();
    }

    void stopMotors(){
        robot.AvGMoteur.setPower(0);
        robot.ArGMoteur.setPower(0);
        robot.AvDMoteur.setPower(0);
        robot.ArDMoteur.setPower(0);
        robot.Hanger.setPower(0);
        robot.montePanier.setPower(0);
        robot.roueTri.setPower(0);
    }

    private void sleep(int t){
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            OPMode.menuChoix=true;
            OPMode.telemetryProxy.addData("Interrupted Exception occured", "Line %d", e.getStackTrace()[0].getLineNumber());
            OPMode.telemetryProxy.update();

        }
    }
}
