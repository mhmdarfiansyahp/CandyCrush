import Database.Connect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.awt.Color;
import java.util.List;

public class CandyCrush extends JFrame{
    private JPanel panelutama;
    private JButton startButton;
    private JLabel lblnama;
    private JLabel lblwaktu;
    public JLabel lblpoint;
    private JPanel panelcandy;
    private JPanel Panel;
    private JPanel panelcandy2;
    private JLabel lblhigh;
    public String nama;

    public JButton[][] candyButtons;
    private Candy[][] candies;
    public final int ROWS = 6;
    public final int COLS = 6;
    public int firstClickRow = -1;
    public int firstClickCol = -1;
    public int points = 0;
    private int lastPoints = 0;
    int row, col;
    private int gameTime = 60;
    private int winningPoints = 1000;
    private Timer gameTimer;
    int previousPoints = 0;
    private boolean isGameOverShown = false;
    private boolean isWinShown = false;


    public CandyCrush(){
        FrameConfig();
        //initializeGame();
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initializeGame();
                lblpoint.setText(String.valueOf(points));
                updateUI();
                startGameTimer();
                gameTimer.start();

                // Menonaktifkan tombol Start saat permainan dimulai
                startButton.setEnabled(false);
            }
        });
    }
    public void FrameConfig() {
        add(panelutama);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void initializeGame() {
        candyButtons = new JButton[ROWS][COLS];
        candies = new Candy[ROWS][COLS];

        // Inisialisasi permen pada setiap sel
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                candies[i][j] = new Candy();
                candies[i][j].setType(getRandomCandyType(i, j));
            }
        }
        int lastPoints = getLastPointsFromDatabase(nama);

        // Jika pemain baru, atur poinnya ke 0
        if (lastPoints == -1) {
            points = 0;
            savePlayerScore(nama, points);
        } else {
            // Jika pemain sudah ada di database, lanjutkan permainan dengan poin terakhirnya
            points = 0; // Set poin ke 0 saat pemain memulai permainan baru
            // Simpan poin terakhir dari database ke variabel previousPoints
            previousPoints = lastPoints;
        }

        lblpoint.setText(String.valueOf(points));

        int highestScore = getHighestScoreFromDatabase(nama);
        lblhigh.setText(String.valueOf(highestScore));

        //initializeGraph();

        // Inisialisasi tombol untuk setiap sel
        panelcandy2.removeAll();
        panelcandy2.setLayout(new GridLayout(ROWS, COLS));
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                candyButtons[i][j] = new JButton();
                candyButtons[i][j].setPreferredSize(new Dimension(50, 50));
                candyButtons[i][j].setOpaque(false); // Set the button background to be transparent
                candyButtons[i][j].setContentAreaFilled(false); // Set the button content area to be transparent
                candyButtons[i][j].setBorderPainted(false); // Remove the button border
                candyButtons[i][j].addActionListener(new CandyButtonListener(i, j, this));
                panelcandy2.add(candyButtons[i][j]);
            }
        }

        panelcandy2.revalidate();
        panelcandy2.repaint();
    }

    public void refillCandies() {
        for (int j = 0; j < COLS; j++) {
            int emptyCount = 0;
            for (int i = ROWS - 1; i >= 0; i--) {
                if (candies[i][j].isBroken()) {
                    emptyCount++;
                } else if (emptyCount > 0) {
                    candies[i + emptyCount][j] = candies[i][j];
                    candies[i][j] = new Candy();
                    candies[i][j].setType("empty");
                }
            }

            for (int i = 0; i < emptyCount; i++) {
                candies[i][j] = new Candy();
                candies[i][j].setType(getRandomCandyType(i, j)); // Menyertakan argumen row dan col saat memanggil getRandomCandyType()
            }
        }
    }

    public ImageIcon getImageForCandyType(String candyType) {
        //String imagePath = "C:\\Users\\arfan\\OneDrive\\Documents\\Kampus\\Semester 2\\Project\\icon\\";
        switch (candyType) {
            case "red":
                return new ImageIcon( "src/Gambar/apel-removebg-preview.png");
            case "blue":
                return new ImageIcon( "src/Gambar/terong-removebg-preview.png");
            case "green":
                return new ImageIcon( "src/Gambar/alpukat-removebg-preview.png");
            case "yellow":
                return new ImageIcon( "src/Gambar/pisang-removebg-preview.png");
            case "orange":
                return new ImageIcon( "src/Gambar/jeruk-removebg-preview.png");
            case "empty":
                return null;
            default:
                return null;
        }
    }

    public void updateUI() {
        // Memperbarui tampilan tombol berdasarkan data permen
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                String candyType = candies[i][j].getType();
                ImageIcon candyIcon = getImageForCandyType(candyType);
                if (candyIcon != null) {
                    candyButtons[i][j].setIcon(candyIcon);
                } else {
                    candyButtons[i][j].setIcon(null); // Jika jenis permen "empty", hapus gambar
                }
            }
        }
        lblnama.setText(nama); // Menampilkan nama di label
    }


    public String getRandomCandyType(int row, int col) {
        String[] candyTypes = {"red", "blue", "green", "yellow", "orange"};
        String prev1 = (row > 0) ? candies[row - 1][col].getType() : "";
        String prev2 = (row > 1) ? candies[row - 2][col].getType() : "";
        String prev3 = (col > 0) ? candies[row][col - 1].getType() : "";
        String prev4 = (col > 1) ? candies[row][col - 2].getType() : "";

        String randomType;
        do {
            int randomIndex = (int) (Math.random() * candyTypes.length);
            randomType = candyTypes[randomIndex];
        } while ((randomType.equals(prev1) && randomType.equals(prev2)) || (randomType.equals(prev3) && randomType.equals(prev4)));

        return randomType;
    }

    public void swapCandies(int row1, int col1, int row2, int col2) {
        Candy candy1 = candies[row1][col1];
        Candy candy2 = candies[row2][col2];

        // Pertukaran permen
        candies[row1][col1] = candy2;
        candies[row2][col2] = candy1;

        // Cek apakah ada kombinasi setelah pertukaran
        boolean hasCombination = checkForCombination();

        if (hasCombination) {
            // Ada kombinasi, pecahkan permen dan tambahkan poin
            breakCandies();

            // Poin yang diperoleh sama dengan jumlah permen yang pecah
            int pointsEarned = countBrokenCandies() * 10;
            points += pointsEarned;
            lblpoint.setText(String.valueOf(points));

            // Peremajaan permen-permen yang kosong
            refillCandies();

            // Cek apakah ada kombinasi baru setelah peremajaan
            boolean hasNewCombination = checkForCombination();

            if (hasNewCombination) {
                // Jika ada kombinasi baru, ulangi proses pecahan dan peremajaan
                breakCandies();
                pointsEarned = countBrokenCandies() * 10;
                points += pointsEarned;
                lblpoint.setText(String.valueOf(points));
                refillCandies();
            }
        } else {
            // Tidak ada kombinasi, batalkan pertukaran permen
            candies[row1][col1] = candy1;
            candies[row2][col2] = candy2;
        }

        updateUI();
    }


    public void breakCandies() {
        int brokenPoints = 0;
        // Set poin terakhir sebelum menghitung poin baru


        // Menghitung jumlah permen yang pecah dalam setiap baris
        int[] brokenCountRows = new int[ROWS];
        for (int i = 0; i < ROWS; i++) {
            int count = 1;
            for (int j = 0; j < COLS - 1; j++) {
                if (candies[i][j].getType().equals(candies[i][j + 1].getType())) {
                    count++;
                } else {
                    if (count >= 3) {
                        for (int k = j; k > j - count; k--) {
                            candies[i][k].setBroken(true);
                        }
                        points += brokenPoints * 10; // Menambahkan poin berdasarkan jumlah permen yang pecah
                    }
                    count = 1;
                }
            }
            // Periksa permen terakhir dalam baris setelah perulangan
            if (count >= 3) {
                for (int k = COLS - 1; k >= COLS - count; k--) {
                    candies[i][k].setBroken(true);
                }
                points += count * 10; // Menambahkan poin berdasarkan jumlah permen yang pecah
            }
            brokenCountRows[i] = count;
        }

        // Menghitung jumlah permen yang pecah dalam setiap kolom
        int[] brokenCountCols = new int[COLS];
        for (int j = 0; j < COLS; j++) {
            int count = 1;
            for (int i = 0; i < ROWS - 1; i++) {
                if (candies[i][j].getType().equals(candies[i + 1][j].getType())) {
                    count++;
                } else {
                    if (count >= 3) {
                        for (int k = i; k > i - count; k--) {
                            candies[k][j].setBroken(true);
                        }
                        points += brokenPoints * 10; // Menambahkan poin berdasarkan jumlah permen yang pecah
                    }
                    count = 1;
                }
            }
            if (count >= 3) {
                for (int k = ROWS - 1; k >= ROWS - count; k--) {
                    candies[k][j].setBroken(true);
                }
                points += count * 10; // Menambahkan poin berdasarkan jumlah permen yang pecah
            }
            brokenCountCols[j] = count;
        }
    }

    public int countBrokenCandies() {
        int count = 0;

        // Hitung jumlah permen yang pecah
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (candies[i][j].isBroken()) {
                    count++;
                }
            }
        }

        return count;
    }

    public boolean checkForCombination() {
        boolean combinationFound = false;

        // Periksa kombinasi horizontal
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS - 2; j++) {
                if (candies[i][j].getType().equals(candies[i][j + 1].getType())
                        && candies[i][j].getType().equals(candies[i][j + 2].getType())) {
                    combinationFound = true;
                    candies[i][j].setBroken(true);
                    candies[i][j + 1].setBroken(true);
                    candies[i][j + 2].setBroken(true);
                }
            }
        }

        // Periksa kombinasi vertikal
        for (int i = 0; i < ROWS - 2; i++) {
            for (int j = 0; j < COLS; j++) {
                if (candies[i][j].getType().equals(candies[i + 1][j].getType())
                        && candies[i][j].getType().equals(candies[i + 2][j].getType())) {
                    combinationFound = true;
                    candies[i][j].setBroken(true);
                    candies[i + 1][j].setBroken(true);
                    candies[i + 2][j].setBroken(true);
                }
            }
        }

        return combinationFound;
    }

    private void handleGameOver() {
        if (!isGameOverShown) {
            isGameOverShown = true;
            if (points >= winningPoints) {
                JOptionPane.showMessageDialog(this, "Congratulations! You Win!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
                savePlayerScore(nama, points); // Simpan data pemain ke database jika pemain menang
            } else {
                JOptionPane.showMessageDialog(this, "Game Over", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            }

            // Simpan total poin yang baru (points + previousPoints) ke database
            if (points >= winningPoints) {
                int totalPoints = points + previousPoints;
                savePlayerScore(nama, totalPoints);
                // Set previousPoints ke total poin yang baru untuk digunakan di permainan berikutnya
                previousPoints = totalPoints;
            }

            resetGame();

            HalamanAwal halamanAwal = new HalamanAwal();
            halamanAwal.setVisible(true);
            dispose(); // Menutup halaman CandyCrush
        }
    }


    private void resetGame() {
        // Reset semua variabel dan tampilan permainan
        points = 0;
        lblpoint.setText(String.valueOf(points));
        gameTime = 60;
        updateUI();

        // Menonaktifkan tombol Start untuk mencegah memulai permainan lebih dari satu kali
        startButton.setEnabled(false);

    }

    private void startGameTimer() {
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameTime--;
                lblwaktu.setText(String.valueOf(gameTime));

                if (gameTime == 0) {
                    gameTimer.stop();
                    handleGameOver();
                }
            }
        });

        gameTimer.start();
    }

    private void savePlayerScore(String playerName, int points) {
        if (points >= winningPoints) {
            try {
                // Buat objek koneksi ke database
                Connect connection = new Connect();

                // Query SQL untuk memeriksa apakah pemain sudah ada di database
                String checkIfExistsSql = "SELECT * FROM Leaderboard WHERE Nama = ?";
                PreparedStatement checkIfExistsStatement = connection.conn.prepareStatement(checkIfExistsSql);
                checkIfExistsStatement.setString(1, playerName);
                ResultSet resultSet = checkIfExistsStatement.executeQuery();

                if (resultSet.next()) {
                    // Jika pemain sudah ada di database, update poinnya
                    int existingScore = resultSet.getInt("Score");
                    if (points > existingScore) {
                        String updateSql = "UPDATE Leaderboard SET Score = ? WHERE Nama = ?";
                        PreparedStatement updateStatement = connection.conn.prepareStatement(updateSql);
                        updateStatement.setInt(1, points);
                        updateStatement.setString(2, playerName);
                        updateStatement.executeUpdate();
                        updateStatement.close();
                    }
                } else {
                    // Jika pemain belum ada di database, simpan data pemain baru
                    String insertSql = "INSERT INTO Leaderboard (Nama, Score) VALUES (?, ?)";
                    PreparedStatement insertStatement = connection.conn.prepareStatement(insertSql);
                    insertStatement.setString(1, playerName);
                    insertStatement.setInt(2, points);
                    insertStatement.executeUpdate();
                    insertStatement.close();
                }

                // Tutup koneksi dan result set
                resultSet.close();
                connection.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private int getLastPointsFromDatabase(String playerName) {
        int lastPoints = -1;

        try {
            // Buat objek koneksi ke database
            Connect connection = new Connect();

            // Query SQL untuk mengambil data pemain berdasarkan nama
            String sql = "SELECT Score FROM Leaderboard WHERE Nama = ?";

            // Buat prepared statement untuk mengirim data ke database
            PreparedStatement preparedStatement = connection.conn.prepareStatement(sql);
            preparedStatement.setString(1, playerName);

            // Eksekusi query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Jika nama pemain ditemukan di database, ambil poin terakhirnya
            if (resultSet.next()) {
                lastPoints = resultSet.getInt("Score");
            }

            // Tutup koneksi dan result set
            resultSet.close();
            connection.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lastPoints;
    }

    private int getHighestScoreFromDatabase(String playerName) {
        int highestScore = -1;

        try {
            // Buat koneksi ke database
            Connect connection = new Connect();

            // Query SQL untuk mendapatkan nilai tertinggi berdasarkan nama pemain
            String sql = "SELECT MAX(Score) AS HighestScore FROM Leaderboard WHERE Nama = ?";

            // Buat pernyataan siap pakai untuk mengirim data ke database
            PreparedStatement preparedStatement = connection.conn.prepareStatement(sql);
            preparedStatement.setString(1, playerName);

            // Eksekusi query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Jika nama pemain ditemukan di database, ambil nilai tertingginya
            if (resultSet.next()) {
                highestScore = resultSet.getInt("HighestScore");
            }

            // Tutup koneksi dan result set
            resultSet.close();
            connection.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return highestScore;
    }


}
