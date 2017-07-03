package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Théo Friberg on 13.6.2017.
 */

        @TeleOp(name="Fidgut Spinnerino", group="Olari")
        public class FirstOpMode extends LinearOpMode{

    public static double[] vecFor(double angle){
        return new double[]{Math.sin(angle), Math.cos(angle)};
    }

    public static double[] normalize(double[] input){
        double c = Math.sqrt(input[0]*input[0] + input[1]*input[1]);
        if(c != 0){
            return new double[]{input[0]/c, input[1]/c};
        }else{
            throw new ArithmeticException("Division by zero");
        }
    }

    public static double angleFrom(double[] input) throws ArithmeticException{
        double[] vec = normalize(input);
        double base = Math.acos(vec[1]);
        if(vec[0] < 0){
            base = Math.PI*2 - Math.acos(vec[1]);
        }
        if(vec[1] == 0){
            if(vec[0] > 0){
                return Math.PI / 2;
            }else if(vec[0] < 0){
                return -Math.PI / 2;
            }else{
                throw new ArithmeticException("Angle for zero vector.");
            }
        }
        return base;
    }

    double lengthOf(double[] vector){
        return Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1]);
    }

    double[] toLocalCoordinates(double[] inputVector, double globalRotation){
        if(lengthOf(inputVector) != 0) { // The angle is only defined for a non-null vector
            return vecFor(angleFrom(inputVector) - globalRotation);
        }else{
            return new double[]{0, 0};
        }
    }

    @Override
            public void runOpMode() throws InterruptedException {


                // Hardware aquirement

                DcMotor[] motors = new DcMotor[]{hardwareMap.dcMotor.get("moottori1"), hardwareMap.dcMotor.get("moottori2"), hardwareMap.dcMotor.get("moottori3")};
                DcMotor[] hetulat = new DcMotor[]{hardwareMap.dcMotor.get("harja1"), hardwareMap.dcMotor.get("harja2"), hardwareMap.dcMotor.get("harja3")};
                DcMotor nostin = hardwareMap.dcMotor.get("nostin");
                bno055driver imutus = new bno055driver("imu", hardwareMap);

                // Diagnostic print of attached components

                for(int i = 0; i < hardwareMap.allDeviceMappings.size(); i++){
                    String line = hardwareMap.allDeviceMappings.get(i).entrySet().toString();
                    if(!line.equals("[]"))
                        telemetry.addLine(line);
                }
                telemetry.update();

                // Driver presses play

                waitForStart();

                // Clear the screen

                telemetry.update();

                double origAngle = 0;

                double comb_amount = 0;

                while (opModeIsActive()) {

                    // Reset orig angle

                    if(gamepad1.a){
                        origAngle = Math.toRadians(imutus.getAngles()[0]);
                    }

                    //  Input in global coordinates

                    double[] globalInput = new double[]{-gamepad1.left_stick_x, -gamepad1.left_stick_y};
                    double globalAngle = Math.toRadians(imutus.getAngles()[0]) - origAngle;
                    double[] localInput = toLocalCoordinates(globalInput, globalAngle);

                    // Rotation amount

                    double rotation_amount = 0.0;

                    if(gamepad1.left_bumper) {
                        rotation_amount += 1.0;
                    }

                    if(gamepad1.right_bumper){
                        rotation_amount -= 1.0;
                    }

                    // Base rotational direction of the combs.

                    if(gamepad1.dpad_down) {
                        comb_amount = -1.0;
                    }

                    if(gamepad1.dpad_up){
                        comb_amount = 1.0;
                    }

                    if(gamepad1.dpad_left){
                        comb_amount = 0;
                    }

                    // The states of the three combs

                    double[] comb_states = new double[]{comb_amount, comb_amount, comb_amount};

                    // The direction in local coordinates of the comb to invert

                    double[] comb_target = toLocalCoordinates(new double[]{-gamepad1.right_stick_x, -gamepad1.right_stick_y}, globalAngle);

                    // If a comb should be inverted

                    int comb_id = -1;

                    if(lengthOf(comb_target) > 0){

                        // Find the angle of the target comb's opposite leg

                        double comb_angle = angleFrom(comb_target);

                        // Map the angle from radian space to a 0 to 2 integer space

                        comb_id = (int) (comb_angle / (2*Math.PI/3));

                        // Pick the comb that is two places counterclockwise from the one computed
                        // NOTE: negative modulus seems screwy so this is done with a positive constant; Point here being:  nv-1 = 2 mod 3

                        comb_id = (comb_id + 1) % 3;

                        // Invert the computed comb's rotation direction

                        comb_states[comb_id] *=-1;
                    }

                    for(int i = 0; i < 3; i++){
                        hetulat[i].setPower(comb_states[i]);
                    }

                    final double[][] engine_vectors = new double[][]{
                        new double[]{1, 0},
                        new double[]{-0.5,  0.8660254038},
                        new double[]{-0.5, -0.8660254038}
                    };

                    double[] motor_proportions = new double[3];

                    for(int i = 0; i < 3; i++){

                        // Dot the input vector with the engine directions to do a component decomposition
                        motor_proportions[i] = engine_vectors[i][0] * localInput[0] + engine_vectors[i][1] * localInput[1];
                    }
                    if (rotation_amount != 0) {
                        for(int i = 0; i < 3; i++){

                            // Incorporate the rotations to the activation

                            motor_proportions[i] /= 2;
                            motor_proportions[i] += rotation_amount*0.5;
                        }
                    }

                    for(int i = 0; i < 3; i++) {

                        // Activate engines

                        motors[i].setPower(motor_proportions[i]*0.7);
                    }

                    double liftPower = 0;

                    if(gamepad1.x){
                        liftPower-=1;
                    }

                    if(gamepad1.b){
                        liftPower+=1;
                    }

                    if(gamepad1.y){
                        liftPower=-0.2;
                    }

                    nostin.setPower(liftPower);

                    telemetry.addLine("Angle from starting position: "+Math.round(imutus.getAngles()[0]) +"°");
                    telemetry.addLine("Angle from last calibration: "+Math.round(imutus.getAngles()[0]-Math.toDegrees(origAngle))+"°");
                    telemetry.addLine("Gamepad input (inverted): ["+globalInput[0]+", "+globalInput[1]+"]");
                    telemetry.addLine("Bumpers: ["+gamepad1.left_bumper+", "+gamepad1.right_bumper+"]");
                    telemetry.addLine("Local target coordinates: ["+localInput[0]+", "+localInput[1]+"]");
                    telemetry.addLine("Engine activations: ["+motor_proportions[0]+", "+motor_proportions[1]+", "+motor_proportions[2]+"]");
                    telemetry.addLine("Comb local target: ["+comb_target[0]+", "+comb_target[1]+"]");
                    telemetry.addLine("Comb activations: ["+comb_states[0]+", "+comb_states[1]+", "+comb_states[2]+"]");
                    telemetry.addLine("Inverte comb: "+comb_id);
                    telemetry.update();
                }

    }
}
