import java.awt.Button;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ServidorTCP {

	public static void main(String[] args) {

		Integer turno = 0;
		Boolean jugando = true, jugador;

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
			socketDelServidor = new ServerSocket(40000);
			System.out.println("Esperando a que se conecten los usuarios...");
		} catch (IOException ioe) {
			System.err.println("Error al abrir el socket de servidor : " + ioe);
			System.exit(-1);
		}

		// FASE DE JUEGO

		try {
			// Esperamos a que alguien se conecte a nuestroSocket
			
			while (jugando && turno <= 9) {
				Socket socketDelCliente1 = socketDelServidor.accept();
				Socket socketDelCliente2 = socketDelServidor.accept();
				System.out.println("Clientes conectados");
				ArrayList<BotonCliente> arrayBotones;
				if (turno == 0) {
					System.out.println("turno 0");
					// Asignación de booleanos
					DataOutputStream dos1 = new DataOutputStream(socketDelCliente1.getOutputStream());
					dos1.writeBoolean(true);

					DataOutputStream dos2 = new DataOutputStream(socketDelCliente2.getOutputStream());
					dos2.writeBoolean(false);
					turno++;
				} else {
					System.out.println("Turno: "+turno);
					if (turno % 2 != 0) {
						System.out.println("Turno del jugador 1!");
						ObjectInputStream ois = new ObjectInputStream(socketDelCliente1.getInputStream());
						arrayBotones = (ArrayList<BotonCliente>) ois.readObject();
						if (comprobarVictoria(arrayBotones)) {
							// Se ha ganado
							System.out.println("Se ha ganado el jugador 1");
							jugando = false;
							ObjectOutputStream oos2 = new ObjectOutputStream(socketDelCliente2.getOutputStream());
							oos2.writeObject(arrayBotones);
							// Se tendrá que enviar los resultados de la partida a los jugadores
						}
						
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
					socketDelCliente1.close();
					socketDelCliente2.close();

				}
				
			}

			// Tirar la moneda y enviar la información de quién empieza

			// * El otro cliente recibe los objetos BotonCliente
			// que contienen el bstate y el símbolo
			// en sus propio tablero tiene que setear lo siguiente: los viejos los cambia
			// por los nuevos
			// los que tengan el bstate = true --> deshabilitar el botón
			// los que tengan el simbolo "X" los pone a "X"
			// los que tengan el símbolo "O" los pone a "O"

			// recibe información de los 9 bstates y también +1 a la fase

			// comprobar información
			// comprobarVictoria();

			// cargar información y la fase al otro

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	private static Boolean comprobarVictoria(ArrayList<BotonCliente> botones) {		
		int numeroCasos = 0;
		boolean[] casosVictoria = {
				// X verticales
				(
					    (botones.get(0).getBstate() && botones.get(0).getSimbolo().equals("X")) && 
					    (botones.get(1).getBstate() && botones.get(1).getSimbolo().equals("X")) && 
					    (botones.get(2).getBstate() && botones.get(2).getSimbolo().equals("X"))
					),
					(
					    (botones.get(3).getBstate() && botones.get(3).getSimbolo().equals("X")) && 
					    (botones.get(4).getBstate() && botones.get(4).getSimbolo().equals("X")) && 
					    (botones.get(5).getBstate() && botones.get(5).getSimbolo().equals("X"))
					),
					(
					    (botones.get(6).getBstate() && botones.get(6).getSimbolo().equals("X")) && 
					    (botones.get(7).getBstate() && botones.get(7).getSimbolo().equals("X")) && 
					    (botones.get(8).getBstate() && botones.get(8).getSimbolo().equals("X"))
					),

				// X horizontales
					(
						    (botones.get(0).getBstate() && botones.get(0).getSimbolo().equals("X")) && 
						    (botones.get(3).getBstate() && botones.get(3).getSimbolo().equals("X")) &&
						    (botones.get(6).getBstate() && botones.get(6).getSimbolo().equals("X"))
					),
					(
						    (botones.get(1).getBstate() && botones.get(1).getSimbolo().equals("X")) && 
						    (botones.get(4).getBstate() && botones.get(4).getSimbolo().equals("X")) && 
						    (botones.get(7).getBstate() && botones.get(7).getSimbolo().equals("X")) 
					),
					(
						    (botones.get(2).getBstate() && botones.get(2).getSimbolo().equals("X")) && 
							(botones.get(5).getBstate() && botones.get(5).getSimbolo().equals("X")) && 
							(botones.get(8).getBstate() && botones.get(8).getSimbolo().equals("X"))
					),

				// X oblicuos
					(
						    (botones.get(0).getBstate() && botones.get(0).getSimbolo().equals("X")) && 
						    (botones.get(4).getBstate() && botones.get(4).getSimbolo().equals("X")) && 
						    (botones.get(8).getBstate() && botones.get(8).getSimbolo().equals("X"))  
					),
					(
						    (botones.get(6).getBstate() && botones.get(6).getSimbolo().equals("X")) && 
						    (botones.get(4).getBstate() && botones.get(4).getSimbolo().equals("X")) && 
						    (botones.get(2).getBstate() && botones.get(2).getSimbolo().equals("X"))  
					),

				// O verticales
					(
					    (botones.get(0).getBstate() && botones.get(0).getSimbolo().equals("O")) && 
					    (botones.get(1).getBstate() && botones.get(1).getSimbolo().equals("O")) && 
					    (botones.get(2).getBstate() && botones.get(2).getSimbolo().equals("O"))
					),
					(
					    (botones.get(3).getBstate() && botones.get(3).getSimbolo().equals("O")) && 
					    (botones.get(4).getBstate() && botones.get(4).getSimbolo().equals("O")) && 
					    (botones.get(5).getBstate() && botones.get(5).getSimbolo().equals("O"))
					),
					(
					    (botones.get(6).getBstate() && botones.get(6).getSimbolo().equals("O")) && 
					    (botones.get(7).getBstate() && botones.get(7).getSimbolo().equals("O")) && 
					    (botones.get(8).getBstate() && botones.get(8).getSimbolo().equals("O"))
					),

				// O horizontales
					(
					    (botones.get(0).getBstate() && botones.get(0).getSimbolo().equals("O")) && 
					    (botones.get(3).getBstate() && botones.get(3).getSimbolo().equals("O")) &&
					    (botones.get(6).getBstate() && botones.get(6).getSimbolo().equals("O"))
					),
					(
					    (botones.get(1).getBstate() && botones.get(1).getSimbolo().equals("O")) && 
					    (botones.get(4).getBstate() && botones.get(4).getSimbolo().equals("O")) && 
					    (botones.get(7).getBstate() && botones.get(7).getSimbolo().equals("O")) 
					),
					(
					    (botones.get(2).getBstate() && botones.get(2).getSimbolo().equals("O")) && 
					    (botones.get(5).getBstate() && botones.get(5).getSimbolo().equals("O")) && 
					    (botones.get(8).getBstate() && botones.get(8).getSimbolo().equals("O"))
					),

				// O oblicuos
					(
					    (botones.get(0).getBstate() && botones.get(0).getSimbolo().equals("O")) && 
					    (botones.get(4).getBstate() && botones.get(4).getSimbolo().equals("O")) && 
					    (botones.get(8).getBstate() && botones.get(8).getSimbolo().equals("O"))  
					),
					(
					    (botones.get(6).getBstate() && botones.get(6).getSimbolo().equals("O")) && 
					    (botones.get(4).getBstate() && botones.get(4).getSimbolo().equals("O")) && 
					    (botones.get(2).getBstate() && botones.get(2).getSimbolo().equals("O"))  
					)
		};
		
		for (int i = 0; i < casosVictoria.length; i++) {
			if (casosVictoria[i]) {
				numeroCasos+=1;
			} 
		}
		
		if(numeroCasos>=1) {
			return true;
		}else return false;
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
