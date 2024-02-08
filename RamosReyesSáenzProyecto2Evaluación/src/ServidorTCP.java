import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServidorTCP {

	public static void main(String[] args) {
		// Primero indicamos la dirección IP local
		try {
			System.out.println("LocalHost = " + InetAddress.getLocalHost().toString());
		} catch (UnknownHostException uhe) {
			System.err.println("No puedo saber la dirección IP local : " + uhe);
		}

		// Abrimos un "Socket de Servidor" TCP en el puerto 1234.
		ServerSocket socketDelServidor = null;
		

		try {
			socketDelServidor = new ServerSocket(1234);
			System.out.println("Esperando a que se conecten los usuarios...");
		} catch (IOException ioe) {
			System.err.println("Error al abrir el socket de servidor : " + ioe);
			System.exit(-1);
		}
		
		// Inicializar el juego
		boolean[][]mesa= new boolean[3][3];	
		boolean jugando = false;
		
		initJuego(mesa, jugando);
		
		while(jugando) {
			
			try {
				// Esperamos a que alguien se conecte a nuestroSocket
				Socket socketDelCliente1 = socketDelServidor.accept();
				Socket socketDelCliente2 = socketDelServidor.accept();
				
				// Flujos de entrada y salida del Cliente 1
				DataInputStream dis1 = new DataInputStream(socketDelCliente1.getInputStream());
				DataOutputStream dos1 = new DataOutputStream(socketDelCliente1.getOutputStream());
				
				// Flujos de entrada y salida del Cliente 1
				DataInputStream dis2 = new DataInputStream(socketDelCliente2.getInputStream());
				DataOutputStream dos2 = new DataOutputStream(socketDelCliente2.getOutputStream());
				

				
			} catch (IOException e) {	
				e.printStackTrace();
			}

		}
	}

	private static void initJuego(boolean[][] juego, boolean jugando) {
		jugando = true;
		
		juego[0][0] = false;
		juego[0][1] = false;
		juego[0][2] = false;
		juego[1][0] = false;
		juego[1][1] = false;
		juego[1][2] = false;
		juego[2][0] = false;
		juego[2][1] = false;
		juego[2][2] = false;
	}

}
