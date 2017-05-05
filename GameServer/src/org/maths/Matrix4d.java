/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.maths;

/**
 *
 * @author Userr
 */
public class Matrix4d {
    private double x,  x0,  x1,  x2,  y,  y0,  y1,  y2,  z,  z0,  z1,  z2,  i,  i0,  i1,  i2;
    public Matrix4d(double x, double x0, double x1, double x2, double y, double y0, double y1, double y2, double z, double z0, double z1, double z2, double i, double i0, double i1, double i2) {
        this.x=x;
        this.x0=x0;
        this.x1=x1;
        this.x2=x2;
        this.y=y;
        this.y0=y0;
        this.y1=y1;
        this.y2=y2;
        this.z=z;
        this.z0=z0;
        this.z1=z1;
        this.z2=z2;
        this.i=i;
        this.i0=i0;
        this.i1=i1;
        this.i2=i2;
    }
    public double determinant(){
        return x*det3(y0,y1,y2,z0,z1,z2,i0,i1,i2)-x0*det3(y,y1,y2,z,z1,z2,i,i1,i2)+x1*det3(y,y0,y2,z,z0,z2,i,i0,i2)-x2*det3(y,y0,y1,z,z0,z1,i,i0,i1);
    }
    private double det3(double x,double x0,double x1,double y,double y0,double y1,double z,double z0,double z1){
        return x*det2(y0,y1,z0,z1)-x0*det2(y,y1,z,z1)+x1*det2(y,y0,z,z0);
    }
    private double det2(double x,double x0,double y,double y0){
        return x*y0-y*x0;
    }
}
