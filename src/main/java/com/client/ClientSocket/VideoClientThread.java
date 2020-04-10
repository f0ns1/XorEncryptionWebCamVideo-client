package com.client.ClientSocket;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.socket.frame.Frame;

public class VideoClientThread extends Thread {
	private final String formatType = "jpg";
	private VideoCap videoCap;
	private Socket socket;
	private String ip;
	private int port;
	private boolean calling;

	public VideoClientThread(VideoCap videoCap, Socket socket, String ip, int port, boolean calling) {
		this.videoCap = videoCap;
		this.socket = socket;
		this.ip = ip;
		this.port = port;
		this.calling = calling;
	}

	public void run() {
		try {
			//SocketAddress proxyAddr = new InetSocketAddress("127.0.0.1", 8080);
			//Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddr);
			socket = new Socket();
			socket.setSoTimeout(5000);
			socket.connect(new InetSocketAddress(ip,port));
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			Frame f;
			BufferedImage bufferedImage = null;
			while (calling) {
				try {
					ByteArrayOutputStream fbaos = new ByteArrayOutputStream();
					bufferedImage = videoCap.getOneFrame();
					
					ImageIO.write(xorEncryption(bufferedImage), formatType, fbaos);
					
					oos.writeObject(new Frame(fbaos.toByteArray()));
					oos.flush();
					bufferedImage.flush();
					// Thread.sleep(33);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e.getMessage());
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public BufferedImage xorEncryption(BufferedImage bufferedImage) {
		    String image = bufferedImage.toString();
		 	System.out.println("image : "+image);
		    System.out.println("data: "+bufferedImage.getData());
		    System.out.println("source: "+bufferedImage.getSource());
		    System.out.println("whth: "+bufferedImage.getWidth());
		    System.out.println("hegth: "+bufferedImage.getHeight());
		    System.out.println("1,1: "+bufferedImage.getRGB(1, 1));
		    for(int x =0 ; x < bufferedImage.getWidth(); x++) {
		    	for(int y=0; y<bufferedImage.getHeight(); y++) {
		    		//System.out.println("position: ["+x+","+y+"] ="+bufferedImage.getRGB(x, y));
		    		//int val =bufferedImage.getRGB(x, y);
		    		//System.out.println("position: ["+x+","+y+"] ="+val);
		    		//int resp = val ^ 1111111;
		    		//System.out.println("position: ["+x+","+y+"] ="+resp);
		    		bufferedImage.setRGB(x, y, bufferedImage.getRGB(x, y)^ 1010101);
		    	}
		    }
		return bufferedImage;
	}

}