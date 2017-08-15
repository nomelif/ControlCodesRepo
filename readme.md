This is the code for Team Denmarks robot, "The Last Prototype".

Our robot utilizes all 8 DC motors in addition to all 4 servos.
For the most part the names of the DcMotor/Servo objects should be pretty self-explanatory.

There are 2 DC motors for each side( e.g. leftMotor0 and leftMotor1 ), to deviler additional power to our chain drive. Even though they are cast to DcMotorEx, none of the features from the extended version are actually currently used, but they have been on/off in earlier versions of the OpMode.

There are also 2 DC motors called pullUpMotor0 and pullUpMotor1, which are used to winch the cord allowing the robot to lift itself off the ground.

The DC motor called elevator drives the elevation system, consisting of a poly cord on pulleys.

The DC motor called pickUp drives the wheels in front of the robot, which are used to pick up balls.

The Servo sorterServo, is used to push the balls off into the Orange ball container, we are here using it instead of a DC motor because we were out.

We have 2 servos called orange/blue-gateservo, which are used to open up the gates.

We have a servo used for the arm, so that we can place the hook on the bar.

The program in itself is not very complicated and (sadly) I was not able to include some "cool" maths or programming tricks. Also I am not very accustomed to programming in Java. So I have been pulling my hair out not being able to use the same features as in C/C++.
Feel free to ask me anything if you are interested, my name is Rasmus Tollund from Team Denmark, email: rasmusgtollund@gmail.com
