package org.lasarobotics.vision.test.detection.objects;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 * A detectable object
 */
public abstract class Detectable {
    public abstract double left();

    public abstract double right();

    public abstract double top();

    public abstract double bottom();

    public abstract double width();

    public abstract double height();

    public Point topLeft() {
        return new Point(left(), top());
    }

    public Point bottomRight() {
        return new Point(right(), bottom());
    }

    /**
     * Gets the average color of the contour
     *
     * @param img      The image matrix, of any color size
     * @param imgSpace The image's color space
     * @return The average color of the region
     */
    public org.lasarobotics.vision.test.util.color.Color averageColor(Mat img, org.lasarobotics.vision.test.util.color.ColorSpace imgSpace) {
        //Coerce values to stay within screen dimensions
        double leftX = org.lasarobotics.vision.test.util.MathUtil.coerce(0, img.cols() - 1, left());
        double rightX = org.lasarobotics.vision.test.util.MathUtil.coerce(0, img.cols() - 1, right());

        double topY = org.lasarobotics.vision.test.util.MathUtil.coerce(0, img.rows() - 1, top());
        double bottomY = org.lasarobotics.vision.test.util.MathUtil.coerce(0, img.rows() - 1, bottom());

        //Input points into array for calculation
        //TODO rectangular submatrix-based calculation isn't perfectly accurate when you have ellipses or weird shapes
        Mat subMat = img.submat((int) topY, (int) bottomY, (int) leftX, (int) rightX);

        //Calculate average and return new color instance
        return org.lasarobotics.vision.test.util.color.Color.create(Core.mean(subMat), imgSpace);
    }
}
