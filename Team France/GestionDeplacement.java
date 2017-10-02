package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

public class GestionDeplacement implements Runnable {
    HardwareWeRobotJ robot;
    Gamepad gamepad;
    protected double R1x, R1y;
    boolean killed;

public GestionDeplacement(HardwareWeRobotJ robot, Gamepad gamepad){
    Thread thread = new Thread(this);
    this.robot = robot;
    this.gamepad = gamepad;
    this.killed = false;
    initDeplacement();
    thread.start();
}

    public void initDeplacement()
    {
        R1x = gamepad.right_stick_x;
        R1y = -gamepad.right_stick_y;
    }

    /////////////////////////////FONCTION MOUVEMENTS ROBOTS////////////////////

    protected void avance(double vitesse){
        robot.AvDMoteur.setPower(vitesse);
        robot.AvGMoteur.setPower(1.1*vitesse);
        robot.ArDMoteur.setPower(vitesse);
        robot.ArGMoteur.setPower(1.1*vitesse);
    }

    protected void tourne(double vitesse){
        double v = 0.75*vitesse;
        robot.AvDMoteur.setPower(-v);
        robot.AvGMoteur.setPower(v);
        robot.ArDMoteur.setPower(-v);
        robot.ArGMoteur.setPower(v);
    }
    static double convertPowerToCurve(double input){
        // return Range.clip(0.7*Math.pow(input, 3.0) + 0.4*input, -1.0, 1.0);
        return 0.3*Math.pow(input,3.0)+0.3*Math.pow(input,5.0)+input/Math.abs(input)*0.2;
    }

    ///////////////////////// MENU INTERMEDIAIRE //////////////////



    protected  void choixModeTri(){
        boolean choix = false;
        OPMode.menuChoix=true;
        OPMode.telemetryProxy.addLine("**** CHOIX DU MODE DE TRI ****");
        OPMode.telemetryProxy.addLine(" Bouton X : GAUCHE");
        OPMode.telemetryProxy.addLine(" Bouton B : DROITE");
        OPMode.telemetryProxy.addLine(" Bouton Y : UNE SEULE COULEUR");
        OPMode.telemetryProxy.addLine(" Bouton A : MANUEL");
        OPMode.telemetryProxy.addLine(" CHOIX ? .........");
        OPMode.telemetryProxy.update();
        while (!choix){
            if (gamepad.x){
                OPMode.modeTri = ModeTri.GAUCHE;
                choix = true;
            }
            if (gamepad.b){
                OPMode.modeTri = ModeTri.DROITE;
                choix = true;
            }
            if (gamepad.y){
                OPMode.modeTri = ModeTri.UNI;
                choix = true;
            }
            if (gamepad.a){
                OPMode.modeTri = ModeTri.MANUEL;
                choix = true;
            }
        }
        OPMode.menuChoix = false;

    }

    /////////////////////////BOUCLE PRINCIPALE/////////////////////


    @Override
    public void run(){
        while (!killed){
            initDeplacement();
            if ((R1y > Math.abs(R1x)) | (-R1y > Math.abs(R1x))) {
                avance(convertPowerToCurve(R1y));
            } else {
                tourne(convertPowerToCurve(R1x));
            }

            /////////// Selection Affichage /////////////////////
            if (gamepad.right_bumper) {
                choixModeTri();
            }

            if (gamepad.left_bumper) {
                OPMode.debug = !OPMode.debug;
            }
        }
    }
    void kill() {
        killed = true;
    }
}