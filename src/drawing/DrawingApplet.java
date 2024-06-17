package drawing;

import javax.swing.*;

import geometry.Convex;
import geometry.EdgeData;
import geometry.KdTree;
import geometry.Util;
import geometry.VoronoiCell;
import geometry.Sector;

import java.awt.*;
import java.awt.event.*;

import processing.core.PApplet;
import processing.event.Event;
import processing.core.PVector;

import trapmap.Segment;
import trapmap.Trapezoid;
import trapmap.TrapMap;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.*;
import java.util.List;



public class DrawingApplet extends PApplet implements ActionListener {
	private static final long serialVersionUID = 1L;

	/* Modes */
	enum Mode {
		DRAW_CONVEX, INCONVEXTEST, UNIT_BALL, VORONOI_DEF, VORONOI_FIND, VORONOI_HILBERT
	};

	private final static Mode[] MODES = { Mode.DRAW_CONVEX, /* Mode.INCONVEXTEST, */ /* Mode.UNIT_BALL, */
			Mode.VORONOI_DEF, Mode.VORONOI_HILBERT /* Mode.VORONOI_FIND */ };
	private int currentMode = 0;
	private final static int NUMBER_MODES = 3;
	private final static String SWITCH_MODE = "Change to mode: ";
	static String FILENAME_CONVEX;
	static String FILENAME_VORONOI;

	// Buttons
	private Button button1, button2, button3;

	// Geometric objects
	public HilbertGeometryDraw geometry;
	public VoronoiDraw voronoi;
	final static double epsilon = 4.0;
	static double radius = 0.2;
	final static double RADIUS_STEP = 0.01;

	/* Variables for moving points */
	private float xOffset = 0.0f;
	private float yOffset = 0.0f;
	private boolean locked = false;
	private int indexOfMovingPoint = -1;
	private int indexOfSelectedPoint = -1;

	/* Panels and Buttons */
	private Button reinit;
	private Choice polygon;
	private Choice regularPolygon;
	private Choice grid;
	private Panel convexPanel;
	private CheckboxGroup drawMode;
	private Checkbox drawConvex;
	private Checkbox drawSpokes;
	private Checkbox drawVoronoi;
	private Checkbox hilbertVoronoi;

	// DEBUGGING
	KdTree<KdTree.XYZPoint> tree;
	List<Sector> secs;

	public static void main(String[] args) {
		if (args != null) {
			if (args.length > 0)
				FILENAME_CONVEX = args[0];
			if (args.length > 1)
				FILENAME_VORONOI = args[1];
		}
		PApplet.main(new String[] { "drawing.DrawingApplet" });
	}

	/*
	 * Setting up visualization interface
	 */
	public void setup() {
		size(1000, 800);
		initButton();

		this.geometry = new HilbertGeometryDraw(this, FILENAME_CONVEX);
		if (FILENAME_VORONOI != null)
			this.voronoi = new VoronoiDraw(geometry, FILENAME_VORONOI, this);
		else
			this.voronoi = new VoronoiDraw(geometry, this);

		// set starting mode
		this.currentMode = 0;

		// if points in Convex is not on the hull, add it to the HilbertGeometry object
		// if(this.geometry.convex.points.length > 0) {

//		// make sure there are no balls in geometry object
//		if (this.geometry.ballCount() > 0) {
//			for (int index = 0; index < this.geometry.ballCount(); index++) {
//				this.geometry.removeBall(index);
//			}
//		}
//
//		// Inserted non-hull points into HilbertGeometry object; no longer desired
//		for (Point2D.Double p : this.geometry.convex.points) {
//			if (this.geometry.convex.findPoint(p) == -1) {
//				double r = 2;
//				this.geometry.addCenterPoint(p, r);
//			}
//		}
	}


	// Method to give a list of coordinates
	public static List<List<Integer>> generatePolygonCoordinates(int n) {
        List<Integer> xCoords = new ArrayList<>();
        List<Integer> yCoords = new ArrayList<>();

        int radius = 300;

        for (int j = 0; j < n; j++) {
            double angle = 2 * Math.PI * j / n + Math.PI / 2;
            int x = (int) (500 + radius * Math.cos(angle));
            int y = (int) (400 + radius * Math.sin(angle));
            xCoords.add(x);
            yCoords.add(y);
        }

        List<List<Integer>> coordinates = new ArrayList<>();
        coordinates.add(xCoords);
        coordinates.add(yCoords);
        return coordinates;
	}


