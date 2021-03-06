package net.bytten.gameutil;

import java.io.Serializable;
import java.util.Arrays;

public class Rect2D implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public final double x, y, w, h;
    
    public Rect2D(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
    
    public static Rect2D fromExtremes(double x, double y, double right, double bottom) {
        return new Rect2D(x, y, right-x, bottom-y);
    }
    
    public static Rect2D fromExtremes(Vec2D min, Vec2D max) {
        return fromExtremes(min.x, min.y, max.x, max.y);
    }
    
    public double top() {
        return y;
    }
    
    public double left() {
        return x;
    }
    
    public double right() {
        return x+w;
    }
    
    public double bottom() {
        return y+h;
    }
    
    public Vec2D topLeft() {
        return new Vec2D(left(), top());
    }
    public Vec2D topRight() {
        return new Vec2D(right(), top());
    }
    public Vec2D bottomLeft() {
        return new Vec2D(left(), bottom());
    }
    public Vec2D bottomRight() {
        return new Vec2D(right(), bottom());
    }
    public Vec2D midPoint() {
        return new Vec2D(x + w/2, y + h/2);
    }
    
    public boolean contains(double x, double y) {
        return x >= this.x && x < this.x+w && y >= this.y && y < this.y+h;
    }
    
    public boolean contains(Vec2D pos) {
        return contains(pos.x, pos.y);
    }
    
    public Vec2D min() {
        return topLeft();
    }
    
    public Vec2D max() {
        return bottomRight();
    }
    
    public Vec2D size() {
        return new Vec2D(w,h);
    }
    
    public Vec2D halfSize() {
        return new Vec2D(w/2, h/2);
    }
    
    public boolean overlaps(Rect2D other) {
        Vec2D mid = midPoint(),
            omid = other.midPoint();
        Vec2D half = halfSize(),
            ohalf = other.halfSize();
        return Math.abs(mid.x - omid.x) < half.x + ohalf.x &&
            Math.abs(mid.y - omid.y) < half.y + ohalf.y;
    }
    
    public Rect2I floor() {
        return Rect2I.fromExtremes(min().floor(), max().floor());
    }
    
    public Rect2I ceil() {
        return Rect2I.fromExtremes(min().ceil(), max().ceil());
    }
    
    public Rect2I integral() {
        return Rect2I.fromExtremes(min().floor(), max().ceil());
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof Rect2D) {
            Rect2D or = (Rect2D)other;
            return x == or.x && y == or.y && w == or.w && h == or.h;
        }
        return super.equals(other);
    }
    
    @Override
    public String toString() {
        return "Rect2D("+Double.toString(x)+", "+Double.toString(y)+", "+
            Double.toString(w)+", "+Double.toString(h)+")";
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{ x, y, w, h });
    }

    public Rect2D scale(double m) {
        return new Rect2D(x*m, y*m, w*m, h*m);
    }
    
    public Rect2D translate(Vec2D other) {
        return translate(other.x, other.y);
    }
    
    public Rect2D translate(double dx, double dy) {
        return new Rect2D(x + dx, y + dy, w, h);
    }

}
