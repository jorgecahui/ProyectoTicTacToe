package com.example.ticTacToe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TicTacToe extends JFrame implements ActionListener {
    private JButton[] buttons = new JButton[9];
    private boolean turn = true; 
    private JTextField player1Field, player2Field;
    private JLabel turnLabel;
    private String player1, player2;
    private Connection connection;

    public TicTacToe() {
        setTitle("Tic Tac Toe");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3));
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 40));
            buttons[i].setFocusPainted(false);
            buttons[i].addActionListener(this);
            buttons[i].setEnabled(false);
            boardPanel.add(buttons[i]);
        }

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 2));

        player1Field = new JTextField();
        player2Field = new JTextField();
        controlPanel.add(new JLabel("Nombre Jugador 1:"));
        controlPanel.add(player1Field);
        controlPanel.add(new JLabel("Nombre Jugador 2:"));
        controlPanel.add(player2Field);

        JButton startButton = new JButton("Iniciar");
        startButton.addActionListener(e -> startGame());
        controlPanel.add(startButton);

        JButton cancelButton = new JButton("Anular");
        cancelButton.addActionListener(e -> resetGame());
        controlPanel.add(cancelButton);

        turnLabel = new JLabel("Turno: ");
        add(turnLabel, BorderLayout.SOUTH);
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.NORTH);

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:tictactoe.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        player1 = player1Field.getText();
        player2 = player2Field.getText();
        if (player1.isEmpty() || player2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese nombres de ambos jugadores.");
            return;
        }
        for (JButton button : buttons) {
            button.setEnabled(true);
            button.setText("");
        }
        turn = true;
        turnLabel.setText("Turno: " + player1 + " (X)");
    }

    private void resetGame() {
        for (JButton button : buttons) {
            button.setText("");
            button.setEnabled(false);
        }
        turnLabel.setText("Turno: ");
    }

    private void endGame(String winner) {
        for (JButton button : buttons) {
            button.setEnabled(false);
        }
        JOptionPane.showMessageDialog(this, "Ganador: " + winner);
        saveResult(winner);
    }

    private void saveResult(String winner) {
        String sql = "INSERT INTO resultados(nombre_partida, nombre_jugador1, nombre_jugador2, ganador, punto, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "Partida " + System.currentTimeMillis());
            pstmt.setString(2, player1);
            pstmt.setString(3, player2);
            pstmt.setString(4, winner);
            pstmt.setInt(5, 1);
            pstmt.setString(6, "Terminado");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkWinner() {
        int[][] winPositions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
        };

        for (int[] pos : winPositions) {
            if (buttons[pos[0]].getText().equals(buttons[pos[1]].getText()) &&
                buttons[pos[1]].getText().equals(buttons[pos[2]].getText()) &&
                !buttons[pos[0]].getText().equals("")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton buttonClicked = (JButton) e.getSource();
        buttonClicked.setText(turn ? "X" : "O");
        buttonClicked.setEnabled(false);
        if (checkWinner()) {
            endGame(turn ? player1 : player2);
        } else {
            turn = !turn;
            turnLabel.setText("Turno: " + (turn ? player1 + " (X)" : player2 + " (O)"));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TicTacToe frame = new TicTacToe();
            frame.setVisible(true);
        });
    }
}
