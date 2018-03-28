package org.usfirst.frc.team5431.robot.vision;

import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team5431.robot.Constants;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision {
	public static CubeDetector cubeDetector;
	public static UsbCamera camera, cameraTwo;
	public static CvSink cv;
	//public static CvSource proc;
	public static Mat image;
	//public static MjpegServer server;
	//public static VideoSource source;
	public static boolean 
			visionTargetFound = false, 
			initialized = false;
	public static double 
			visionAngle = 0, 
			visionDistance = 0;
	
	public static enum TargetMode {
		Normal, Cube
	}
	
	public static TargetMode targetting = TargetMode.Normal;
	
	public final static void init() {
		if(initialized) return;
		cubeDetector = new CubeDetector();
		image = new Mat();
		
		camera.setResolution(Constants.Vision.IMAGE_WIDTH, Constants.Vision.IMAGE_HEIGHT);
		camera.setFPS(Constants.Vision.FPS);
		cameraTwo.setResolution(Constants.Vision.IMAGE_WIDTH, Constants.Vision.IMAGE_HEIGHT);
		cameraTwo.setFPS(Constants.Vision.FPS);
		cv = CameraServer.getInstance().getVideo();
		CameraServer.getInstance().addServer("source", 1180).setSource(camera);
		/*
		proc = CameraServer.getInstance().putVideo("processed", Constants.Vision.IMAGE_WIDTH, Constants.Vision.IMAGE_HEIGHT);
		proc.putFrame(Mat.zeros(Constants.Vision.IMAGE_HEIGHT, Constants.Vision.IMAGE_WIDTH, CvType.CV_8UC3)); //Put a blank frame
		proc.setFPS(10);
		proc.setConnected(true);
		proc.setDescription("test");
		*/
		
		initialized = true;
	}
	
	public final static void setCubeCamera(UsbCamera c) {
		camera = c;
	}
	
	public final static void setFieldCamera(UsbCamera c) {
		cameraTwo = c;
	}
	
	public final static void setCameraNormal() {
		camera.setBrightness(50);
		camera.setExposureManual(30);
	}
	
	public final static void setCameraCube() {
		camera.setBrightness(10);
		camera.setExposureManual(25);
	}
	
    public final static Rect getRectangle(final MatOfPoint contour) {
    	return Imgproc.boundingRect(contour);
    }
    
    public final static double getCenterX(final Rect boundingBox) {
    	return boundingBox.x + (boundingBox.width / 2);
    }
    
    public final static double getCenterY(final Rect boundingBox) {
    	return boundingBox.y + (boundingBox.height / 2);
    }
    
    public final static boolean inSameSpot(final Rect parent, final Rect child) {
    	return (getCenterX(parent) == getCenterX(child)) && (getCenterY(parent) == getCenterY(child));
    }
    
    public final static double getArea(final MatOfPoint contour) {
    	return Imgproc.contourArea(contour);
    }
	
    private final static void processCubeFrame() {
    	cv.grabFrame(image);
    	if(image != null) {
    		if(!image.empty()) {
    			cubeDetector.process(image);
    			image = cubeDetector.cvDilateOutput(); //Set the output
    			final List<MatOfPoint> cPoints = cubeDetector.filterContoursOutput();
    			if(cPoints.size() == 0) {
    				visionTargetFound = false;
    				return;
    			}
    			
    			MatOfPoint largest = cPoints.get(0);
    			for(final MatOfPoint ps : cPoints) {
    				if(getArea(ps) > getArea(largest)) {
    					largest = ps;
    				}
    			}
    			
    			final Rect cubeRect = getRectangle(largest);
    			final double centerX = getCenterX(cubeRect);
    			visionAngle = Constants.Vision.getHorzAngle(centerX);
    			SmartDashboard.putNumber("CubeAngle", visionAngle);
    			visionDistance = getCenterY(cubeRect);
    			visionTargetFound = true;
    		} else {
    			visionTargetFound = false;
    		}
    	} else {
    		visionTargetFound = false;
    	}
    	
    	SmartDashboard.putBoolean("FoundCube", visionTargetFound);
    }
    
    public final static boolean hasTarget() {
    	return visionTargetFound;
    }
    
    public final static double getAngle() {
    	return visionAngle;
    }
    
    public final static void setTargetMode(TargetMode mode) {
    	targetting = mode;
    }
    
    public final static void setCubeTargetMode() {
    	targetting = TargetMode.Cube;
    }
    
    public final static void setNormalTargetMode() {
    	targetting = TargetMode.Normal;
    }
    
    public final static void periodic() {
    	switch(targetting) {
    	case Normal:
    		SmartDashboard.putBoolean("ProcessingCube", false);
    		break;
    	case Cube:
    		SmartDashboard.putBoolean("ProcessingCube", true);
    		processCubeFrame();
    	}
    	
    	//Publish to the other camera server
    	/*if(image != null) {
    		if(!image.empty()) proc.putFrame(image);
    	}*/
    	SmartDashboard.putString("TargettingMode", targetting.toString());
    }
	
}

