package com.project.lift;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private byte number;
    private
    List<Byte> passengersFloor;

    private Floor() {

    }

    public Floor(byte number) {
        this.number = number;
        this.passengersFloor = new ArrayList<>();
    }

    public byte getNumber() {
        return number;
    }

    public List<Byte> getPassengersFloor() {
        return passengersFloor;
    }

    public boolean add(Byte nextFloor) {
        this.passengersFloor.add(nextFloor);
        return true;
    }

}
