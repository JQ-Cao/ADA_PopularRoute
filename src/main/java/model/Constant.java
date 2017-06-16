package model;

/**
 * Created by caojiaqing on 11/06/2017.
 */
public class Constant {
    // 左转cost，单位米，double
    public static double leftHandCost = 50.0;
    // 左转判断角度差，单位度，double型
    public static double leftHandAzimuthDis = 20.0;


    // 高频点候选匹配link数
    public static int projectionNum_HF = 6;
    // 低频点候选匹配link数
    public static int projectionNum_LF = 10;
    // 高频点平均回传周期上限，单位s
    public static int averageInterval = 10;
}
