package app.backend.model;

import javax.persistence.*;

@Entity
public class Pattern {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    @Column
    private String name;
    @Column(columnDefinition="LONGBLOB")
    private byte[] data;
    @Column
    private boolean isBenchmark;
    @Column
    private Long parentId;

    @Transient
    private boolean[]  bitData;

    public Pattern() {

    }

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public byte[] getData() {return data;}
    public void setData(byte[] data) {this.data = data;}

    public boolean isBenchmark() {return isBenchmark;}
    public void setBenchmark(boolean isBenchmark) {this.isBenchmark = isBenchmark;}

    public Long getParentId() {return parentId;}
    public void setParentId(Long parentId) {this.parentId = parentId;}

    public boolean[] getBitData() {
        if (bitData == null) {
            boolean[] bits = new boolean[data.length * 8];
            for (int i = 0; i < data.length * 8; i++) {
                if ((data[i / 8] & (1 << (7 - (i % 8)))) > 0)
                    bits[i] = true;
            }
            bitData = bits;
        }
        return bitData;
    }

    public void setBitData(boolean[] bitData) {this.bitData = bitData;}

    @Override
    public String toString() {
        return name;
    }

    public Pattern copy(boolean[] bitData) {
        Pattern copy = new Pattern();
        copy.id = this.id;
        copy.isBenchmark = this.isBenchmark;
        copy.data = this.data;
        copy.name = this.name;
        copy.parentId = this.parentId;
        copy.bitData = bitData;
        return copy;
    }
}
