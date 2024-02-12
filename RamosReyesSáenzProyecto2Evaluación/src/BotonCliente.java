import java.io.Serializable;

public class BotonCliente implements Serializable{
	
	private Boolean bstate = false;
	private String simbolo = " ";
	
	public BotonCliente() {
	}

	public Boolean getBstate() {
		return bstate;
	}

	public void setBstate(Boolean bstate) {
		this.bstate = bstate;
	}

	public String getSimbolo() {
		return simbolo;
	}

	public void setSimbolo(String simbolo) {
		this.simbolo = simbolo;
	}

	public String toString() {
		return "BotonCliente [bstate=" + bstate + ", simbolo=" + simbolo + "]";
	}
		
}
