package modules.database.factory;

import modules.database.entities.Country;
import modules.database.entities.LayoutConfig;
import modules.database.entities.LayoutFragment;
import modules.database.entities.User;

import java.util.ArrayList;

/**
 * Created by florian on 02.12.15.
 */
public class SimpleLayoutConfigurationFactory {

    private LayoutConfig layoutConfig = new LayoutConfig();

    private ArrayList<LayoutFragment> fragments = new ArrayList<>();

    public LayoutConfig build (){

        for (LayoutFragment fragment : fragments) {
            fragment.setLayoutConfig(layoutConfig);
        }

        return layoutConfig;
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
