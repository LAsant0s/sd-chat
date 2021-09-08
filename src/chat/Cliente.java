package chat;

import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.*;

public class Cliente extends Thread {
	
	public static boolean done = false;
	private Socket conexao;
	
	public Cliente(Socket s) {
		conexao = s;
	}

	public static void main(String[] args) {
		try {
			Socket conexao = new Socket("localhost", 2000);
			PrintStream saida = new PrintStream(conexao.getOutputStream());
			
			BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Entre com seu nome: ");
			
			String meuNome = teclado.readLine();
			saida.println(meuNome);
			
			Thread t = new Cliente(conexao);
			t.start();
			String linha;
			
			while (true) {
				if (done) {
					break;
				}
				System.out.println("> ");
				linha = teclado.readLine();
				saida.println(linha);
			}
			
		} catch (IOException e) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	public void run(){
		try {
			BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
			String linha;
			while (true) {
				linha = entrada.readLine();
				if (linha.trim().equals("")){
					System.out.println("Conexao encerrada!!!");
					break;
				}
				System.out.println();
				System.out.println(linha);
				System.out.print("...> ");
			}
			done = true;
			} catch (IOException e) {
				Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, e);
			}
		}

}
