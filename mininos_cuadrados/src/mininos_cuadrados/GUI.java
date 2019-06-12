/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mininos_cuadrados;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
/**
 *
 * @author alber
 */


public class GUI extends JFrame{
	
	//  constants
	
	static int  N = 50;
	static int  WIDTH  = 650;
	static int  HEIGHT = 600;
	static int  SLIDERMINVALUE = 1;
	static int  PIXELS_PER_XYUNIT = (int) (WIDTH / N);
	static int  SHIFT_X = 10;
	static int  SHIFT_Y = 10;   
	
	static JSlider slider;
	static JLabel labelMaxCost,labelCost,view;
	static ButtonGroup buttonGroup;
	static JRadioButton iterativeRadio = new JRadioButton("iterativo");
	static JRadioButton recursiveRadio = new JRadioButton("recursivo");
	static JTextField  maxCostOfSegmentTextField;
	static JPanel panel1, panel2, panel3;
	static Mininos_cuadrados sls;
	static BufferedImage surface;	
	static int  sliderMaxValue = 100;
	
	double costOfSegment = 10.0;   
	int 	maxCostOfSegment= 10; 
	int 	slidervalue;
	public enum DynamicProgStyle {ITERATIVE, RECURSIVE};
	static DynamicProgStyle iterativeOrRecursive = DynamicProgStyle.RECURSIVE;
	static Graphics g;

