package ui;

import engine.SoundManager;
import entity.Arrow;
import entity.PlayerStats;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.ActiveEvent;

public class UpgradeShop extends JDialog {

    private PlayerStats stats;

    public UpgradeShop(JFrame parent, PlayerStats stats)
    {
        super(parent,"Upgrades Shop", true);
        this.stats=stats;
        buildUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(15, 15, 15));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //TITLE

        JLabel title = new JLabel("| UPGRADE SHOP", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(255, 200, 50));
        main.add(title, BorderLayout.NORTH);

        //Coin Display

         JLabel coinInfo = new JLabel("Your Coins: "+stats.getCoins(), SwingConstants.CENTER);
         coinInfo.setFont(new Font("Consolas", Font.PLAIN, 14));
        coinInfo.setForeground(new Color(100,220,100));

        //Items Panel
        JPanel items = new JPanel(new GridLayout(0, 1, 5, 8));
        items.setBackground(new Color(15, 15, 30));

        addShopItem(items, coinInfo, "Power Arrows x5", "Deals 3x damage", 50, Arrow.ArrowType.POWER, 5);
        addShopItem(items, coinInfo, "Thunder Arrows x10", "Chain lightning", 70, Arrow.ArrowType.THUNDER, 10);
        addShopItem(items, coinInfo, " Ice Arrows x10", "Freezes birds", 60, Arrow.ArrowType.ICE, 10);
        addShopItem(items, coinInfo, "Net Arrows x5" ," Captures bird", 45, Arrow.ArrowType.NET, 5);

        JPanel center = new JPanel(new BorderLayout(5,5));
        center.setBackground(new Color(15,15,30));
        center.add(coinInfo, BorderLayout.NORTH);
        center.add(items, BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close (S)");
        closeBtn.setFont(new Font("Arial", Font.BOLD,13));
        closeBtn.setBackground(new Color(60,60,100));
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setBorder(BorderFactory.createLineBorder(new Color(100,100,180)));
        closeBtn.setFocusable(false);
        closeBtn.addActionListener( e ->{
            SoundManager.play("src/sounds/button_click.wav");
            dispose();
        });
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(new Color(15,15,30));
        btnPanel.add(closeBtn);
        main.add(btnPanel,BorderLayout.SOUTH);

        setContentPane(main);
        setBackground(new Color(15,15,30));


    }

          private void addShopItem(JPanel panel, JLabel coinLabel, String name, String desc, int cost, Arrow.ArrowType type, int amount ){

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT,10,4));
        row.setBackground(new Color(25,25,25));
        row.setBorder(BorderFactory.createLineBorder(new Color(60,60,100),1));

        JLabel nameL = new JLabel(name);
        nameL.setFont(new Font("Arial", Font.BOLD, 13));
        nameL.setForeground(Color.WHITE);
        nameL.setPreferredSize(new Dimension(170,20));

        JLabel descL = new JLabel(desc);
        descL.setFont(new Font("Arial", Font.ITALIC, 11));
        descL.setForeground(new Color(160,160,180));
        descL.setPreferredSize(new Dimension(130,20));

        JLabel costL = new JLabel("💰" +cost);
        costL.setFont(new Font("Arial", Font.BOLD, 13));
        costL.setForeground(new Color(255,210,50));
        costL.setPreferredSize(new Dimension(60,20));

        JButton buyBtn = new JButton("BUY");
        buyBtn.setFont(new Font("Arial", Font.BOLD, 12));
        buyBtn.setBackground(new Color(40,130,40));
        buyBtn.setForeground(Color.BLACK);
        buyBtn.setBorder(BorderFactory.createLineBorder(new Color(60,180,60)));
        buyBtn.setFocusable(false);
        buyBtn.setPreferredSize(new Dimension(60 ,24));
              buyBtn.addActionListener( e ->{
                  SoundManager.play("src/sounds/button_click.wav");

            if (stats.spendCoins(cost)) {
                SoundManager.play("src/sounds/arrow-purchase.wav");
                stats.addArrows(type, amount);
                coinLabel.setText("Your Coins: " + stats.getCoins());
                JOptionPane.showMessageDialog(this,
                        amount + "x " + name.split(" ")[0] + " arrows added!",
                        "Purchased!", JOptionPane.INFORMATION_MESSAGE);

            } else {
                SoundManager.play("src/sounds/error-pop.wav");
                JOptionPane.showMessageDialog(this,
                        "Not enough coins! Need " + cost,
                        "Insufficient Coins", JOptionPane.WARNING_MESSAGE);
            }
        });

        row.add(nameL);
        row.add(descL);
        row.add(costL);
        row.add(buyBtn);
        panel.add(row);

    }
}
