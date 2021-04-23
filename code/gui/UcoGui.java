package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import function.Assemble;
import function.SymbolTable;
import function.UcoFileOpen;
import function.UcoInfo;

public class UcoGui extends JFrame implements ActionListener {
	String[] source = // ¸í·É¾î ¹è¿­
			{ "nop", "bgn", "sym", "end", "proc", "ret", "ldp", "push", "call", "lod", "lda", "ldc", "str", "ldi",
					"sti", "not", "neg", "inc", "dec", "dup", "add", "sub", "mult", "div", "mod", "gt", "lt", "ge",
					"le", "eq", "ne", "and", "or", "swp", "ujp", "tjp", "fjp" };

	int[] exe = new int[37];

	JScrollPane ucoScroll, memoryScroll, cpuScroll, symbolScroll, pointerScroll, statsScroll;
	JTable ucoTable, memoryTable, CPUStackTable, symbolTable, pointerTable;
	JTextArea resultText;
	JMenuBar mb;
	JMenu file;
	JMenuItem open, save, exit;
	JButton step, run;
	JFileChooser fchooser;
	FileWriter writer;
	BufferedReader br;
	DefaultTableModel ucodtm, memorydtm, cpudtm, symboldtm, pointerdtm;
	File filechoose;
	UcoFileOpen uco;
	String[] data = new String[6];
	Vector<SymbolTable> symbol = new Vector<SymbolTable>();
	Vector<UcoInfo> vector = new Vector<UcoInfo>();
	int[] Memory;
	Stack<Integer> stack;
	Assemble ass;

