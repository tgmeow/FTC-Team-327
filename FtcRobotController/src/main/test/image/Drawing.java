package org.lasarobotics.vision.test.image;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Methods for drawing shapes onto images
 */
public class Drawing {
    public static void drawCircle(Mat img, Point center, int diameter, org.lasarobotics.vision.test.util.color.Color color) {
        drawCircle(img, center, diameter, color, 2);
    }

    public static void drawCircle(Mat img, Point center, int diameter, org.lasarobotics.vision.test.util.color.Color color, int thickness) {
        Imgproc.circle(img, center, diameter, color.getScalarRGBA(), thickness);
    }

    public static void drawEllipse(Mat img, org.lasarobotics.vision.test.detection.objects.Ellipse ellipse, org.lasarobotics.vision.test.util.color.Color color) {
        drawEllipse(img, ellipse, color, 2);
    }

    public static void drawEllipse(Mat img, org.lasarobotics.vision.test.detection.objects.Ellipse ellipse, org.lasarobotics.vision.test.util.color.Color color, int thickness) {
        Imgproc.ellipse(img, ellipse.center(), ellipse.size(), ellipse.angle(), 0, 360, color.getScalarRGBA(), thickness);
    }

    public static void drawEllipses(Mat img, List<org.lasarobotics.vision.test.detection.objects.Ellipse> ellipses, org.lasarobotics.vision.test.util.color.Color color) {
        drawEllipses(img, ellipses, color, 2);
    }

    public static void drawEllipses(Mat img, List<org.lasarobotics.vision.test.detection.objects.Ellipse> ellipses, org.lasarobotics.vision.test.util.color.Color color, int thickness) {
        for (org.lasarobotics.vision.test.detection.objects.Ellipse ellipse : ellipses)
            drawEllipse(img, ellipse, color, thickness);
    }

    public static void drawArc(Mat img, org.lasarobotics.vision.test.detection.objects.Ellipse ellipse, org.lasarobotics.vision.test.util.color.Color color, double angleDegrees) {
        drawArc(img, ellipse, color, angleDegrees, 2);
    }

    public static void drawArc(Mat img, org.lasarobotics.vision.test.detection.objects.Ellipse ellipse, org.lasarobotics.vision.test.util.color.Color color, double angleDegrees, int thickness) {
        Imgproc.ellipse(img, ellipse.center(), ellipse.size(), ellipse.angle(), 0, angleDegrees, color.getScalarRGBA(), thickness);
    }

    public static void drawText(Mat img, String text, Point origin, float scale, org.lasarobotics.vision.test.util.color.Color color) {
        drawText(img, text, origin, scale, color, Anchor.TOPLEFT);
    }

    public static void drawText(Mat img, String text, Point origin, float scale, org.lasarobotics.vision.test.util.color.Color color, Anchor locationOnImage) {
        if (locationOnImage == Anchor.BOTTOMLEFT)
            Transform.flip(img, Transform.FlipType.FLIP_ACROSS_Y);
        Imgproc.putText(img, text, origin, Core.FONT_HERSHEY_SIMPLEX, scale, color.getScalarRGBA(), 2, Core.LINE_8,
                (locationOnImage == Anchor.BOTTOMLEFT || locationOnImage == Anchor.BOTTOMLEFT_UNFLIPPED_Y));
        if (locationOnImage == Anchor.BOTTOMLEFT)
            Transform.flip(img, Transform.FlipType.FLIP_ACROSS_Y);
    }

    public static void drawLine(Mat img, Point point1, Point point2, org.lasarobotics.vision.test.util.color.Color color) {
        drawLine(img, point1, point2, color, 2);
    }

    public static void drawLine(Mat img, Point point1, Point point2, org.lasarobotics.vision.test.util.color.Color color, int thickness) {
        Imgproc.line(img, point1, point2, color.getScalarRGBA(), thickness);
    }

    public static void drawContour(Mat img, org.lasarobotics.vision.test.detection.objects.Contour contour, org.lasarobotics.vision.test.util.color.Color color) {
        drawContour(img, contour, color, 2);
    }

    public static void drawContour(Mat img, org.lasarobotics.vision.test.detection.objects.Contour contour, org.lasarobotics.vision.test.util.color.Color color, int thickness) {
        List<MatOfPoint> contoursOut = new ArrayList<>();
        contoursOut.add(contour.getData());
        Imgproc.drawContours(img, contoursOut, -1, color.getScalarRGBA(), thickness);
    }

    public static void drawContours(Mat img, List<org.lasarobotics.vision.test.detection.objects.Contour> contours, org.lasarobotics.vision.test.util.color.Color color) {
        drawContours(img, contours, color, 2);
    }

    public static void drawContours(Mat img, List<org.lasarobotics.vision.test.detection.objects.Contour> contours, org.lasarobotics.vision.test.util.color.Color color, int thickness) {
        List<MatOfPoint> contoursOut = new ArrayList<>();
        for (org.lasarobotics.vision.test.detection.objects.Contour contour : contours)
            contoursOut.add(contour.getData());
        Imgproc.drawContours(img, contoursOut, -1, color.getScalarRGBA(), thickness);
    }

    public static void drawRectangles(Mat img, List<org.lasarobotics.vision.test.detection.objects.Rectangle> rects, org.lasarobotics.vision.test.util.color.Color color) {
        for (org.lasarobotics.vision.test.detection.objects.Rectangle r : rects)
            drawRectangle(img, r.topLeft(), r.bottomRight(), color, 2);
    }

    public static void drawRectangles(Mat img, List<org.lasarobotics.vision.test.detection.objects.Rectangle> rects, org.lasarobotics.vision.test.util.color.Color color, int thickness) {
        for (org.lasarobotics.vision.test.detection.objects.Rectangle r : rects)
            drawRectangle(img, r.topLeft(), r.bottomRight(), color, thickness);
    }

    public static void drawRectangle(Mat img, org.lasarobotics.vision.test.detection.objects.Rectangle rect, org.lasarobotics.vision.test.util.color.Color color) {
        drawRectangle(img, rect.topLeft(), rect.bottomRight(), color, 2);
    }

    public static void drawRectangle(Mat img, org.lasarobotics.vision.test.detection.objects.Rectangle rect, org.lasarobotics.vision.test.util.color.Color color, int thickness) {
        drawRectangle(img, rect.topLeft(), rect.bottomRight(), color, thickness);
    }

    public static void drawRectangle(Mat img, Point topLeft, Point bottomRight, org.lasarobotics.vision.test.util.color.Color color) {
        drawRectangle(img, topLeft, bottomRight, color, 2);
    }

    public static void drawRectangle(Mat img, Point topLeft, Point bottomRight, org.lasarobotics.vision.test.util.color.Color color, int thickness) {
        Imgproc.rectangle(img, topLeft, bottomRight, color.getScalarRGBA(), thickness);
    }

    public enum Anchor {
        TOPLEFT,
        BOTTOMLEFT,
        BOTTOMLEFT_UNFLIPPED_Y
    }
}
