package com.alibaba.cloud.faceengine;

public class DetectParameter {
    public int checkQuality;
    public int checkLiveness;
    public int checkAge;
    public int checkGender;
    public int checkExpression;
    public int checkGlass;
    public Rect roi;

    @Override
    public String toString() {
        return "DetectParameter{" +
                "checkQuality=" + checkQuality +
                ", checkLiveness=" + checkLiveness +
                ", checkAge=" + checkAge +
                ", checkGender=" + checkGender +
                ", checkExpression=" + checkExpression +
                ", checkGlass=" + checkGlass +
                '}';
    }
}
