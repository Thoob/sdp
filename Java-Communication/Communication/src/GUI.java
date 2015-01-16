import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;


public class GUI extends JFrame {

	public GUI(){
		setTitle("Controller");
        
        setSize(300,300);
        
        JDesktopPane desktopPane = new JDesktopPane();
        desktopPane.setBackground(Color.WHITE);
        getContentPane().add(desktopPane, BorderLayout.CENTER);
        
        JButton Start = new JButton("Start");
        Start.setSelectedIcon(null);
        Start.setIcon(null);
        Start.setForeground(Color.BLACK);
        desktopPane.setLayer(Start, 0);
        Start.setBackground(Color.GREEN);
        Start.setBounds(0, 220, 101, 42);
        desktopPane.add(Start);
        Start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Start");
				// TODO Auto-generated method stub
				
			}
        	
        });

        
        JButton Stop = new JButton("Stop");
        Stop.setBackground(Color.RED);
        Stop.setForeground(new Color(0, 0, 0));
        Stop.setBounds(177, 220, 107, 42);
        desktopPane.add(Stop);
        Stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Stop");
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        JButton button_left = new JButton();
        button_left.setBounds(25, 61, 70, 70);
        ImageIcon left = new ImageIcon("assets\\left.png");
        button_left.setIcon(left);
        Image left1 = left.getImage().getScaledInstance(button_left.getWidth(), button_left.getHeight(), Image.SCALE_DEFAULT);
        left.setImage(left1);
        desktopPane.add(button_left);
        button_left.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Left");
				// TODO Auto-generated method stub
				
			}
        	
        });

        
        JButton button_up = new JButton();
        button_up.setBounds(105, 11, 70, 70);
        ImageIcon up = new ImageIcon("assets\\up.png");
        button_up.setIcon(up);
        Image up1 = up.getImage().getScaledInstance(button_up.getWidth(), button_up.getHeight(), Image.SCALE_DEFAULT);
        up.setImage(up1);
        desktopPane.add(button_up);
        button_up.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Up");
				// TODO Auto-generated method stub
				
			}
        	
        });

        
        JButton button_right = new JButton();
        button_right.setBounds(187, 61, 70, 70);
        ImageIcon right = new ImageIcon("assets\\right.png");
        button_right.setIcon(right);
        Image right1 = right.getImage().getScaledInstance(button_right.getWidth(), button_right.getHeight(), Image.SCALE_DEFAULT);
        right.setImage(right1);
        desktopPane.add(button_right);
        button_right.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Right");
				// TODO Auto-generated method stub
				
			}
        	
        });

        
        JButton button_down = new JButton();
        button_down.setBounds(105, 113, 70, 70);
        ImageIcon down = new ImageIcon("assets\\down.png");
        button_down.setIcon(down);
        Image down1 = down.getImage().getScaledInstance(button_down.getWidth(), button_down.getHeight(), Image.SCALE_DEFAULT);
        down.setImage(down1);
        desktopPane.add(button_down);
        button_down.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JButton o =(JButton) (arg0.getSource());
				
				System.out.println("Down");
				// TODO Auto-generated method stub
				
			}
        	
        });


        setVisible(true);
	}
}
