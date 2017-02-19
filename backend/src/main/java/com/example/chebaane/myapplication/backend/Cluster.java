package com.example.chebaane.myapplication.backend;

/**
 * Created by chebaane on 04/02/2017.
 */
import java.util.*;

public class Cluster {

    private final List<Point> points = new ArrayList<Point>();
    private Point centroid;

    public Cluster() {
    }

    public Cluster(Point firstPoint) {
        centroid = firstPoint;
    }

    public Point getCentroid(){
        return centroid;
    }

    public void updateCentroid(){
        double newx = 0d, newy = 0d, newz = 0d;
        for (Point point : points){
            newx += point.x; newy += point.y; newz += point.z;
        }
        centroid = new Point(newx / points.size(), newy / points.size(), newz / points.size());
    }

    public List<Point> getPoints() {
        return points;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder("This cluster contains the following points:\n");
        for (Point point : points)
            builder.append(point.toString() + ",\n");
        return builder.deleteCharAt(builder.length() - 2).toString();
    }
}
