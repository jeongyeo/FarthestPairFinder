/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package farthestpairassignment;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;

/**
 *
 * @author parkd9255
 */
public class FarthestPairAssignment extends JFrame{

    int pointSize = 12;
    int numPoints = 100;
     
    Point2D[] S = new Point2D[ numPoints ]; //the set S
    Point2D[] farthestPair = new Point2D[ 2 ]; //the two points of the farthest pair
    Point2D[] farthestPair_1 = new Point2D[ 2 ];
    
    static ArrayList<Point2D> convexHull = new ArrayList(); //the vertices of the convex hull of S
     
    Color convexHullColour = Color.blue;
    Color genericColour = Color.yellow;
    
    //fills S with random points
    public void makeRandomPoints() {
        
        Random rand = new Random();
 
        for (int i = 0; i < numPoints; i++) {
            int x = 50 + rand.nextInt(700);
            int y = 50 + rand.nextInt(700);
            S[i] = new Point2D( x, y );            
        }        
    }

    
    public void paint(Graphics g) {
        //black background
        g.setColor(Color.black);
        g.fillRect(0, 0, 800, 800);
        //draw the sides of the polygon containing the points in the convex hull
        
        for (int i = 0; i < convexHull.size()-1; i++) {
            g.setColor(convexHullColour);
            g.drawLine((int)convexHull.get(i).x, (int)convexHull.get(i).y, (int)convexHull.get(i+1).x, (int)convexHull.get(i+1).y);
        }
        //draw last point to first point
        g.drawLine((int)convexHull.get(0).x, (int)convexHull.get(0).y, (int) convexHull.get(convexHull.size()-1).x, (int) convexHull.get(convexHull.size()-1).y);
        
        //draw the points
        for (int i = 0; i < numPoints; i++) {
            g.setColor(S[i].color);
            g.fillOval((int)S[i].x-3, (int)S[i].y-3, 6, 6);
        }
                
        //draw a red line connecting the farthest pair
        g.setColor(Color.red);
        g.drawLine((int) farthestPair[0].x, (int) farthestPair[0].y, (int) farthestPair[1].x, (int) farthestPair[1].y);
        //g.setColor(Color.green);
        //g.drawLine((int) farthestPair_1[0].x, (int) farthestPair_1[0].y, (int) farthestPair_1[1].x, (int) farthestPair_1[1].y);

    }
    
    public Point2D findLowestPoint(){
        Point2D lowestPoint = S[0];
        for (
                int i = 0; i < numPoints; i++) {
            if (lowestPoint.y < S[i].y)
                lowestPoint = S[i];
        }
        return lowestPoint;
    }
    
    public Point2D whichIsLonger(Point2D vertex, Point2D x, Point2D y){
        double a = Math.sqrt(Math.pow((vertex.x - x.x), 2)+ Math.pow((vertex.y - x.y),2));
        double b = Math.sqrt(Math.pow((vertex.x - y.x), 2)+ Math.pow((vertex.y - y.y),2));
        if (b < a)
            return x;
        else
            return y;
    }
    public void addToConvexHull(Point2D a){
        convexHull.add(a);
        a.visited = true;
    }
    public void findConvexHull(){
        //finds lowest point and adds that to first points in convex hull
        Point2D lowestPoint = findLowestPoint();
        addToConvexHull(lowestPoint);
        
        //set parameters
        Vector mainVector = new Vector(1, 0);
        Point2D mainVertex = lowestPoint;
        Point2D minVertexWithAngle = null;
        
        while (true){
            //reset minAngle to pi every time a new point is found
            double minAngle = Math.PI;
            for (int i = 0; i < S.length; i++){
                //set vector to find angle to mainVector
                Vector subVector = S[i].subtract(mainVertex);
                double subAngle = mainVector.getAngle(subVector);
                //check which vertex has lowest angle
                if( minAngle > subAngle ){
                    minAngle = subAngle;
                    minVertexWithAngle = S[i];
                }
                // checks colinear points
                else if (minAngle == subAngle){
                    //compare which is longer and set that as minPointWithAngle
                    minVertexWithAngle = whichIsLonger(mainVertex, S[i], minVertexWithAngle);
                }
            }
            if( minVertexWithAngle.visited == true ){
                break;
            }
            addToConvexHull( minVertexWithAngle );
            //reset paramerters
            mainVector = minVertexWithAngle.subtract(mainVertex);
            mainVertex = minVertexWithAngle;
            
        }
    }

