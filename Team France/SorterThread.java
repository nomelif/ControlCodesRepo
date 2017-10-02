package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

class SorterThread implements Runnable{
    private DcMotorImplEx shuffleMotor;
    private LynxI2cColorRangeSensor centerColor;
    boolean running = false;
    public boolean blocked = false;
    private boolean killed = false;
    private boolean tour = true;

    public LynxI2cColorRangeSensor getCenterColor() {
        return centerColor;
    }

    public DcMotor getShuffleMotor() {
        return shuffleMotor;
    }

    SorterThread(DcMotorImplEx tri, LynxI2cColorRangeSensor distcol){
        Thread thread = new Thread(this);
        shuffleMotor = tri;
        centerColor  = distcol;


        thread.start();
    }

    public void run(){
        this.shuffleMotor.setPower(1);
        running = true;
        killed=false;
        while (!killed) {
            while (running) {
                switch (OPMode.modeTri) {
                    case DROITE:
                        if (centerColor.getDistance(DistanceUnit.MM) < 60) {this.triDC(1);}
                        break;
                    case GAUCHE:
                        if (centerColor.getDistance(DistanceUnit.MM) < 60) {this.triDC(-1);}
                        break;
                    case UNI:
                        if (centerColor.getDistance(DistanceUnit.MM) < 60){
                            if (tour){
                                OPMode.rtPosCible -= 96;
                            }
                            else {
                                OPMode.rtPosCible += 96;

                            }
                            tour = !tour;
                            this.shuffleMotor.setTargetPosition(OPMode.rtPosCible);
                            while (this.shuffleMotor.isBusy()) {
                                OPMode.telemetryProxy.addLine("Stucked in isBusy UNI");
                                OPMode.telemetryProxy.addData("thread bloqué ?",blocked);
                                OPMode.telemetryProxy.addData("Position roue tri :", this.shuffleMotor.getCurrentPosition());
                                OPMode.telemetryProxy.addData("vraie Position cible rt :", this.shuffleMotor.getTargetPosition());
                                OPMode.telemetryProxy.addData("roueTrie enabled ?",this.shuffleMotor.isMotorEnabled());
                                OPMode.telemetryProxy.addData("roueTrie busy ?",this.shuffleMotor.isBusy());
                                OPMode.telemetryProxy.update();
                                if (!OPMode.opModActif) {
                                    break;
                                }
                            }
                        }
                        break;
                    case MANUEL:
                    {
                        sleep(10);
                        break;
                    }
                }
            }

                sleep(10);
        }

            stopMotors();
            sleep(10);
    }


    void kill(){
       running = false;
        killed = true;

        stopMotors();
        OPMode.telemetryProxy.addLine("Sorter thread has been killed!");
        /*FirstOpMode.telemetryProxy.update();*/
    }

    void relaunch(){running = true;}
    void stop(){running = false;}

    private void triDC(int sens) {
        /*if (this.centerColor.blue() < this.centerColor.red()){this.rtPosCible -= 96;}
        else {this.rtPosCible += 96;}*/
        if (this.centerColor.blue()<this.centerColor.red()) {
            OPMode.rtPosCible -= 96*sens;
        } else {
            OPMode.rtPosCible += 96*sens;
        }
        this.shuffleMotor.setTargetPosition(OPMode.rtPosCible);
        while (this.shuffleMotor.isBusy()) {
            blocked=true;
            /*OPMode.telemetryProxy.addLine("Stucked in isBusy");
            OPMode.telemetryProxy.addData("thread bloqué ?",blocked);
            OPMode.telemetryProxy.addData("Position roue tri :", this.shuffleMotor.getCurrentPosition());
            OPMode.telemetryProxy.addData("vraie Position cible rt :", this.shuffleMotor.getTargetPosition());
            OPMode.telemetryProxy.addData("roueTrie enabled ?",this.shuffleMotor.isMotorEnabled());
            OPMode.telemetryProxy.addData("roueTrie busy ?",this.shuffleMotor.isBusy());
            OPMode.telemetryProxy.update();*/
            if (!OPMode.opModActif) break;
        }
        blocked = false;
    }




    private void sleep(int t){
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
           OPMode.telemetryProxy.addData("Interrupted Exception occured", "Line %d", e.getStackTrace()[0].getLineNumber());
            OPMode.telemetryProxy.update();

        }
    }

    private void stopMotors(){
        //shuffleMotor.setPower(0);
    }

}