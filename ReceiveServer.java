import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 
 */

/**
 * @author HP
 *
 */
public class ReceiveServer {
	public ReceiveServer(Notification recNotif) {
		ServerSocket socket = null;
		String addr;
		DataInputStream is;
		Socket clientSocket = null;
		
		try {
			socket = new ServerSocket(3000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			recNotif.received("Error: " + e.toString(), null);
		}
		try {
			clientSocket = socket.accept();
			is = new DataInputStream(clientSocket.getInputStream());
			addr = is.readLine();
			int length = is.readInt();
			is.close();
			clientSocket = socket.accept();
			is = new DataInputStream(clientSocket.getInputStream());
			String[] fileNames = new String[length];
			long[] fileLengths = new long[length];
			for (int i = 0; i < length; i++) {
				fileNames[i] = is.readLine();
				fileLengths[i] = is.readLong();
				is.close();
				clientSocket = socket.accept();
				is = new DataInputStream(clientSocket.getInputStream());
			}
			File[] files2 = new File[length];
			for (int i = 0; i < length; i++) {
				files2[i] = new File(fileNames[i]);
				if (fileLengths[i]<Integer.MAX_VALUE) {
					byte[] b = new byte[(int)fileLengths[i]];
					is.readFully(b);
					OpenOption[] options = new OpenOption[] {
						StandardOpenOption.APPEND,
						StandardOpenOption.CREATE
					};
					Files.write(Paths.get(System.getProperty("user.dir")+"/"+fileNames[i]), b, options);
				} else {
					int numm = (fileLengths[i]%Integer.MAX_VALUE)==0?
							(int) fileLengths[i]/Integer.MAX_VALUE:
							(int) (fileLengths[i]/Integer.MAX_VALUE)+1;
					byte[][] bbs = new byte[numm][Integer.MAX_VALUE];
					long count = fileLengths[i];
					for (int j = 0; j < numm; j++) {
						int sub;
						if ((count - Integer.MAX_VALUE) < 0) {
							sub = (int) count;
						} else {
							sub = Integer.MAX_VALUE;
							count -= Integer.MAX_VALUE;
						}
						is.readFully(bbs[j], 0, sub);
					}
					OpenOption[] options = new OpenOption[] {
							StandardOpenOption.APPEND,
							StandardOpenOption.CREATE
						};
					for (int j = 0; j < bbs.length; j++) {
						Files.write(Paths.get(System.getProperty("user.dir")+"/"+fileNames[i]), bbs[j], options);
					}
				}
			}
			recNotif.received("Received from" + addr, fileNames);
		} catch (IOException e) {
			// TODO: handle exception
			recNotif.received("Error: " + e.toString(), null);
		}

		try {
			clientSocket.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			recNotif.received("Error: " + e.toString(), null);
		}
	}
}
