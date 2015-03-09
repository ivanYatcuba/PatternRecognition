package app.util.pattern;

import app.backend.model.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Distorter {

    public List<Pattern> distort(Pattern p, int lim, int distRate){
        return distort(p, lim, distRate, 61);
    }

    public List<Pattern> distort(Pattern p, int lim, int distRate, Integer offset){
        List<Pattern> results = new ArrayList<>();
        for(int i=0; i < lim; i++){
            Pattern nP = new Pattern();
            nP.setData(new byte[p.getData().length]);
            Random rand = new Random();
            if(distRate != 0) {
                for(int j=0; j<nP.getData().length; j++) {
                    nP.getData()[j] = p.getData()[j];
                    for(int k=0 ; k<8; k++) {
                        if(j>offset && rand.nextInt((100) + 1) >= 100-distRate){
                            nP.getData()[j] = BitUtil.revertBit(nP.getData()[j], k);
                        }
                    }
                }
            } else {
                nP.setData(p.getData());
            }

            nP.setName(p.getName()+"_"+i);
            nP.setBenchmark(false);
            nP.setParentId(p.getId());
            results.add(nP);
        }
        return results;
    }
}