    public void findFarthestPair_EfficientWay() {
        double highest = 0, lowest = 0;
        int i_highest = 0, i_lowest = 0;
        for (int i = 0; i < convexHull.size(); i++) {
            if(convexHull.get(i).y > highest){
                i_highest = i;
                highest = convexHull.get(i).y;
            }
            else if (convexHull.get(i).y < lowest){
                i_lowest = i;
                lowest = convexHull.get(i).y;
            }
        }
        
        Vector high = new Vector(1, 0);
        Vector low = new Vector(-1, 0);
        int curr_high = i_highest, curr_low = i_lowest;
        double longest = 0;

        for (int i = 0; i < convexHull.size(); i++) {
            double dist = Math.sqrt(Math.pow(convexHull.get(curr_high).x-convexHull.get(curr_low).x,2)+Math.pow(convexHull.get(curr_high).y-convexHull.get(curr_low).y,2));
            
            Vector new_high = convexHull.get((curr_high+1)%convexHull.size()).subtract(convexHull.get(curr_high));
            Vector new_low = convexHull.get((curr_low+1)%convexHull.size()).subtract(convexHull.get(curr_low));
            
            double angle_high = new_high.getAngle(high);
            double angle_low = new_low.getAngle(low);
            
            if(angle_high > angle_low){
                low = new_low;
                high = new Vector(-low.xComponent, -low.yComponent);
                curr_low = (curr_low+1)%convexHull.size();
            }
            else{
                high = new_high;
                low = new Vector(-high.xComponent, -high.yComponent);
                curr_high = (curr_high+1)%convexHull.size();
            }
            
            if(dist > longest){
                longest = dist;
                farthestPair_1[0] = convexHull.get(curr_high);
                farthestPair_1[1] = convexHull.get(curr_low);
            }
        }
    double dist = Math.sqrt(Math.pow(farthestPair_1[0].x-farthestPair_1[1].x,2)+Math.pow(farthestPair_1[0].y-farthestPair_1[1].y,2));
        System.out.println("Efficient length: " + dist);
    }

    public void findFarthestPair_BruteForceWay() {
        farthestPair[0] = convexHull.get(0);
        farthestPair[1] = convexHull.get(1);
        for (int i = 0; i < convexHull.size()-1; i++) {
            for (int j = i+1; j < convexHull.size(); j++) {
                double length = Math.sqrt(Math.pow((convexHull.get(j).x - convexHull.get(i).x), 2) + Math.pow((convexHull.get(j).y - convexHull.get(i).y), 2));
                if (length > Math.sqrt(Math.pow((farthestPair[1].x - farthestPair[0].x), 2) + Math.pow((farthestPair[1].y - farthestPair[0].y), 2))){
                    farthestPair[0] = convexHull.get(i);
                    farthestPair[1] = convexHull.get(j);
                }
            }
        }
        double dist = Math.sqrt(Math.pow(farthestPair[0].x-farthestPair[1].x,2)+Math.pow(farthestPair[0].y-farthestPair[1].y,2));
        System.out.println("Non-Efficient length: " + dist);
    }
    
   
    public static void main(String[] args) {        
        FarthestPairAssignment fpf = new FarthestPairAssignment();
        
        fpf.setBackground(Color.BLACK);
        fpf.setSize(800, 800);
        fpf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fpf.makeRandomPoints();
        
        fpf.findConvexHull();
        fpf.findFarthestPair_BruteForceWay();
        //fpf.findFarthestPair_EfficientWay();
        fpf.setVisible(true); 
    }
}
