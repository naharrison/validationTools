package lundTools;

import java.util.ArrayList;
import java.util.List;

import org.jlab.clas.fastmc.Clas12FastMC;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.PhysicsEvent;
import org.jlab.detector.base.DetectorType;
import org.jlab.detector.base.GeometryFactory;
import org.jlab.geom.base.Detector;
import org.jlab.physics.io.LundReader;

public class LundSkimmer extends LundReader {
	// ------------------- Fields -------------------------------- //
	// ----------------------------------------------------------- //

   	Clas12FastMC fastMC;
   	double Tscale, Sscale;
   	List<Integer> custCharge = new ArrayList<>();
   	List<Double> custPMin = new ArrayList<>();
   	List<Double> custPMax = new ArrayList<>();
   	List<Double> custThetaMin = new ArrayList<>();
   	List<Double> custThetaMax = new ArrayList<>();
   	List<Double> custPhiMin = new ArrayList<>();
   	List<Double> custPhiMax = new ArrayList<>();

	// ------------------ Constructors --------------------------- //
	// ----------------------------------------------------------- //

   	public LundSkimmer(double T, double S) {
   		super();
   		this.Tscale = T;
   		this.Sscale = S;
    	fastMC = new Clas12FastMC(T, S);
    	fastMC.setDebugMode(1);
   	}

	// --------------------- Methods ----------------------------- //
	// ----------------------------------------------------------- //
	
   	// needs fix... .next() permanently advances the file... need something like .previous() to go back.
 	public void dumpOriginalFile(int start, int end) {
 		for(int k = 1; k < start; k++) this.next();
 		
 		int count = start;
 		while(this.next() && count <= end)
 		{
 			PhysicsEvent pe = this.getEvent();
 			System.out.println("Event number " + count + ":");
 			System.out.println(pe.toLundString());
 			count++;
 		}
 	}
 	
 	public void addFMCDetector(String detName, Detector det) {
    	this.fastMC.addDetector(detName, det);
 	}

 	public void addFMCFilter(int charge, String[] detNames, int[] Nlayers) {
    	this.fastMC.addFilter(charge, detNames, Nlayers);
 	}
 	
 	public void addCustomFilter(int charge, double pMin, double pMax, double thetaMin, double thetaMax, double phiMin, double phiMax) {
 		this.custCharge.add(charge);
 		this.custPMin.add(pMin);
 		this.custPMax.add(pMax);
 		this.custThetaMin.add(thetaMin);
 		this.custThetaMax.add(thetaMax);
 		this.custPhiMin.add(phiMin);
 		this.custPhiMax.add(phiMax);
 	}
 	
   	// needs fix... .next() permanently advances the file... need something like .previous() to go back.
 	public void dumpSkimmedFile(int start, int end) {
 		for(int k = 1; k < start; k++) this.next();
 		
 		int count = start;
 		while(this.next() && count <= end)
 		{
 			PhysicsEvent pe = this.getEvent();
 			PhysicsEvent skimmedEvent = new PhysicsEvent();

 			for(int k = 0; k < pe.count(); k++)
 			{
 				if(this.checkParticle(pe.getParticle(k))) skimmedEvent.addParticle(pe.getParticle(k));
 			}
 			
 			System.out.print(skimmedEvent.toLundString());
 			count++;
 		}
 	}
 	
 	public boolean checkParticle(Particle part) {
 		for(int k = 0; k < custCharge.size(); k++)
 		{
 			if(part.charge() == custCharge.get(k) && part.p() > custPMin.get(k) && part.p() < custPMax.get(k) && Math.toDegrees(part.theta()) > custThetaMin.get(k) && Math.toDegrees(part.theta()) < custThetaMax.get(k) && Math.toDegrees(part.phi()) > custPhiMin.get(k) && Math.toDegrees(part.phi()) < custPhiMax.get(k))
 			{
 				return true;
 			}
 		}
 		if(this.fastMC.checkParticle(part)) return true;
 		
 		return false;
 	}
 	
	// ------------------ Main Method ---------------------------- //
	// ----------------------------------------------------------- //
	
 	public static void main(String[] args) {
 		LundSkimmer ls = new LundSkimmer(-1.0, 1.0);
 		ls.addFile("/Users/harrison/Documents/workspace/validationTools/src/lundTools/testfile.dat");
 		ls.open();
 		
    	Detector detDC = GeometryFactory.getDetector(DetectorType.DC);
    	Detector detEC = GeometryFactory.getDetector(DetectorType.EC);
    	ls.addFMCDetector("DC", detDC);
    	ls.addFMCDetector("EC", detEC);
    	String[] dneg1 = {"DC"};
    	int[] hneg1 = {36};
    	String[] dpos1 = {"DC"};
    	int[] hpos1 = {36};
    	String[] dneutral1 = {"EC"};
    	int[] hneutral1 = {9};
    	ls.addFMCFilter(-1, dneg1, hneg1);
    	ls.addFMCFilter(1, dpos1, hpos1);
    	ls.addFMCFilter(0, dneutral1, hneutral1);
    	
    	// forward tagger acceptance, theta = 2.5 - 5.5 degrees for all charge, p, and phi:
    	ls.addCustomFilter(-1, 0.0, 10000.0, 2.5, 5.5, -1000.0, 1000.0);
    	ls.addCustomFilter(0, 0.0, 10000.0, 2.5, 5.5, -1000.0, 1000.0);
    	ls.addCustomFilter(1, 0.0, 10000.0, 2.5, 5.5, -1000.0, 1000.0);
    	
    	// approx. central detector acceptance:
    	ls.addCustomFilter(-1, 0.7, 1.2, 55.0, 110.0, -1000.0, 1000.0);
    	ls.addCustomFilter(1, 0.7, 1.2, 55.0, 110.0, -1000.0, 1000.0);
    	
    	ls.dumpSkimmedFile(2, 5);
 	}
}