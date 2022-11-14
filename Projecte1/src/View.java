import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class View extends JFrame {

	private JPanel contentPane;
	private Model model = new Model();
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					View frame = new View();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public View() {
		//CONFIG JFRAME
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
		
		JPanel panelSwitches = new JPanel();
		panelSwitches.setLayout(new BoxLayout(panelSwitches,BoxLayout.Y_AXIS));
		
		JPanel panelSliders = new JPanel();
		panelSliders.setLayout(new BoxLayout(panelSliders,BoxLayout.Y_AXIS));
		
		JPanel panelComboBoxs = new JPanel();
		panelComboBoxs.setLayout(new BoxLayout(panelComboBoxs,BoxLayout.Y_AXIS));
        
		JPanel panelLabels = new JPanel();
		panelLabels.setLayout(new BoxLayout(panelLabels,BoxLayout.Y_AXIS));
		
        contentPane.add(panelSwitches);
        contentPane.add(panelSliders);
        contentPane.add(panelComboBoxs);
        contentPane.add(panelLabels);
        
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0,0,500,500);
		
		//CONFIG JMENUBAR
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		//Menu Arxiu
		JMenu menuArxiu = new JMenu("Arxiu");
		menuBar.add(menuArxiu);
		
		//Menu Visualitzacions
		JMenu menuVisualitzacions = new JMenu("Visualitzaci\u00F3ns");
		menuBar.add(menuVisualitzacions);
		
		JMenuItem itemCarregaConf = new JMenuItem("Carregar configuració");
		itemCarregaConf.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				
				//SELECCIO DEL FITXER
				JFileChooser fileChooser = new JFileChooser();
				//Establiment del directori de fileChooser a la carpeta de l'aplicació
				fileChooser.setCurrentDirectory(new File(String.valueOf(System.getProperty("user.dir") + "/")));
				//Seleccio fitxer
				int seleccion=fileChooser.showOpenDialog(contentPane);
				if(seleccion==JFileChooser.APPROVE_OPTION){
					System.out.println("File selected: " + fileChooser.getSelectedFile().getAbsolutePath());
					model.setFile(fileChooser.getSelectedFile());
				}
				
				//CARREGUEM DADES DEL FITXER
				model.carregarConfiguracio();
				ArrayList<ControlsBlock> controls = model.getControls();
				
				for(ArrayList a: controls) {
					for (int b = 0; b<a.size(); b++) {
						System.out.println(a.get(b).getClass());
						switch(String.valueOf(a.get(b).getClass())) {
						//
						case "class CSwitch":
							JToggleButton toggleButton = (JToggleButton) a.get(b);
							panelSwitches.add(toggleButton);
							contentPane.repaint();
							break;
						case "class CSlider":
							CSlider n = (CSlider) a.get(b);
							n.setPreferredSize(new Dimension(500, 50));
							panelSliders.add(n);
					        contentPane.repaint();
							break;
						case "class CDropdown":
							JComboBox combo = (JComboBox) a.get(b);
							combo.setPreferredSize(new Dimension(10, 10));
							combo.setSize(getPreferredSize());
							panelComboBoxs.add(combo);
							panelLabels.repaint();
					        contentPane.repaint();
					        break;
						case "class CSensor":
							CSensor label = (CSensor) a.get(b);
							label.setBackground(Color.pink);
							label.setVisible(true);
							panelLabels.add(label);
							panelLabels.repaint();
					        contentPane.repaint();
					        break;
						}
							
					}
				}
				contentPane.revalidate();
			}});
		
		menuArxiu.add(itemCarregaConf);		
	}

}
