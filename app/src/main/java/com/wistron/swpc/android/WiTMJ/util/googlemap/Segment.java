package com.wistron.swpc.android.WiTMJ.util.googlemap;
//by Haseem Saheed

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Segment {
    /**
     * Points in this segment. *
     */
    private LatLng start;
    /**
     * Points in this segment. *
     */
    private LatLng endPoint;
    /**
     * Turn instruction to reach next segment. *
     */
    private String instruction;
    /**
     * Length of segment. *
     */
    private int length;
    /**
     * Distance covered. *
     */
    private double distance;
    
    /* Maneuver instructions */
    private String maneuver;
    private boolean isSend;

    private final List<LatLng> points;

    /**
     * Create an empty segment.
     */

    public Segment() {
        points = new ArrayList<LatLng>();
    }

    //add by wr
    public void addPoints(final List<LatLng> points) {
        this.points.addAll(points);
    }

    public List<LatLng> getPoints() {
        return points;
    }

    /**
     * Set the turn instruction.
     *
     * @param turn Turn instruction string.
     */

    public void setInstruction(final String turn) {
        this.instruction = turn;
    }

    /**
     * Get the turn instruction to reach next segment.
     *
     * @return a String of the turn instruction.
     */

    public String getInstruction() {
        return instruction;
    }

    /**
     * Add a point to this segment.
     *
     * @param point GeoPoint to add.
     */

    public void setPoint(final LatLng point) {
        start = point;
    }

    /**
     * Get the starting point of this
     * segment.
     *
     * @return a GeoPoint
     */

    public LatLng startPoint() {
        return start;
    }

    /**
     * Creates a segment which is a copy of this one.
     *
     * @return a Segment that is a copy of this one.
     */

    public Segment copy() {
        final Segment copy = new Segment();
        copy.start = start;
        copy.endPoint = endPoint;
        copy.instruction = instruction;
        copy.length = length;
        copy.distance = distance;
        copy.maneuver = maneuver;
        copy.isSend = isSend;
        copy.addPoints(points);
        return copy;
    }

    /**
     * @param length the length to set
     */
    public void setLength(final int length) {
        this.length = length;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    public void setManeuver(String man) {
        maneuver = man;
    }
    
    public String getManeuver() {
        return maneuver;
    }

    public LatLng getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(LatLng endPoint) {
        this.endPoint = endPoint;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }
}
