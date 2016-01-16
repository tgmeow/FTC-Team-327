package org.lasarobotics.vision.test.opmode.extensions;

import org.opencv.core.Mat;

import java.util.List;

/**
 * Extension that supports finding and reading beacon color data
 */
public class BeaconColorExtension implements VisionExtension {
    private final org.lasarobotics.vision.test.util.color.ColorHSV lowerBoundRed = new org.lasarobotics.vision.test.util.color.ColorHSV((int) (305 / 360.0 * 255.0), (int) (0.100 * 255.0), (int) (0.300 * 255.0));
    private final org.lasarobotics.vision.test.util.color.ColorHSV upperBoundRed = new org.lasarobotics.vision.test.util.color.ColorHSV((int) ((360.0 + 5.0) / 360.0 * 255.0), 255, 255);
    private final org.lasarobotics.vision.test.util.color.ColorHSV lowerBoundBlue = new org.lasarobotics.vision.test.util.color.ColorHSV((int) (170.0 / 360.0 * 255.0), (int) (0.100 * 255.0), (int) (0.300 * 255.0));
    private final org.lasarobotics.vision.test.util.color.ColorHSV upperBoundBlue = new org.lasarobotics.vision.test.util.color.ColorHSV((int) (227.0 / 360.0 * 255.0), 255, 255);
    private org.lasarobotics.vision.test.detection.ColorBlobDetector detectorRed;
    private org.lasarobotics.vision.test.detection.ColorBlobDetector detectorBlue;

    public BeaconColorExtension() {

    }

    public void init(org.lasarobotics.vision.test.opmode.VisionOpMode opmode) {
        //Initialize all detectors here
        detectorRed = new org.lasarobotics.vision.test.detection.ColorBlobDetector(lowerBoundRed, upperBoundRed);
        detectorBlue = new org.lasarobotics.vision.test.detection.ColorBlobDetector(lowerBoundBlue, upperBoundBlue);

        //opmode.setCamera(Cameras.PRIMARY);
        //opmode.setFrameSize(new Size(900, 900));
    }

    public void loop(org.lasarobotics.vision.test.opmode.VisionOpMode opmode) {

    }

    public Mat frame(org.lasarobotics.vision.test.opmode.VisionOpMode opmode, Mat rgba, Mat gray) {
        try {
            //Process the frame for the color blobs
            detectorRed.process(rgba);
            detectorBlue.process(rgba);

            //Get the list of contours
            List<org.lasarobotics.vision.test.detection.objects.Contour> contoursRed = detectorRed.getContours();
            List<org.lasarobotics.vision.test.detection.objects.Contour> contoursBlue = detectorBlue.getContours();

            //Get color analysis
            org.lasarobotics.vision.test.ftc.resq.Beacon beacon = new org.lasarobotics.vision.test.ftc.resq.Beacon();
            opmode.beaconColor = beacon.analyzeColor(contoursRed, contoursBlue, rgba, gray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rgba;
    }

    @Override
    public void stop(org.lasarobotics.vision.test.opmode.VisionOpMode opmode) {

    }
}