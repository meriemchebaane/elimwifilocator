package com.example.chebaane.myapplication.backend;

/**
 * Created by chebaane on 04/02/2017.
 */

public class Point {


        private int index = -1; //denotes which Cluster it belongs to
        public double x=0, y=0, z=0;

        public Point(){

        }

        public Point(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Double getSquareOfDistance(Point anotherPoint){
            return  (x - anotherPoint.x) * (x - anotherPoint.x)
                    + (y - anotherPoint.y) *  (y - anotherPoint.y)
                    + (z - anotherPoint.z) *  (z - anotherPoint.z);
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String toString(){
            return "(" + x + "," + y + "," + z + ")";
        }
    }

