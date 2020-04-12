package com.client.ClientSocket.actions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/* This method receives audio input
 * from a UDP packet and plays it.
 */
class PlayAudio implements Runnable {

	private DatagramSocket socket;
	private byte[] tempBuffer;

	public PlayAudio(DatagramSocket socket)	{
		this.socket = socket;
		this.tempBuffer = new byte[4000];
	}

	public void run()	{
		try{

			DatagramPacket inPacket;
			boolean stopPlay = false;

			//Loop until stopPlay is set by another thread.
			while (!stopPlay)	{
				//Put received data into a byte array object
				inPacket = new DatagramPacket(tempBuffer, tempBuffer.length);
				this.socket.receive(inPacket);

				byte[] audioData = inPacket.getData();

				//Get an input stream on the byte array containing the data
				InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
				AudioFormat audioFormat = getAudioFormat();
				AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, audioData.length/audioFormat.getFrameSize());
				DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
				SourceDataLine sourceDataLine = (SourceDataLine)
				AudioSystem.getLine(dataLineInfo);
				sourceDataLine.open(audioFormat);
				sourceDataLine.start();

				try { 
					int cnt;
					//Keep looping until the input read method returns -1 for empty stream.
					while((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1){
						if(cnt > 0){
							//Write data to the internal buffer of the data line where it will be delivered to the speaker.
							sourceDataLine.write(tempBuffer, 0, cnt);
						}
					}
					//Block and wait for internal buffer of the data line to empty.
					sourceDataLine.drain();
					sourceDataLine.close();
				}catch (Exception e) {}
			}
		} catch (Exception e) {}
	}
	
	private AudioFormat getAudioFormat(){
		float sampleRate = 8000.0F;
		//8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;
		//8,16
		int channels = 1;
		//1,2
		boolean signed = true;
		//true,false
		boolean bigEndian = false;
		//true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

}
