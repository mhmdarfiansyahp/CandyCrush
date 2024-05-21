import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CandyButtonListener implements ActionListener {
    private int row;
    private int col;
    private CandyCrush game; // Menyimpan referensi ke objek CandyCrushGame

    public CandyButtonListener(int row, int col, CandyCrush game) {
        this.row = row;
        this.col = col;
        this.game = game; // Inisialisasi referensi CandyCrushGame
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        int clickedRow = -1;
        int clickedCol = -1;

        // Cari posisi baris dan kolom tombol yang diklik
        for (int i = 0; i < game.ROWS; i++) {
            for (int j = 0; j < game.COLS; j++) {
                if (clickedButton == game.candyButtons[i][j]) {
                    clickedRow = i;
                    clickedCol = j;
                    break;
                }
            }
        }

        // Jika tombol yang diklik tidak valid, keluar dari metode
        if (clickedRow == -1 || clickedCol == -1) {
            return;
        }

        // Jika ini adalah klik pertama, simpan posisi dan kembali
        if (game.firstClickRow == -1 && game.firstClickCol == -1) {
            game.firstClickRow = clickedRow;
            game.firstClickCol = clickedCol;
            return;
        }

        // Jika ini adalah klik kedua, lakukan pertukaran dan cek apakah valid
        int row1 = game.firstClickRow;
        int col1 = game.firstClickCol;
        int row2 = clickedRow;
        int col2 = clickedCol;

        // Pertukaran hanya diizinkan jika dua tombol bersebelahan secara horizontal atau vertikal
        boolean isAdjacent = (row1 == row2 && Math.abs(col1 - col2) == 1)
                || (col1 == col2 && Math.abs(row1 - row2) == 1);

        if (isAdjacent) {
            // Lakukan pertukaran permen
            game.swapCandies(row1, col1, row2, col2);

            // Reset klik pertama
            game.firstClickRow = -1;
            game.firstClickCol = -1;
        }
    }
}
