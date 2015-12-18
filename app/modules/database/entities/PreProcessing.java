package modules.database.entities;

import preprocessing.PreProcessor;

import javax.persistence.*;

/**
 * Created by FRudi on 17.12.2015.
 */
@Entity
@Table(name="PreProcessing")
public class PreProcessing extends DomainObject {

    @Id
    @GeneratedValue
    private int id;

    @Enumerated(EnumType.STRING)
    @Column
    private String preProcessor;

    @OneToOne
    private LayoutConfig layoutConfig;

    public PreProcessing(){

    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public String getPreProcessor() {
        return preProcessor;
    }

    public void setPreProcessor(String preProcessor) {
        this.preProcessor = preProcessor;
    }

    public LayoutConfig getLayoutConfig() {
        return layoutConfig;
    }

    public void setLayoutConfig(LayoutConfig layoutConfig) {
        this.layoutConfig = layoutConfig;
    }
}
