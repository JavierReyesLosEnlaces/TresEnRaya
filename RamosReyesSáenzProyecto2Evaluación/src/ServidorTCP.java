import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

public class ServidorTCP {

	public static DataInputStream disJugador1, disJugador2;

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

		try (ServerSocket socketDelServidor = new ServerSocket(40000, 2)) {
			// Se crea el socket servidor y se limita las conexiones a dos

			System.out.println("Esperando a que se conecten los usuarios...");

			System.out.println("Tic Tac Toe Server is Running...");
			var pool = Executors.newFixedThreadPool(200);

			Socket socketDelCliente1 = socketDelServidor.accept();
			disJugador1 = new DataInputStream(socketDelCliente1.getInputStream());
			String nombreJugador1 = disJugador1.readUTF();

			Socket socketDelCliente2 = socketDelServidor.accept();
			disJugador2 = new DataInputStream(socketDelCliente2.getInputStream());
			String nombreJugador2 = disJugador2.readUTF();

			Juego juego = new Juego();
			pool.execute(juego.new Jugador(nombreJugador1, socketDelCliente1, 'X'));
			pool.execute(juego.new Jugador(nombreJugador2, socketDelCliente2, 'O'));

		} catch (IOException ioe) {
			System.err.println("Error al abrir el socket de servidor : " + ioe);
			System.exit(-1);
		}
	}
}

// CLASE JUEGO
class Juego {
	// Se inicializa el tablero
	static Jugador[] tablero = new Jugador[9];

	static Jugador jugadorActivo;

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

	public static void logDePartidas() {

	}

	public static void checkJugada(int posicion, Jugador jugador) {
		if (jugador != jugadorActivo) {
			throw new IllegalStateException("No es tu turno. ");
		} else if (jugador.oponente == null) {
			throw new IllegalStateException("No tienes oponente");
		} else if(tablero[posicion] != null) {
			throw new IllegalStateException("Celda ocupada");
		}
	}

	// CLASE JUGADOR
	class Jugador implements Runnable {

		public static DataInputStream dis;
		public static DataOutputStream dos;
		public static DataOutputStream dosJugadorActivo, dosOponente;

		String nombreJugador;
		Jugador oponente;
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

		public Jugador getOponente() {
			return oponente;
		}

		public void setOponente(Jugador oponente) {
			this.oponente = oponente;
		}

		@Override
		public void run() {
			System.out.println("Hola, soy el jugador " + nombreJugador + "desde el metodo run()");
			try {
				dis = new DataInputStream(socketDelCliente.getInputStream());
				dos = new DataOutputStream(socketDelCliente.getOutputStream());

				// 1. Asignar el jugador activo y el oponente
				if (this.simbolo == 'X') {
					jugadorActivo = this;
				} else {
					if(this.getOponente()==null) {
						oponente = jugadorActivo;
						oponente.setOponente(this);	
					}
				}

				// 2. Envio de la marca al cliente, los dos jugadores mandan su marca a su
				// resoectivo cliente

				dos.writeUTF("1" + this.simbolo);
				// 1 + dato: estamos recibiendo el simbolo para asignarselo al cliente
			

				// El jugador va a estar a la escucha de la posicion clickada por el cliente
				// [enviarInfo()]
				while (true) {
					try {

						int posicion = dis.readInt();
						
						// chequear lo jugada 
						checkJugada(posicion, this);
						
						// Actualizar el tablero
						tablero[posicion] = this;
						
						// Se cambian los roles
						jugadorActivo = this.oponente;

						// Se envía la posicion al oponente
						dosOponente.writeUTF("4"+posicion);
						
						// Se manda el resultado al oponente
						if (seHaGanado(tablero)) {
							// Enviar información al jugadorActivo
							dosJugadorActivo.writeUTF("2Has ganado, " + this.getNombreJugador());

							// Enviar información al oponente
							dosOponente.writeUTF("2Has ganado, " + oponente.getNombreJugador());

							// logDePartidas();
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						dos.writeUTF("3"+e.getMessage());
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
