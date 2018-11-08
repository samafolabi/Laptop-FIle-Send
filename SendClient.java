import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 */

/**
 * @author HP
 *
 */
public class SendClient {
	public SendClient(File[] files, InetAddress cAddr, Notification sendNotif) {
		Socket socket = null;
		DataOutputStream out;
		FileInputStream is;
		byte[] bytes = new byte[4096];
		
		InetAddress sAddr = null;
		try {
			sAddr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			sendNotif.sent("Error: " + e.toString());
		}
		
		try {
			socket = new Socket(cAddr, 3000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			sendNotif.sent("Error: " + e.toString());
		}
		try {
			out = new DataOutputStream(socket.getOutputStream());
			out.write(sAddr.toString().getBytes());
			out.write('\n');
			out.writeInt(files.length);
			out.write('\n');
			out.close();
			socket = new Socket(cAddr, 3000);
			out = new DataOutputStream(socket.getOutputStream());
			for (int i = 0; i < files.length; i++) {
				out.write(files[i].getName().getBytes());
				out.write('\n');
				out.writeLong(files[i].length());
				out.write('\n');
				out.close();
				socket = new Socket(cAddr, 3000);
				out = new DataOutputStream(socket.getOutputStream());
			}
			for (int i = 0; i < files.length; i++) {
				is = new FileInputStream(files[i]);
				int count;
				while ((count = is.read(bytes)) > 0) {
					out.write(bytes, 0, count);
				}
			}
			sendNotif.sent("Sent to " + cAddr);
		} catch (IOException e) {
			// TODO: handle exception
			sendNotif.sent("Error: " + e.toString());
		}

		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			sendNotif.sent("Error: " + e.toString());
		}
	}
}
