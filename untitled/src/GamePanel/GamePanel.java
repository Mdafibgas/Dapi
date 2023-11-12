package GamePanel;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.io.*;
import java.util.Scanner;
import java.sql.*;


public class GamePanel extends JPanel implements ActionListener {

    // logic variables
    public boolean playerX;
    boolean gameDone = false;
    int winner = -1;
    int player1wins = 0, player2wins = 0;
    int[][] board = new int[3][3];

    // paint variables
    int lineWidth = 5;
    int lineLength = 270;
    int x = 15, y = 100; // location of first line
    int offset = 95; // square width
    int a = 0;
    int b = 5;
    int selX = 0;
    int selY = 0;

    // COLORS
    Color turtle = new Color(0x80bdab);
    Color orange = new Color(0xfdcb9e);
    Color offwhite = new Color(0xf7f7f7);
    Color darkgray = new Color(0x3f3f44);
    Color lightblue = new Color(173, 216, 230);
    // COMPONENTS
    JButton jButton;
    JButton historyButton;

    private JButton deleteHistoryButton;

    private String playerNameX;
    private String playerNameO;

    // CONSTRUCTOR
    public GamePanel() {
        Dimension size = new Dimension(420, 300);
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);
        addMouseListener(new XOListener());
        playerNameX = JOptionPane.showInputDialog("Masukkan nama pemain X:");
        playerNameO = JOptionPane.showInputDialog("Masukkan nama pemain O:");
        simpanNamaKeDatabase(playerNameX, playerNameO);
        jButton = new JButton("Play Again?");
        jButton.addActionListener(this);
        jButton.setBounds(100, 130, 105, 40);
        add(jButton);
        resetGame();
        setLayout(null);
        historyButton = new JButton("History"); // Inisialisasi JButton
        historyButton.addActionListener(this);
        historyButton.setBounds(308, 200, 105, 30);
        add(historyButton);
        deleteHistoryButton = new JButton("Reset Score");
        deleteHistoryButton.addActionListener(this);
        deleteHistoryButton.setBounds(308, 250, 105, 30);
        add(deleteHistoryButton);

    }
    private void simpanNamaKeDatabase(String playerNameX, String playerNameO) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tic tac toe", "root" , "");
            Statement stmt = conn.createStatement();

            // Periksa apakah nama pemain X sudah ada
            ResultSet resultX = stmt.executeQuery("SELECT * FROM player_names WHERE player_name = '" + playerNameX + "'");
            if (!resultX.next()) {
                String queryX = "INSERT INTO player_names (player_name) VALUES ('" + playerNameX + "')";
                stmt.executeUpdate(queryX);
            }

            // Periksa apakah nama pemain O sudah ada
            ResultSet resultO = stmt.executeQuery("SELECT * FROM player_names WHERE player_name = '" + playerNameO + "'");
            if (!resultO.next()) {
                String queryO = "INSERT INTO player_names (player_name) VALUES ('" + playerNameO + "')";
                stmt.executeUpdate(queryO);
            }

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetGame() {
        playerX = true;
        winner = -1;
        gameDone = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0; // all spots are empty
            }
        }
        getJButton().setVisible(false);
    }

    public void paintComponent(Graphics page) {
        super.paintComponent(page);
        drawBoard(page);
        drawUI(page);
        drawGame(page);
    }

    public void drawBoard(Graphics page) {
        setBackground(lightblue);
        page.setColor(darkgray);
        page.fillRoundRect(x, y, lineLength, lineWidth, 5, 30);
        page.fillRoundRect(x, y + offset, lineLength, lineWidth, 5, 30);
        page.fillRoundRect(y, x, lineWidth, lineLength, 30, 5);
        page.fillRoundRect(y + offset, x, lineWidth, lineLength, 30, 5);
    }

    public void drawUI(Graphics page) {
        // SET COLOR AND FONT
        page.setColor(darkgray);
        page.fillRect(300, 0, 120, 300);
        Font font = new Font("Helvetica", Font.PLAIN, 20);
        page.setFont(font);

        // SET WIN COUNTER
        page.setColor(offwhite);
        page.drawString("SCORE", 320, 30);
        page.drawString(": " + player1wins, 362, 70);
        page.drawString(": " + player2wins, 362, 105);

        // DRAW score X
        ImageIcon xIcon = new ImageIcon("orangex.png");
        Image xImg = xIcon.getImage();
        Image newXImg = xImg.getScaledInstance(27, 27, java.awt.Image.SCALE_SMOOTH);
        ImageIcon newXIcon = new ImageIcon(newXImg);
        page.drawImage(newXIcon.getImage(), 44 + offset * 1 + 190, 47 + offset * 0, null);

        // DRAW score O
        page.setColor(offwhite);
        page.fillOval(43 + 190 + offset, 80, 30, 30);
        page.setColor(darkgray);
        page.fillOval(49 + 190 + offset, 85, 19, 19);

        // DRAW WHOS TURN or WINNER
        page.setColor(offwhite);
        Font font1 = new Font("Serif", Font.ITALIC, 20);
        page.setFont(font1);

        if (gameDone) {
            if (winner == 1) { // x
                page.drawString("The winner is ",  310, 150);
                page.drawString(playerNameX, 345, 190);
            } else if (winner == 2) { // o
                page.drawString("The winner is ", 310, 150);
                page.drawString( playerNameO, 345, 190);
            } else if (winner == 3) { // tie
                page.drawString("It's a tie", 330, 178);
            }
        } else {
            Font font2 = new Font("Serif", Font.ITALIC, 20);
            page.setFont(font2);
            page.drawString("Its", 350, 160);
            if (playerX) {
                page.drawString(playerNameX + " Turn", 320, 180);
            } else {
                page.drawString(playerNameO + " Turn", 320, 180);
            }
        }

    }

    public void drawGame(Graphics page) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {

                } else if (board[i][j] == 1) {
                    ImageIcon xIcon = new ImageIcon("orangex.png");
                    Image xImg = xIcon.getImage();
                    page.drawImage(xImg, 30 + offset * i, 30 + offset * j, null);
                } else if (board[i][j] == 2) {
                    page.setColor(offwhite);
                    page.fillOval(30 + offset * i, 30 + offset * j, 50, 50);
                    page.setColor(turtle);
                    page.fillOval(40 + offset * i, 40 + offset * j, 30, 30);
                }
            }
        }
        repaint();
    }

    public void checkWinner() {
        if (gameDone == true) {
            System.out.print("gameDone");
            return;
        }
        // vertical
        int temp = -1;
        if ((board[0][0] == board[0][1])
                && (board[0][1] == board[0][2])
                && (board[0][0] != 0)) {
            temp = board[0][0];
        } else if ((board[1][0] == board[1][1])
                && (board[1][1] == board[1][2])
                && (board[1][0] != 0)) {
            temp = board[1][1];
        } else if ((board[2][0] == board[2][1])
                && (board[2][1] == board[2][2])
                && (board[2][0] != 0)) {
            temp = board[2][1];

            // horizontal
        } else if ((board[0][0] == board[1][0])
                && (board[1][0] == board[2][0])
                && (board[0][0] != 0)) {
            temp = board[0][0];
        } else if ((board[0][1] == board[1][1])
                && (board[1][1] == board[2][1])
                && (board[0][1] != 0)) {
            temp = board[0][1];
        } else if ((board[0][2] == board[1][2])
                && (board[1][2] == board[2][2])
                && (board[0][2] != 0)) {
            temp = board[0][2];

            // diagonal
        } else if ((board[0][0] == board[1][1])
                && (board[1][1] == board[2][2])
                && (board[0][0] != 0)) {
            temp = board[0][0];
        } else if ((board[0][2] == board[1][1])
                && (board[1][1] == board[2][0])
                && (board[0][2] != 0)) {
            temp = board[0][2];
        } else {

            // CHECK FOR A TIE
            boolean notDone = false;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == 0) {
                        notDone = true;
                        break;
                    }
                }
            }
            if (notDone == false) {
                temp = 3;
            }
        }
        if (temp > 0) {
            winner = temp;
            if (winner == 1) {
                player1wins++;
                updatePlayerStats(playerNameX, player1wins + player2wins, player1wins);
                System.out.println("winner is " + playerNameX);
            } else if (winner == 2) {
                player2wins++;
                updatePlayerStats(playerNameO, player1wins + player2wins, player2wins);
                System.out.println("winner is " + playerNameO);
            } else if (winner == 3) {
                System.out.println("It's a tie");
            }
            gameDone = true;
            getJButton().setVisible(true);

            simpanRiwayatKeDatabase(playerNameX, playerNameO, player1wins, player2wins);
        }

    }

    public void updatePlayerStats(String playerName, int totalGames, int wins) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tic tac toe", "root", "");
            Statement stmt = conn.createStatement();

            // Update total_games dan wins untuk pemain yang sudah ada
            String updateQuery = "UPDATE player_stats SET total_games = total_games + " + totalGames + ", wins = wins + " + wins + " WHERE player_name = '" + playerName + "'";
            stmt.executeUpdate(updateQuery);

            // Jika pemain belum ada dalam tabel, tambahkan pemain baru
            ResultSet result = stmt.executeQuery("SELECT * FROM player_stats WHERE player_name = '" + playerName + "'");
            if (!result.next()) {
                String insertQuery = "INSERT INTO player_stats (player_name, total_games, wins) VALUES ('" + playerName + "', " + totalGames + ", " + wins + ")";
                stmt.executeUpdate(insertQuery);
            }

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void simpanRiwayatKeDatabase(String playerNameX, String playerNameO, int player1wins, int player2wins) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tic tac toe", "root", "");
            String query = "INSERT INTO game_history (playerX_name, playerO_name, playerX_score, playerO_score) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, playerNameX);
            preparedStatement.setString(2, playerNameO);
            preparedStatement.setInt(3, player1wins);
            preparedStatement.setInt(4, player2wins);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            conn.close(); // Ingat untuk menutup koneksi setelah penggunaan
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void tampilkanRiwayat(String playerNameX, String playerNameO) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tic tac toe", "root", "");
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM game_history WHERE playerX_name = ? AND playerO_name = ?");
            stmt.setString(1, playerNameX);
            stmt.setString(2, playerNameO);
            ResultSet rs = stmt.executeQuery();

            StringBuilder history = new StringBuilder("History Match:\n\n");

            while (rs.next()) {
                String playerX = rs.getString("playerX_name");
                String playerO = rs.getString("playerO_name");
                int playerXScore = rs.getInt("playerX_score");
                int playerOScore = rs.getInt("playerO_score");

                history.append(playerX).append(" vs ").append(playerO).append(", Score: ").append(playerXScore).append(" - ").append(playerOScore).append("\n");
            }

            JOptionPane.showMessageDialog(this, history.toString(), "History Match", JOptionPane.PLAIN_MESSAGE);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        private void deleteGameHistory(String playerNameX, String playerNameO) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tic tac toe", "root", "");
                String query = "DELETE FROM game_history WHERE playerX_name = ? AND playerO_name = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, playerNameX);
                preparedStatement.setString(2, playerNameO);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    private void resetPlayerScores() {
        player1wins = 0;
        player2wins = 0;
        repaint();
    }

    public JButton getJButton() {	return jButton; }

    public void setPlayerXWins(int a) {
        player1wins = a;
    }

    public void setPlayerOWins(int a) {
        player2wins = a;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tic Tac Toe");
        frame.getContentPane();

        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);

        frame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                try {
                    File file = new File("score.txt");
                    Scanner sc = new Scanner(file);
                    gamePanel.setPlayerXWins(Integer.parseInt(sc.nextLine()));
                    gamePanel.setPlayerOWins(Integer.parseInt(sc.nextLine()));
                    sc.close();
                } catch (IOException io) {
                    // file doesnt exist
                    File file = new File("score.txt");
                }
            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    private class XOListener implements MouseListener {

        public void mouseClicked(MouseEvent event) {
            selX = -1;
            selY = -1;
            if (gameDone == false) {
                a = event.getX();
                b = event.getY();
                int selX = 0, selY = 0;
                if (a > 12 && a < 99) {
                    selX = 0;
                } else if (a > 103 && a < 195) {
                    selX = 1;
                } else if (a > 200 && a < 287) {
                    selX = 2;
                } else {
                    selX = -1;
                }

                if (b > 12 && b < 99) {
                    selY = 0;
                } else if (b > 103 && b < 195) {
                    selY = 1;
                } else if (b > 200 && b < 287) {
                    selY = 2;
                } else {
                    selY = -1;
                }
                if (selX != -1 && selY != -1) {

                    if (board[selX][selY] == 0) {
                        if (playerX) {
                            board[selX][selY] = 1;
                            playerX = false;
                        } else {
                            board[selX][selY] = 2;
                            playerX = true;
                        }
                        checkWinner();
                        System.out.println(" CLICK= x:" + a + ",y: " + b + "; selX,selY: " + selX + "," + selY);

                    }
                } else {
                    System.out.println("invalid click");
                }
            }
        }

        public void mouseReleased(MouseEvent event) {}
        public void mouseEntered(MouseEvent event) {}
        public void mouseExited(MouseEvent event) {}
        public void mousePressed(MouseEvent event) {}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jButton) {
            resetGame();
        } else if (e.getSource() == historyButton) {
            tampilkanRiwayat(playerNameX, playerNameO);
        } else if (e.getSource() == deleteHistoryButton) {
            deleteGameHistory(playerNameX, playerNameO);
            player1wins = 0;
            player2wins = 0;
            repaint();
        }
    }



}
