import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente1 {

	public static void main(String[] args) {

		// Se lee el primer argumento (la ip)
		InetAddress direcc = null;
		try {
			direcc = InetAddress.getByName(args[0]);					
		} catch (UnknownHostException uhe) {
			System.err.println("Host no encontrado : " + uhe);
			System.exit(-1);
		}

		int puerto = 1234; 
		
		DataInputStream dis = null;
		DataOutputStream dos = null; 
		Integer x = null , y = null;
		boolean valor = false;
		
		try {
			// Crea un nuevo socket y lo conecta a 	una dirección y un puerto específicos
			Socket socketDelCliente = new Socket(direcc, puerto);

			// Se extraen los streams de entrada y salida del socket del cliente
			dis = new DataInputStream(socketDelCliente.getInputStream());
			dos = new DataOutputStream(socketDelCliente.getOutputStream());
						
            // Escribir las coordenadas x, y y el valor booleano
            dos.writeInt(x);
            dos.writeInt(y);
            dos.writeBoolean(valor);

            // Flushing para asegurar que los datos se envíen
            dos.flush();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}
