package com.client.ClientSocket;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MyFrame extends JFrame {
	private JPanel contentPane;
	public VideoCap videoCap = new VideoCap();

	public static void main(String[] args) {
		nu.pattern.OpenCV.loadShared();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyFrame frame = new MyFrame();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			
		});
		
	}

	public MyFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1280, 720);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		new MyThread().start();
		Socket socket = new Socket();
		String ip = "127.0.0.1";
		int port = 8989;
		boolean calling = true;
		VideoClientThread client = new VideoClientThread(videoCap, socket, ip, port, calling);
		client.run();

		
	}

	public void paint(Graphics g) {
		g = contentPane.getGraphics();
		g.drawImage(videoCap.getOneFrame(), 0, 0, this);
	}

	class MyThread extends Thread {

		@Override
		public void run() {
			for (;;) {
				repaint();

				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
