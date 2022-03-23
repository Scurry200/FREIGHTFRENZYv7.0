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
    private ElapsedTime retract = new ElapsedTime();
    private DcMotorEx leftFront, leftBack, rightFront, rightBack, intake, intakeB, lift, liftB;
    private Servo v4b1, v4b2, dep, duccRot, duccTilt;
    private CRServo duccL, duccR, duccEx;
    private boolean direction, togglePrecision;
    private double factor;
    //test
    boolean reverse;
    private Rev2mDistanceSensor Distance;
    EasyToggle toggleUp = new EasyToggle("up", false, 1, false, false);
    EasyToggle toggleDown = new EasyToggle("down", false, 1, false, false);
    int top = 550;
    final int liftGrav = (int)(9.8 * 3);
    private LiftPID liftPID = new LiftPID(.03, 0, 0);
    int liftError = 0;
    int liftTargetPos = 0;
    boolean find = false;
    boolean extend = false;
    boolean succing = false;
    boolean yellow = false;
    double full = 100; //distance sensor reading for filled deposit
    double reading = 0;
    double rot = .74;
    double tilt = .5;
    double extendD = 0;
    double velo = .01;
    boolean lock = false;
    int duccDirection = -1;
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

        duccRot = hardwareMap.servo.get("DRoT");
        duccTilt = hardwareMap.servo.get("DT");
        duccEx = hardwareMap.crservo.get("DE");

        Distance = (Rev2mDistanceSensor) hardwareMap.get(DistanceSensor.class, "detect");

        v4b1.setPosition(.18);
        v4b2.setPosition(.18);
        dep.setPosition(.63);


    }

    @Override
    public void start() {
        reading = Distance.getDistance(DistanceUnit.MM);
    }

    @Override
    public void loop() {
        toggleUp.updateStart(gamepad2.right_bumper/*gamepad2.dpad_up*/);
        toggleDown.updateStart(gamepad2.left_bumper/*gamepad2.dpad_down*/);
        //thanks jeff


        drive();
        ducc();
        succ();
        duccSpin();
        deposit();
        macroLift();
        if(v4b1.getPosition() < .4) {
            reading = Distance.getDistance(DistanceUnit.MM);
        }


        telemetry.addData("lift", lift.getCurrentPosition());
        telemetry.addData("liftTargetPos", liftTargetPos);
        telemetry.addData("lift power", lift.getPower());
        telemetry.addData("distance", Distance.getDistance(DistanceUnit.MM));
        telemetry.addData("rot", duccRot.getPosition());
        telemetry.addData("tilt", duccTilt.getPosition());

        telemetry.update();

        double distance = Distance.getDistance(DistanceUnit.CM);
        if(yellow)
            blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.YELLOW);
        else if(distance > 5)
            blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE);
        else
            blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.DARK_RED);


        //deadwheel time
        // deadwheels were a life :(
        // maybe next year?
    }

    public void drive(){
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
    }

    public void succ() {
        if(liftTargetPos == 0) {
            if (gamepad1.left_trigger > .5) {
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






    public void ducc(){
        extendD = gamepad2.left_stick_y;
        if(Math.abs(extendD) < .1)
            extendD = 0;
        if(Math.abs(gamepad2.right_stick_x) > .1)
            rot += velo * gamepad2.right_stick_x;
        if(Math.abs(gamepad2.right_stick_y) > .1)
            tilt += velo * gamepad2.right_stick_y;
        duccEx.setPower(Range.clip(extendD, -1, 1));
        duccRot.setPosition(Range.clip(rot, -1, 1));
        duccTilt.setPosition(Range.clip(tilt, -1, 1));

        if(gamepad2.left_stick_button)
            velo = .005;
        if(gamepad2.right_stick_button)
            velo = .01;

    }

    public void macroLift() {
        liftError = liftTargetPos - lift.getCurrentPosition();
        if(toggleUp.nowTrue() && !succing){ // this scares me too much
            yellow = true;
            liftTargetPos = top;
            find = true;
            extend = true;
            lock = true;
        } else if(toggleDown.nowTrue()) {
            extend = false;
            yellow = false;
            v4b1.setPosition(.18);
            v4b2.setPosition(.18);
            retract.reset();
            liftTargetPos = 0;
            find = false;
            lift.setPower(Range.clip(liftPID.getCorrection(liftError), 0, .2));
            liftB.setPower(lift.getPower());
            lock = false;
            top = 250;
        }
        if(find) {
            lift.setPower(Range.clip(liftPID.getCorrection(liftError), 0, .9));
            liftB.setPower(lift.getPower());
        } else {
            lift.setPower(Range.clip(liftPID.getCorrection(liftError), 0, .2));
            liftB.setPower(lift.getPower());
        }
        if(extend) {
            if(Math.abs(liftError) < 100){
                v4b1.setPosition(.81);;
                v4b2.setPosition(.81);
                extend = false;
            }
        } else {
            if(retract.milliseconds() > 100 && retract.milliseconds() < 150) {

            }

        }
        if(gamepad2.dpad_down/*gamepad2.left_bumper*/){
            top = 250;
        }
        if(gamepad2.dpad_up/*gamepad2.right_bumper*/){
            top = 550;
        }

    }

    public void duccSpin() {
        if (gamepad1.right_bumper) {
            duccL.setPower(duccDirection);

            duccR.setPower(duccDirection);
        } else {
            duccL.setPower(0);
            duccR.setPower(0);
        }
        if(gamepad1.a){
            duccDirection = 1;
        }
        if(gamepad1.b){
            duccDirection = -1;
        }
    }

    public void deposit() {
        if (gamepad2.y && !succing) {
            v4b1.setPosition(.81);
            v4b2.setPosition(.81);
            lock = true;
            //dep position
        } else if (gamepad2.b /*gamepad2.x*/) {
            v4b1.setPosition(.18);
            v4b2.setPosition(.18);
            lock = false;
            //in position
        } else if (gamepad2.x /*gamepad2.b*/ && !succing) {
            v4b1.setPosition(.5);
            v4b2.setPosition(.5);
            lock = true;
            //vertical position for asserting dominance
        }
        //tiernan bad

        if (gamepad2.right_trigger > .5 && v4b1.getPosition() > .4) {
            dep.setPosition(.23);
        } else if(lock){
            dep.setPosition(.46);
        } else {
            dep.setPosition(.63);
        }

    }

}
