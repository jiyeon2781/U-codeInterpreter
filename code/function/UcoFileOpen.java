package function;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class UcoFileOpen {
	Vector<UcoInfo> vector = new Vector<UcoInfo>();
	Vector<SymbolTable> symbol = new Vector<SymbolTable>();

	UcoInfo uco;
	SymbolTable inputsymbol;

	File file;
	FileReader fr;
	BufferedReader br;
	String[] strings, remove;
	String nu = " ";
	static int startPC = 1;

	public void fileOpen(File file) {
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line = "";
			strings = null;
			remove = null;
			int address = 1;
			while ((line = br.readLine()) != null) {
				String label = " ";
				if (line.charAt(0) != ' ') {
					label = line.substring(0, 10).trim();
					inputsymbol = new SymbolTable(label, address);
					symbol.add(inputsymbol);
				}
				remove = line.substring(11).split("%");
				strings = remove[0].split("\\s+");
				if (strings.length == 1) {
					uco = new UcoInfo(label, strings[0], nu, nu, nu);
				} else if (strings.length == 2) {
					uco = new UcoInfo(label, strings[0], strings[1], nu, nu);
					if (strings[0].equals("bgn")) {
						startPC = address;
					}
				} else if (strings.length == 3) {
					uco = new UcoInfo(label, strings[0], strings[1], strings[2], nu);
				} else if (strings.length == 4) {
					uco = new UcoInfo(label, strings[0], strings[1], strings[2], strings[3]);
				}
				vector.add(uco);
				address++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getPC() {
		return startPC;
	}

	public Vector<UcoInfo> getUcoInfo() {
		return this.vector;
	}

	public Vector<SymbolTable> getSymbol() {
		return this.symbol;
	}

}