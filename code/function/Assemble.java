package function;

import java.util.Stack;
import java.util.Vector;

import javax.swing.JOptionPane;

public class Assemble {
	String[] source = // 명령어 배열
			{ "nop", "bgn", "sym", "end", "proc", "ret", "ldp", "push", "call", "lod", "lda", "ldc", "str", "ldi",
					"sti", "not", "neg", "inc", "dec", "dup", "add", "sub", "mult", "div", "mod", "gt", "lt", "ge",
					"le", "eq", "ne", "and", "or", "swp", "ujp", "tjp", "fjp" };

	int SP = 0;
	int BP = -1;
	int PC;
	int pushCount = 0;
	int True = -1;
	int False = 0;

	int flag = 0;
	int ans;

	Vector<SymbolTable> symbol;
	Vector<UcoInfo> vector;

	Stack<Integer> stack = new Stack<Integer>(); // CPU스택
	int[] Memory = new int[1000]; // 메모리스택

	int[] sourceCount = new int[37]; // 명령어별 사용 횟수 세기
	int MemoryAccessCount = 0; // 메모리 접근 횟수

	void bgn(int offset) {
		SP += offset;
		BP += 1;
	}

	void end() {
		PC = vector.size() + 1;
	}

	void proc(int offset) {
		SP += offset;
	}

	void ret() {
		flag = PC;
		PC = Memory[BP + 1];
		BP = Memory[BP];

	}

	void ldp() {
		SP += 2;
	}

	void push() {
		Memory[SP++] = stack.pop();
		pushCount++;
	}

	void call(String label) {
		if (label.equals("read")) {
			String input;
			while (true) {
				int temp = 0;
				input = JOptionPane.showInputDialog(null, "정수을 입력해주세요", "Input", JOptionPane.OK_CANCEL_OPTION);
				for (int i = 0; i < input.length(); i++) {
					if (input.charAt(i) > 47 && input.charAt(i) < 58) // 정수
						temp++;
				}
				if (temp == input.length() && !input.equals(""))
					break;
				JOptionPane.showMessageDialog(null, "정수만 입력해주세요.", "error", JOptionPane.ERROR_MESSAGE);
			}
			int inputNum = Integer.parseInt(input);
			Memory[Memory[SP - 1]] = inputNum;
		} else if (label.equals("write")) {
			flag = -1;
			ans = Memory[SP - 1];
		} else if (label.equals("lf")) {
			flag = -2;
		} else {
			flag = PC;
			for (int i = 0; i < symbol.size(); i++) {
				if (symbol.get(i).Label.equals(label)) {
					Memory[SP - 2 - pushCount] = BP;
					Memory[SP - 1 - pushCount] = PC;
					BP = SP - 2 - pushCount;
					PC = symbol.get(i).Address;
					break;
				}
			}
		}
		pushCount = 0;
	}

	void lod(int block, int offset) {
		if (block == 1) {
			stack.push(Memory[offset]);
		} else if (block == 2) {
			stack.push(Memory[BP + 2 + offset]);
		}
	}

	void lda(int block, int offset) {
		if (block == 1) {
			stack.push(offset);
		} else if (block == 2) {
			stack.push(BP + 2 + offset);
		}
	}

	void str(int block, int offset) {
		if (block == 1) {
			Memory[offset] = stack.pop();
		} else if (block == 2) {
			Memory[BP + 2 + offset] = stack.pop();
		}
	}

	void ldi() {
		int top = Memory[stack.pop()];
		stack.push(top);
	}

	void sti() {
		int data = stack.pop();
		int address = stack.pop();
		Memory[address] = data;
	}

	void not() {
		int top = stack.pop();
		if (top == True)
			stack.push(False);
		else
			stack.push(True);

	}

	void neg() {
		int top = stack.pop();
		stack.push(-1 * top);
	}

	void inc() {
		int top = stack.pop();
		stack.push(++top);
	}

	void dec() {
		int top = stack.pop();
		stack.push(--top);
	}

	void dup() {
		int top = stack.pop();
		stack.push(top);
		stack.push(top);
	}

	void add() {
		int top1 = stack.pop();
		int top2 = stack.pop();
		stack.push(top2 + top1);
	}

	void sub() {
		int top1 = stack.pop();
		int top2 = stack.pop();
		stack.push(top2 - top1);
	}

	void mult() {
		int top1 = stack.pop();
		int top2 = stack.pop();
		stack.push(top2 * top1);
	}

	void div() {
		int top1 = stack.pop();
		int top2 = stack.pop();
		stack.push(top2 / top1);
	}

	void mod() {
		int top1 = stack.pop();
		int top2 = stack.pop();
		stack.push(top2 % top1);
	}

	void gt() {
		int top1 = stack.pop();
		int top2 = stack.pop();
		if (top2 > top1)
			stack.push(True);
		else
			stack.push(False);
	}

	void lt() {
		int top1 = stack.pop();
		int top2 = stack.pop();
		if (top2 < top1)
			stack.push(True);
		else
			stack.push(False);
	}

	void ge() {
		int top1 = stack.pop();
		int top2 = stack.pop();
		if (top2 >= top1)
			stack.push(True);
		else
			stack.push(False);
	}

	void le() {
		int top1 = stack.pop();
		int top2 = stack.pop();
		if (top2 <= top1)
			stack.push(True);
		else
			stack.push(False);
	}