	public void initButton() {
		this.drawMode = new CheckboxGroup();
		this.drawConvex = new Checkbox("Insert Convex", this.drawMode, true);
		this.drawVoronoi = new Checkbox("Brute-force Voronoi", this.drawMode, false);
		this.hilbertVoronoi = new Checkbox("Fast Voronoi", this.drawMode, false);
		// this.drawConvex.setBackground(DrawUtil.WHITE);
		// this.drawSpokes.setBackground(DrawUtil.WHITE);
		// this.drawSpokes = new Checkbox("Insert Sites", this.drawMode, false);
		this.reinit = new Button("Reinitialize");
		// this.reinit.setBackground(DrawUtil.WHITE);
		this.polygon = new Choice();
		this.regularPolygon = new Choice();
		this.grid = new Choice();
		this.polygon.add("triangle");
		this.polygon.add("square");
		this.polygon.add("rectangle");
		this.polygon.add("pentagon");
		this.polygon.add("hexagon");
		this.polygon.add("circle");
		this.grid.add("triangular");
		this.grid.add("square");
		this.grid.add("hexagonal");
		this.grid.add("concentric circles");
		this.grid.add("spiral");
		this.grid.add("sine curve");
		this.grid.add("perpendicular lines");
		this.grid.add("flower");
		this.regularPolygon.add("7");
		this.regularPolygon.add("8");
		this.regularPolygon.add("9");
		this.regularPolygon.add("10");
		this.regularPolygon.add("11");
		this.regularPolygon.add("12");
		this.regularPolygon.add("13");
		this.regularPolygon.add("14");
		this.regularPolygon.add("15");
		this.regularPolygon.add("16");
		this.regularPolygon.add("17");
		this.regularPolygon.add("18");
		this.regularPolygon.add("19");
		this.regularPolygon.add("20");
		this.regularPolygon.add("21");
		this.regularPolygon.add("22");
		this.regularPolygon.add("23");
		this.regularPolygon.add("24");
		this.regularPolygon.add("25");
		this.regularPolygon.add("26");
		this.regularPolygon.add("27");
		this.regularPolygon.add("28");
		this.regularPolygon.add("29");
		this.regularPolygon.add("30");
		this.convexPanel = new Panel();
		this.convexPanel.add(this.drawConvex);
		this.convexPanel.add(this.drawVoronoi);
		this.convexPanel.add(this.hilbertVoronoi);
		this.convexPanel.add(this.reinit);
		this.convexPanel.add(this.polygon);
		this.convexPanel.add(this.grid);
		this.convexPanel.add(this.regularPolygon);
		// this.convexPanel.add(this.drawSpokes);
		// this.convexPanel.setBackground(DrawUtil.WHITE);
		this.convexPanel.setName("Drawing mode");
		this.add((Component) this.convexPanel);
		this.frame.setTitle("Voronoi In Hilbert Metrics");

		// System.out.println(this.frame.getTitle());

		// Adding ActionListener to bottoms
		drawConvex.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				currentMode = 0;
			}
		});

		polygon.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				int mode = polygon.getSelectedIndex();
				// System.out.println(Integer.toString(mode));
				geometry.reset();
				synchronized (voronoi) {
				voronoi.reset();
				}
				// draw triangle
				if (mode == 0) {
					List<Integer> tri_xs = Arrays.asList(200,800,500);
					List<Integer> tri_ys = Arrays.asList(650,650,130);

					for (int i = 0; i < tri_xs.size(); i++) {
						Point2D.Double r = new Point2D.Double(tri_xs.get(i), tri_ys.get(i));
						geometry.convex.addPoint(r);
					}
				}
				// draw square
				if (mode == 1) {
					List<Integer> square_xs = Arrays.asList(200,800,800,200);
					List<Integer> square_ys = Arrays.asList(100,100,700,700);

					for (int i = 0; i < square_xs.size(); i++) {
						Point2D.Double r = new Point2D.Double(square_xs.get(i), square_ys.get(i));
						geometry.convex.addPoint(r);
					}
				}
				// draw rectangle
				if (mode == 2) {
					List<Integer> square_xs = Arrays.asList(100,900,900,100);
					List<Integer> square_ys = Arrays.asList(100,100,700,700);

					for (int i = 0; i < square_xs.size(); i++) {
						Point2D.Double r = new Point2D.Double(square_xs.get(i), square_ys.get(i));
						geometry.convex.addPoint(r);
					}
				}
				// draw pentagon
				if (mode == 3) {
					// List<Integer> square_xs = Arrays.asList(300,700,824,500,176);
					// List<Integer> square_ys = Arrays.asList(750,750,370,95,370);
					List<Integer> square_xs = Arrays.asList(340,660,759,500,241);
					List<Integer> square_ys = Arrays.asList(630,630,316,128,316);

					for (int i = 0; i < square_xs.size(); i++) {
						Point2D.Double r = new Point2D.Double(square_xs.get(i), square_ys.get(i));
						geometry.convex.addPoint(r);
					}
				}
				// draw hexagon
				if (mode == 4) {
					List<Integer> square_xs = Arrays.asList(675,325,150,325,675,850);
					List<Integer> square_ys = Arrays.asList(97,97,400,703,703,400);

					for (int i = 0; i < square_xs.size(); i++) {
						Point2D.Double r = new Point2D.Double(square_xs.get(i), square_ys.get(i));
						geometry.convex.addPoint(r);
					}
				}
				// draw circle
				if (mode == 5) {
					// List<Integer> square_xs = Arrays.asList(800, 785, 742, 676, 592, 500, 408, 324, 258, 215, 200, 215, 258, 324, 408, 500, 592, 676, 742, 785);
					List<Integer> square_xs = Arrays.asList(800, 793, 774, 742, 700, 650, 592, 531, 469, 408, 351, 300, 258, 226, 207, 200, 207, 226, 258, 300, 350, 408, 469, 531, 592, 650, 700, 742, 774, 793);
					// List<Integer> square_ys = Arrays.asList(400, 492, 576, 642, 685, 700, 685, 642, 576, 492, 400, 308, 224, 158, 115, 100, 115, 158, 224, 308);
					List<Integer> square_ys = Arrays.asList(400, 462, 522, 576, 622, 659, 685, 698, 698, 685, 659, 622, 576, 522, 462, 400, 338, 278, 224, 178, 141, 115, 102, 102, 115, 141, 178, 224, 278, 338);

					for (int i = 0; i < square_xs.size(); i++) {
						Point2D.Double r = new Point2D.Double(square_xs.get(i), square_ys.get(i));
						geometry.convex.addPoint(r);
					}
				}
			}			
		});

		grid.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				int mode = grid.getSelectedIndex();
				// System.out.println(Integer.toString(mode));
				// geometry.reset();
				// synchronized (voronoi) {
				// voronoi.reset();
				// }
				// draw triangular grid
				if (mode == 0) {
					List<Integer> tri_x1 = Arrays.asList(96, 197, 298, 399, 500, 601, 702, 803, 904);
					List<Integer> tri_x2 = Arrays.asList(146, 247, 348, 449, 551, 652, 753, 854);
					List<Integer> tri_y1 = Arrays.asList(130,303,477,650);
					List<Integer> tri_y2 = Arrays.asList(217,390,564);

					for (int i = 0; i < tri_x1.size(); i++) {
						for (int j = 0; j < tri_y1.size(); j++) {
							Point2D.Double r = new Point2D.Double(tri_x1.get(i), tri_y1.get(j));
							if (geometry.isInConvex(r)) {
								indexOfSelectedPoint = geometry.findCenterPoint(r);
								if (indexOfSelectedPoint == -1) {
									geometry.addCenterPoint(r, radius);
									indexOfSelectedPoint = geometry.findCenterPoint(r);
								}
							
								// add to Voronoi Object
								voronoi.addPoint(r);
								voronoi.computeVoronoi();
								voronoi.hasChanged = true;
							} else {
								voronoi.removePoint(r);
							}
						}
					}

					for (int i = 0; i < tri_x2.size(); i++) {
						for (int j = 0; j < tri_y2.size(); j++) {
							Point2D.Double r = new Point2D.Double(tri_x2.get(i), tri_y2.get(j));
							if (geometry.isInConvex(r)) {
								indexOfSelectedPoint = geometry.findCenterPoint(r);
								if (indexOfSelectedPoint == -1) {
									geometry.addCenterPoint(r, radius);
									indexOfSelectedPoint = geometry.findCenterPoint(r);
								}
							
								// add to Voronoi Object
								voronoi.addPoint(r);
								voronoi.computeVoronoi();
								voronoi.hasChanged = true;
							} else {
								voronoi.removePoint(r);
							}
						}
					}
				}

				// draw square grid
				if (mode == 1) {

					List<Integer> square_xs = Arrays.asList(0,100,200,300,400,500,600,700,800,900);
					List<Integer> square_ys = Arrays.asList(0,100,200,300,400,500,600,700);

					for (int i = 0; i < square_ys.size(); i++) {
						for (int j = 0; j < square_xs.size(); j++) {
							Point2D.Double r = new Point2D.Double(square_xs.get(j), square_ys.get(i));	
							if (geometry.isInConvex(r)) {
								indexOfSelectedPoint = geometry.findCenterPoint(r);
								if (indexOfSelectedPoint == -1) {
									geometry.addCenterPoint(r, radius);
									indexOfSelectedPoint = geometry.findCenterPoint(r);
								}
							
								// add to Voronoi Object
								voronoi.addPoint(r);
								voronoi.computeVoronoi();
								voronoi.hasChanged = true;
								// System.out.println("site added: " + mouseX + ", " + mouseY);
							} else {
								voronoi.removePoint(r);
							}
						}
					}
				}

				// draw hexagonal grid
				if (mode == 2) {
					
					List<Integer> hex_x1 = Arrays.asList(149,325,413,589,677,853);
					List<Integer> hex_x2 = Arrays.asList(193,281,457,545,721,809);
					List<Integer> hex_y1 = Arrays.asList(703,551,399,247,95);
					List<Integer> hex_y2 = Arrays.asList(627,475,323,171);

					for (int i = 0; i < hex_x1.size(); i++) {
						for (int j = 0; j < hex_y1.size(); j++) {
							Point2D.Double r = new Point2D.Double(hex_x1.get(i), hex_y1.get(j));
							if (geometry.isInConvex(r)) {
								indexOfSelectedPoint = geometry.findCenterPoint(r);
								if (indexOfSelectedPoint == -1) {
									geometry.addCenterPoint(r, radius);
									indexOfSelectedPoint = geometry.findCenterPoint(r);
								}
							
								// add to Voronoi Object
								voronoi.addPoint(r);
								voronoi.computeVoronoi();
								voronoi.hasChanged = true;
							} else {
								voronoi.removePoint(r);
							}
						}
					}

					for (int i = 0; i < hex_x2.size(); i++) {
						for (int j = 0; j < hex_y2.size(); j++) {
							Point2D.Double r = new Point2D.Double(hex_x2.get(i), hex_y2.get(j));
							if (geometry.isInConvex(r)) {
								indexOfSelectedPoint = geometry.findCenterPoint(r);
								if (indexOfSelectedPoint == -1) {
									geometry.addCenterPoint(r, radius);
									indexOfSelectedPoint = geometry.findCenterPoint(r);
								}
							
								// add to Voronoi Object
								voronoi.addPoint(r);
								voronoi.computeVoronoi();
								voronoi.hasChanged = true;
							} else {
								voronoi.removePoint(r);
							}
						}
					}
				}
				
				// concentric circles 
				if (mode == 3) {
					List<Integer> xs = Arrays.asList(500, 570, 556, 521, 478, 443, 430, 443, 478, 521, 556, 640, 613, 543, 456, 386, 360, 386, 456, 543, 613, 710, 669, 564, 435, 330, 290, 330, 435, 564, 669, 780, 726, 586, 413, 273, 220, 273, 413, 586, 726, 850, 783, 608, 391, 216, 150, 216, 391, 608, 783);
					List<Integer> ys = Arrays.asList(400, 400, 441, 466, 466, 441, 400, 358, 333, 333, 358, 400, 482, 533, 533, 482, 400, 317, 266, 266, 317, 400, 523, 599, 599, 523, 400, 276, 200, 200, 276, 400, 564, 666, 666, 564, 400, 235, 133, 133, 235, 400, 605, 732, 732, 605, 400, 194, 67, 67, 194);

					for (int i = 0; i < xs.size(); i++) {						
						Point2D.Double r = new Point2D.Double(xs.get(i), ys.get(i));
						if (geometry.isInConvex(r)) {
							indexOfSelectedPoint = geometry.findCenterPoint(r);
							if (indexOfSelectedPoint == -1) {
								geometry.addCenterPoint(r, radius);
								indexOfSelectedPoint = geometry.findCenterPoint(r);
							}
						
							// add to Voronoi Object
							voronoi.addPoint(r);
							voronoi.computeVoronoi();
							voronoi.hasChanged = true;
						} else {
							voronoi.removePoint(r);
						}						
					}
					
				}
				
				// spiral
				if (mode == 4) {
					List<Integer> xs = Arrays.asList(512, 509, 500, 483, 463, 443, 430, 427, 438, 463, 500, 543, 585, 620, 638, 636, 610, 563, 500, 430, 365, 315, 292, 299, 340, 410, 499, 596, 683, 748, 777, 764, 708, 616, 500, 377, 267, 187, 153, 171, 242, 357, 499);
					List<Integer> ys = Arrays.asList(412, 424, 434, 440, 436, 423, 400, 370, 338, 311, 296, 295, 314, 350, 399, 456, 510, 552, 573, 568, 534, 476, 400, 317, 240, 183, 157, 167, 216, 297, 399, 509, 608, 680, 712, 696, 632, 529, 400, 264, 142, 55, 18);

					for (int i = 0; i < xs.size(); i++) {						
						Point2D.Double r = new Point2D.Double(xs.get(i), ys.get(i));
						if (geometry.isInConvex(r)) {
							indexOfSelectedPoint = geometry.findCenterPoint(r);
							if (indexOfSelectedPoint == -1) {
								geometry.addCenterPoint(r, radius);
								indexOfSelectedPoint = geometry.findCenterPoint(r);
							}
						
							// add to Voronoi Object
							voronoi.addPoint(r);
							voronoi.computeVoronoi();
							voronoi.hasChanged = true;
						} else {
							voronoi.removePoint(r);
						}						
					}					
				}
				// sine
				if (mode == 5) {
					List<Integer> xs = Arrays.asList(200, 218, 236, 254, 272, 290, 309, 327, 345, 363, 381, 400, 418, 436, 454, 472, 490, 509, 527, 545, 563, 581, 600, 618, 636, 654, 672, 690, 709, 727, 745, 763, 781);
					List<Integer> ys = Arrays.asList(400, 418, 437, 454, 469, 481, 490, 497, 499, 498, 494, 486, 475, 461, 445, 428, 409, 390, 371, 354, 338, 324, 313, 305, 301, 300, 302, 309, 318, 330, 345, 362, 381);

					for (int i = 0; i < xs.size(); i++) {						
						Point2D.Double r = new Point2D.Double(xs.get(i), ys.get(i));
						if (geometry.isInConvex(r)) {
							indexOfSelectedPoint = geometry.findCenterPoint(r);
							if (indexOfSelectedPoint == -1) {
								geometry.addCenterPoint(r, radius);
								indexOfSelectedPoint = geometry.findCenterPoint(r);
							}
						
							// add to Voronoi Object
							voronoi.addPoint(r);
							voronoi.computeVoronoi();
							voronoi.hasChanged = true;
						} else {
							voronoi.removePoint(r);
						}						
					}					
				}
				
				// perpendicular lines
				if (mode == 6) {
					List<Integer> xs = Arrays.asList(50,125,200,275,350,425,500,575,650,725,800,875);
					List<Integer> ys = Arrays.asList(100,175,250,325,400,475,550,625,700,775);

					for (int i = 0; i < xs.size(); i++) {						
						Point2D.Double r = new Point2D.Double(xs.get(i), 400);
						if (geometry.isInConvex(r)) {
							indexOfSelectedPoint = geometry.findCenterPoint(r);
							if (indexOfSelectedPoint == -1) {
								geometry.addCenterPoint(r, radius);
								indexOfSelectedPoint = geometry.findCenterPoint(r);
							}
						
							// add to Voronoi Object
							voronoi.addPoint(r);
							voronoi.computeVoronoi();
							voronoi.hasChanged = true;
						} else {
							voronoi.removePoint(r);
						}						
					}		
					
					for (int i = 0; i < xs.size(); i++) {						
						Point2D.Double r = new Point2D.Double(500, ys.get(i));
						if (geometry.isInConvex(r)) {
							indexOfSelectedPoint = geometry.findCenterPoint(r);
							if (indexOfSelectedPoint == -1) {
								geometry.addCenterPoint(r, radius);
								indexOfSelectedPoint = geometry.findCenterPoint(r);
							}
						
							// add to Voronoi Object
							voronoi.addPoint(r);
							voronoi.computeVoronoi();
							voronoi.hasChanged = true;
						} else {
							voronoi.removePoint(r);
						}						
					}					
				}

				// flower grid
				if (mode == 7) {

					int radius = 150;
					int num_points = 20;

					// List<Integer> xs = Arrays.asList(650,646,637,621,600,575,546,515,485,454,426,400,379,363,354,350,354,363,379,400,425,454,485,515,546,575,600,621,637,646);
					List<Integer> xs = Arrays.asList(650,642,621,588,546,500,454,412,379,358,350,358,379,412,454,500,546,588,621,642);
					// List<Integer> ys = Arrays.asList(400,431,461,488,511,529,542,549,549,542,529,511,488,461,431,400,369,339,312,289,271,258,251,251,258,271,289,312,339,369);
					List<Integer> ys = Arrays.asList(400,446,488,521,542,550,542,521,488,446,400,354,312,279,258,250,258,279,312,354);
					
					for (int i = 0; i < num_points; i++) {
						Point2D.Double r = new Point2D.Double(xs.get(i), ys.get(i));
							if (geometry.isInConvex(r)) {
								indexOfSelectedPoint = geometry.findCenterPoint(r);
								if (indexOfSelectedPoint == -1) {
									geometry.addCenterPoint(r, radius);
									indexOfSelectedPoint = geometry.findCenterPoint(r);
								}
							
								// add to Voronoi Object
								voronoi.addPoint(r);
								voronoi.computeVoronoi();
								voronoi.hasChanged = true;
							} else {
								voronoi.removePoint(r);
							}		
					}

					for (int i = 0; i < num_points; i = i + 5) {		
						for (int j = 0; j<num_points; j++) {	
							double phi = j*2 * Math.PI/ num_points;

							int x = (int) (xs.get(i) + radius * Math.cos(phi));
            				int y = (int) (ys.get(i) + radius * Math.sin(phi));

							Point2D.Double r = new Point2D.Double(x, y);
							if (geometry.isInConvex(r)) {
								indexOfSelectedPoint = geometry.findCenterPoint(r);
								if (indexOfSelectedPoint == -1) {
									geometry.addCenterPoint(r, radius);
									indexOfSelectedPoint = geometry.findCenterPoint(r);
								}
							
								// add to Voronoi Object
								voronoi.addPoint(r);
								voronoi.computeVoronoi();
								voronoi.hasChanged = true;
							} else {
								voronoi.removePoint(r);
							}		
						}				
					}		
				}
			}			
		});

		regularPolygon.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				int n = Integer.valueOf(regularPolygon.getSelectedItem());
				// System.out.println(Integer.toString(mode));
				geometry.reset();
				synchronized (voronoi) {
				voronoi.reset();

				List<List<Integer>> coordinates = generatePolygonCoordinates(n);

				List<Integer> xs = coordinates.get(0);
				List<Integer> ys = coordinates.get(1);

				for (int i = 0; i < xs.size(); i++) {
					Point2D.Double r = new Point2D.Double(xs.get(i), ys.get(i));
					geometry.convex.addPoint(r);
				}
				}
			}
		});

		drawVoronoi.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				currentMode = 1;
			}
		});

		hilbertVoronoi.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				currentMode = 2;
			}
		});

		reinit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				geometry.reset();
				synchronized (voronoi) {
					voronoi.reset();
				}
			}
		});
		/*
		 * drawSpokes.addItemListener(new ItemListener() {
		 * 
		 * @Override public void itemStateChanged(ItemEvent event) { currentMode = 1;
		 * voronoi.computeVoronoi(); } });
		 */
	}

	/*
	 * Method for drawing convex hull
	 */
	public void draw() {
		background(220);
		textFont(createFont("Arial", 12, true), 12); // font used
		fill(0); // font color

		if (this.geometry.convex.convexHull.length < 3)
			voronoi.drawPoints(); // no convex Hull to display.
		else {
			this.geometry.draw(false, -1);
		}
		if (MODES[currentMode].toString().contains("HILBERT")) { // true
			voronoi.drawPoints();
			this.voronoi.hasChanged = false;
		}

		if (MODES[currentMode].toString().contains("VORONOI")) { // true
			voronoi.drawPoints();
			this.voronoi.hasChanged = false;
		}

		for (this.indexOfSelectedPoint = 0; this.indexOfSelectedPoint < this.geometry
				.ballCount(); this.indexOfSelectedPoint++) {
			geometry.draw(true, this.indexOfSelectedPoint);
		}
		this.indexOfSelectedPoint = -1;
	}

	/*
	 * Determines if a point is within EPSILON distance away from some point on a
	 * list Q: What is the rationale for choosing the value of EPSILON?
	 */
	public int findPoint(int x, int y, LinkedList<Point2D.Double> pts) {
		Point2D.Double p = new Point2D.Double(x, y);

		int index = 0;
		boolean found = false;
		for (Point2D.Double q : pts) {
			if (q.distanceSq(p) < epsilon) {
				found = true;
				break;
			}
			index++;
		}
		if (found == true)
			return index;
		else
			return -1;
	}

	public void mouseClicked() {
		Point2D.Double p = new Point2D.Double(mouseX, mouseY);

		if (MODES[currentMode] == Mode.DRAW_CONVEX && mouseButton == LEFT) {
			this.geometry.convex.addPoint(p);
			this.voronoi.hasChanged = true;
			System.out.println("Point added to convex: (" + mouseX + ", " + mouseY + ")");
			if (this.voronoi.numPoints() > 0)
				this.voronoi.computeVoronoi();
		} else if (MODES[currentMode] == Mode.UNIT_BALL && mouseButton == LEFT) {
			this.indexOfSelectedPoint = this.geometry.findCenterPoint(p);
			if (this.indexOfSelectedPoint == -1) {
				this.geometry.addCenterPoint(p, radius);
				this.indexOfSelectedPoint = this.geometry.findCenterPoint(p);
			}
		} else if (MODES[currentMode] == Mode.UNIT_BALL && mouseButton == RIGHT) {
			int removedPoint = this.geometry.findCenterPoint(p);
			if (removedPoint == this.indexOfSelectedPoint) {
				this.indexOfSelectedPoint = -1;
			}
			this.geometry.convex.removePoint(p);
		} else if (MODES[currentMode] == Mode.INCONVEXTEST && mouseButton == LEFT) {
			if (this.geometry.isInConvex(p)) {
				System.out.println("Is in convex.");
			} else {
				System.out.println("Not in convex.");
			}
		}
		/*
		 * Brute force Voronoi mode
		 */
		else if (MODES[currentMode] == Mode.VORONOI_DEF && mouseButton == LEFT) {
			// add to HilbertGeometry Object
			if (this.geometry.isInConvex(p)) {
				this.indexOfSelectedPoint = this.geometry.findCenterPoint(p);
				if (this.indexOfSelectedPoint == -1) {
					this.geometry.addCenterPoint(p, radius);
					this.indexOfSelectedPoint = this.geometry.findCenterPoint(p);
				}

				// add to Voronoi Object
				this.voronoi.addPoint(p);
				this.voronoi.computeVoronoi();
				this.voronoi.hasChanged = true;
				System.out.println("site added: " + mouseX + ", " + mouseY);
			} else {
				this.voronoi.removePoint(p);
			}

		} else if (MODES[currentMode] == Mode.VORONOI_DEF && mouseButton == RIGHT) {
			// remove point from HilbertGeometry object
			int removedPoint = this.geometry.findCenterPoint(p);
			if (removedPoint == this.indexOfSelectedPoint) {
				this.indexOfSelectedPoint = -1;
			}

			// remove point from Voronoi object
			this.voronoi.removePoint(p);
			this.voronoi.computeVoronoi();
		}
		/*
		 * Randomize Insertion Voronoi mode
		 */
		else if (MODES[currentMode] == Mode.VORONOI_HILBERT && mouseButton == LEFT) {
			if (this.geometry.isInConvex(p)) {
				System.out.println("Site added: (" + mouseX + ", " + mouseY + ")");
				// add to HilbertGeometry Object
				this.indexOfSelectedPoint = this.geometry.findCenterPoint(p);
				if (this.indexOfSelectedPoint == -1) {
					this.geometry.addCenterPoint(p, radius);
					this.indexOfSelectedPoint = this.geometry.findCenterPoint(p);
				}
				// add to Voronoi Object
				this.voronoi.addPoint(p);
				if (this.voronoi.numPoints() < 2) {
					this.voronoi.computeVoronoi();
				} else {
					Point2D.Double p1 = geometry.getCenterPoint(geometry.centerPoints.length - 2);
					this.geometry.drawHilbertVoronoi(this.voronoi.computeHilbertVoronoi(p1, p));
					this.voronoi.computeVoronoi();
				}
				this.voronoi.hasChanged = true;

			} else {
				this.voronoi.removePoint(p);
			}

		}

		else if (MODES[currentMode] == Mode.VORONOI_FIND && mouseButton == LEFT) {
			this.voronoi.colorPoint(p);
		}
	}

	public void mousePressed() {
		Point2D.Double p = new Point2D.Double();
		p.x = (double) mouseX;
		p.y = (double) mouseY;
		if (MODES[currentMode] == Mode.DRAW_CONVEX) {
			indexOfMovingPoint = this.geometry.findPoint(p);
		} else if (MODES[currentMode] == Mode.UNIT_BALL) {
			indexOfMovingPoint = this.geometry.findCenterPoint(p);
		} else if (MODES[currentMode] == Mode.VORONOI_DEF) {
			indexOfMovingPoint = this.voronoi.findPoint(p);
		} else if (MODES[currentMode] == Mode.VORONOI_HILBERT) {
			indexOfMovingPoint = this.voronoi.findPoint(p);
		}
		if (indexOfMovingPoint > -1) {
			if (MODES[currentMode] == Mode.DRAW_CONVEX) {
				locked = true;
				xOffset = mouseX - (float) (double) this.geometry.getPoint(indexOfMovingPoint).x;
				yOffset = mouseY - (float) (double) this.geometry.getPoint(indexOfMovingPoint).y;
			} else if (MODES[currentMode] == Mode.UNIT_BALL) {
				locked = true;
				xOffset = mouseX - (float) (double) this.geometry.getCenterPoint(indexOfMovingPoint).x;
				yOffset = mouseY - (float) (double) this.geometry.getCenterPoint(indexOfMovingPoint).y;
			} else if (MODES[currentMode] == Mode.VORONOI_DEF) {
				locked = true;
				xOffset = mouseX - (float) (double) this.voronoi.getPoint(indexOfMovingPoint).x;
				yOffset = mouseY - (float) (double) this.voronoi.getPoint(indexOfMovingPoint).y;
			} else if (MODES[currentMode] == Mode.VORONOI_HILBERT) {
				locked = true;
				xOffset = mouseX - (float) (double) this.voronoi.getPoint(indexOfMovingPoint).x;
				yOffset = mouseY - (float) (double) this.voronoi.getPoint(indexOfMovingPoint).y;
			}
		}
	}

	public void mouseReleased() {
		if (locked) {
			locked = false;
			indexOfMovingPoint = -1;
		}
	}

	public void mouseDragged() {
		if (locked) {
			Point2D.Double q = new Point2D.Double(mouseX - xOffset, mouseY - yOffset);
			if (MODES[currentMode] == Mode.DRAW_CONVEX) {
				this.geometry.movePoint(indexOfMovingPoint, q);
				this.voronoi.hasChanged = true;
			} else if (MODES[currentMode] == Mode.UNIT_BALL) {
				this.geometry.moveCenterPoint(indexOfMovingPoint, q);
			} else if (MODES[currentMode] == Mode.VORONOI_DEF) {
				this.geometry.moveCenterPoint(indexOfMovingPoint, q);
				this.voronoi.movePoint(indexOfMovingPoint, q);
				this.voronoi.hasChanged = true;
			} else if (MODES[currentMode] == Mode.VORONOI_HILBERT) {
				this.geometry.moveCenterPoint(indexOfMovingPoint, q);
				this.voronoi.movePoint(indexOfMovingPoint, q);
				this.voronoi.hasChanged = true;
			}
		}
	}

	/*
	 * Switch modes with key board input
	 */
