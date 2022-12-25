
public class Instruction implements Comparable<Instruction>{
static int count=1;
int id;
int time;
String inst="";
String operation="";
String dest;
String operand1="";
String operand2="";
String address="";
String station;
String execValue="";

// for GUI
String issuedIn="-";
String execIn="-";
String wbIn="-";
//----------

boolean mem;
boolean finished=false;
boolean justGotValues=false;
boolean firstExec=true;
// for GUI
boolean issued=false;
boolean finishedExec=false;
//----------
public Instruction() {
	this.id=count;
	count++;
	mem=false;
}

public String toString() {
	return this.inst;
}

@Override
public int compareTo(Instruction o) {
	
	return this.id-o.id;
}

	
}
