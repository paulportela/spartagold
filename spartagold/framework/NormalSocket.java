package spartagold.framework;




import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Encapsulates the standard Socket object of the Java library
 * to fit the SocketInterface of the PeerBase system.
 * 
 * @author Nadeem Abdul Hamid
 *
 */
public class NormalSocket implements SocketInterface {

	private Socket s;
	private InputStream is;
	private OutputStream os;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	
	/**
	 * Creates a stream socket and connects it to the specified port number on the named host.
	 * 
	 * @param host the host name, or <code>null</code> for the loopback address
	 * @param port the port number
	 * @throws IOException if an I/O error occurs when creating the socket
	 * @throws UnknownHostException if the IP address of the host could not be determined
	 */
	public NormalSocket(String host, int port) throws IOException, UnknownHostException 
	{	
		this(new Socket(host, port));
	}
	
	
	/**
	 * Encapsulates a normal Java API Socket object.
	 * @param socket an already-open socket connection
	 * @throws IOException
	 */
	public NormalSocket(Socket socket) throws IOException 
	{
		s = socket;
		is = s.getInputStream();
		os = s.getOutputStream();	
		
		oos = new ObjectOutputStream(s.getOutputStream());
		ois = new ObjectInputStream(s.getInputStream());
		
	}
	
	
	/* (non-Javadoc)
	 * @see peerbase.SocketInterface#close()
	 */
	public void close() throws IOException {
		is.close();
		os.close();
		s.close();
	}

	/* (non-Javadoc)
	 * @see peerbase.SocketInterface#read()
	 */
	public int read() throws IOException {
		return is.read();
	}

	/* (non-Javadoc)
	 * @see peerbase.SocketInterface#read(byte[])
	 */
	public int read(byte[] b) throws IOException {
		return is.read(b);
	}

	/* (non-Javadoc)
	 * @see peerbase.SocketInterface#write(byte[])
	 */
	public void write(byte[] b) throws IOException {
		os.write(b);
		os.flush();
	}
	
	public void writeObject(Object ob) throws IOException
	{
		oos.writeObject(ob);
	}


	@Override
	public Object readObject() throws IOException
	{
		try
		{
			return ois.readObject();
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
		
	}

}
