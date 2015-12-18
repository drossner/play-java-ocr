package modules.database.entities;

import postprocessing.PostProcessor;
import preprocessing.PreProcessor;

import javax.persistence.*;

/**
 * Created by FRudi on 17.12.2015.
 */
@Entity
@Table(name="PreProcessing")
public class PostProcessing extends DomainObject {

    @Id
    @GeneratedValue
    private int id;

    @Enumerated(EnumType.STRING)
    @Column
    private String postProcessor;

    @OneToOne
    private LayoutConfig layoutConfig;

    public PostProcessing(){

    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public LayoutConfig getLayoutConfig() {
        return layoutConfig;
    }

    public void setLayoutConfig(LayoutConfig layoutConfig) {
        this.layoutConfig = layoutConfig;
    }

    public String getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(String postProcessor) {
        this.postProcessor = postProcessor;
    }
}
