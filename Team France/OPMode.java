/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import android.os.AsyncTask;
import android.view.KeyEvent;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.util.Range;
//import com.qualcomm.robotcore.util.Range;

//import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * This OpMode uses WeRobotJ hardware class to define the devices on the robot.
 * All device access is managed through the HardwareWeRobotJ class.
 * The code is structured as a LinearOpMode
 *
 * This particular OpMode executes a ...
 */

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

enum ModeTri{
    DROITE,
    GAUCHE,
    UNI,
    MANUEL
}

@TeleOp(name="WeRobot: mainV1", group="WeRobot")
//@Disabled
public class OPMode extends LinearOpMode {

    /* Declare OpMode members. */
    protected HardwareWeRobotJ robot           = new HardwareWeRobotJ();
    protected static SorterThread  TriThread;
    private Gamepad_handler_2 mGamepadHandler;
    private GestionDeplacement deplThread;
    public static boolean debug = false;
    public static boolean menuChoix = false;
    public static Telemetry telemetryProxy;
    public static int rtPosCible = 0;
    public static boolean opModActif ;
    public static ModeTri modeTri ;
    protected double R1x, R1y, L1y, L1x;
   /*protected double convertPowerToCurve(double input){
        // return Range.clip(0.7*Math.pow(input, 3.0) + 0.4*input, -1.0, 1.0);
        return 0.3*0.5*Math.pow(input,3.0)+0.3*0.5*Math.pow(input,5.0)+input/Math.abs(input)*0.2;
    }*/



    @Override
    public void runOpMode() {

        opModActif = true;
        modeTri = ModeTri.DROITE;

        telemetryProxy = telemetry;

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        double initSortieO = 0.3;
        double initSortieB = 0.4;
        gamepad1.setJoystickDeadzone((float) 0.05);
        gamepad2.setJoystickDeadzone((float) 0.05);
        telemetry.addData("robot avec thread ", "Prêt ! ");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        debug = false;
        TriThread = new SorterThread(robot.roueTri,robot.colorSensor);
        robot.roueTri.setTargetPosition(0);
        menuChoix = false;
        rtPosCible = 0;


        /***  Avec DEUX Manettes ***/
        mGamepadHandler = new Gamepad_handler_2(robot,gamepad2);

        /*** Avec UNE Manette  ***/
        //mGamepadHandler = new Gamepad_handler_2(robot,gamepad1);


        deplThread = new GestionDeplacement(robot,gamepad1);

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive() ) {
            robot.roueTri.setMotorEnable();
            this.telem();
            // Pause for metronome tick.  40 mS each cycle = update 25 times a second.
            robot.waitForTick(40);
        }

        opModActif = false;
        this.TriThread.kill();
        this.mGamepadHandler.kill();
        this.deplThread.kill();
    }


    protected void telem(){
        if (debug){
            telemetry.addLine("**** INFO DEBUGGAGE ****");
            telemetry.addData("Sorties orange :", robot.sortieOranges.getPosition());
            telemetry.addData("Position roue tri :", robot.roueTri.getCurrentPosition());
            telemetry.addData("vraie Position cible rt :", robot.roueTri.getTargetPosition());
            telemetry.addData("vraie Position cible thread :", this.TriThread.getShuffleMotor().getTargetPosition());
            telemetry.addData("Position cible",rtPosCible);
            telemetry.addData("Vitesse roue tri  :", robot.roueTri.getPower());
            telemetry.addData("Mode roue tri     :", robot.roueTri.getMode());
            telemetry.addData("Distance (mm) :", robot.colorSensor.getDistance(DistanceUnit.MM));
            telemetry.addData("Rouge :", (float) robot.colorSensor.red() / robot.colorSensor.alpha());
            telemetry.addData("Vert  :", (float) robot.colorSensor.green() / robot.colorSensor.alpha());
            telemetry.addData("Bleu  :", (float) robot.colorSensor.blue() / robot.colorSensor.alpha());
            telemetry.addData("argb  :", (float) robot.colorSensor.argb() / robot.colorSensor.alpha());
            telemetry.addData("Rouge thread",(float) this.TriThread.getCenterColor().red());
            telemetry.addData("Vert thread",(float) this.TriThread.getCenterColor().green());
            telemetry.addData("Bleu thread",(float) this.TriThread.getCenterColor().blue());
            telemetry.addData("Distance thread",(float) this.TriThread.getCenterColor().getDistance(DistanceUnit.MM));
            telemetry.addData("position panier  :", robot.montePanier.getCurrentPosition());
           /* telemetry.addData("mode panier",robot.montePanier.getMode());
            telemetry.addData("Puissance panier",robot.montePanier.getPower());
            telemetry.addData("Freinage ?",robot.montePanier.getZeroPowerBehavior());
            telemetry.addData("Position hanger ", robot.Hanger.getCurrentPosition());
            telemetry.addData("Puissance hanger", robot.Hanger.getPower());*/
            telemetry.addData("Thread",TriThread.running);
            telemetry.addData("y stick",this.R1y);
            telemetry.addData("thread bloqué ?",this.TriThread.blocked);
            telemetry.addData("roueTrie enabled ?",robot.roueTri.isMotorEnabled());
            telemetry.addData("roueTrie busy ?",robot.roueTri.isBusy());
            //telemetry.addData("powertocurve",this.convertPowerToCurve(this.R1y));
            telemetry.addLine("");
        }
        if (!menuChoix) {
            telemetry.addLine("**** INFO ETAT ****");
            telemetry.addLine("");
            telemetry.addData("Mode de tri ", modeTri);
            telemetry.addData("Etat verrou ", (robot.verrouPanier.getPosition() == 1) ? "Fermé" : "Ouvert");
            telemetry.addData("roue tri enabled ?",robot.roueTri.isMotorEnabled());
            telemetry.addLine("");
            telemetry.addLine("**** INFO MENUS ****");
            telemetry.addLine("MENU TRI --> Bouton R1");
            telemetry.addLine("INFO DEBUGGAGE ON/OFF --> Bouton L1");
            telemetry.addLine("");
            telemetry.addLine("**** INFO TOUCHES****");
            telemetry.addLine("REGLAGE ROUE TRI   :   PAD  <--   |   -->");
            telemetry.addLine("TRI MANUEL         :   STICK GAUCHE   <--   |   -->");
            telemetry.addLine("VERROU PANIER      :   BOUTONS A   |   B");
            telemetry.addLine("PANIER             :   PAD   HAUT   |   BAS");
            telemetry.addLine("SORTIES BOULES :");
            if (modeTri == ModeTri.UNI || modeTri==ModeTri.MANUEL){
                telemetry.addLine("         Sortie droite --> Bouton Y // Sortie Gauche --> Bouton X");
            }
            /*else if (modeTri==ModeTri.MANUEL){
                telemetry.addLine("         Choisir un autre mode de tri pour activer la sortie des boules");
            }*/
            else {
                telemetry.addLine("         Sortie Boules oranges   -->   Bouton orange (Y)");
                telemetry.addLine("         Sortie Boules bleues   -->   Bouton bleu (X)");
            }
            telemetry.addLine("CROCHET             :   STICK GAUCHE   HAUT   |   BAS");
            telemetryProxy.update();
        }
    }
}