	static Point2D[] 				coords;      
	static ArrayList<SegmentosLinea> SegmentosLinea;   

		
	public  GUI(){	
		
		iterativeRadio = new JRadioButton("iterative",false);
		recursiveRadio = new JRadioButton("recursive",true);
		
				
		buttonGroup = new ButtonGroup();
		buttonGroup.add(iterativeRadio);
		buttonGroup.add(recursiveRadio);
		
		iterativeRadio.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				iterativeOrRecursive = DynamicProgStyle.ITERATIVE;
				sls = new Mininos_cuadrados(coords, costOfSegment);
				SegmentosLinea = sls.resulveIterativo();
				redraw();
			}
		});
		
		recursiveRadio.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				iterativeOrRecursive = DynamicProgStyle.RECURSIVE;
				sls = new Mininos_cuadrados(coords, costOfSegment );
				SegmentosLinea = sls.resuelveRecursivo();
				redraw();
			}
		});

		

		JLabel costMaxLabel = new JLabel("       Entrada: ");
		maxCostOfSegmentTextField  = new JTextField(new Integer(maxCostOfSegment).toString(),5); 
		sliderMaxValue = Integer.parseInt(maxCostOfSegmentTextField.getText());		
		maxCostOfSegmentTextField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				maxCostOfSegment = Integer.parseInt(maxCostOfSegmentTextField.getText());
			}});
		
		slider = new JSlider(SLIDERMINVALUE,sliderMaxValue, sliderMaxValue);     
		slider.addChangeListener(new SliderAction());  
		
		labelMaxCost  = new JLabel((new Double(maxCostOfSegment)).toString());
		labelCost = new JLabel("Segmentos = ");
				
		panel1 = new JPanel();
		panel1.add(iterativeRadio);
		panel1.add(recursiveRadio);
		add(panel1, BorderLayout.NORTH); 
		
				
		panel2 = new JPanel();
		panel2.add(slider);
		panel2.add(labelCost); 
		panel2.add(labelMaxCost);          
		panel2.add(costMaxLabel);
		panel2.add(maxCostOfSegmentTextField);
		add(panel2, BorderLayout.CENTER); 

				
		surface = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		g = surface.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0,0,WIDTH,HEIGHT);
		view = new JLabel(new ImageIcon(surface));

		panel3 = new JPanel();
		panel3.add(view);
		add(panel3, BorderLayout.SOUTH); 

		setSize(WIDTH + 100,HEIGHT + 100);    
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		
		redraw();
	}

	
	public class SliderAction implements ChangeListener{
		
		public void stateChanged(ChangeEvent ce){
			
			
			
			slidervalue = slider.getValue();
			costOfSegment = slidervalue*1.0 / sliderMaxValue * maxCostOfSegment;
			String str = Double.toString(costOfSegment);
			labelMaxCost.setText(str);
			
			sls = new Mininos_cuadrados(coords, costOfSegment);
			solve(iterativeOrRecursive);
			redraw();
			}
	}
	
	public static void solve(DynamicProgStyle whichStyle){
		if (whichStyle == DynamicProgStyle.RECURSIVE)
			SegmentosLinea = sls.resuelveRecursivo();
		else
			SegmentosLinea = sls.resulveIterativo();
		
		for (SegmentosLinea l : SegmentosLinea)
			System.out.println(l);
	}
	
		
	public static void redraw(){ 
		
		int ovalwidth = 8;
		int ovalradius = ovalwidth/2;
		g.setColor(new Color(220, 220, 220));  
		g.fillRect(0,0,WIDTH,HEIGHT);
		view.repaint();
		g.setColor(Color.BLACK);
				
		
		for(int i = 0;i<N;i++)		{
			g.fillOval( SHIFT_X + (int)( coords[i].getX()*PIXELS_PER_XYUNIT ) - ovalradius, 
					    (int)(HEIGHT - (coords[i].getY() *PIXELS_PER_XYUNIT)) - ovalradius  - SHIFT_Y, 
					    ovalwidth, ovalwidth ); 
		}

		
		g.drawLine(SHIFT_X, HEIGHT - SHIFT_Y, WIDTH,   HEIGHT - SHIFT_Y);
		for (int i=0; i < WIDTH; i += PIXELS_PER_XYUNIT)
			g.drawLine(SHIFT_X + i, HEIGHT - SHIFT_Y, SHIFT_X + i,   HEIGHT);
		
		g.drawLine(SHIFT_X, HEIGHT - SHIFT_Y, SHIFT_X,   SHIFT_Y);
		for (int j=0; j < HEIGHT; j += PIXELS_PER_XYUNIT)
			g.drawLine(0, HEIGHT - SHIFT_Y - j , SHIFT_X, HEIGHT - SHIFT_Y - j);
		
	
		double x1,y1,x2,y2;
		SegmentosLinea segment;
				
		g.setColor(Color.RED);
		for (int i = 0; i< SegmentosLinea.size(); i++){
			segment = SegmentosLinea.get(i);
			x1 = segment.i ;
			x2 = segment.j ;
			y1 = (segment.a  *x1  + segment.b );
			y2 = (segment.a  *x2  + segment.b );

			x1 = x1*PIXELS_PER_XYUNIT; 
			y1 = y1*PIXELS_PER_XYUNIT;
			x2 = x2*PIXELS_PER_XYUNIT;
			y2 = y2*PIXELS_PER_XYUNIT;

			g.drawLine(SHIFT_X + (int) x1, 
					   (int) (HEIGHT- y1)  - SHIFT_Y, 
					   SHIFT_X + (int) x2, 
					   (int) (HEIGHT-y2)   - SHIFT_Y);
		}	
		
	}

	public static void main(String s[]){ 

		int  costSegment = 10;
		
		Point2D[] points = new Point2D[N];    
		double error, a, b;
		int scaleError = 2;

		Random rand = new Random(); 
		
		
		for (int i = 0;  i < N/2; i++){
			a = 1;
			b = 10;
			points[i] = new Point2D.Float();
			error = rand.nextDouble()*2 - 1;   
			points[i].setLocation(i * 1.0, a*i + b + scaleError*error);  
		}
				
		for (int i = N/2-3;  i < 3*N/4; i++){
			a = 0;
			b = N/2;
			points[i] = new Point2D.Float();
			error = rand.nextDouble()*2 - 1;   
			points[i].setLocation(i * 1.0, a*i + b + scaleError*error);   
		}
						
		for (int i = 3*N/4;  i < N; i++){
			a = -1.2;
			b = 1.6*N;
			points[i] = new Point2D.Float();
			error = rand.nextDouble()*2 - 1;   
			points[i].setLocation(i * 1.0, a*i + b + scaleError*error);   
		}
				
		sls = new Mininos_cuadrados(points, costSegment );
		solve( iterativeOrRecursive );
		coords = points;
		
		new GUI();
	}

}
