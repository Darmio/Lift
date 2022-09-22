package com.project.lift;

import java.util.List;
import java.util.stream.Collectors;

public class LiftRunner {
    private Lift lift;
    private boolean stop;
    private int step;
    private int countIterations;

    public LiftRunner(Lift lift) {
        this.lift = lift;
    }

    public void setCountIterations(int countIterations) {
        this.countIterations = countIterations;
    }

    public void start(byte count) {
        lift.setCurFloor((byte) 1);
        for (byte i = 1; i <= count; i++) {
            lift.add(RandomHelper.getFloor((byte) 2, lift.getMAX_FLOOR()));
        }

    }

    public void run() {
        int i = 1;
        Lift.Direction direct = lift.getCurrentDirection();
        while (!stop && (lift.countPassenersInLift() + lift.countPassenersWait() > 0)) {
            if (i > this.countIterations) {
                //      setStop(true);
                break;
            }
            printStep();
            Floor curFloor = lift.getCurFloor();
            lift.goNextFloor();
            printWait(curFloor);

            if (direct != lift.getCurrentDirection()) {
                direct = lift.getCurrentDirection();
                System.out.println("***** CHANGE DIRECTION " + direct);
            }
            System.out.println(System.lineSeparator());
            i++;
        }
    }

    public void printStep() {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        step = 0;
        step++;
        sb.append("Step " + lift.getCurFloor().getNumber()).append(" " + lift.getCurrentDirection()).append(System.lineSeparator());
        List<Byte> inLift = lift.getCurPassengers();
        List<Byte> wait = lift.getCurFloor().getPassengersFloor();
        int cLift = inLift.size();
        for (int k = 0; k < 9; k++) {
            if (i % 3 == 1) {
                sb.append("| ");
            }
            if (i <= cLift) {
                sb.append(String.format("%2d", inLift.get(i - 1)));
            } else {
                sb.append("  ");
            }
            sb.append(" ");
            if (i % 3 == 0) {
                sb.append("| ");
                if (k == 2) {
                    sb.append(lift.getCurFloor().getPassengersFloor().stream().map(x -> x.toString()).collect(Collectors.joining(", ")));
                }
                sb.append(System.lineSeparator());
            }
            i++;
        }
        System.out.print(sb.toString());

    }

    public void printWait(Floor curFloor) {
        if (curFloor.getPassengersFloor().size() > 0) {
            System.out.println("       new wait " + curFloor.getPassengersFloor().stream().map(x -> x.toString()).collect(Collectors.joining(", ")));
        }

    }

}
