import java.awt.Button;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServidorTCP {

	
	public static void main(String[] args) {	

		Integer turno = 1;
		Boolean jugando = false;
		
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
		
		/*
		// Inicializar el juego
		boolean[][]mesa= new boolean[3][3];	
		
		initJuego(mesa, jugando);
		*/
		
		while(jugando) {
			
			try {
				// Esperamos a que alguien se conecte a nuestroSocket
				Socket socketDelCliente1 = socketDelServidor.accept();
				Socket socketDelCliente2 = socketDelServidor.accept();
				
				// Flujos de entrada y salida del Cliente 1
				DataInputStream dis1 = new DataInputStream(socketDelCliente1.getInputStream());
				DataOutputStream dos1 = new DataOutputStream(socketDelCliente1.getOutputStream());
				
				// Flujos de entrada y salida del Cliente 2
				DataInputStream dis2 = new DataInputStream(socketDelCliente2.getInputStream());
				DataOutputStream dos2 = new DataOutputStream(socketDelCliente2.getOutputStream());
				
				// Botón A1
				Boolean bstate_A1 = dis1.readBoolean();
				String btext_A1 = dis1.readLine();				
			
				Boolean bstate_A2 = dis1.readBoolean();
				Boolean bstate_A3 = dis1.readBoolean();
				Boolean bstate_B1 = dis1.readBoolean();
				Boolean bstate_B2 = dis1.readBoolean();
				Boolean bstate_B3 = dis1.readBoolean();
				Boolean bstate_C1 = dis1.readBoolean();
				Boolean bstate_C2 = dis1.readBoolean();
				Boolean bstate_C3 = dis1.readBoolean();
				
				//* El otro cliente recibe los objetos BotonCliente
					// que contienen el bstate y el símbolo
						//en sus propio tablero tiene que setear lo siguiente: los viejos los cambia por los nuevos
							// los que tengan el bstate = true --> deshabilitar el botón
							// los que tengan el simbolo "X" los pone a "X"
							// los que tengan el símbolo "O" los pone a "O"
				
				
				// recibe información de los 9 bstates y también +1 a la fase
				
				// comprobar información
				// comprobarVictoria();
				
				// cargar información y la fase al otro
				
					
				
			} catch (IOException e) {	
				e.printStackTrace();
			}

		}
	}

	// BORRAR?
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
