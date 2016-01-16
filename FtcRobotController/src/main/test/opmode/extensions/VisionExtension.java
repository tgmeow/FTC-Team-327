package org.lasarobotics.vision.test.opmode.extensions;

import org.opencv.core.Mat;

/**
 * Interface for vision extensions for VisionOpMode
 */
public interface VisionExtension {
    void init(org.lasarobotics.vision.test.opmode.VisionOpMode opmode);

    void loop(org.lasarobotics.vision.test.opmode.VisionOpMode opmode);

    Mat frame(org.lasarobotics.vision.test.opmode.VisionOpMode opmode, Mat rgba, Mat gray);

    void stop(org.lasarobotics.vision.test.opmode.VisionOpMode opmode);
}
