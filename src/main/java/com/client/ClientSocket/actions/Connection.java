package com.client.ClientSocket.actions;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Connection {

	private int receive_port_audio=9992;
	private int receive_port_video=9991;
	private String receive_socket;
	private int send_port_audio=9993;
	private int send_port_video=9994;
	private String send_socket;
	private InetAddress client_ip;

	public void VOIPConnection() {
		// Assign the target IP address
		try {
			client_ip = InetAddress.getByName("127.0.0.1");
		} catch (Exception e) {
			System.out.println("Error: Client received invalid IP address.");
		}
		// Initiate sockets to use for audio streaming
		DatagramSocket receive_socket = null;
		DatagramSocket send_socket_audio = null;
		DatagramSocket send_socket_video = null;
		try {
			//receive_socket = new DatagramSocket(receive_port);
			send_socket_audio = new DatagramSocket(send_port_audio);
			send_socket_video = new DatagramSocket(send_port_video);
		} catch (Exception e) {
		}

		Thread CaptureAudio = new Thread(new SendAudio(send_socket_audio, client_ip, receive_port_audio));
		CaptureAudio.start();
		Thread sendVideo = new Thread(new SendVideo(send_socket_video, client_ip, receive_port_video));
		sendVideo.start();
		//Thread PlayAudio = new Thread(new PlayAudio(receive_socket));
		//PlayAudio.start();
	}

}
