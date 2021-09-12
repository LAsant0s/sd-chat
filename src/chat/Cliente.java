package chat;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Cliente extends Thread {
	
	public static boolean done = false;

	private static JTextField digiteField;
	private static JTextArea chat;
	private static PrintStream saida;
	private static String nome;

	private Socket conexao;
	
	public Cliente(Socket s) {
		conexao = s;
	}

	public static void main(String[] args) {
		try {
			Socket conexao = new Socket("localhost", 2000);
			saida = new PrintStream(conexao.getOutputStream());
			nome = JOptionPane.showInputDialog("Entre com o seu nome: ");
			
			// Painel Conectados
			
	        JFrame frame = new JFrame("Chat");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setSize(400, 400);
	        
			JPanel panelPrincipal = new JPanel();	 
			panelPrincipal.setLayout(new BorderLayout());
			
			JPanel painelConectados = new JPanel();	  
			painelConectados.setLayout(new BorderLayout(10, 10));
			painelConectados.setBorder(new EmptyBorder(10, 10, 10, 10));
	        
	        String[] conectados = { "", "Elian", "Henrique", "Luan", "Cleiton" };
	        @SuppressWarnings({ "rawtypes", "unchecked" })
			JComboBox conectadosCombo = new JComboBox(conectados);
	        JLabel conectadosLabel = new JLabel("Conectados:", JLabel.TRAILING);
	        conectadosLabel.setLabelFor(conectadosCombo);
	        
	        painelConectados.add(conectadosLabel, BorderLayout.WEST);
	        painelConectados.add(conectadosCombo, BorderLayout.CENTER);

	        // Painel Enviar Mensagem
	        
	        JPanel painelEnviar = new JPanel();
	        painelEnviar.setLayout(new BorderLayout(10, 50));
	        painelEnviar.setBorder(new EmptyBorder(10, 10, 10, 10));
	        
	        JLabel digiteLabel = new JLabel("Digite:", JLabel.TRAILING);
	        digiteField = new JTextField(20);
	        JButton digiteEnviar = new JButton("Enviar");
	        painelEnviar.add(digiteLabel, BorderLayout.WEST);
	        painelEnviar.add(digiteField, BorderLayout.CENTER);
	        painelEnviar.add(digiteEnviar, BorderLayout.EAST);
	        
	        panelPrincipal.add(painelConectados, BorderLayout.PAGE_START);
	        panelPrincipal.add(painelEnviar, BorderLayout.CENTER);

	        // Chat
	        chat = new JTextArea();
	        JScrollPane scroll = new JScrollPane(chat);

	        // Adiciona o chat e o os botões de ação
	        frame.getContentPane().add(scroll, BorderLayout.CENTER);
	        frame.getContentPane().add(panelPrincipal, BorderLayout.PAGE_END);
	        frame.setVisible(true);
	        
	        digiteEnviar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					writeChatFromClient();
				}
			});
		       
			saida.println(nome);
			Thread t = new Cliente(conexao);
			t.start();
			while (true) {
				if (done) {
					break;
				}
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
				if (linha.trim().equals("")) {
					System.out.println("Conexao encerrada!!!");
					break;
				}
				if(linha != null) {
					writeChatFromServer(linha);
				}
			}
			done = true;
		} catch (IOException e) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public static void writeChatFromClient() {
		String currentChat = chat.getText();
		String newMessage = digiteField.getText();
		saida.println(newMessage);
		newMessage = nome + ": " + newMessage;
		currentChat = !currentChat.equals("") ? currentChat + "\n" : currentChat; 
		chat.setText(currentChat + newMessage);
		digiteField.setText("");
	}
	
	public static void writeChatFromServer(String message) {
		String currentChat = chat.getText();
		currentChat = !currentChat.equals("") ? currentChat + "\n" : currentChat; 
		chat.setText(currentChat + message);
		digiteField.setText("");
	}
}
