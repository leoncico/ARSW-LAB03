/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author hcadavid
 */
public class Producer extends Thread {

    private LinkedBlockingQueue<Integer> queue = null;

    private int dataSeed = 0;
    private Random rand=null;
    private final long stockLimit;
    private Object lock;

    public Producer(LinkedBlockingQueue<Integer> queue,int stockLimit, Object lock) {
        this.queue = queue;
        rand = new Random(System.currentTimeMillis());
        this.stockLimit=stockLimit;
        this.lock = lock;
    }

    @Override
    public void run() {
        while (true) {

            dataSeed = dataSeed + rand.nextInt(100);
            //System.out.println("Producer added " + dataSeed);
            synchronized(lock){
                //lock.notifyAll();
                try {
                    queue.put(dataSeed);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            System.out.println(queue.toString());

            // try {
            //      Thread.sleep(0);
            // } catch (InterruptedException ex) {
            //      Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            // }


        }
    }
}
