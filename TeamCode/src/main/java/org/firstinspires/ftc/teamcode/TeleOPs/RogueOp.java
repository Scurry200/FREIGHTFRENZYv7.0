package org.firstinspires.ftc.teamcode.TeleOPs;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.EasyToggle;
import org.firstinspires.ftc.teamcode.PIDS.LiftPID;

//not paying attention in CS2 pog
//this is the competition teleop. please make it clean. Seb on May 7th, 2021.
@TeleOp(name="RogueOp")
public class RogueOp extends OpMode{
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx leftFront, leftBack, rightFront, rightBack, intake, intakeB, lift, liftB;
    private Servo v4b1, v4b2, dep;
    private CRServo duccL, duccR;
    private boolean direction, togglePrecision;
    private double factor;
    //test
    boolean reverse;
    private Rev2mDistanceSensor Distance;
    EasyToggle toggleUp = new EasyToggle("up", false, 1, false, false);
    EasyToggle toggleDown = new EasyToggle("down", false, 1, false, false);
    int top = 605;
    final int liftGrav = (int)(9.8 * 3);
    private LiftPID liftPID = new LiftPID(.05, 0, 0);
    int liftError = 0;
    int liftTargetPos = 0;
    boolean find = false;
    boolean extend = false;
    boolean succing = false;
    boolean yellow = false;
    double full = 100; //distance sensor reading for filled deposit
    double reading = 0;
    RevBlinkinLedDriver blinkinLedDriver;
//thing
    @Override
    public void init() {
        leftFront = (DcMotorEx) hardwareMap.dcMotor.get("FL");
        leftBack = (DcMotorEx) hardwareMap.dcMotor.get("BL");
        rightFront = (DcMotorEx) hardwareMap.dcMotor.get("FR");
        rightBack = (DcMotorEx) hardwareMap.dcMotor.get("BR");

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);




        intake = (DcMotorEx) hardwareMap.dcMotor.get("IN");
        lift = (DcMotorEx) hardwareMap.dcMotor.get("LI");
        intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        lift.setDirection(DcMotor.Direction.REVERSE);

