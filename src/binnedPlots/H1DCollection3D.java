package binnedPlots;

import java.util.ArrayList;
import java.util.List;

import org.root.data.StatNumber;
import org.root.histogram.H1D;
import org.root.pad.TGCanvas;

// this is a 3D collection of 1D histograms (e.g. p resolution binned in p, theta, and phi)

public class H1DCollection3D {

	// ------------------- Fields -------------------------------- //
	// ----------------------------------------------------------- //
	
	private String name;
	private int NhistBins;
	private double histMin;
	private double histMax;
	private int Nv1Bins; // v1 = variable 1
	private double v1Min;
	private double v1Max;
	private int Nv2Bins; // v2 = variable 2
	private double v2Min;
	private double v2Max;
	private int Nv3Bins; // v3 = variable 3
	private double v3Min;
	private double v3Max;
    private List<List<List<H1D>>> hist = new ArrayList<>();
    private List<TGCanvas> can = null;
	
	// ------------------ Constructors --------------------------- //
	// ----------------------------------------------------------- //

	public H1DCollection3D(String NAME, int nhistbins, double histmin, double histmax, int nv1bins, double v1min, double v1max, int nv2bins, double v2min, double v2max, int nv3bins, double v3min, double v3max) {
		name = NAME;
		NhistBins = nhistbins;
		histMin = histmin;
		histMax = histmax;
		Nv1Bins = nv1bins;
		v1Min = v1min;
		v1Max = v1max;
		Nv2Bins = nv2bins;
		v2Min = v2min;
		v2Max = v2max;
		Nv3Bins = nv3bins;
		v3Min = v3min;
		v3Max = v3max;
		
		for(int j = 0; j < Nv1Bins; j++)
		{
			hist.add(new ArrayList<>());
			for(int k = 0; k < Nv2Bins; k++)
			{
				hist.get(j).add(new ArrayList<>());
				for(int m = 0; m < Nv3Bins; m++)
				{
					hist.get(j).get(k).add(new H1D(String.format("%s_%d_%d_%d", name, j, k, m), String.format("%s_%d_%d_%d", name, j, k, m), NhistBins, histMin, histMax));
				}
			}
		}
	}

	// --------------------- Methods ----------------------------- //
	// ----------------------------------------------------------- //
	
	public String getName(){
		return name;
	}
	
	public int getNhistBins(){
		return NhistBins;
	}
	
	public double getHistMin(){
		return histMin;
	}
	
	public double getHistMax(){
		return histMax;
	}
	
	public int getNv1Bins(){
		return Nv1Bins;
	}
	
	public double getV1Min(){
		return v1Min;
	}
	
	public double getV1Max(){
		return v1Max;
	}
	
	public int getNv2Bins(){
		return Nv2Bins;
	}
	
	public double getV2Min(){
		return v2Min;
	}
	
	public double getV2Max(){
		return v2Max;
	}
	
	public int getNv3Bins(){
		return Nv3Bins;
	}
	
	public double getV3Min(){
		return v3Min;
	}
	
	public double getV3Max(){
		return v3Max;
	}
	
	public H1D getHistogram(int index1, int index2, int index3){
		H1D result = null;

		if(index1 < 0 || index1 >= Nv1Bins || index2 < 0 || index2 >= Nv2Bins || index3 < 0 || index3 >= Nv3Bins)
		{
			System.out.println("getHistogram error - index out of range");
			return result;
		}
		
		result = hist.get(index1).get(index2).get(index3);
		return result;
	}

	// calculate the bin numbers for variable 1 and 2 and fill the corresponding histogram with "value"
	public void addPoint(double value, double v1Value, double v2Value, double v3Value){
		int v1Bin = (int) Math.floor((Nv1Bins*(v1Value - v1Min))/(v1Max - v1Min)); // floor() rounds to -inf instead of 0
		int v2Bin = (int) Math.floor((Nv2Bins*(v2Value - v2Min))/(v2Max - v2Min)); // floor() rounds to -inf instead of 0
		int v3Bin = (int) Math.floor((Nv3Bins*(v3Value - v3Min))/(v3Max - v3Min)); // floor() rounds to -inf instead of 0
		
		if(v1Bin >=0 && v1Bin < Nv1Bins && v2Bin >=0 && v2Bin < Nv2Bins && v3Bin >= 0 && v3Bin < Nv3Bins) hist.get(v1Bin).get(v2Bin).get(v3Bin).fill(value);
	}

	// draw all the plots on a canvas
	public void draw(){

		for(int m = 0; m < Nv3Bins; m++)
		{
			can.add(new TGCanvas(String.format("%s_can_%d", name, m), String.format("%s_can_%d", name, m), 1000, 750, Nv1Bins, Nv2Bins));
			for(int j = 0; j < Nv1Bins; j++)
			{
				for(int k = 0; k < Nv2Bins; k++)
				{
					can.get(m).cd(Nv1Bins*(Nv2Bins - k - 1) + j);
					can.get(m).draw(hist.get(j).get(k).get(m));
				}
			}

		}

	}

	// return a H1DCollection3D that is the ratios of the two parameters (must be same binning scheme)
	public static H1DCollection3D divide(H1DCollection3D num, H1DCollection3D den){

		if(num.getNhistBins() != den.getNhistBins() || num.getHistMin() != den.getHistMin() || num.getHistMax() != den.getHistMax() || num.getNv1Bins() != den.getNv1Bins() || num.getNv2Bins() != den.getNv2Bins() || num.getNv3Bins() != den.getNv3Bins())
		{
			System.out.println("divide error - inconsistent binning schemes");
			System.out.println(num.getNhistBins() + "=" + den.getNhistBins() + " " + num.getHistMin() + "=" + den.getHistMin() + " " + num.getHistMax() + "=" + den.getHistMax() + " " + num.getNv1Bins() + "=" + den.getNv1Bins() + " " + num.getNv2Bins() + "=" + den.getNv2Bins() + " " + num.getNv3Bins() + "=" + den.getNv3Bins());
			return null;
		}
		
		H1DCollection3D answer = new H1DCollection3D(num.getName()+"_DIV", num.getNhistBins(), num.getHistMin(), num.getHistMax(), num.getNv1Bins(), num.getV1Min(), num.getV1Max(), num.getNv2Bins(), num.getV2Min(), num.getV2Max(), num.getNv3Bins(), num.getV3Min(), num.getV3Max());
		
		StatNumber result = new StatNumber();
		StatNumber denom = new StatNumber();
		for(int i = 0; i < num.getNv1Bins(); i++)
		{
			for(int j = 0; j < num.getNv2Bins(); j++)
			{
				for(int m = 0; m < num.getNv3Bins(); m++)
				{
					for(int k = 0; k < num.getNhistBins(); k++)
					{
						result.set(num.getHistogram(i, j, m).getBinContent(k), num.getHistogram(i, j, m).getBinError(k));
						denom.set(den.getHistogram(i, j, m).getBinContent(k), den.getHistogram(i, j, m).getBinError(k));
						result.divide(denom);
						answer.getHistogram(i, j, m).setBinContent(k, result.number());
						answer.getHistogram(i, j, m).setBinError(k, result.error());
					}
				}
			}
		}

		return answer;
	}

	// ---------------- Main function ---------------------------- //
	// ----------------------------------------------------------- //

	public static void main(String[] args) {
		
	}

}
