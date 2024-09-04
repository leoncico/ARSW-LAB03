package edu.eci.arsw.highlandersim;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final CopyOnWriteArrayList<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private boolean pausedGame= false;
    private boolean running=true;
    private final Object lockA;
    private final Object lockB;
    private boolean isDead = false;

    public Immortal(String name, CopyOnWriteArrayList<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb,Object lock) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
        this.lockA=lock;
        lockB = new Object();
    }

    public void run() {

        while (running && immortalsPopulation.size()>1 && getHealth()>0) {
                while(pausedGame){
                    synchronized (lockA) {
                        try {
                            lockA.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    
                    }
                }
            Immortal im;
            int myIndex = immortalsPopulation.indexOf(this);
            int nextFighterIndex = r.nextInt(immortalsPopulation.size());
            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }
            im = immortalsPopulation.get(nextFighterIndex);
            
            fight(this, im);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void fight(Immortal i1, Immortal i2) {
        Immortal firstThread;
        Immortal secondThread;
            if (i1.hashCode() < i2.hashCode()) {
                firstThread = i1;
                secondThread = i2;
            } else {
                firstThread = i2;
                secondThread = i1;
            }
            synchronized(firstThread){
                synchronized(secondThread){
                    if (i2.getHealth() > 0) {
                        i2.changeHealth(i2.getHealth() - defaultDamageValue);
                        i1.health += defaultDamageValue;
                        updateCallback.processReport("Fight: " + i1 + " vs " + i2+"\n");
                    }
                    else {
                        if (!i2.isDead) {
                            i2.markAsDead();
                            updateCallback.processReport(i1 + " says:" + i2 + " is already dead!\n");
                        }
                        
                    }
                }
            }
    }

    public void clearList(){
        for(Immortal i: immortalsPopulation){
            if(i.getHealth()<=0){
                immortalsPopulation.remove(i);
            }
        }
    }
    
    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
        
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

    public void pauseMethod(){
        pausedGame=true;
        if(isDead){
            clearList();
        }
    }

    public void activeImmortal(){
        synchronized (lockA) {
            pausedGame=false;
            lockA.notify();
        }
    }

    public void stopMethod(){
        running=false;
    }

    public void markAsDead() {
        isDead = true;
        running = false;
    }

}
