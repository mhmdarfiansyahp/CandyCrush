import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LeaderboardForm extends JFrame {
    private ArrayList<String> leaderboardData;
    private int points;

    public LeaderboardForm(ArrayList<String> leaderboardData, int points) {
        this.leaderboardData = leaderboardData;
        this.points = points;

        initLeaderboardData(); // Panggil method untuk mengambil data dari database dan menampilkannya

        setTitle("Leaderboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Tampilan fullscreen
    }

    private void initLeaderboardData() {
        JPanel leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Leaderboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardPanel.add(titleLabel);
        leaderboardPanel.add(Box.createVerticalStrut(10));

        // Add leaderboard entries from the ArrayList
        for (int i = 0; i < leaderboardData.size(); i++) {
            JLabel entryLabel = new JLabel(i + 1 + ". " + leaderboardData.get(i));
            entryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            leaderboardPanel.add(entryLabel);
        }

        leaderboardPanel.add(Box.createVerticalStrut(10));

        JLabel pointsLabel = new JLabel("Points: " + points);
        pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardPanel.add(pointsLabel);

        getContentPane().add(leaderboardPanel);
    }
}