	void eq() {
		int top1 = stack.pop();
		int top2 = stack.pop();
		if (top2 == top1)
			stack.push(True);
		else
			stack.push(False);
	}

	void ne() {
		int top1 = stack.pop();
		int top2 = stack.pop();
		if (top2 != top1)
			stack.push(True);
		else
			stack.push(False);
	}

	void and() {
		int top = stack.pop() & stack.pop();
		if (top == 1)
			stack.push(True);
		else
			stack.push(False);
	}

	void or() {
		int top = stack.pop() | stack.pop();
		if (top == 1)
			stack.push(True);
		else
			stack.push(False);
	}

	void swp() {
		int top1 = stack.pop();
		int top2 = stack.pop();
		stack.push(top1);
		stack.push(top2);
	}

	void ujp(String label) {
		for (int i = 0; i < symbol.size(); i++) {
			if (symbol.get(i).Label.equals(label)) {
				PC = symbol.get(i).Address;
			}
		}
	}

	public void setPC(int PC) {
		this.PC = PC;
	}

	public int getPC() {
		return PC;
	}

	public int getBP() {
		return BP;
	}

	public int getSP() {
		return SP;
	}

	public int[] getMemory() {
		return Memory;
	}

	public Stack<Integer> getStack() {
		return stack;
	}

	public int getFlag() {
		return flag;
	}

	public int getAns() {
		return ans;
	}

	public int[] getSourceCount() {
		return sourceCount;
	}

	public int getMemoryAccessCount() {
		return MemoryAccessCount;
	}

	public void run(Vector<UcoInfo> vector, Vector<SymbolTable> symbol) {
		this.vector = vector;
		this.symbol = symbol;
		flag = 0;
		int i = (PC++) - 1;

		String Source = vector.get(i).Source;
		int val1 = 0, val2 = 0;

		if (vector.get(i).val1 != " " && !Source.equals("call") && !Source.contentEquals("fjp")
				&& !Source.contentEquals("tjp") && !Source.contentEquals("ujp"))
			val1 = Integer.parseInt(vector.get(i).val1);
		if (vector.get(i).val2 != " ")
			val2 = Integer.parseInt(vector.get(i).val2);

		switch (Source) {
		case "nop":
			sourceCount[0]++;
			break;

		case "bgn":
			bgn(val1);
			sourceCount[1]++;
			break;

		case "sym":
			sourceCount[2]++;
			break;

		case "end":
			sourceCount[3]++;
			end();
			break;

		case "proc":
			proc(val1);
			sourceCount[4]++;
			break;

		case "ret":
			ret();
			sourceCount[5]++;
			break;

		case "ldp":
			ldp();
			sourceCount[6]++;
			break;

		case "push":
			push();
			sourceCount[7]++;
			break;

		case "call":
			call(vector.get(i).val1);
			sourceCount[8]++;
			break;

		case "lod":
			MemoryAccessCount++;
			lod(val1, val2);
			sourceCount[9]++;
			break;

		case "lda":
			MemoryAccessCount++;
			lda(val1, val2);
			sourceCount[10]++;
			break;

		case "ldc":
			MemoryAccessCount++;
			stack.push(val1);
			sourceCount[11]++;
			break;

		case "str":
			MemoryAccessCount++;
			str(val1, val2);
			sourceCount[12]++;
			break;

		case "ldi":
			MemoryAccessCount++;
			ldi();
			sourceCount[13]++;
			break;

		case "sti":
			MemoryAccessCount++;
			sti();
			sourceCount[14]++;
			break;

		case "not":
			not();
			sourceCount[15]++;
			break;

		case "neg":
			neg();
			sourceCount[16]++;
			break;

		case "inc":
			inc();
			sourceCount[17]++;
			break;

		case "dec":
			dec();
			sourceCount[18]++;
			break;

		case "dup":
			dup();
			sourceCount[19]++;
			break;

		case "add":
			add();
			sourceCount[20]++;
			break;

		case "sub":
			sub();
			sourceCount[21]++;
			break;

		case "mult":
			mult();
			sourceCount[22]++;
			break;

		case "div":
			div();
			sourceCount[23]++;
			break;

		case "mod":
			mod();
			sourceCount[24]++;
			break;

		case "gt":
			gt();
			sourceCount[25]++;
			break;

		case "lt":
			lt();
			sourceCount[26]++;
			break;

		case "ge":
			ge();
			sourceCount[27]++;
			break;

		case "le":
			le();
			sourceCount[28]++;
			break;

		case "eq":
			eq();
			sourceCount[29]++;
			break;

		case "ne":
			ne();
			sourceCount[30]++;
			break;

		case "and":
			and();
			sourceCount[31]++;
			break;

		case "or":
			or();
			sourceCount[32]++;
			break;

		case "swp":
			swp();
			sourceCount[33]++;
			break;

		case "ujp":
			ujp(vector.get(i).val1);
			sourceCount[34]++;
			break;

		case "tjp":
			if (stack.pop() == True)
				ujp(vector.get(i).val1);
			sourceCount[35]++;
			break;

		case "fjp":
			if (stack.pop() == False)
				ujp(vector.get(i).val1);
			sourceCount[36]++;
			break;
		}

	}

}