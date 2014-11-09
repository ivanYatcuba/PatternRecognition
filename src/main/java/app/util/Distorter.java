package app.util;

import app.backend.model.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class Distorter {

    @Autowired
    private BitUtil bitUtil;

    public List<Pattern> distort(Pattern p, int lim, int distRate){
        List<Pattern> results = new ArrayList<>();
        for(int i=0; i < lim; i++){
            Pattern nP = new Pattern();
            nP.setData(new byte[p.getData().length]);
            Random rand = new Random();
            for(int j=0; j<nP.getData().length; j++) {
                nP.getData()[j] = p.getData()[j];
                for(int k=0 ; k<8; k++) {
                    if(j>61 && rand.nextInt((100) + 1) >= 100-distRate){
                        nP.getData()[j] = bitUtil.revertBit(nP.getData()[j], k);
                    }
                }
            }
            nP.setName(p.getName()+"_"+i);
            nP.setBenchmark(false);
            nP.setParentId(p.getId());
            results.add(nP);
        }
        return results;
    }
}
