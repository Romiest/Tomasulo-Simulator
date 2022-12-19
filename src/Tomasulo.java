import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Tomasulo {

	static Queue<Instruction> instructionQueue = new LinkedList<Instruction>();
	static PriorityQueue<Instruction> finished = new PriorityQueue<Instruction>();
	static Queue<Instruction> turnInMemory = new LinkedList<Instruction>();
	static HashMap<String, String> regFile = new HashMap<>();
	static double[] dataMemory = new double[1024];

	static int finishedInstructions = 0;
	static int clock = 1;
	static boolean memOccupied = false;

// latencies
	static int addLatency = 2;
	static int subLatency = 2;
	static int mulLatency = 10;
	static int divLatency = 40;
	static int loadLatency = 2;
	static int storeLatency = 2;

// stations
	static Instruction[] Astations = new Instruction[3];
	static Instruction[] Mstations = new Instruction[2];
	static Instruction[] Lstations = new Instruction[3];
	static Instruction[] Sstations = new Instruction[3];

	public static void readFile(String filename) {

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

					if (memOccupied == false) {
						instruction.mem = true;
						memOccupied = true;
					}

					else {
						turnInMemory.add(instruction);
					}

					if (station == Lstations) {
						regFile.put(instruction.dest, "L" + (i + 1));

					}
					String st = (station == Lstations ? "L" : "S") + (i + 1);
					instruction.station = st;

					instruction.dest = regFile.get(instruction.dest);
				}
				station[i] = instruction;

				System.out.println("Instruction number " + instruction.id + " got issued in " + instruction.station);
				return true;

			}
		}
		String s = station == Mstations ? "M" : station == Astations ? "A" : station == Lstations ? "L" : "S";
		System.out.println(s + " station is full so instruction " + instruction.id + " did not get issued");
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
						I.time--;
						I.firstExec = false;
					}

					else {
						I.time--;
						if (I.time > -1)
							System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id + " has "
									+ I.time + " cycles left to finish execution");
					}
				} else {
					System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id
							+ " still waiting to get the correct values");
				}

				if (I.time == 0) {
					if (I.operation.equals("ADD"))
						I.execValue = ADD(I.operand1, I.operand2);

					else
						I.execValue = SUB(I.operand1, I.operand2);

					System.out.println("Instruction " + I.id + " finished execution");
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
						I.time--;
						I.firstExec = false;
					} else {
						I.time--;
						if (I.time > -1)
							System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id + " has "
									+ I.time + " cycles left to finish execution");
					}
				} else {
					System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id
							+ " still waiting to get the correct values");
				}

				if (I.time == 0) {

					if (I.operation.equals("MUL"))
						I.execValue = MUL(I.operand1, I.operand2);

					else
						I.execValue = DIV(I.operand1, I.operand2);

					System.out.println("Instruction " + I.id + " finished execution");
					finished.add(I);
				}

			}
		}
		for (int i = 0; i < Lstations.length; i++) {
			if (Lstations[i] != null) {
				Instruction I = Lstations[i];

				if (I.mem == true) {
					
					if(I.firstExec) {
						System.out.println("("+"station "+I.station+") "+"Instruction " + I.id + " started execution");
						I.time--;
						I.firstExec=false;
					}
					else {
					I.time--;
					if (I.time > -1)
						System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id + " ("
								+ I.operation + ")" + " has " + I.time + " cycles left to finish execution");
					}
				} else {
					System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id + " (" + I.operation
							+ ")" + " still waiting for the memory");
				}

				if (I.time == 0) {
					load(I);
					System.out.println("Instruction " + I.id + " (" + I.operation + ")" + " finished execution");
					finished.add(I);
					turnInMemory();
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

				else if (noDependence(I) && I.mem == true) {
					
					if(I.firstExec) {
						System.out.println("("+"station "+I.station+") "+"Instruction " + I.id + " started execution");
						I.time--;
						I.firstExec=false;
					}
					else {
					I.time--;
					if (I.time > -1)
						System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id + " ("
								+ I.operation + ")" + " has " + I.time + " cycles left to finish execution");
					}	
				}

				else {
					System.out.println("(" + "station " + I.station + ") " + "Instruction " + I.id + " (" + I.operation
							+ ")" + " still waiting to get the correct values or cant access memory yet");
				}

				if (I.time == 0) {
					System.out.println("Instruction " + I.id + " finished execution");
					finished.add(I);
					turnInMemory();
				}

				if (I.time <= -1 && I.finished == true) {
					System.out.println("Instruction " + I.id + " stored the value in memory");
					store(I);

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
		
//			if (I.operation.equals("L") || I.operation.equals("S")) {
//				if (!turnInMemory.isEmpty()) {
//					Instruction inst = turnInMemory.poll();
//					inst.mem = true;
//				} else {
//					memOccupied = false;
//				}
//			}
			I.finished = true;
			finishedInstructions++;
		}

	}

	public static void EmptyStations() {
		for (int i = 0; i < Astations.length; i++) {	
			if (Astations[i] != null) {
				
				Instruction I = Astations[i];
				if (I.finished ) {
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
				if (I.finished ) {
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

	public static void turnInMemory() {
		if (!turnInMemory.isEmpty()) {
			Instruction inst = turnInMemory.poll();
			inst.mem = true;
		} else {
			memOccupied = false;
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
			clock++;
			System.out.println("------------------------------------------");
		}
		System.out.println("REG FILE:-");
		for (int i = 0; i < 32; i++) {
			System.out.println("F" + i + ": " + regFile.get("F" + i));
		}
	}

	public static void main(String[] args) {
		readFile("Assembly.txt");
		regFile.put("F0", "36");
		regFile.put("F1", "2");
		regFile.put("F2", "3");
		// regFile.put("F3", "2");
		regFile.put("F4", "4");
		regFile.put("F5", "5");
		regFile.put("F6", "1");
		regFile.put("F8", "8");
		regFile.put("F9", "12");
		regFile.put("F7", "7");
		regFile.put("F10", "2");
		regFile.put("F11", "5");
		dataMemory[100] = 9;
		Tom();

		// System.out.println(instructionQueue.peek().time);
	}

}
