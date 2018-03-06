package com.harish.faciallandmarkdetection;

/**
 * Created by hdv98 on 3/6/2018.
 */

public class NativeClass {
    public static native String getMessage();
    public static native void landmarkDetection(long addrInput, long addrOutput);
}
