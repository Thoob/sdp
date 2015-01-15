import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class GUI extends JFrame {

	public GUI(){
		
		JPanel panel = new JPanel(new BorderLayout());
		add(panel);
		
		JLabel movementsLabel = new JLabel("Movements");
        panel.add(movementsLabel, BorderLayout.NORTH);
        
        JButton startBtn = new JButton("Start");
        startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
        panel.add(startBtn, BorderLayout.EAST);
        
        JButton stopBtn = new JButton("Stop");
        panel.add(stopBtn, BorderLayout.WEST);
        
        setSize(300,300);

        setVisible(true);
	}
}