//	public void keyPressed() {
//
//		if (this.key == 'q') {
//			this.currentMode = (this.currentMode + 1) % 3;
//			if (this.currentMode == 0) {
//				System.out.println("in drawing convex hull mode");
//			}
//		}
//		if (this.currentMode == 1) {
//			System.out.println("in drawing ball mode");
//		}
//		if (this.currentMode == 2) {
//			System.out.println("in drawing voronoi diagram mode");
//		}
//	}

	@Override
	public void actionPerformed(ActionEvent event) {
//       if (event.getSource() == newConvex) {
//         this.geometry.reset();
//         synchronized(this.voronoi) {
//           this.voronoi.reset(); 
//         } 
//		 radius = 1;
//		 } else if (event.getSource() == plusButton) {
//		 if (this.indexOfSelectedPoint != - 1) {
//		 this.geometry.updateRadius(indexOfSelectedPoint, RADIUS_STEP);
//		 }
//		 } else if (event.getSource() == minusButton) {
//		 if (this.indexOfSelectedPoint != -1) {
//		 this.geometry.updateRadius(indexOfSelectedPoint, -RADIUS_STEP);
//		 }
//		 } else if (event.getSource() == toggleMode) {
//		 currentMode = (currentMode + 1) % NUMBER_MODES;
//		 toggleMode.setLabel(SWITCH_MODE + MODES[(currentMode + 1) %
//		 NUMBER_MODES].toString());
//		 }
	}
}
