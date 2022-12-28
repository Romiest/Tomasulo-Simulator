import java.awt.EventQueue;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.event.ActionEvent;
import java.awt.Graphics;



public class GUI extends JFrame {

	private JPanel contentPane;
	private JTable instructionsTable;
	private JScrollPane scrollPane_1;
	private JTable table_1;
	private JTable loadBufferTable;
	private JTable storeBufferTable;
	private JTable AstationTable;
	private JTable MstationTable;
	private JTextField addLatencyTextField;
	private JTextField subLatencyTextField;
	private JTextField mulLatencyTextField;
	private JTextField divLatencyTextField;
	private JTextField loadLatencyTextField;
	private JTextField storeLatencyTextField;
	private  int index=0;
	JButton btnPrevCycleButton = new JButton("prev cycle");
	JButton btnNextCycleButton = new JButton("Next cycle");
	
	
	
	static int addLat;
	static int subLat;
	static int mulLat;
	static int divLat;
	static int loadLat;
	static int storeLat;
	private JTable memTable;



	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					GUI frame = new GUI(new ArrayList<Cycle>());
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public void updateTables(ArrayList<Cycle> cycles) {
		String [] [] instructionTable=new String[cycles.get(index).InstructionTableState.size()][4];
		String [] [] Astations=new String[cycles.get(index).AstationsState.size()][5];
		String [] [] Mstations=new String[cycles.get(index).MstationsState.size()][5];
		String [] [] Lstations=new String[cycles.get(index).LstationsState.size()][3];
		String [] [] Sstations=new String[cycles.get(index).SstationsState.size()][4];
		String [] [] regFile=new String[32][2];
		String [] [] dataMem=new String[1024][2];
		
		for(int i=0;i<instructionTable.length;i++) {
			String [] curr=cycles.get(index).InstructionTableState.get(i).split("&");
			String [] curr2=curr[1].split(" ");
			String [] res=new String[4];
			res[0]=curr[0];
			for(int j=1;j<=3;j++) {
				res[j]=curr2[j-1];
			}
				instructionTable[i]=res;
			
		}
		
		
		for(int i=0;i<Astations.length;i++) {
			String [] curr=cycles.get(index).AstationsState.get(i).split(" ");
				Astations[i]=curr;
			
		}
		for(int i=0;i<Mstations.length;i++) {
			String [] curr=cycles.get(index).MstationsState.get(i).split(" ");
				Mstations[i]=curr;
			
		}
		for(int i=0;i<Lstations.length;i++) {
			String [] curr=cycles.get(index).LstationsState.get(i).split(" ");
				Lstations[i]=curr;
			
		}
		for(int i=0;i<Sstations.length;i++) {
			String [] curr=cycles.get(index).SstationsState.get(i).split(" ");
				Sstations[i]=curr;
			
		}
		for(int i=0;i<regFile.length;i++) {
			String [] curr=cycles.get(index).regFileState.get(i).split(" ");
				regFile[i]=curr;
			
		}
		for(int i=0;i<dataMem.length;i++) {
			String [] curr=cycles.get(index).dataMemState.get(i).split(" ");
				dataMem[i]=curr;
			
		}
		instructionsTable.setModel(new DefaultTableModel(
				instructionTable,
				new String[] {
					"Instruction", "issue", "execution complete", "write back"
				}
			));
		
		MstationTable.setModel(new DefaultTableModel(
				Mstations,
					new String[] {
						"station", "time", "operation", "operand1", "operand2"
					}
				));
		
		loadBufferTable.setModel(new DefaultTableModel(
				Lstations,
				new String[] {
					"station", "time", "address"
				}
			));
		
		storeBufferTable.setModel(new DefaultTableModel(
				Sstations,
				new String[] {
					"station", "time", "adress", "value"
				}
			));
		
		table_1.setModel(new DefaultTableModel(
				regFile,
					new String[] {
						"register", "value"
					}
				));
		
		AstationTable.setModel(new DefaultTableModel(
				Astations,
				new String[] {
					"station", "time", "operation", "operand1", "operand2"
				}
			));
		
		memTable.setModel(new DefaultTableModel(
				dataMem,
				new String[] {
					"address", "value"
				}
			));
		
		
				if(index==0)
					btnPrevCycleButton.setEnabled(false); 
				else
					btnPrevCycleButton.setEnabled(true);
				
				if(index==cycles.size()-1) 
					btnNextCycleButton.setEnabled(false);
				else
					btnNextCycleButton.setEnabled(true);
					
				
				
		
	
		
		
	}
	public void writeFile(String text) throws IOException {
	FileWriter w=new FileWriter("Assembly.txt");
	w.write(text);
	w.close();
		
	}
	
	
	public GUI(ArrayList<Cycle> cycles) {
		
		 

		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1105, 755);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setForeground(new Color(0, 0, 0));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("please enter  instructions");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setBounds(10, 10, 248, 26);
		contentPane.add(lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(338, 49, 475, 229);
		contentPane.add(scrollPane);
		
		instructionsTable = new JTable();
		instructionsTable.setBackground(new Color(192, 192, 192));
		instructionsTable.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
			},
			new String[] {
				"Instruction", "issue", "execution complete", "write back"
			}
		));
		instructionsTable.getColumnModel().getColumn(2).setPreferredWidth(131);
		scrollPane.setViewportView(instructionsTable);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 49, 232, 229);
		contentPane.add(scrollPane_1);
		
		JTextArea instructiontextArea = new JTextArea();
		scrollPane_1.setViewportView(instructiontextArea);
		
		JLabel lblNewLabel_1 = new JLabel("Register File");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		lblNewLabel_1.setBounds(935, 14, 92, 20);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("clk cycle :  "+index);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel_2.setBounds(501, 18, 112, 13);
		contentPane.add(lblNewLabel_2);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(914, 49, 130, 229);
		contentPane.add(scrollPane_2);
		
		table_1 = new JTable();
		table_1.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
			},
			new String[] {
				"register", "value"
			}
		));
		table_1.getColumnModel().getColumn(0).setPreferredWidth(90);
		table_1.getColumnModel().getColumn(1).setPreferredWidth(97);
		scrollPane_2.setViewportView(table_1);
		
		JLabel lblNewLabel_3 = new JLabel("load bufffer");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 16));
		lblNewLabel_3.setBounds(277, 414, 106, 20);
		contentPane.add(lblNewLabel_3);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(226, 444, 210, 74);
		contentPane.add(scrollPane_3);
		
		loadBufferTable = new JTable();
		loadBufferTable.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null},
				{null, null, null},
				{null, null, null},
			},
			new String[] {
				"station", "time", "address"
			}
		));
		scrollPane_3.setViewportView(loadBufferTable);
		
		JScrollPane scrollPane_3_1 = new JScrollPane();
		scrollPane_3_1.setBounds(226, 601, 210, 74);
		contentPane.add(scrollPane_3_1);
		
		storeBufferTable = new JTable();
		storeBufferTable.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
			},
			new String[] {
				"station", "time", "adress", "value"
			}
		));
		scrollPane_3_1.setViewportView(storeBufferTable);
		
		JLabel lblNewLabel_3_1 = new JLabel("store bufffer");
		lblNewLabel_3_1.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 16));
		lblNewLabel_3_1.setBounds(277, 571, 106, 20);
		contentPane.add(lblNewLabel_3_1);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(490, 444, 391, 74);
		contentPane.add(scrollPane_4);
		
		AstationTable = new JTable();
		AstationTable.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null},
			},
			new String[] {
				"station", "time", "operation", "operand1", "operand2"
			}
		));
		scrollPane_4.setViewportView(AstationTable);
		
		JLabel lblNewLabel_3_2 = new JLabel("ADD/SUB stations");
		lblNewLabel_3_2.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 16));
		lblNewLabel_3_2.setBounds(591, 414, 162, 20);
		contentPane.add(lblNewLabel_3_2);
		
		JLabel lblNewLabel_3_2_1 = new JLabel("MUL/DIV stations");
		lblNewLabel_3_2_1.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 16));
		lblNewLabel_3_2_1.setBounds(591, 571, 162, 20);
		contentPane.add(lblNewLabel_3_2_1);
		
		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(490, 601, 391, 58);
		contentPane.add(scrollPane_5);
		
		MstationTable = new JTable();
		MstationTable.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null, null},
				{null, null, null, null, null},
			},
			new String[] {
				"station", "time", "operation", "operand1", "operand2"
			}
		));
		scrollPane_5.setViewportView(MstationTable);
		
		
		btnNextCycleButton.setBounds(631, 288, 182, 29);
		btnNextCycleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		
				index++;
				lblNewLabel_2.setText("clk cycle :  "+(index+1));
				updateTables(cycles);
				
				
				
				
			}
		});
		contentPane.add(btnNextCycleButton);
		
	
		btnPrevCycleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				index--;
				lblNewLabel_2.setText("clk cycle :  "+(index+1));
				updateTables(cycles);
				
			
			}
		});
		btnPrevCycleButton.setBounds(338, 288, 182, 29);
		contentPane.add(btnPrevCycleButton);
		
		JButton btnRunButton = new JButton("Run Tomasulo");
		btnRunButton.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
		btnRunButton.setBounds(10, 292, 232, 29);
		btnRunButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					writeFile(instructiontextArea.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				setLatencies();
				Tomasulo.readFile("Assembly.txt");
				addValues();
				Tomasulo.Tom();
				lblNewLabel_2.setText("clk cycle :  "+(index+1));
				updateTables(cycles);
				btnRunButton.setEnabled(false);
			}
		});
		
		contentPane.add(btnRunButton);
		
		JButton btnFinalCycleButton = new JButton("get final cycle");
		btnFinalCycleButton.setBounds(490, 345, 182, 29);
		contentPane.add(btnFinalCycleButton);
		btnFinalCycleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				index=cycles.size()-1;
				updateTables(cycles);
				lblNewLabel_2.setText("clk cycle :  "+(index+1));
			}
		});
		
		JLabel lblNewLabel_4 = new JLabel("add latency");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		lblNewLabel_4.setBounds(10, 435, 100, 13);
		contentPane.add(lblNewLabel_4);
		
		JLabel lblNewLabel_4_1 = new JLabel("sub latency");
		lblNewLabel_4_1.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		lblNewLabel_4_1.setBounds(10, 469, 100, 13);
		contentPane.add(lblNewLabel_4_1);
		
		JLabel lblNewLabel_4_2 = new JLabel("mul latency");
		lblNewLabel_4_2.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		lblNewLabel_4_2.setBounds(10, 503, 100, 13);
		contentPane.add(lblNewLabel_4_2);
		
		JLabel lblNewLabel_4_3 = new JLabel("div latency");
		lblNewLabel_4_3.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		lblNewLabel_4_3.setBounds(10, 537, 100, 13);
		contentPane.add(lblNewLabel_4_3);
		
		JLabel lblNewLabel_4_4 = new JLabel("load latency");
		lblNewLabel_4_4.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		lblNewLabel_4_4.setBounds(10, 571, 100, 13);
		contentPane.add(lblNewLabel_4_4);
		
		JLabel lblNewLabel_4_5 = new JLabel("store latency");
		lblNewLabel_4_5.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		lblNewLabel_4_5.setBounds(10, 606, 100, 13);
		contentPane.add(lblNewLabel_4_5);
		
		addLatencyTextField = new JTextField();
		addLatencyTextField.setBounds(112, 434, 38, 19);
		contentPane.add(addLatencyTextField);
		addLatencyTextField.setColumns(10);
		
		subLatencyTextField = new JTextField();
		subLatencyTextField.setColumns(10);
		subLatencyTextField.setBounds(112, 468, 38, 19);
		contentPane.add(subLatencyTextField);
		
		mulLatencyTextField = new JTextField();
		mulLatencyTextField.setColumns(10);
		mulLatencyTextField.setBounds(112, 502, 38, 19);
		contentPane.add(mulLatencyTextField);
		
		divLatencyTextField = new JTextField();
		divLatencyTextField.setColumns(10);
		divLatencyTextField.setBounds(112, 536, 38, 19);
		contentPane.add(divLatencyTextField);
		
		loadLatencyTextField = new JTextField();
		loadLatencyTextField.setColumns(10);
		loadLatencyTextField.setBounds(112, 571, 38, 19);
		contentPane.add(loadLatencyTextField);
		
		storeLatencyTextField = new JTextField();
		storeLatencyTextField.setColumns(10);
		storeLatencyTextField.setBounds(112, 605, 38, 19);
		contentPane.add(storeLatencyTextField);
		
		JScrollPane scrollPane_6 = new JScrollPane();
		scrollPane_6.setBounds(914, 382, 130, 277);
		contentPane.add(scrollPane_6);
	    String[][] data = new String[1024][2];
		
		memTable = new JTable();
		memTable.setModel(new DefaultTableModel(
			data,
			new String[] {
				"address", "value"
			}
		));
		 memTable.setPreferredScrollableViewportSize(new Dimension(500, 500));
		scrollPane_6.setViewportView(memTable);
		
		JLabel lblNewLabel_1_1 = new JLabel("Data Memory\r\n");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		lblNewLabel_1_1.setBounds(924, 347, 109, 21);
		contentPane.add(lblNewLabel_1_1);
		
	
	}
	
	public void addValues() {
		// write Tomasulo.regFile.put(name of register,value)
		// write Tomasulo.datMemory[index]= value 
		Tomasulo.regFile.put("F0", "36");
		Tomasulo.regFile.put("F1", "5");
		Tomasulo.regFile.put("F2", "3");
		Tomasulo.regFile.put("F3", "2");
		Tomasulo.regFile.put("F4", "4");
		Tomasulo.regFile.put("F5", "5");
		Tomasulo.regFile.put("F6", "1");
		Tomasulo.regFile.put("F8", "8");
		Tomasulo.regFile.put("F9", "12");
		Tomasulo.regFile.put("F7", "7");
		Tomasulo.regFile.put("F10", "2");
		Tomasulo.regFile.put("F11", "5");
		Tomasulo.dataMemory[100] = 9;
		Tomasulo.dataMemory[101] = 5;

		
	}

	
	public  void setLatencies() {
		addLat = Integer.parseInt(addLatencyTextField.getText());
		 subLat =Integer.parseInt(subLatencyTextField.getText());
		 mulLat = Integer.parseInt(mulLatencyTextField.getText());
		 divLat = Integer.parseInt(divLatencyTextField.getText());
		 loadLat = Integer.parseInt(loadLatencyTextField.getText());
		 storeLat =Integer.parseInt(storeLatencyTextField.getText());
		 
		 System.out.println("here"+(addLat-2));
	}
}
