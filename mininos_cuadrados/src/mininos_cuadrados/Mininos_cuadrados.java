/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mininos_cuadrados;
import java.awt.geom.Point2D;
import java.util.ArrayList;
/**
 *
 * @author alber
 */
public class Mininos_cuadrados {

	ArrayList <SegmentosLinea> SegmentosLinea = new ArrayList<SegmentosLinea>();

	Point2D[]  points;

	int      lengthPlusOne=0;  
	double   costSegment;  

	double  a[][];         
	double  b[][];          
	double  e_ij[][];       
	double  opt[];			 


		
	public Mininos_cuadrados(Point2D[] points,  double costOfSegment){
		this.points = points;
		this.costSegment = costOfSegment;

		e_ij = new double[points.length][points.length];
		a    = new double[points.length][points.length];
		b    = new double[points.length][points.length];

		lengthPlusOne = 1 + points.length;
		opt  = new double[ points.length ];
		calculaEijAB();
	}
	

	public void calculaEijAB(){

		
		double   denominador;

		double[]  sumX  = new double[ lengthPlusOne ];
		double[]  sumY  = new double[ lengthPlusOne ];
		double[]  sumX2 = new double[ lengthPlusOne ]; 
		double[]  sumY2 = new double[ lengthPlusOne ];
		double[]  sumXY = new double[ lengthPlusOne ];

		for (int i=0; i< points.length; i++){  
			
			sumX[i+1]  = sumX[i]  + points[i].getX();
			sumY[i+1]  = sumY[i]  + points[i].getY();
			sumX2[i+1] = sumX2[i] + points[i].getX() * points[i].getX(); 
			sumY2[i+1] = sumY2[i] + points[i].getY() * points[i].getY();
			sumXY[i+1] = sumXY[i] + points[i].getX() * points[i].getY();   
		}
		

		for (int i=0; i< points.length; i++){    
			for (int j = i+1; j < points.length; j++){                                
				denominador = (Math.pow(sumX[j+1]-sumX[i],2.0) - (j + 1 - i) * (sumX2[j+1] - sumX2[i]));
				if (denominador == 0){
					System.out.println("No single minimum exists e.g. the minimum is a line running along an infinitely long valley. ");
					a[i][j] = 0.0;
					b[i][j] = 0.0;					
				}
				else{
					a[i][j] = ((sumY[j+1] - sumY[i])*(sumX[j+1] - sumX[i]) 
							- (j + 1 - i) * (sumXY[j+1] - sumXY[i]))/ denominador;
					b[i][j] = ((sumX[j+1] - sumX[i])*(sumXY[j+1]- sumXY[i]) 
							-  (sumX2[j+1] - sumX2[i])*(sumY[j+1] - sumY[i]) )/ denominador;

					e_ij[i][j] = (sumY2[j+1]-sumY2[i]) 
							- 2*a[i][j]*         (sumXY[j+1]-sumXY[i])
							- 2*b[i][j]*          (sumY[j+1] -sumY[i])
							+   a[i][j]*a[i][j] * (sumX2[j+1]-sumX2[i])
							+ 2*a[i][j]*b[i][j] * (sumX[j+1]-sumX[i]) 
							+   b[i][j]*b[i][j] * (j+1 - i);
				}
			}
		}
	}  


	public void calculaIterativo( ){
	 
		opt[0]=costSegment;
		opt[1]=costSegment;
				
		for(int j = 2; j < points.length; j++){
			opt[j] = Double.POSITIVE_INFINITY;
			for(int i = 1; i < j + 1; i++){
				opt[j] = opt[j] > (opt[i-1] + e_ij[i][j] + costSegment)?
						opt[i-1] + e_ij[i][j] + costSegment: opt[j];
				
				// Check if point 0 is a solution
				if(opt[j] > costSegment + e_ij[0][j])
					opt[j] = costSegment + e_ij[0][j];
			}
		}
		
	}
	

	public double calculaRecursivo(int j){
		
		if(opt[j] == 0){
			
			if(j == 0)
				opt[0] = costSegment;
			else if(j == 1)
				opt[1] = costSegment;
			
			else{
				opt[j] = Double.POSITIVE_INFINITY;
				for(int i = 1; i <	j + 1; i++){
					opt[j] = opt[j] > calculaRecursivo(i-1) + e_ij[i][j] + costSegment?
							calculaRecursivo(i-1) + e_ij[i][j] + costSegment : opt[j];
					
					if(opt[j] > costSegment + e_ij[0][j])
						opt[j] = costSegment + e_ij[0][j];
				}
			}
		}
		return opt[j];

	}
 
	
	public void calculaSegmentos(int j){

		SegmentosLinea tempLineSegment = new SegmentosLinea(); 
		
		if(opt[j] == costSegment + e_ij[0][j]){
			
			tempLineSegment.i = 0;
			tempLineSegment.j = j;
			tempLineSegment.a = a[0][j];
			tempLineSegment.b = b[0][j];
			tempLineSegment.error = e_ij[0][j];			
			SegmentosLinea.add(tempLineSegment);
		}
		else{
			
			for(int i = 1; i < j + 1; i++){
				if(opt[j] == opt[i - 1] + e_ij[i][j] + costSegment){
					
					tempLineSegment.i = i;
					tempLineSegment.j = j;
					tempLineSegment.a = a[i][j];
					tempLineSegment.b = b[i][j];
					tempLineSegment.error = e_ij[i][j];					
					SegmentosLinea.add(tempLineSegment);					
					calculaSegmentos(i-1);
					break;
				}
			}
		}
	}

	public ArrayList<SegmentosLinea> resulveIterativo(){

		System.out.println("De manera iterativa...");
		calculaIterativo();
		calculaSegmentos( points.length - 1);  
		return(SegmentosLinea);
	}

	public ArrayList<SegmentosLinea> resuelveRecursivo(){

		System.out.println("\nDe manera recursiva...");
		calculaRecursivo( points.length - 1);
		calculaSegmentos( points.length - 1); 
		return(SegmentosLinea);
	}
}


class SegmentosLinea{
	int i,j;  
	double a,b,error;  

	public String toString(){
		return	 " (" + new Integer(i) + "," + new Integer(j) + ") " 
				+ "   linea descrita en  y = " + String.format("%.2f",a) + " x + "
				+ String.format("%.2f ", b) + ",  valor e= " + String.format("%.2f", error);
	}


}
