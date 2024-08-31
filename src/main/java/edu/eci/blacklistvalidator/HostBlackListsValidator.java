/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.blacklistvalidator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.eci.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {
    HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
    private static final int BLACK_LIST_ALARM_COUNT=5;
    private final AtomicInteger globalCount = new AtomicInteger(0);
    private boolean running;
    private boolean trustworthy;

    public List<Integer> checkHost(String ipaddress, int numberThreads){
        LinkedList<Integer> blackListOcurrences=new LinkedList<Integer>();
        int[] partesRango = getRanges(numberThreads);
        ArrayList<HostBlackListsValidatorThreads> threads = new ArrayList<HostBlackListsValidatorThreads>();
        running = true;
        trustworthy = true;

        for(int i=0; i<numberThreads; i++){
            HostBlackListsValidatorThreads thread = new HostBlackListsValidatorThreads(ipaddress, partesRango[i], partesRango[i+1], globalCount);
            thread.start();
            threads.add(thread);
        }
        
        int checkedListsCount = 0;
        while(running){
            for(HostBlackListsValidatorThreads i: threads){
                    globalCount.addAndGet(i.getOcurrences());
            }

            if (globalCount.get()>=BLACK_LIST_ALARM_COUNT){
                running=false;
                trustworthy = false;
            }
        }

        for(HostBlackListsValidatorThreads i: threads){
                checkedListsCount += i.getCheckedListsCount();
                blackListOcurrences.addAll(i.getBlackListOcurrences());
        }
        if(trustworthy){
            skds.reportAsTrustworthy(ipaddress);
        }
        else{
            skds.reportAsNotTrustworthy(ipaddress);
        }

        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        return blackListOcurrences;
    }

    private int[] getRanges(int numberThreads){
        int numberLists = skds.getRegisteredServersCount();
        int residue = numberLists % numberThreads;
        int parts = numberLists / numberThreads;
        int[] partesRango= new int[numberThreads+1];
        int j = 0;
        for(int i = 0; i <= numberThreads; i++){
            partesRango[i] = j;
            j += parts;
            if(i < residue){
                j += 1;
            }
        }
        return partesRango;
    }

    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
}
