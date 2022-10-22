package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;
@Config
@Autonomous(group = "drive")
public class TestAuto extends LinearOpMode {

    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;

    static final double FEET_PER_METER = 1;

    // Lens intrinsics
    // UNITS ARE PIXELS
    // NOTE: this calibration is for the C920 webcam at 800x448.
    // You will need to do your own calibration for other configurations!
    double fx = 578.272;
    double fy = 578.272;
    double cx = 402.145;
    double cy = 221.506;

    // UNITS ARE METERS
    double tagsize = 0.166;
    int numFramesWithoutDetection = 0;
    public int TSEPos = 3;

    final float DECIMATION_HIGH = 3;
    final float DECIMATION_LOW = 2;
    final float THRESHOLD_HIGH_DECIMATION_RANGE_METERS = 1.0f;
    final int THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION = 4;

    public SampleMecanumDrive drive;
    public Drive d;

    Trajectory deposit;
    TrajectorySequence duck1;
    Trajectory duck2;

    public enum State {
        START,
        EXTEND,
        RETRACT
    }


    public Pose2d startPose;

    State state = State.START;
    ElapsedTime timer = new ElapsedTime();

    @Override
    public void runOpMode(){
        timer.reset();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        drive = new SampleMecanumDrive(hardwareMap);
        d = new Drive(hardwareMap, telemetry);
//        d.setup();

        startPose = new Pose2d(-60, -60, Math.toRadians(180));
//        startPose = new Pose2d(-60, -60, Math.toRadians(90));
        drive.setPoseEstimate(startPose);
        doCV();
        while(!isStarted() && !isStarted()){
            ArrayList<AprilTagDetection> detections = aprilTagDetectionPipeline.getDetectionsUpdate();

            if (detections != null) {
                telemetry.addData("FPS", camera.getFps());
                telemetry.addData("Overhead ms", camera.getOverheadTimeMs());
                telemetry.addData("Pipeline ms", camera.getPipelineTimeMs());

                if (detections.size() == 0) {
                    numFramesWithoutDetection++;
                    if (numFramesWithoutDetection >= THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION){
                        aprilTagDetectionPipeline.setDecimation(DECIMATION_LOW);
                        TSEPos = 3;
                    }
                } else {
                    numFramesWithoutDetection = 0;
                    if (detections.get(0).pose.z < THRESHOLD_HIGH_DECIMATION_RANGE_METERS) aprilTagDetectionPipeline.setDecimation(DECIMATION_HIGH);
                    if (detections.size() > 0) {


                        AprilTagDetection detection = detections.get(0);

                        if (detection.pose.x < 0) TSEPos = 1;
                        else if (detection.pose.x >= 0) TSEPos = 2;
                        telemetry.addData("test",detection.pose.x);
                    }
                }
            }
            telemetry.addData("TSE", TSEPos);
            telemetry.update();
        }

        waitForStart();



        telemetry.addData("Realtime analysis", TSEPos);



        Trajectory amogus = drive.trajectoryBuilder(new Pose2d(-12.74, -59.85, Math.toRadians(23.36)))
                .splineTo(new Vector2d(0.00, -41.88), Math.toRadians(36.92))
                .splineTo(new Vector2d(6.50, -41.00), Math.toRadians(7.71))
                .splineTo(new Vector2d(18.88, -57.75), Math.toRadians(-86.34))
                .splineTo(new Vector2d(39.88, -57.75), Math.toRadians(73.44))
                .addDisplacementMarker(() -> {})
                .splineTo(new Vector2d(46.38, 37.38), Math.toRadians(-8.43))
                .splineTo(new Vector2d(64.50, 26.25), Math.toRadians(-85.67))
                .splineTo(new Vector2d(58.38, 2.63), Math.toRadians(213.69))
                .addDisplacementMarker(() -> {})
                .splineTo(new Vector2d(38.38, 47.25), Math.toRadians(118.30))
                .splineTo(new Vector2d(-0.75, 64.88), Math.toRadians(174.88))
                .splineTo(new Vector2d(-30.13, 58.88), Math.toRadians(218.81))
                .splineTo(new Vector2d(-35.50, 45.88), Math.toRadians(247.54))
                .splineTo(new Vector2d(-35.50, 42.50), Math.toRadians(270.00))
                .splineTo(new Vector2d(-36.25, 42.00), Math.toRadians(191.98))
                .splineTo(new Vector2d(-45.50, 39.50), Math.toRadians(195.12))
                .splineTo(new Vector2d(-49.13, 37.38), Math.toRadians(222.71))
                .splineTo(new Vector2d(-56.13, 27.50), Math.toRadians(270.00))
                .splineTo(new Vector2d(-48.50, 18.63), Math.toRadians(-36.38))
                .splineTo(new Vector2d(-35.25, 14.50), Math.toRadians(3.50))
                .splineTo(new Vector2d(-20.88, 17.38), Math.toRadians(60.95))
                .splineTo(new Vector2d(-18.25, 29.00), Math.toRadians(95.91))
                .splineTo(new Vector2d(-26.38, 38.75), Math.toRadians(161.57))
                .splineTo(new Vector2d(-33.38, 41.88), Math.toRadians(171.87))
                .splineTo(new Vector2d(-53.25, 36.50), Math.toRadians(251.57))
                .splineTo(new Vector2d(-54.88, 24.88), Math.toRadians(-79.70))
                .splineTo(new Vector2d(-41.75, 12.63), Math.toRadians(-11.31))
                .splineTo(new Vector2d(-35.13, 1.13), Math.toRadians(255.96))
                .splineTo(new Vector2d(-35.75, -54.25), Math.toRadians(269.35))
                .splineTo(new Vector2d(-21.75, -62.63), Math.toRadians(10.12))
                .build();

        Trajectory untitled0 = drive.trajectoryBuilder(new Pose2d(-47.85, -48.00, Math.toRadians(88.46)))
                .splineToSplineHeading(new Pose2d(-48.15, 48.44, Math.toRadians(90.00)), Math.toRadians(90.00))
                .splineToLinearHeading(new Pose2d(47.85, 48.15, Math.toRadians(-87.34)), Math.toRadians(0.00))
                .splineToConstantHeading(new Vector2d(48.30, -47.41), Math.toRadians(0))
                .build();

        if(isStopRequested()) return;

        drive.followTrajectorySequence(duck1);
        //drive.followTrajectory(deposit);


        while(!isStopRequested()){
            drive.update();

            //drive.followTrajectorySequence(duck1);

//            switch (state){
//                case START:
//
//
//                    drive.followTrajectory(deposit);
//                    break;
//                case EXTEND:
//                    switch(TSEPos){
//                        case 1:
//                            d.slideDrive.setTargetPosition(-3500);
//                            d.slideDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                            d.slideDrive.setPower(1);
//                            if(d.slideDrive.getCurrentPosition() >= -3400)  {
//                                d.ramp.setPosition(0.4);
//                                d.servo.setPosition(0);
//                            }
//                            else {
//                                d.ramp.setPosition(0.5);
//                                d.servo.setPosition(0.5);
//                            }
//                            break;
//                        case 2:
//                            d.slideDrive.setTargetPosition(-3500);
//                            d.slideDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                            d.slideDrive.setPower(1);
//                            if(d.slideDrive.getCurrentPosition() >= -3400)  {
//                                d.ramp.setPosition(0.2);
//                                d.servo.setPosition(0);
//                            }
//                            else {
//                                d.ramp.setPosition(0.5);
//                                d.servo.setPosition(0.5);
//                            }
//                            break;
//                        case 3:
//
//                            d.slideDrive.setTargetPosition(-4400);
//                            d.slideDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                            d.slideDrive.setPower(1);
//                            if(d.slideDrive.getCurrentPosition() <= -4300) {
//                                d.ramp.setPosition(0.5);
//                                d.servo.setPosition(0);
//                            }
//                            else if(d.slideDrive.getCurrentPosition() >= -400) d.servo.setPosition(0.5);
//                            else {
//                                d.ramp.setPosition(0.5);
//                                d.servo.setPosition(0.5);
//                            }
//                            break;
//                        default:
//                            break;
//                    }
//
//                    if(d.slideDrive.getCurrentPosition() >  d.slideDrive.getTargetPosition()-50 && d.slideDrive.getCurrentPosition() < d.slideDrive.getTargetPosition() + 50) {
//                        timer.reset();
//                        state = State.RETRACT;
//                    }
//                    break;
//
//                case RETRACT:
//                    d.slideDrive.setTargetPosition(-1000);
//    //                d.position = 4;
//    //                if(d.slideDrive.getCurrentPosition() >  d.slideDrive.getTargetPosition()-50 && d.slideDrive.getCurrentPosition() < d.slideDrive.getTargetPosition() + 50)
//    //                    drive.followTrajectorySequence(duck1);
//                    break;
//                default:
//                    break;
//            }


        //d.slide(d.position);

        }

    }

    public void doCV(){
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened() { camera.startStreaming(1280,720, OpenCvCameraRotation.UPSIDE_DOWN); FtcDashboard.getInstance().startCameraStream(camera, 500); }
            @Override
            public void onError(int errorCode) {}
        });


    }
}