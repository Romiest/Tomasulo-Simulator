import java.awt.EventQueue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Tomasulo {

	static Queue<Instruction> instructionQueue = new LinkedList<Instruction>();
	static PriorityQueue<Instruction> finished = new PriorityQueue<Instruction>();
	static HashMap<String, String> regFile = new HashMap<>();
	static double[] dataMemory = new double[1024];
	static ArrayList<Instruction> allInstructions = new ArrayList<Instruction>();
	static ArrayList<Cycle> cycles = new ArrayList<Cycle>();
	static int index = 0;

	static int finishedInstructions = 0;
	static int clock = 1;
	static boolean memOccupied = false;

// latencies
	static int addLatency = 1;
	static int subLatency = 1;
	static int mulLatency = 1;
	static int divLatency = 1;
	static int loadLatency = 1;
	static int storeLatency = 1;



// stations
	static Instruction[] Astations = new Instruction[3];
	static Instruction[] Mstations = new Instruction[2];
	static Instruction[] Lstations = new Instruction[3];
	static Instruction[] Sstations = new Instruction[3];

	public static void readFile(String filename) {
		addLatency = GUI.addLat;
		subLatency = GUI.subLat;
		mulLatency = GUI.mulLat;
		divLatency = GUI.divLat;
		loadLatency = GUI.loadLat;
		storeLatency = GUI.storeLat;

		for (int i = 0; i < 32; i++) {
			regFile.put("F" + i, "0.0");
		}

		try {
			File f = new File(filename);
			Scanner myReader = new Scanner(f);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				parse(data);

			}

			myReader.close();

		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

	public static String ADD(String operand1, String operand2) {
		String res = "";
		double o1 = Double.parseDouble(operand1);
		double o2 = Double.parseDouble(operand2);
		double result = o1 + o2;

		res += result;

		return res;
	}

	public static String SUB(String operand1, String operand2) {
		String res = "";
		double o1 = Double.parseDouble(operand1);
		double o2 = Double.parseDouble(operand2);
		double result = o1 - o2;
		res += result;

		return res;
	}

	public static String MUL(String operand1, String operand2) {
		String res = "";
		double o1 = Double.parseDouble(operand1);
		double o2 = Double.parseDouble(operand2);
		double result = o1 * o2;
		res += result;

		return res;
	}

	public static String DIV(String operand1, String operand2) {
		String res = "";
		double o1 = Double.parseDouble(operand1);
		double o2 = Double.parseDouble(operand2);
		double result = o1 / o2;
		res += result;

		return res;
	}

	public static void store(Instruction I) {
		int i = Integer.parseInt(I.address);
		System.out.println("The value " + I.dest + " has been stored in memory address " + i);
		dataMemory[i] = Double.parseDouble((I.dest));
	}

	public static void load(Instruction I) {
		int i = Integer.parseInt(I.address);
		I.execValue = dataMemory[i] + "";
	}

	public static int getLatency(String op) {
		switch (op) {
		case "ADD":
			return addLatency;
		case "SUB":
			return subLatency;
		case "MUL":
			return mulLatency;
		case "DIV":
			return divLatency;
		case "L":
			return loadLatency;
		case "S":
			return storeLatency;
		}
		return -1;
	}

	public static void parse(String data) {
		// L.D F6,100
		// S.D F2,100
		// MUL.D F0,F2,F4

		String[] firstSplit = data.split(" ");
		String[] rightSide = firstSplit[1].split(",");
		Instruction I = new Instruction();

		if (rightSide.length == 3) { // ALU operation
			I.inst = data;
			I.operation = firstSplit[0].substring(0, 3);
			I.dest = rightSide[0];
			I.operand1 = rightSide[1];
			I.operand2 = rightSide[2];
			I.time = getLatency(I.operation);

		} else { // load or store
			I.operation = firstSplit[0].charAt(0) + "";
			I.time = getLatency(I.operation);
			I.inst = data;
			I.address = rightSide[1];
			I.dest = rightSide[0];

		}
		instructionQueue.add(I);
		allInstructions.add(I); // for GUI

	}

	public static boolean issue(Instruction instruction) {
		if (instruction.operation.equals("MUL") || instruction.operation.equals("DIV")) {
			return insert(instruction, Mstations);
		} else if (instruction.operation.equals("ADD") || instruction.operation.equals("SUB")) {
			return insert(instruction, Astations);
		} else if (instruction.operation.equals("L")) {
			return insert(instruction, Lstations);
		} else if (instruction.operation.equals("S")) {
			return insert(instruction, Sstations);
		}
		return false;
	}

	public static boolean insert(Instruction instruction, Instruction[] station) {

		for (int i = 0; i < station.length; i++) {
			if (station[i] == null) {
				if (station == Astations || station == Mstations) {// ALU

					// get the values wither its the normal from regFile or dependent on someThing
					// else
					instruction.operand1 = (regFile.get(instruction.operand1));
					instruction.operand2 = (regFile.get(instruction.operand2));

					// to know which station are we currently assigned to
					String st = (station == Astations ? "A" : "M") + (i + 1);

					// adjust the regFile to make the values of the specified register dependent on
					// the result of the station
					regFile.put(instruction.dest, st);
					instruction.station = st;

				} else { // LOAD OR STORE


					if (station == Lstations) {
						regFile.put(instruction.dest, "L" + (i + 1));

					}
					String st = (station == Lstations ? "L" : "S") + (i + 1);
					instruction.station = st;

					instruction.dest = regFile.get(instruction.dest);
				}
				station[i] = instruction;

				System.out.println("Instruction number " + instruction.id + " (" + instruction.operation
						+ ")  got issued in " + instruction.station);

				instruction.issuedIn = clock + ""; // GUI

				return true;

			}
		}
		String s = station == Mstations ? "MUL/DIV"
				: station == Astations ? "ADD/SUB" : station == Lstations ? "LOAD" : "STORE";
		System.out.println(s + " stations is full so instruction " + instruction.id + " did not get issued");
		return false;
	}

	public static boolean noDependence(Instruction I) {

		if (I.operation.equals("S")) {
			char op = (I.dest).charAt(0);
			if (op == 'A' || op == 'M' || op == 'L')
				return false;
		} else {

			char op1 = (I.operand1).charAt(0);
			char op2 = (I.operand2).charAt(0);
			if (op1 == 'A' || op1 == 'M' || op1 == 'L')
				return false;
			if (op2 == 'A' || op2 == 'M' || op2 == 'L')
				return false;
		}

		return true;
	}

	public static void execute() {
		for (int i = 0; i < Astations.length; i++) {
			if (Astations[i] != null) {
				Instruction I = Astations[i];

				if (I.justGotValues) {
					System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id
							+ " will start executing next cycle");

					I.justGotValues = false;
				}

				else if (noDependence(I)) {

					if (I.firstExec) {
						System.out.println(
								"(" + "station " + I.station + ") " + "Instruction " + I.id + " started execution");
						I.execIn = clock + "..";
						I.time--;
						I.firstExec = false;
					}

					else {
						if (I.time > 0)
							I.time--;
						if (I.time > -1)
							System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id + " has "
									+ I.time + " cycles left to finish execution");
					}
				} else {
					System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id
							+ " still waiting to get the correct values");
				}

				if (I.time == 0 && !I.finishedExec) {
					I.finishedExec = true;

					if (I.operation.equals("ADD"))
						I.execValue = ADD(I.operand1, I.operand2);

					else
						I.execValue = SUB(I.operand1, I.operand2);

					System.out.println("Instruction " + I.id + " finished execution");
					I.execIn += clock + "";
					finished.add(I);
				}

			}
		}
		for (int i = 0; i < Mstations.length; i++) {
			if (Mstations[i] != null) {
				Instruction I = Mstations[i];

				if (I.justGotValues) {
					System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id
							+ " will start executing next cycle");

					I.justGotValues = false;
				}

				else if (noDependence(I)) {

					if (I.firstExec) {
						System.out.println(
								"(" + "station " + I.station + ") " + "Instruction " + I.id + " started execution");
						I.execIn = clock + "..";
						I.time--;
						I.firstExec = false;
					} else {
						if (I.time > 0)
							I.time--;
						if (I.time > -1)
							System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id + " has "
									+ I.time + " cycles left to finish execution");
					}
				} else {
					System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id
							+ " still waiting to get the correct values");
				}

				if (I.time == 0 && !I.finishedExec) {
					I.finishedExec = true;

					if (I.operation.equals("MUL"))
						I.execValue = MUL(I.operand1, I.operand2);

					else
						I.execValue = DIV(I.operand1, I.operand2);

					System.out.println("Instruction " + I.id + " finished execution");
					I.execIn += clock;

					finished.add(I);
				}

			}
		}
		for (int i = 0; i < Lstations.length; i++) {
			if (Lstations[i] != null) {
				Instruction I = Lstations[i];

				if (I.firstExec) {
					System.out.println(
							"(" + "station " + I.station + ") " + "Instruction " + I.id + " started execution");
					I.execIn = clock + "..";
					I.time--;
					I.firstExec = false;
				} else {
					if (I.time > 0)
						I.time--;

					if (I.time > -1)
						System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id + " ("
								+ I.operation + ")" + " has " + I.time + " cycles left to finish execution");
				}

				if (I.time == 0 && !I.finishedExec) {
					I.finishedExec = true;
					load(I);
					System.out.println("Instruction " + I.id + " (" + I.operation + ")" + " finished execution");
					I.execIn += clock;
					finished.add(I);

				}

			}
		}
		for (int i = 0; i < Sstations.length; i++) {
			if (Sstations[i] != null) {
				Instruction I = Sstations[i];

				if (I.justGotValues) {
					System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id
							+ " will start executing next cycle");

					I.justGotValues = false;
				}

				else if (noDependence(I)) {

					if (I.firstExec) {
						System.out.println(
								"(" + "station " + I.station + ") " + "Instruction " + I.id + " started execution");
						I.execIn = clock + "..";
						I.time--;
						I.firstExec = false;
					} else {
						if (I.time > 0)
							I.time--;
						if (I.time > -1)
							System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id + " ("
									+ I.operation + ")" + " has " + I.time + " cycles left to finish execution");
					}
				}

				else {
					System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id + " (" + I.operation
							+ ")" + " still waiting to get the correct values");
				}

				if (I.time == 0 && !I.finishedExec) {
					I.finishedExec = true;
					System.out.println("Instruction " + I.id + " finished execution");
					I.execIn += clock;
					System.out.println("Instruction " + I.id + " stored the value in memory");
					store(I);
					finished.add(I);

				}

			}
		}

	}

	public static void wb() {

		if (!finished.isEmpty()) {
			Instruction I = finished.poll();
			if (!I.operation.equals("S")) {
				System.out.println("instruction number " + I.id + " (" + I.operation + ")" + " writes its value on CDB "
						+ I.execValue);
				String station = I.station;

				// loop on regFile to give correct values
				String f = "F";
				for (int i = 0; i < 32; i++) {
					String reg = (f + i + "");
					if (regFile.get(reg).equals(station)) {

						System.out.println("register " + reg + " value has been replaced from station " + station
								+ " to " + I.execValue);
						regFile.put(reg, I.execValue);
					}
				}

				// loop on A stations
				for (int i = 0; i < Astations.length; i++) {
					if (Astations[i] != null) {
						Instruction inst = Astations[i];
						if (inst.operand1.equals(station)) {
							inst.operand1 = I.execValue;
							System.out.println("instruction " + inst.id + "(" + inst.operation + ") "
									+ " operand1 value has been replaced from station " + station + " to "
									+ I.execValue);
							if (noDependence(inst)) {
								inst.justGotValues = true;
							}
						}

						if (inst.operand2.equals(station)) {
							inst.operand2 = I.execValue;
							System.out.println("instruction " + inst.id + "(" + inst.operation + ") "
									+ " operand2 value has been replaced from station " + station + " to "
									+ I.execValue);

							if (noDependence(inst)) {
								inst.justGotValues = true;
							}
						}

					}
				}

				// loop on M stations
				for (int i = 0; i < Mstations.length; i++) {
					if (Mstations[i] != null) {
						Instruction inst = Mstations[i];
						if (inst.operand1.equals(station)) {
							inst.operand1 = I.execValue;
							System.out.println("instruction " + inst.id + "(" + inst.operation + ") "
									+ " operand value has been replaced from station " + station + " to "
									+ I.execValue);
							if (noDependence(inst)) {
								inst.justGotValues = true;
							}
						}
						if (inst.operand2.equals(station)) {
							inst.operand2 = I.execValue;
							System.out.println("instruction " + inst.id + "(" + inst.operation + ") "
									+ " operand value has been replaced from station " + station + " to "
									+ I.execValue);

							if (noDependence(inst)) {
								inst.justGotValues = true;
							}
						}
					}
				}
				// loop on Stores
				for (int i = 0; i < Sstations.length; i++) {
					if (Sstations[i] != null) {
						Instruction inst = Sstations[i];
						if (inst.dest.equals(station)) {
							System.out.println("instruction " + inst.id + " (" + inst.operation + ") "
									+ " operand value has been replaced from station " + station + " to "
									+ I.execValue);
							inst.dest = I.execValue;
							if (noDependence(inst)) {
								inst.justGotValues = true;
							}
						}

					}
				}

			}
			I.wbIn = clock + "";
			I.finished = true;
			finishedInstructions++;
		}

	}

	public static void EmptyStations() {
		for (int i = 0; i < Astations.length; i++) {
			if (Astations[i] != null) {

				Instruction I = Astations[i];
				if (I.finished) {
					System.out.println(
							"Instruction " + I.id + " (" + I.operation + ")" + " left the station " + I.station);
					Astations[i] = null;
				}
			}
		}
		for (int i = 0; i < Mstations.length; i++) {
			if (Mstations[i] != null) {
				Instruction I = Mstations[i];
				if (I.finished) {
					System.out.println(
							"Instruction " + I.id + " (" + I.operation + ")" + " left the station " + I.station);
					Mstations[i] = null;
				}
			}
		}
		for (int i = 0; i < Lstations.length; i++) {
			if (Lstations[i] != null) {
				Instruction I = Lstations[i];
				if (I.finished) {
					System.out.println(
							"Instruction " + I.id + " (" + I.operation + ")" + " left the station " + I.station);
					Lstations[i] = null;
				}
			}
		}

		for (int i = 0; i < Sstations.length; i++) {
			if (Sstations[i] != null) {
				Instruction I = Sstations[i];
				if (I.finished) {
					System.out.println(
							"Instruction " + I.id + " (" + I.operation + ")" + " left the station " + I.station);
					Sstations[i] = null;
				}
			}
		}
	}

	public static void Tom() {
		int size = instructionQueue.size();

		while (finishedInstructions < size) {
			System.out.println("CLK cycle : " + clock);

			wb();
			execute();

			if (!instructionQueue.isEmpty()) {

				Instruction inst = instructionQueue.peek();

				if (issue(inst)) {
					instructionQueue.poll();

				}

			}
			EmptyStations();

			System.out.println("Instruction Queue: " + instructionQueue);
			populateCycles();
			clock++;
			System.out.println("------------------------------------------");
		}
		System.out.println("REG FILE:-");
		for (int i = 0; i < 32; i++) {
			System.out.println("F" + i + ": " + regFile.get("F" + i));
		}
	}

	public static void populateCycles() {
		Cycle c1 = new Cycle();

		// populate the instruction table
		for (int i = 0; i < allInstructions.size(); i++) {
			String curr = "";
			Instruction I = allInstructions.get(i);

			curr += I.inst + "&" + I.issuedIn + " " + I.execIn + " " + I.wbIn;
			c1.InstructionTableState.add(curr);
		}

		// populate the register file
		for (int i = 0; i < 32; i++) {
			String curr = "";
			curr += "F" + (i) + " " + regFile.get("F" + i);
			c1.regFileState.add(curr);
		}

		// populate cycle stations
		for (int i = 0; i < Astations.length; i++) {
			String curr = "";
			if (Astations[i] != null) {
				Instruction I = Astations[i];
				curr += I.station + " " + I.time + " " + I.operation + " " + I.operand1 + " " + I.operand2;

			}
			c1.AstationsState.add(curr);

		}

		for (int i = 0; i < Mstations.length; i++) {
			String curr = "";
			if (Mstations[i] != null) {
				Instruction I = Mstations[i];
				curr += I.station + " " + I.time + " " + I.operation + " " + I.operand1 + " " + I.operand2;

			}
			c1.MstationsState.add(curr);

		}
		for (int i = 0; i < Lstations.length; i++) {
			String curr = "";
			if (Lstations[i] != null) {
				Instruction I = Lstations[i];
				curr += I.station + " " + I.time + " " + I.address;

			}
			c1.LstationsState.add(curr);

		}
		for (int i = 0; i < Sstations.length; i++) {
			String curr = "";
			if (Sstations[i] != null) {
				Instruction I = Sstations[i];
				curr += I.station + " " + I.time + " " + I.address + " " + I.dest;

			}
			c1.SstationsState.add(curr);

		}

		cycles.add(c1);

	}

	public static void main(String[] args) {

		GUI g = new GUI(cycles);
		g.setVisible(true);

	}

}
