package org.example;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {

    private ProcesadorA08 procesador;
    private JTextArea txtTerminal;
    private JTextField txtPayload;

    public MainUI() {
        BaseDeDatos db = new BaseDeDatos();
        procesador = new ProcesadorA08(db);

        setTitle("Demostración A08: RCE + DB Invisible");
        setSize(680, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout(10, 10));

        txtTerminal = new JTextArea();
        txtTerminal.setBackground(Color.BLACK);
        txtTerminal.setForeground(Color.GREEN);
        txtTerminal.setFont(new Font("Consolas", Font.BOLD, 14));
        txtTerminal.setEditable(false);
        txtTerminal.setText("[SISTEMA INICIADO]\nConexión a Base de Datos establecida...\nEsperando paquete de datos...\n\n");
        add(new JScrollPane(txtTerminal), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
        panelInferior.setBackground(Color.DARK_GRAY);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtPayload = new JTextField();
        txtPayload.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtPayload.setBackground(Color.BLACK);
        txtPayload.setForeground(Color.WHITE);
        txtPayload.setCaretColor(Color.GREEN);

        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 10, 0));
        panelBotones.setBackground(Color.DARK_GRAY);

        JButton btnGenerar = new JButton("1. Generar Token");
        btnGenerar.setBackground(Color.BLACK);
        btnGenerar.setForeground(Color.GREEN);

        JButton btnProcesar = new JButton("2. Procesar");
        btnProcesar.setBackground(Color.RED);
        btnProcesar.setForeground(Color.WHITE);

        JButton btnLimpiar = new JButton("Limpiar Terminal");
        btnLimpiar.setBackground(Color.GRAY);
        btnLimpiar.setForeground(Color.WHITE);

        panelBotones.add(btnGenerar);
        panelBotones.add(btnProcesar);
        panelBotones.add(btnLimpiar);

        panelInferior.add(new JLabel("Payload Base64: ", SwingConstants.RIGHT), BorderLayout.WEST);
        panelInferior.add(txtPayload, BorderLayout.CENTER);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);



        btnLimpiar.addActionListener(e -> txtTerminal.setText("[SISTEMA LIMPIO]\nEsperando paquete de datos...\n\n"));

        btnGenerar.addActionListener(e -> {
            String datos = "usuario:jesus;rol:normal;comando:ninguno";

//            String token = procesador.generarTokenVulnerable(datos);
             String token = procesador.generarTokenSeguro(datos);

            txtPayload.setText(token);
            registrarLog("[INFO] Token generado y entregado al usuario.");
        });

        btnProcesar.addActionListener(e -> {
            String payloadRecibido = txtPayload.getText();


//            procesador.validarVulnerable(payloadRecibido, this::registrarLog);
             procesador.validarSeguro(payloadRecibido, this::registrarLog);
        });
    }

    private void registrarLog(String mensaje) {
        txtTerminal.append("> " + mensaje + "\n");
        txtTerminal.setCaretPosition(txtTerminal.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainUI().setVisible(true);
        });
    }
}