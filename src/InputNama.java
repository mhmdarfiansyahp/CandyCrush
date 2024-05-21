import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InputNama extends JFrame {
    private JButton loginButton;
    private JTextField txtNama;
    private JPanel inputNama;
    private String nama;
    private CandyCrush candyCrush;
    private Label lblnama;

    public InputNama(CandyCrush candyCrush) {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nama = txtNama.getText();
                if (nama.isEmpty()) {
                    JOptionPane.showMessageDialog(InputNama.this, "Nama harus diisi", "Peringatan", JOptionPane.WARNING_MESSAGE);
                } else {
                    candyCrush.nama = nama; // Menyimpan nama ke variabel nama di CandyCrush
                    candyCrush.updateUI(); // Menampilkan nama di label CandyCrush

                    dispose(); // Menutup frame InputNama
                }
            }
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                CandyCrush candyCrush = new CandyCrush();
                InputNama inputNama = new InputNama(candyCrush);
                inputNama.setVisible(true);
            }
        });
    }
}
