import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0,0,500,500);
		
		//CONFIG JMENUBAR
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		//Menu Arxiu
		JMenu menuArxiu = new JMenu("Arxiu");
		menuBar.add(menuArxiu);
		
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
				
				for (ArrayList a : model.getControls()) {
					for (int b = 0; b<a.size(); b++) {
						System.out.println(a.get(b).getClass());
					}
				}
				//
				
			}});
		menuArxiu.add(itemCarregaConf);
		
		//Menu Visualitzacions
		JMenu menuVisualitzacions = new JMenu("Visualitzaci\u00F3ns");
		menuBar.add(menuVisualitzacions);
		
		//CONFIG JFRAME CONTENTPANE
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
	}

}
