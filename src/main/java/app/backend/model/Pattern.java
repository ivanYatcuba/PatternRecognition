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

    @Override
    public String toString() {
        return name;
    }
}
