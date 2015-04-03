package app.util.pattern;

import app.backend.model.Pattern;
import app.util.ImageWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Distorter {

    public List<Pattern> distort(Pattern p, int lim, int distRate){
        List<Pattern> results = new ArrayList<>();
        for(int i=0; i < lim; i++){
            Pattern nP = new Pattern();
            nP.setImage(p.getImage());
            Random rand = new Random();
            if(distRate != 0) {
                for(int j=0; j< nP.getImage().getDataSize(); j++) {
                    if(rand.nextInt((100) + 1) >= 100-distRate){
                        nP.revertPixel(j);
                    }
                }
            }
            nP.setPixel(0, 0, ImageWrapper.RED);
            nP.setName(p.getName()+"_"+i);
            nP.setBenchmark(false);
            nP.setParentId(p.getId());
            results.add(nP);
        }
        return results;
    }
}
