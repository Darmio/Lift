package com.project.lift;

import java.util.*;
import java.util.stream.Collectors;


public class Lift {
    private  byte MAX_FLOOR;
    private byte MAX_PASSENGERS = 5;
    private Floor[] floors;
    private List curPassengers;
    private Floor curFloor;
    private byte numberCurFloor;
    private byte curMaxFloor;
    private Direction currentDirection;

    private Lift(){

    }

    public Lift(byte maxFloor, byte passengOnFloor){
        this.MAX_FLOOR = maxFloor;
        this.curPassengers = new ArrayList();
        this.floors = new Floor[MAX_FLOOR];
        fillFloor();
        setCurFloor((byte)1);
    }

    public byte countPassenersInLift(){
        return (byte) curPassengers.size();
    }

    public int countPassenersWait(){
        return Arrays.stream(floors).map(x->x.getPassengersFloor().size()).reduce(Integer::sum).get();
    }
  /*  public List<Byte> getPassenersWait(byte floor){
        return floors[floor-1].getPassengersFloor();
    }*/
    public void goNextFloor(){

        int count  = remove();
        add();
        Optional<Byte> nextOpt = getNextFloor();
        byte nextCh = 1;
        if(nextOpt.isEmpty()){
            nextCh  = getDirectionWaitPass();
            changeDirection();
            add();
        }

        setRandomFloorForComePass(count);

         nextOpt = getNextFloor();

        byte next = nextOpt.orElse(nextCh);

        setCurMaxFloor();
        setCurFloor(next);
    }

    public boolean add(byte passsenger) {
        if(curPassengers.size()>=MAX_PASSENGERS || passsenger==numberCurFloor){
            return false;
        }
        this.curPassengers.add(passsenger);
        return true;
    }

    private boolean add(){
        if(curPassengers.size()>=MAX_PASSENGERS){
            //lift is full or floor passenger the same
            return false;
        }

        byte passInLift = (byte) this.curPassengers.size();
        List<Byte> floorPass = curFloor.getPassengersFloor();
        int count = floorPass.size();
        byte numbFloor = numberCurFloor;
        if(count>0){
            if(passInLift==0){
                long countUp = floorPass.stream().filter(x->x>numbFloor).count();
                long countDown = floorPass.size()-countUp;
                Direction direction = this.currentDirection;
                if(countUp!=countDown) {
                    direction = countUp > countDown? Direction.UP:Direction.DOWN;
                }
                if(!this.currentDirection.equals(direction)){
                    changeDirection();
                }
            }
            List<Byte> goInLift;
        if(this.currentDirection == Direction.UP){
            goInLift = floorPass.stream().filter(x->x>numbFloor).limit(MAX_PASSENGERS - passInLift).collect(Collectors.toList());
                    this.curPassengers.addAll(goInLift);
        } else {
            goInLift = floorPass.stream().filter(x->x<numbFloor).limit(MAX_PASSENGERS - passInLift).collect(Collectors.toList());
                    this.curPassengers.addAll(goInLift);
                }
            goInLift.stream().forEach(floorPass::remove);
        }
        return true;
    }

    private int remove(){
        if(curPassengers.size()==0 || curFloor==null){
            return 0;
        }
        List<Byte> l = (List<Byte>) curPassengers.stream().filter(e->e.equals(Byte.valueOf(numberCurFloor))).collect(Collectors.toList());
        int count = l.size();
        l.stream().forEach(x->curPassengers.remove(x));
       return count;
    }

    private void setRandomFloorForComePass(int countPass){
        Floor curFloor = floors[numberCurFloor-1];
        for(byte i =0; i<countPass; i++){
            curFloor.add(RandomHelper.getFloor(numberCurFloor,MAX_FLOOR));
        }
    }

    private void fillFloor(){
        for(byte i=0; i<MAX_FLOOR; i++) {
            this.floors[i] = new Floor((byte)(i+1));
        }


    }

    private void setCurMaxFloor() {
        Optional<Byte>  maxInLift =  curPassengers.stream().max(Comparator.naturalOrder());
        Optional<Byte>  maxWait = Arrays.stream(floors).filter(x->x.getPassengersFloor().size()>0).map(x->x.getNumber()).max(Comparator.naturalOrder());

        if(maxInLift.isPresent() && maxWait.isPresent()){
            this.curMaxFloor = (byte) Math.max(maxInLift.get(), maxWait.get());
        }else if(maxWait.isPresent()){
            this.curMaxFloor = (byte) maxWait.get();
        } else if(maxInLift.isPresent()){
            this.curMaxFloor = maxInLift.get();
        } else{
            this.curMaxFloor = 1;
        }
    }

