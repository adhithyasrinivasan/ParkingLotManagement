package com.bt.parkinglot.domain;

public class SlotResponse {

    public String getSlot() {
        return Slot;
    }

    public void setSlot(String slot) {
        this.Slot = slot;
    }

    private String Slot;

    @Override
    public String toString() {
        return  Slot;
    }
}
