package com.client.ClientSocket;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import org.apache.commons.codec.binary.Hex;

import com.android.dx.util.ByteArray;
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

	public static AudioFormat getAudioFormat() {
		float sampleRate = 16000;
		int sampleSizeInBits = 8;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		return format;
	}

	public void run() {
		try {
			socket.setSoTimeout(5000);
			socket.connect(new InetSocketAddress(ip, port));
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			Frame f;
			BufferedImage bufferedImage = null;
			TargetDataLine line = null;
			AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
			ByteArrayOutputStream fbaos = new ByteArrayOutputStream();
			File wavFile = null;
			try {
				wavFile = new File("/tmp/RecordAudio.wav");
				AudioFormat format = getAudioFormat();
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
				// checks if system supports the data line
				if (!AudioSystem.isLineSupported(info)) {
					System.out.println("Line not supported");
					System.exit(0);
				}
				line = (TargetDataLine) AudioSystem.getLine(info);
				line.open(format);
			} catch (Exception e) {
				e.printStackTrace();
			}

			while (calling) {
				try {

					line.start();
					AudioInputStream ais = new AudioInputStream(line);
					System.out.println("Start recording...");
					bufferedImage = videoCap.getOneFrame();
					ImageIO.write(bufferedImage, formatType, fbaos);
					line.stop();
					AudioSystem.write(ais, fileType, wavFile);
					byte[] fileContent = Files.readAllBytes(wavFile.toPath());
					oos.writeObject(new Frame(encrypt(fbaos.toByteArray()), encrypt(fileContent)));
					oos.flush();
					bufferedImage.flush();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e.getMessage());
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Usign xor encryption
	public BufferedImage xorEncryption(BufferedImage bufferedImage)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

		for (int x = 0; x < bufferedImage.getWidth(); x++) {
			for (int y = 0; y < bufferedImage.getHeight(); y++) {
				bufferedImage.setRGB(x, y, bufferedImage.getRGB(x, y) ^ 010101);
			}
		}
		return bufferedImage;
	}

	// Using AES encryption
	private String encrypt(byte[] img) {
		byte[] out = null;
		try {
			byte[] key = "MyPrivateKeyFroEncryption".getBytes();
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			out = cipher.doFinal(img);
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return Base64.getEncoder().encodeToString(out);

	}

}