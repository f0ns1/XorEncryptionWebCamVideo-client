package com.client.ClientSocket.actions;

import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;



import java.awt.image.BufferedImage;

public class VideoCap {

	 static{
	    	nu.pattern.OpenCV.loadShared();
	        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

    public VideoCapture cap;
    public Mat2Image mat2Img = new Mat2Image();

    VideoCap(){
        cap = new VideoCapture();
        cap.open(0);
    }

    public BufferedImage getOneFrame() {
        cap.read(mat2Img.mat);
        return mat2Img.getImage(mat2Img.mat);
    }
}
