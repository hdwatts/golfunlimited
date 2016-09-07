package com.hdwatts.LostFractals;

import com.hdwatts.LostFractals.Entities.Particle;

import java.util.ArrayList;

/**
 * Created by Dean Watts on 1/17/2015.
 */
public class ParticleManager {
    ArrayList<Particle[]> particles;
    ArrayList<String> names;

    public ParticleManager(){
        particles = new ArrayList<Particle[]>();
        names = new ArrayList<String>();
    }

    public void addParticles(Particle[] p, String name){
        particles.add(p);
        names.add(particles.indexOf(p),name);
    }

    public Particle[] getParticles(String name){
        if(names.indexOf(name) == -1){
            return null;
        }
        return particles.get(names.indexOf(name));
    }

    public void removeParticles(String name){
        if(names.indexOf(name) == -1)
            return;
        particles.remove(names.indexOf(name));
        names.remove(name);
    }

}
