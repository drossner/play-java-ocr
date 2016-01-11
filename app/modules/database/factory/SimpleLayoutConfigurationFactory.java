package modules.database.factory;

import modules.database.LayoutConfigurationController;
import modules.database.entities.*;
import postprocessing.PostProcessor;
import preprocessing.PreProcessor;

import java.util.ArrayList;

/**
 * Created by florian on 02.12.15.
 */
public class SimpleLayoutConfigurationFactory {

    private LayoutConfig layoutConfig = new LayoutConfig();

    private ArrayList<LayoutFragment> fragments = new ArrayList<>();
    private ArrayList<PreProcessing> preProcessing = new ArrayList<>();
    private ArrayList<PostProcessing> postProcessing = new ArrayList<>();

    /**
     * setzt die fragments, pre- und postprocessoren der Layoutconfig und gibt diese dann zurück
     * @return layout konfiguration mit den vorher gesetzten werten
     */
    public LayoutConfig build (){
        LayoutConfigurationController controller = new LayoutConfigurationController();

        controller.persistObject(layoutConfig);

        for (LayoutFragment fragment : fragments) {
            fragment.setLayoutConfig(layoutConfig);
            controller.persistObject(fragment);
        }

        for (PreProcessing processing : preProcessing) {
            processing.setLayoutConfig(layoutConfig);
            controller.persistObject(processing);
        }

        for (PostProcessing processing : postProcessing) {
            processing.setLayoutConfig(layoutConfig);
            controller.persistObject(processing);
        }

        return layoutConfig;
    }

    /**
     * erzeugt ein neues postprocessing objekt aus dem package database und setzt den klassennamen des übergebenen postprocessors in die variable
     * dieser wird der postprocessing liste hinzugefügt
     * @param postProcessor postprocessor der hinzugefügt werden soll
     * @return sich selbst, für method chain
     */
    public SimpleLayoutConfigurationFactory addPostProcessing(PostProcessor postProcessor){
        PostProcessing post = new PostProcessing();
        post.setPostProcessor(postProcessor.toString());
        this.postProcessing.add(post);

        return this;
    }

    /**
     * erzeugt ein neues preprocessing objekt aus dem package database und setzt den klassennamen des übergebenen preprocessors in die variable
     * dieser wird der preProcessing liste hinzugefügt
     * @param preProcessor preProcessor der hinzugefügt werden soll
     * @return sich selbst, für method chain
     */
    public SimpleLayoutConfigurationFactory addPreProcessing(PreProcessor preProcessor){
        PreProcessing pre = new PreProcessing();
        pre.setPreProcessor(preProcessor.toString());
        this.preProcessing.add(pre);

        return this;
    }

    public SimpleLayoutConfigurationFactory addFragment(LayoutFragment fragment){
        fragments.add(fragment);

        return this;
    }

    public SimpleLayoutConfigurationFactory setName(String name){
        layoutConfig.setName(name);

        return this;
    }

    public SimpleLayoutConfigurationFactory setUser(User user){
        layoutConfig.setUser(user);

        return this;
    }

    public SimpleLayoutConfigurationFactory setLanguage(Country country){
        layoutConfig.setLanguage(country);

        return this;
    }

    /**
     * erzeugt eine neue layout fragment factory
     * @return inner class simplelayoutfragmentfactory
     */
    public SimpleLayoutFragmentFactory createLayoutFragmentFactory(){
        return new SimpleLayoutFragmentFactory();
    }

    /**
     * inner class für die erstellung eines layoutfragments
     */
    public class SimpleLayoutFragmentFactory{

        LayoutFragment fragment = new LayoutFragment();

        /**
         * gibt das Layoutfragment mit den gesetzten werten zurück
         * @return layoutfragment
         */
        public LayoutFragment build(){

            return fragment;
        }

        public SimpleLayoutFragmentFactory setXStart(double xStart){
            fragment.setxStart(xStart);
            return this;
        }

        public SimpleLayoutFragmentFactory setYStart(double yStart){
            fragment.setyStart(yStart);
            return this;
        }

        public SimpleLayoutFragmentFactory setXEnd(double xEnd){
            fragment.setxEnd(xEnd);
            return this;
        }

        public SimpleLayoutFragmentFactory setYEnd(double yEnd){
            fragment.setyEnd(yEnd);
            return this;
        }

        public SimpleLayoutFragmentFactory setType(String type){
            fragment.setType(type);
            return this;
        }
    }
}