	public UcoGui() {
		setTitle("U-Code Interpreter");
		getContentPane().setBackground(new Color(255, 245, 238));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		setBounds(100, 100, 1611, 1011);

		ucoScroll = new JScrollPane();
		ucoScroll.setBounds(30, 30, 850, 505);
		getContentPane().add(ucoScroll);

		memoryScroll = new JScrollPane();
		memoryScroll.setBounds(906, 30, 201, 505);
		getContentPane().add(memoryScroll);

		cpuScroll = new JScrollPane();
		cpuScroll.setBounds(1135, 30, 145, 505);
		getContentPane().add(cpuScroll);

		symbolScroll = new JScrollPane();
		symbolScroll.setBounds(1337, 30, 226, 293);
		getContentPane().add(symbolScroll);

		pointerScroll = new JScrollPane();
		pointerScroll.setBounds(1337, 363, 226, 167);
		getContentPane().add(pointerScroll);

		statsScroll = new JScrollPane();
		statsScroll.setBounds(30, 562, 1250, 363);
		getContentPane().add(statsScroll);

		ucodtm = new DefaultTableModel(0, 0) {
			public boolean isCellEditable(int i, int c) {
				return false;
			}
		};
		memorydtm = new DefaultTableModel(0, 0) {
			public boolean isCellEditable(int i, int c) {
				return false;
			}
		};

		cpudtm = new DefaultTableModel(0, 0) {
			public boolean isCellEditable(int i, int c) {
				return false;
			}
		};

		symboldtm = new DefaultTableModel(0, 0) {
			public boolean isCellEditable(int i, int c) {
				return false;
			}
		};

		pointerdtm = new DefaultTableModel(0, 0) {
			public boolean isCellEditable(int i, int c) {
				return false;
			}
		};

		ucoTable = new JTable(ucodtm);
		ucoTable.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 15));
		ucoTable.setFocusable(false);
		ucoTable.setRowSelectionAllowed(false);
		ucoTable.addMouseListener(new MouseAdapter() { // Å×ÀÌºí ´Ù¸¥°Å ¼±ÅÃ ¸øÇÏ°ÔÇÏ±â
			public void mousePressed(MouseEvent e) {
				ucoTable.setRowSelectionAllowed(false);
			}

			public void mouseReleased(MouseEvent e) {
				if (UcoFileOpen.getPC() != ass.getPC() && ass.getPC() != vector.size() + 1) { // ÆÄÀÏÀ» ¿­±â¸¸ ÇßÀ»¶§, end°¡ ¾Æ´Ò¶§
					ucoTable.setRowSelectionAllowed(true);
					if (ass.getFlag() > 0) // call , ret ¹æÁö
						ucoTable.changeSelection(ass.getFlag() - 2, 1, false, false);
					else
						ucoTable.changeSelection(ass.getPC() - 2, 1, false, false);
				}
			}
		});
		ucoScroll.setViewportView(ucoTable);

		memoryTable = new JTable(memorydtm);
		memoryTable.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 15));
		memoryScroll.setViewportView(memoryTable);
		CPUStackTable = new JTable(cpudtm);
		CPUStackTable.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 15));
		cpuScroll.setViewportView(CPUStackTable);
		symbolTable = new JTable(symboldtm);
		symbolTable.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 15));
		symbolScroll.setViewportView(symbolTable);
		pointerTable = new JTable(pointerdtm);
		pointerTable.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 15));
		pointerScroll.setViewportView(pointerTable);

		resultText = new JTextArea();
		resultText.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 15));
		statsScroll.setViewportView(resultText);

		step = new JButton("STEP");
		step.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 17));
		step.setBackground(new Color(255, 228, 225));
		step.addActionListener(this);
		step.setBounds(1354, 654, 189, 61);
		getContentPane().add(step);

		run = new JButton("RUN");
		run.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 17));
		run.setBackground(new Color(255, 228, 225));
		run.addActionListener(this);
		run.setBounds(1354, 753, 189, 61);
		getContentPane().add(run);

		ucodtm.addColumn("Line");
		ucodtm.addColumn("Label");
		ucodtm.addColumn("Source");
		ucodtm.addColumn("Value1");
		ucodtm.addColumn("Value2");
		ucodtm.addColumn("Value3");

		memorydtm.addColumn("MEMORYSTACK");
		memorydtm.addColumn("");
		memoryTable.getColumnModel().getColumn(1).setPreferredWidth(30);
		cpudtm.addColumn("CPUSTACK");
		symboldtm.addColumn("LINE");
		symboldtm.addColumn("ADDRESS");
		pointerdtm.addColumn("POINTER");

		mb = new JMenuBar();
		file = new JMenu("File");
		file.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 20));

		open = new JMenuItem("Open");
		open.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 18));
		open.addActionListener(this);
		save = new JMenuItem("Save");
		save.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 18));
		save.addActionListener(this);
		exit = new JMenuItem("Exit");
		exit.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 18));
		exit.addActionListener(this);
		file.add(open);
		file.addSeparator();
		file.add(save);
		file.addSeparator();
		file.add(exit);

		save.setEnabled(false);
		mb.add(file);
		setJMenuBar(mb);

		memoryTable.setShowHorizontalLines(false);
		memoryTable.setShowVerticalLines(false);
		CPUStackTable.setShowHorizontalLines(false);
		ucoTable.setShowVerticalLines(false);
		ucoTable.setShowHorizontalLines(false);

		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tcm = CPUStackTable.getColumnModel();
		tcm.getColumn(0).setCellRenderer(dtcr);
		setVisible(true);

	}

	public void stats() // Åë°è
	{
		save.setEnabled(true);
		resultText.append("====================================== uco ÆÄÀÏ³»¿ë ======================================");
		FileReader fr;
		try {
			fr = new FileReader(filechoose);
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				resultText.append("\n" + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		resultText
				.append("\n====================================== ¸Þ¸ð¸® Á¢±Ù È½¼ö ======================================\n");
		resultText.append(Integer.toString(ass.getMemoryAccessCount()));
		resultText
				.append("\n====================================== ¸í·É¾îº° ½ÇÇà È½¼ö ======================================\n");
		for (int i = 0; i < vector.size(); i++) {
			for (int j = 0; j < source.length; j++) {
				if (vector.get(i).Source.equals(source[j])) {
					exe[j]++;
				}
			}
		}

		for (int j = 0; j < source.length; j++) {
			resultText.append(source[j] + " : " + exe[j] + "\t");
			if ((j + 1) % 10 == 0)
				resultText.append("\n");
		}

		resultText
				.append("\n====================================== ¸í·É¾îº° »ç¿ë È½¼ö ======================================\n");
		for (int j = 0; j < source.length; j++) {
			resultText.append(source[j] + " : " + ass.getSourceCount()[j] + "\t");
			if ((j + 1) % 10 == 0)
				resultText.append("\n");
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o == open) {
			save.setEnabled(false);
			ucodtm.setRowCount(0);
			symboldtm.setRowCount(0);
			pointerdtm.setRowCount(0);
			memorydtm.setRowCount(0);
			cpudtm.setRowCount(0);
			resultText.setText(null);
			step.setEnabled(true);
			run.setEnabled(true);
			fchooser = new JFileChooser();
			fchooser.setDialogTitle("Please select a uco file to import");
			FileFilter filter = new FileNameExtensionFilter("*.uco", "uco");
			fchooser.setFileFilter(filter);
			int dialog = fchooser.showOpenDialog(this);

			if (dialog == JFileChooser.APPROVE_OPTION) {
				ass = new Assemble();
				filechoose = fchooser.getSelectedFile();
				uco = new UcoFileOpen();
				uco.fileOpen(filechoose);
				vector = uco.getUcoInfo();
				symbol = uco.getSymbol();
				ass.setPC(UcoFileOpen.getPC());

				for (int i = 0; i < vector.size(); i++) {
					data[0] = Integer.toString(i + 1);
					data[1] = vector.elementAt(i).Label;
					data[2] = vector.elementAt(i).Source;
					data[3] = vector.elementAt(i).val1;
					data[4] = vector.elementAt(i).val2;
					data[5] = vector.elementAt(i).val3;
					ucodtm.addRow(data);
				}

				for (int j = 0; j < symbol.size(); j++) {
					symboldtm.addRow(new Object[] { symbol.get(j).Label, symbol.get(j).Address });
				}
			}

		} else if (o == save) {
			fchooser = new JFileChooser();
			fchooser.setDialogTitle("Please select a uco file to export");
			FileFilter filter = new FileNameExtensionFilter("*.lst", "lst");
			fchooser.setFileFilter(filter);
			int dialog = fchooser.showOpenDialog(this);
			if (dialog == JFileChooser.APPROVE_OPTION) {
				filechoose = fchooser.getSelectedFile();
				BufferedWriter bw;
				try {
					bw = new BufferedWriter(new FileWriter(filechoose));
					bw.write(resultText.getText());
					bw.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		} else if (o == step) {
			ucoTable.setRowSelectionAllowed(true);
			ucoTable.changeSelection(ass.getPC() - 1, 1, false, false);
			ass.run(vector, symbol);
			if (ass.getFlag() == -1)
				resultText.append(Integer.toString(ass.getAns()) + " ");
			if (ass.getFlag() == -2)
				resultText.append("\n");
			pointerdtm.setRowCount(0);
			memorydtm.setRowCount(0);
			cpudtm.setRowCount(0);
			pointerdtm.addRow(new Object[] { "PC     :    " + ass.getPC() });
			pointerdtm.addRow(new Object[] { "BP     :    " + ass.getBP() });
			pointerdtm.addRow(new Object[] { "SP     :    " + ass.getSP() });
			Memory = ass.getMemory();
			stack = ass.getStack();
			for (int i = 999; i >= ass.getSP(); i--) {
				memorydtm.addRow(new Object[] { i + " ¹øÁö  :  " });
			}
			for (int i = ass.getSP() - 1; i >= 0; i--) {
				memorydtm.addRow(new Object[] { i + " ¹øÁö  :  " + Memory[i] });
			}
			memorydtm.setValueAt("<-SP", 999 - ass.getSP(), 1);
			memorydtm.setValueAt("<-BP", 999 - ass.getBP(), 1);
			if (999 - ass.getSP() == 999 - ass.getBP()) {
				memorydtm.setValueAt("<-SP,BP", 999 - ass.getSP(), 1);
			}
			int num = 30;
			for (int i = 0; i < num; i++) {
				cpudtm.addRow(new Object[] { " " });
			}

			int size = stack.size();
			int top[] = new int[size];
			for (int i = 0; i < size; i++) {
				top[i] = stack.pop();
			}
			for (int i = size - 1; i >= 0; i--) {
				stack.push(top[i]);
			}

			for (int i = 0; i < size; i++) {
				cpudtm.removeRow(--num);
				cpudtm.addRow(new Object[] { top[i] });
			}

			setVisible(true);
			memoryScroll.getVerticalScrollBar().setValue(memoryScroll.getVerticalScrollBar().getMaximum());
			cpuScroll.getVerticalScrollBar().setValue(cpuScroll.getVerticalScrollBar().getMaximum());
			if (ass.getPC() == vector.size() + 1) {
				JOptionPane.showMessageDialog(this, "½ÇÇà°úÁ¤À» ¸ðµÎ ³¡¸¶ÃÆ½À´Ï´Ù.", "end", JOptionPane.INFORMATION_MESSAGE);
				step.setEnabled(false);
				run.setEnabled(false);
				stats();

			}
		} else if (o == run) {
			ucoTable.setRowSelectionAllowed(false);
			pointerdtm.setRowCount(0);
			memorydtm.setRowCount(0);
			cpudtm.setRowCount(0);
			while (ass.getPC() != vector.size() + 1) {
				ass.run(vector, symbol);
				if (ass.getFlag() == -1)
					resultText.append(Integer.toString(ass.getAns()) + " ");
				if (ass.getFlag() == -2)
					resultText.append("\n");
			}
			pointerdtm.addRow(new Object[] { "PC     :    " + ass.getPC() });
			pointerdtm.addRow(new Object[] { "BP     :    " + ass.getBP() });
			pointerdtm.addRow(new Object[] { "SP     :    " + ass.getSP() });
			Memory = ass.getMemory();
			for (int i = 999; i >= ass.getSP(); i--) {
				memorydtm.addRow(new Object[] { i + " ¹øÁö  :  " });
			}
			for (int i = ass.getSP() - 1; i >= 0; i--) {
				memorydtm.addRow(new Object[] { i + " ¹øÁö  :  " + Memory[i] });
			}
			memorydtm.setValueAt("<-SP", 999 - ass.getSP(), 1);
			memorydtm.setValueAt("<-BP", 999 - ass.getBP(), 1);
			if (ass.getSP() == ass.getBP()) {
				memorydtm.setValueAt("<-SP,BP", 999 - ass.getSP(), 1);
			}
			setVisible(true);
			memoryScroll.getVerticalScrollBar().setValue(memoryScroll.getVerticalScrollBar().getMaximum());

			JOptionPane.showMessageDialog(this, "½ÇÇà°úÁ¤À» ¸ðµÎ ³¡¸¶ÃÆ½À´Ï´Ù.", "end", JOptionPane.INFORMATION_MESSAGE);
			step.setEnabled(false);
			run.setEnabled(false);
			stats();

		} else if (o == exit) {
			System.exit(0);
		}
	}
}