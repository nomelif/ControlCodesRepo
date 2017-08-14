# Team Armenia's "Աստղիկ" (Astghik)

Team Armenia's robot is working by collecting the balls, sorting them by color
and keeping them in two different containers and then releasing them, once in front of appropriate window.
It is able to collect up to 25 blue balls and up to 9 orange balls.
It is also able to lift itself and hang on the pole.

## Motor and sensor wiring
Robot has eight DC motors three servos and two color sensors.

Motors forward_left, forward_right, back_left and back_right are driving wheels respectively.
Motor "kombayn" rotates the reel in front which collects the balls.
Motor podyomnik1 actuates the mechanism which feeds balls to the sorting mechanism.
Motor sort_motor controls the sorting mechanism.
Motor podyomnik2 actuates the lifting mechanism.

Servo Right_door controls the door of the container of blue balls.
Servo Left_door controls opens the door which releases the orange balls.
Servo helper actuates the mixer in blue ball's container which mixes balls to avoid obstruction.

color1 and color2 are color sensors of the sorting mechanism.
Program reads both sensors and takes the value of the one which accures first.

![Wiring Diagram](https://image.ibb.co/jBrora/ports.png)


MIT License

Copyright (c) 2017 Tumo Center for Creative Technologies

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
