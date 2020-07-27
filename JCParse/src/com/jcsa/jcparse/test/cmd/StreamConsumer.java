package com.jcsa.jcparse.test.cmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * It is used to consume the input-stream bytes
 * 
 * @author yukimula
 *
 */
class StreamConsumer implements Runnable {
	
	/** the buffer to preserve the bytes in input-stream **/
	private StringBuilder buffer;
	/** the maximal number of bytes allowed to preserved **/
	private int max_buff_size;
	/** the input-stream reader to generate string bytes **/
	private InputStreamReader reader;
	
	/**
	 * @param in the input-stream of which bytes will be consumed
	 * @param max_size the maximal number of bytes to preserve 
	 * @param buffer the string buffer to preserve the input-stream
	 * @throws Exception 
	 */
	protected StreamConsumer(InputStream in, int max_size, StringBuilder buffer) throws Exception {
		if(in == null)
			throw new IllegalArgumentException("Invalid stream: null");
		else {
			this.max_buff_size = max_size; this.buffer = buffer;
			this.reader = new InputStreamReader(in); 
		}
	}
	
	@Override
	public void run() {
		//BufferedReader reader = new BufferedReader(rs);
		char[] buff = new char[1024]; int length;
		while((length = this.get(buff)) != -1) {
			if(buffer.length() <= max_buff_size)
				buffer.append(buff, 0, length);
			else break;
		}
		/* end of all */
		try {
			reader.close(); 
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * read the bytes of characters from input-stream into buff
	 * @param buff the buffer to preserve characters from input-stream
	 * @return the number of bytes being read or -1 when input-stream is at EOF.
	 */
	private int get(char[] buff) {
		try {
			return reader.read(buff);
		} catch (IOException e) {
			// e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * forcely to close the reader
	 */
	protected void terminate() { 
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
}
