package com.project.lift;

public class MainLift {

    public static void main(String... args){
        byte pass1Floor = RandomHelper.getNumb((byte)1, (byte)5);
        byte floors = RandomHelper.getNumb(5, 20);
        byte passengOnFloor = RandomHelper.getNumb(1, 10);

        int countIterations = 100;

        System.out.println("floors = "+floors+ " passengOnFloor = "+passengOnFloor);

        Lift lift = new Lift(floors,passengOnFloor);
        LiftRunner liftRunner = new LiftRunner(lift);
        liftRunner.setCountIterations(countIterations);

        liftRunner.start(pass1Floor);
        liftRunner.run();

    }
}
