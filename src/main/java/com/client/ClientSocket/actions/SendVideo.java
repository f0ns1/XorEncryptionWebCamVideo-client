package com.client.ClientSocket.actions;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class SendVideo implements Runnable {
	private byte[] tempBuffer;
	private DatagramSocket socket;
	private InetAddress ip;
	private int port;

	public SendVideo(DatagramSocket socket, InetAddress ip, int port)	{
		this.socket = socket;
		this.ip = ip;
		this.port = port;
		this.tempBuffer = new byte[40000];
	}


	public void run()	{  
		try {
			boolean stopCapture = false;
			VideoCap videoCap= new VideoCap();
			try{
				//Loop until stopCapture is set by another thread.
				while(!stopCapture){
					//Read data from the internal buffer of the data line.
					ByteArrayOutputStream fbaos = new ByteArrayOutputStream();
					BufferedImage bufferedImage = videoCap.getOneFrame();
					ImageIO.write(bufferedImage, "jpg", fbaos);
					if(fbaos.size() > 0){
						byte[] encData= encrypt(fbaos.toByteArray());
						
						DatagramPacket outPacket = new DatagramPacket(encData, encData.length, this.ip, this.port);
					
						this.socket.send(outPacket);
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
				e.printStackTrace();
		}
	}
	

	
	
	private byte[] process(byte[] encData) {
		int cont=0;
		for(int i=0; i<tempBuffer.length; i++) {
			if(i < (tempBuffer.length - encData.length)) {
				tempBuffer[i]=0;
			}else {
				tempBuffer[i]=encData[cont];
				cont++;
			}
		}
		System.out.println("encData= "+encData.length);
		System.out.println("encData= "+tempBuffer.length);
		return tempBuffer;
	}


	private byte[] encrypt(byte[] bytes) {
		byte[] out = null;
		try {
			byte[] key = "MyPrivateKeyFroEncryption".getBytes();
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			out = cipher.doFinal(bytes);
			System.out.println("out length = "+out.length);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error while encrypting: " + e.toString());
		}		
		return out;
	}
}