    private Optional<Byte> getNextFloor() {
        byte passInLift = (byte) this.curPassengers.size();

        Optional<Byte> nextFloorWait = Optional.empty();
        Optional<Byte> nextInLift;

        if(this.currentDirection == Direction.UP) {
            nextInLift =  curPassengers.stream().filter(x -> (Byte) x > numberCurFloor).min(Comparator.naturalOrder());
        }else{
            nextInLift =  curPassengers.stream().filter(x -> (Byte) x < numberCurFloor).max(Comparator.naturalOrder());
        }

        if(passInLift < MAX_PASSENGERS){
            nextFloorWait = getNextWaitFloor();
        }
        if(nextFloorWait.isPresent() && nextInLift.isPresent()){
            if(this.currentDirection == Direction.UP){
                return Optional.of(Byte.valueOf((byte) Math.min(nextInLift.get(), nextFloorWait.get())));
            }else{
                return Optional.of(Byte.valueOf((byte) Math.max(nextInLift.get(), nextFloorWait.get())));
            }
        }else if(nextFloorWait.isPresent()){
            return nextFloorWait;
        }

        return nextInLift;
    }

    private Optional<Byte> getNextWaitFloor(){
        byte passInLift = (byte) this.curPassengers.size();
        if(this.currentDirection == Direction.UP){
            byte numbCurFloor = (byte) (numberCurFloor+1);
            if(this.curPassengers.isEmpty()){
                //take from current floor
                numbCurFloor = this.numberCurFloor;
            }
            for(byte i  = numbCurFloor; i<=MAX_FLOOR; i++){
                List<Byte> floorPass;
                if((floorPass = floors[i-1].getPassengersFloor()).size()>0){
                    byte finalI = i;
                    long count = floorPass.stream().filter(x->x>=finalI).limit(MAX_PASSENGERS - passInLift).count();
                    if(count>0){
                        return Optional.of(i);
                    }
                }
            }
        } else {
            byte numbCurFloor = (byte) (this.numberCurFloor-1);
          /*  if(this.curPassengers.isEmpty()){
                numberCurFloor = this.curMaxFloor;
                System.out.println("curmax "+this.curMaxFloor);
            }*/
            for(byte i = numbCurFloor; i>0; i--){
                List<Byte> floorPass;
                if((floorPass = floors[i-1].getPassengersFloor()).size()>0){
                    byte finalI = i;
                    long count = floorPass.stream().filter(x->x<=finalI).limit(MAX_PASSENGERS - passInLift).count();
                    if(count>0){
                        return Optional.of(i);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private byte getDirectionWaitPass(){
       long up = Arrays.stream(floors).filter(x->x.getNumber()>numberCurFloor).count();
       long down = Arrays.stream(floors).filter(x->x.getNumber()<numberCurFloor).count();
       if(up>down){
           return this.curMaxFloor;
       }
       return 1;
    }

    public void setCurFloor(byte numbCurFloor) {
        this.curFloor = floors[numbCurFloor-1];
        if(numbCurFloor==MAX_FLOOR || numbCurFloor==1 || numbCurFloor==this.curMaxFloor){
            setCurrentDirection();
        }
        this.numberCurFloor = numbCurFloor;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }
    
    private void changeDirection(){
        if(this.currentDirection==Direction.UP){
            this.currentDirection = Direction.DOWN;
        }else{
            this.currentDirection = Direction.UP;
        }
    }

    public void setCurrentDirection() {
        if(numberCurFloor==MAX_FLOOR || numberCurFloor==this.curMaxFloor){
            this.currentDirection = Direction.DOWN;
        }else if(numberCurFloor == 1){
            this.currentDirection = Direction.UP;
        }
    }
    /*public void setCurrentDirection(Direction direction) {
            this.currentDirection = direction;
        }*/

   /* private byte getNumberCurFloor(){
        if(curFloor==null){
            return 1;
        }
        return curFloor.getNumber();
    } */

    public byte getMAX_FLOOR() {
        return MAX_FLOOR;
    }

    public Floor getCurFloor() {
        return curFloor;
    }

    public List<Byte> getCurPassengers() {
        return curPassengers;
    }

    public enum Direction{
        UP, DOWN;
    }
}
