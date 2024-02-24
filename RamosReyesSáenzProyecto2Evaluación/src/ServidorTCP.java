import java.awt.Button;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ServidorTCP {

	public static DataInputStream disJugador1, disJugador2;
	public static void main(String[] args) {

		Integer turno = 0;
		Boolean jugando = true, jugador;
		Jugador [] tablero;
		

		// FASE INICIAL
		// Primero indicamos la dirección IP local
		try {
			System.out.println("LocalHost = " + InetAddress.getLocalHost().toString());
		} catch (UnknownHostException uhe) {
			System.err.println("No puedo saber la dirección IP local : " + uhe);
		}

		// Abrimos un "Socket de Servidor" TCP en el puerto 1234.
		ServerSocket socketDelServidor = null;

		try {
			// Se crea el socket servidor y se limita las conexiones a dos
			socketDelServidor = new ServerSocket(40000,2);
			System.out.println("Esperando a que se conecten los usuarios...");
		} catch (IOException ioe) {
			System.err.println("Error al abrir el socket de servidor : " + ioe);
			System.exit(-1);
		}

		try {
			// Se inicializa el tablero 
			tablero = new Jugador[9];
			
			// Cuando un cliente se conecta, aparece una ventana que pide su nombre de usuario
			// El cliente introduce un nombre, le da a ENTER y ENTONCES SE INTENTAN CONECTAR AL SERVIDOR
			
			Socket socketDelCliente1 = socketDelServidor.accept();
			disJugador1 = new DataInputStream(socketDelCliente1.getInputStream());
			String nombreJugador1 = disJugador1.readUTF();
			Jugador jugador1 = new Jugador (nombreJugador1, socketDelCliente1, 'X');
			System.out.println("Se ha creado el usuario "+nombreJugador1);

			Socket socketDelCliente2 = socketDelServidor.accept();
			disJugador2 = new DataInputStream(socketDelCliente2.getInputStream());
			String nombreJugador2 = disJugador2.readUTF();
			Jugador jugador2 = new Jugador (nombreJugador2, socketDelCliente2, 'O');
			System.out.println("Se ha creado el usuario "+nombreJugador2);
			
			//while (jugando && turno <= 9) {
			
			int numJ1 = disJugador1.readInt();
			int numJ2 = disJugador2.readInt();
			
			while (jugando && turno <= 9) {
				/*
	            while (input.hasNextLine()) {
	                var command = input.nextLine();
	                if (command.startsWith("QUIT")) {
	                    return;
	                } else if (command.startsWith("MOVE")) {
	                    processMoveCommand(Integer.parseInt(command.substring(5)));
	                }
	            }
	            */
				
				
				if(turno%2!=0) {
					// Un jugador manda la información de qué se ha posicionado en una cuadro del tablero
					int posJugador1 = disJugador1.readInt();					
					// Por cada turno se modifica el tablero
					tablero [posJugador1] = jugador1;
				}else {
					int posJugador2 = disJugador2.readInt();
					tablero [posJugador2] = jugador2;
				}
				
				// Por cada turno se comprueba el tablero
				seHaGanado(tablero);
				
				// Por cada turno se envian los datos del tablero
				
				String codigoTablero = cifrarTablero(tablero);
				
				turno ++;
			}
			
			
			
			/*
			while (jugando && turno <= 9) {
				
				System.out.println("Clientes conectados");
				
				ArrayList<BotonCliente> arrayBotones;
							
				System.out.println("Turno: "+turno);
				if (turno % 2 != 0) {
					System.out.println("Turno del jugador 1!");
					ObjectInputStream ois = new ObjectInputStream(socketDelCliente1.getInputStream());
					arrayBotones = (ArrayList<BotonCliente>) ois.readObject();
					if (comprobarVictoria(arrayBotones)) {
						// Se ha ganado
						System.out.println("Se ha ganado el jugador 1");
						jugando = false;
						
						// Se tendrá que enviar los resultados de la partida a los jugadores
					}
					ObjectOutputStream oos = new ObjectOutputStream(socketDelCliente2.getOutputStream());
					oos.writeObject(arrayBotones);
					
					//ois.close();
					//oos.close();
					
				} else {
					System.out.println("Turno del jugador 2!");
					ObjectInputStream ois2 = new ObjectInputStream(socketDelCliente2.getInputStream());
					arrayBotones = (ArrayList<BotonCliente>) ois2.readObject();
					if (comprobarVictoria(arrayBotones)) {
						System.out.println("Se ha ganado el jugador 2");
						jugando = false;
					}
					ObjectOutputStream oos1 = new ObjectOutputStream(socketDelCliente1.getOutputStream());
					oos1.writeObject(arrayBotones);
					
				}
				turno++;
				
				arrayBotones.clear();									
				
				

				ObjectInputStream ois = new ObjectInputStream(socketDelCliente1.getInputStream());
				System.out.println(" El servidor ha recibido el objeto: "+ois.readObject().toString());
				
				
			}
			*/
		} catch (IOException e) {
			System.out.println("No se ha conseguido establecer la conexion con los jugadores. ");		
		}

	}
	
	

    private static String cifrarTablero(ServidorTCP.Jugador[] tablero) {
    	String codigoTablero = "";
		for(Jugador j : tablero) {
			if(j == null) {
				
			}
		}
		
		return null;
	}



	public static boolean seHaGanado(Jugador[] tablero) {
        return (tablero[0] != null && tablero[0] == tablero[1] && tablero[0] == tablero[2])
                || (tablero[3] != null && tablero[3] == tablero[4] && tablero[3] == tablero[5])
                || (tablero[6] != null && tablero[6] == tablero[7] && tablero[6] == tablero[8])
                || (tablero[0] != null && tablero[0] == tablero[3] && tablero[0] == tablero[6])
                || (tablero[1] != null && tablero[1] == tablero[4] && tablero[1] == tablero[7])
                || (tablero[2] != null && tablero[2] == tablero[5] && tablero[2] == tablero[8])
                || (tablero[0] != null && tablero[0] == tablero[4] && tablero[0] == tablero[8])
                || (tablero[2] != null && tablero[2] == tablero[4] && tablero[2] == tablero[6]);
    }
	

// CLASE JUGADOR
static class Jugador implements Runnable{
	
	String nombreJugador;
	Socket socketDelCliente;
	char simbolo;
	
	public Jugador(String nombreJugador, Socket socketDelCliente, char simbolo) {
		this.nombreJugador = nombreJugador;
		this.socketDelCliente = socketDelCliente;
		this.simbolo = simbolo;
	}

	public String getNombreJugador() {
		return nombreJugador;
	}

	public void setNombreJugador(String nombreJugador) {
		this.nombreJugador = nombreJugador;
	}

	public Socket getSocketDelCliente() {
		return socketDelCliente;
	}

	public void setSocketDelCliente(Socket socketDelCliente) {
		this.socketDelCliente = socketDelCliente;
	}

	public char getSimbolo() {
		return simbolo;
	}

	public void setSimbolo(char simbolo) {
		this.simbolo = simbolo;
	}

	@Override
	public void run() {
		System.out.println("Hola, soy el jugador "+nombreJugador + "desde el metodo run()");
		
	}		
}

}
