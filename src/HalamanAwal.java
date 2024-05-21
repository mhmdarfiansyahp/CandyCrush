import Database.Connect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HalamanAwal extends JFrame {
    private JButton mulaiButton;
    private JPanel JButton;
    private JPanel JGambar;
    private JPanel Awal;
    private JButton LeaderboardButton;
    private JButton Keluar;
    private String nama;
    private ArrayList<String> leaderboardData = new ArrayList<>();
    private int lastPoints = 0;
    private CandyCrush candyCrush;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                HalamanAwal halamanAwal = new HalamanAwal();
                halamanAwal.setVisible(true);
            }
        });
    }

    public HalamanAwal() {
        setFullScreen(); // Panggil method setFullScreen() sebelum menambahkan panel
        add(Awal);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Awal");
        setUndecorated(true); // Matikan dekorasi jendela agar tampilan fullscreen
        setLocationRelativeTo(null);
        setVisible(true);

        mulaiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nama = JOptionPane.showInputDialog(HalamanAwal.this, "Masukkan Nama");
                if (nama != null && !nama.isEmpty()) {
                    // Menyimpan nama yang dimasukkan
                    HalamanAwal.this.nama = nama;

                    // Membuat instance dari kelas CandyCrush
                    candyCrush = new CandyCrush();

                    // Mengatur nama pada objek CandyCrush
                    candyCrush.nama = nama;

                    // Cek apakah nama pemain sudah ada di database
                    int lastPoints = checkPlayerInDatabase(nama);
                    if (lastPoints != -1) {
                        // Jika nama pemain sudah ada di database, ambil poin terakhirnya
                        candyCrush.points = lastPoints;
                        candyCrush.lblpoint.setText(String.valueOf(lastPoints));
                    }

                    // Menutup halaman awal
                    dispose();

                    // Menampilkan game CandyCrush
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            candyCrush.setVisible(true);
                        }
                    });
                }
            }
        });

        LeaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Leaderboard leaderboard = new Leaderboard();
                leaderboard.FrameConfig();
            }
        });
        Keluar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int response = JOptionPane.showConfirmDialog(HalamanAwal.this,
                        "Apakah Anda yakin ingin keluar dari permainan?",
                        "Konfirmasi Keluar",
                        JOptionPane.YES_NO_OPTION);

                if (response == JOptionPane.YES_OPTION) {
                    dispose(); // Tutup frame dan keluar dari program
                }
            }
        });
    }

    private void setFullScreen() {
        // Mendapatkan referensi ke toolkit
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        // Dapatkan ukuran layar utama
        Dimension screenSize = toolkit.getScreenSize();
        // Set tampilan ke mode full screen (maximized)
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Menyembunyikan dekorasi jendela, seperti judul dan tombol close
        setUndecorated(true);
        // Ubah ukuran frame sesuai ukuran layar utama
        setSize(screenSize.width, screenSize.height);
        // Geser frame ke posisi (0,0) untuk memastikan tampilan di pojok kiri atas layar utama
        setLocation(0, 0);
    }

    private int checkPlayerInDatabase(String playerName) {
        try {
            // Buat objek koneksi ke database
            Connect connection = new Connect();

            // Query SQL untuk mengambil data pemain berdasarkan nama
            String sql = "SELECT Score FROM Leaderboard WHERE Nama = ?";
            PreparedStatement preparedStatement = connection.conn.prepareStatement(sql);
            preparedStatement.setString(1, playerName);

            // Eksekusi query
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Jika nama pemain sudah ada di database, ambil poin terakhirnya
                int lastPoints = resultSet.getInt("Score");
                connection.conn.close();
                return lastPoints;
            } else {
                // Jika nama pemain belum ada di database, kembalikan nilai -1
                connection.conn.close();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


}
