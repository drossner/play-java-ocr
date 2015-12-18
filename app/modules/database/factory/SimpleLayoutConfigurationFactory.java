package modules.database.factory;

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

    public LayoutConfig build (){

        for (LayoutFragment fragment : fragments) {
            fragment.setLayoutConfig(layoutConfig);
        }

        for (PreProcessing processing : preProcessing) {
            processing.setLayoutConfig(layoutConfig);
        }

        for (PostProcessing processing : postProcessing) {
            processing.setLayoutConfig(layoutConfig);
        }

        return layoutConfig;
    }

    public SimpleLayoutConfigurationFactory addPostProcessing(PostProcessor postProcessor){
        PostProcessing post = new PostProcessing();
        post.setPostProcessor(postProcessor.toString());
        this.postProcessing.add(post);

        return this;
    }

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

    public SimpleLayoutFragmentFactory createLayoutFragmentFactory(){
        return new SimpleLayoutFragmentFactory();
    }

    public class SimpleLayoutFragmentFactory{

        LayoutFragment fragment = new LayoutFragment();

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
