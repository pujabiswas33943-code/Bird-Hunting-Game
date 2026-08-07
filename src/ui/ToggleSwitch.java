package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ToggleSwitch extends JComponent {
    private boolean on;
    private final int trackWidth = 60;
    private final int trackHeight = 30;
    private Runnable onToggle;

    public ToggleSwitch(boolean initialState) {
        this.on = initialState;
        setPreferredSize(new Dimension(trackWidth, trackHeight));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                on = !on;
                repaint();
                if (onToggle != null) onToggle.run();
            }
        });
    }

    public void setOnToggle(Runnable r) {
        this.onToggle = r;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean state) {
        this.on = state;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Track (background pill shape)
        Color trackColor = on ? new Color(60, 200, 100) : new Color(90, 90, 100);
        g2.setColor(trackColor);
        g2.fillRoundRect(0, 0, trackWidth, trackHeight, trackHeight, trackHeight);

        // Border
        g2.setColor(new Color(255, 255, 255, 70));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, trackWidth - 1, trackHeight - 1, trackHeight, trackHeight);

        // Knob (circle)
        int knobDiameter = trackHeight - 6;
        int knobX = on ? trackWidth - knobDiameter - 3 : 3;
        g2.setColor(Color.WHITE);
        g2.fillOval(knobX, 3, knobDiameter, knobDiameter);

        g2.dispose();
    }
}