package org.firstinspires.ftc.teamcode.TeleOPs;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
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

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.EasyToggle;
//not paying attention in CS2 pog

// this is a test teleop class for testing. Do not use in competition. - Seb on may 7th, 2021.
@TeleOp(name="TestOp")
public class TestOp extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx leftFront, leftBack, rightFront, rightBack, intake, lift, ducc;
    private Servo arm1, arm2, dep, fold;
    private boolean direction, togglePrecision;
    Orientation angles;

    private double factor;
    //test
    boolean reverse;
    BNO055IMU imu;

    EasyToggle toggleA = new EasyToggle("a", false, 1, false, false);


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

        ducc = (DcMotorEx) hardwareMap.dcMotor.get("DU");
        ducc.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ducc.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ducc.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        arm1 = hardwareMap.servo.get("arm1");
        arm2 = hardwareMap.servo.get("arm2");
        dep = hardwareMap.servo.get("dep");
        fold = hardwareMap.servo.get("fold");



        arm1.setDirection(Servo.Direction.REVERSE);



        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        imu.initialize(parameters);
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        fold.setPosition(.5);
        //color sensor is named color

    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        toggleA.updateStart(gamepad1.a);
        //toggles precision mode if the right stick button is pressed
        drive();
        intake();
        duck();
        arm();
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);




        telemetry.update();
        toggleA.updateEnd();

        //deadwheel time
        // deadwheels were a lie :(
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

    public void intake(){
        if(gamepad1.left_trigger > .5){
            intake.setPower(1);
            fold.setPosition(.27);
        } else if (gamepad1.left_bumper){
            intake.setPower(-1);
            fold.setPosition(.27);
        } else {
            intake.setPower(0);
            fold.setPosition(.5);
        }


    }
    public void duck(){
        if(gamepad1.right_bumper && gamepad1.a) {
            ducc.setPower(1);
        } else if(gamepad1.right_bumper) {
            ducc.setPower(.5);
        } else {
            ducc.setPower(0);
        }
    }

    public void arm(){
        if(gamepad1.x){
            arm1.setPosition(.18);
            arm2.setPosition(.18);
        } else if (gamepad1.b){
            arm1.setPosition(.5);
            arm2.setPosition(.5);
        } else if (gamepad1.y){
            arm1.setPosition(.83);
            arm2.setPosition(.83);

        }
    }

    }