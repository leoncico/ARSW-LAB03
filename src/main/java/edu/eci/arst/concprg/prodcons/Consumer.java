/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author hcadavid
 */
public class Consumer extends Thread{
    
    private LinkedBlockingQueue<Integer> queue;
    private Object lock;
    private int elem;
    
    
    public Consumer(LinkedBlockingQueue<Integer> queue, Object lock){
        this.queue=queue;
        this.lock = lock;        
    }
    
    @Override
    public void run() {
        while (true) {

            if (!queue.isEmpty()) {
                elem=queue.poll();
           }
            System.out.println("Consumer consumes "+elem);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }          
        }
    }
}