        liftB = (DcMotorEx) hardwareMap.dcMotor.get("LIB");
        liftB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftB.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        liftB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        blinkinLedDriver = hardwareMap.get(RevBlinkinLedDriver.class, "lights");
        blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE);

        liftB.setDirection(DcMotor.Direction.REVERSE);

        intakeB = (DcMotorEx) hardwareMap.dcMotor.get("INB");
        intakeB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeB.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        v4b1 = hardwareMap.servo.get("v4b1");
        v4b2 = hardwareMap.servo.get("v4b2");
        dep = hardwareMap.servo.get("dep");
        duccL = hardwareMap.crservo.get("DL");
        duccR = hardwareMap.crservo.get("DR");

        duccL.setDirection(DcMotorSimple.Direction.REVERSE);

        v4b1.setDirection(Servo.Direction.REVERSE);

        Distance = (Rev2mDistanceSensor) hardwareMap.get(DistanceSensor.class, "detect");

        v4b1.setPosition(.19);
        v4b2.setPosition(.19);
        dep.setPosition(.63);




    }

    @Override
    public void start() {
        reading = Distance.getDistance(DistanceUnit.MM);
    }

    @Override
    public void loop() {
        toggleUp.updateStart(gamepad2.dpad_up);
        toggleDown.updateStart(gamepad2.dpad_down);

        //toggles precision mode if the right stick button is pressed
        if (gamepad1.left_stick_button)
            togglePrecision = true;
        else if (gamepad1.right_stick_button)
            togglePrecision = false;

        //sets the factor multiplied to the power of the motors
        factor = togglePrecision ? .3 : 1; //the power is 1/5th of its normal value while in precision mode

        // Do not mess with this, if it works, it works
        // it doesnt work
        /*
        double x = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
        double stickAngle = Math.atan2(direction ? -gamepad1.left_stick_y : gamepad1.left_stick_y, direction ? gamepad1.left_stick_x : -gamepad1.left_stick_x); // desired robot angle from the angle of stick
        double powerAngle = stickAngle - (Math.PI / 4); // conversion for correct power values
        double rightX = -gamepad1.right_stick_x; // right stick x axis controls turning
        final double leftFrontPower = Range.clip(x * Math.cos(powerAngle) - rightX, -1.0, 1.0);
        final double leftRearPower = Range.clip(x * Math.sin(powerAngle) - rightX, -1.0, 1.0);
        final double rightFrontPower = Range.clip(x * Math.sin(powerAngle) + rightX, -1.0, 1.0);
        final double rightRearPower = Range.clip(x * Math.cos(powerAngle) + rightX, -1.0, 1.0);

        //leftFront.setDirection(DcMotor.Direction.FORWARD);
        //leftBack.setDirection(DcMotor.Direction.FORWARD);
        //rightFront.setDirection(DcMotor.Direction.REVERSE);
        //rightBack.setDirection(DcMotor.Direction.REVERSE);
        leftFront.setPower(leftFrontPower * factor);
        leftBack.setPower(leftRearPower * factor);
        rightFront.setPower(rightFrontPower * factor);
        rightBack.setPower(rightRearPower * factor);
        */

        if (Math.abs(gamepad1.left_stick_y) > 0.1 || Math.abs(gamepad1.left_stick_x) > 0.1  || Math.abs(gamepad1.right_stick_x) > 0.1) {
            double FLP = gamepad1.left_stick_y - gamepad1.left_stick_x - gamepad1.right_stick_x;
            double FRP = -gamepad1.left_stick_y - gamepad1.left_stick_x - gamepad1.right_stick_x;
            double BLP = gamepad1.left_stick_y + gamepad1.left_stick_x - gamepad1.right_stick_x;
            double BRP = -gamepad1.left_stick_y + gamepad1.left_stick_x - gamepad1.right_stick_x;

            double max = Math.max(Math.max(Math.abs(FLP), Math.abs(FRP)), Math.max(Math.abs(BLP), Math.abs(BRP)));

            if (max > 1) {
                FLP /= max;
                FRP /= max;
                BLP /= max;
                BRP /= max;
            }

            if (gamepad1.right_trigger > 0.5) {
                leftFront.setPower(FLP * 0.35);
                rightFront.setPower(FRP * 0.35);
                leftBack.setPower(BLP * 0.35);
                rightBack.setPower(BRP * 0.35);
                telemetry.addData("FrontLeftPow:", FLP * 0.35);
                telemetry.addData("FrontRightPow:", FRP * 0.35);
                telemetry.addData("BackLeftPow:", BLP * 0.35);
                telemetry.addData("BackRightPow:", BRP * 0.35);
            } else {
                leftFront.setPower(FLP);
                rightFront.setPower(FRP);
                leftBack.setPower(BLP);
                rightBack.setPower(BRP);

            }

        } else {
            leftFront.setPower(0);
            rightFront.setPower(0);
            leftBack.setPower(0);
            rightBack.setPower(0);
        }



        succ();
        duccSpin();
        deposit();
        macroLift();


        telemetry.addData("lift", lift.getCurrentPosition());
        telemetry.addData("liftTargetPos", liftTargetPos);
        telemetry.addData("lift power", lift.getPower());
        telemetry.update();

        double distance = Distance.getDistance(DistanceUnit.CM);
        if(yellow)
            blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.YELLOW);
        else if(distance > 5)
            blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE);
        else
            blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.DARK_RED);


        //deadwheel time
        // deadwheels were a lie :(
        // maybe next year?
    }

    public void succ() {
        if(liftTargetPos == 0) {
            if (gamepad1.left_trigger > .5) {
                reading = Distance.getDistance(DistanceUnit.MM);
                if (reading < full) {
                    intake.setPower(1);
                    intakeB.setPower(1);
                    succing = false;
                } else {
                    intake.setPower(-1);
                    intakeB.setPower(-1);
                    succing = true;
                }
            } else if (gamepad1.left_bumper) {
                yellow = true;
                intake.setPower(1);
                intakeB.setPower(1);
                succing = false;
            } else {
                yellow = false;
                intake.setPower(0);
                intakeB.setPower(0);
                succing = false;
            }
        }
    }








    public void lift() {

        if (gamepad2.dpad_up) {
            lift.setPower(.6);
        } else if (gamepad2.dpad_down) {
            lift.setPower(-.6);
        } else {
            lift.setPower(0);
        }
    }

    public void macroLift() {

        liftError = liftTargetPos - lift.getCurrentPosition();
        if(toggleUp.nowTrue() && !succing){ // this scares me too much
            yellow = true;
            liftTargetPos = top;
            find = true;
            extend = true;
        } else if(toggleDown.nowTrue()) {
            extend = false;
            yellow = false;
            v4b1.setPosition(.19);
            v4b2.setPosition(.19);
            liftTargetPos = 0;
            find = false;
            lift.setPower(Range.clip(liftPID.getCorrection(liftError), 0, 1));
            liftB.setPower(lift.getPower());
        }
        if(find) {
            lift.setPower(Range.clip(liftPID.getCorrection(liftError), -1, 1));
            liftB.setPower(lift.getPower());
        } else {
            lift.setPower(Range.clip(liftPID.getCorrection(liftError), 0, 1));
            liftB.setPower(lift.getPower());
        }
        if(extend) {
            if(Math.abs(liftError) < 100){
                v4b1.setPosition(.81);;
                v4b2.setPosition(.81);
                extend = false;
            }
        }
        if(gamepad2.left_bumper){
            top = 350;
        }
        if(gamepad2.right_bumper){
            top = 650;
        }

    }

    public void duccSpin() {
        if (gamepad2.a) {
            duccL.setPower(-1);

            duccR.setPower(-1);
        } else {
            duccL.setPower(0);
            duccR.setPower(0);
        }
    }

    public void deposit() {
        if (gamepad2.y && !succing) {
            v4b1.setPosition(.81);
            v4b2.setPosition(.81);
            //dep position
        } else if (gamepad2.x) {
            v4b1.setPosition(.19);
            v4b2.setPosition(.19);
            //in position
        } else if (gamepad2.b && !succing) {
            v4b1.setPosition(.5);
            v4b2.setPosition(.5);
            //vertical position for asserting dominance
        }
        //tiernan bad

        if (gamepad2.right_trigger > .5 && v4b1.getPosition() > .4) {
            dep.setPosition(.23);
        } else if(reading < full){
            dep.setPosition(.46);
        } else{
            dep.setPosition(.63);
        }

    }

}
