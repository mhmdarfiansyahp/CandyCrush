import Database.Connect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Leaderboard extends JFrame {
    private JPanel panel1;
    private JTable table1;
    private JTable table2;
    private JButton kembaliButton;

    public Leaderboard() {
        // Tidak perlu menambahkan listener pada tabel karena tidak ada aksi klik yang dibutuhkan
        kembaliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                HalamanAwal halamanAwal = new HalamanAwal();
                halamanAwal.setVisible(true);
            }
        });
    }

    public void FrameConfig() {
        // Inisialisasi tabel dengan model kosong saat pertama kali dibuka
        table1.setModel(new DefaultTableModel());
        table2.setModel(new DefaultTableModel());

        ambilDataDanPerbaruiUI();

        add(panel1);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void ambilDataDanPerbaruiUI() {
        try {
            Connect connection = new Connect();
            String sql = "SELECT Nama, Score FROM Leaderboard ORDER BY Score DESC";
            ResultSet resultSet = connection.stat.executeQuery(sql);

            // Buat model tabel untuk data Nama
            DefaultTableModel modelNama = new DefaultTableModel();
            modelNama.addColumn("Nama");

            // Buat model tabel untuk data Score
            DefaultTableModel modelScore = new DefaultTableModel();
            modelScore.addColumn("Score");

            while (resultSet.next()) {
                String namaPemain = resultSet.getString("Nama");
                int skor = resultSet.getInt("Score");

                // Tambahkan data ke dalam model tabel sesuai dengan nama panel dari database
                if (isPanelNama(namaPemain)) {
                    modelNama.addRow(new Object[]{namaPemain});
                }

                // Tambahkan data skor ke dalam model tabel Score
                modelScore.addRow(new Object[]{skor});
            }

            connection.conn.close();

            // Set model tabel ke JTable table1
            table1.setModel(modelNama);

            // Set model tabel ke JTable table2
            table2.setModel(modelScore);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isPanelNama(String namaPemain) {
        try {
            Connect connection = new Connect();
            String sql = "SELECT COUNT(*) AS count FROM Leaderboard WHERE Nama = ?";
            java.sql.PreparedStatement statement = connection.conn.prepareStatement(sql);
            statement.setString(1, namaPemain);
            ResultSet resultSet = statement.executeQuery();
            int count = 0;
            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }
            connection.conn.close();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
